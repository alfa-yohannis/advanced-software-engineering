# The TTS stack on Kubernetes

Code for session 6 (Software Infrastructure Orchestration) of IT30213 Advanced Software Engineering & DevOps. The application is the text-to-speech (TTS) system from sessions 4 and 5: you send a text, a worker turns it into speech, and a web page plays the MP3 file. What is new here is where it runs. The same six services are described as Kubernetes objects, deployed to a local cluster with Minikube, and reached through an ingress instead of a published port.

## How the pieces fit together

```
browser --HTTP--> ingress --/api/*--------> api --job and status--> redis
                  ingress --/ and /mp3/*--> web <--MP3 file-------- minio

worker <--next job---- redis
worker --MP3 file----> minio
worker --job status--> api
```

| Object | What it does |
|---|---|
| `deployment/api` | FastAPI. Creates jobs, answers status requests and takes status updates from the worker. |
| `deployment/web` | FastAPI. Serves the page and the MP3 files, which it reads from MinIO. |
| `deployment/worker` | Takes jobs from Redis and makes the MP3 files with Piper and ffmpeg. |
| `deployment/redis` | The queue of waiting jobs and the status of every job. |
| `statefulset/minio` | S3-compatible object storage for the MP3 files, with a PersistentVolumeClaim for its data. |
| `job/minio-bootstrap` | A one-off placeholder job that prints a line. The bucket itself is created by the worker on its first upload. |
| `hpa/worker` | Scales the worker between 1 and 5 pods at 70 percent CPU. It needs the metrics-server addon. |
| `ingress/tts-web`, `ingress/tts-api` | Route `/` to the web service and `/api` to the API, both on the host `dev.tts.local`. |

Configuration is split the way Kubernetes expects it: `configmap/tts-config` holds the plain settings (`APP_ENV`, `MP3_BUCKET`, `REDIS_URL`, `API_BASE_URL`, `S3_ENDPOINT`, `S3_REGION`) and `secret/tts-secrets` holds the credentials (`S3_ACCESS_KEY`, `S3_SECRET_KEY`, `INTERNAL_TOKEN`). Every deployment reads both with `envFrom`.

```
app/                       the same application as sessions 4 and 5
compose/docker-compose.yml the stack on Docker Compose, for comparison
iac/kubernetes/base/       the objects above, shared by all environments
iac/kubernetes/overlays/   dev, staging and prod, each patching images, replicas and config
iac/terraform/             the same stack through the Docker provider, as in session 5
```

The overlays follow one pattern: `base` carries the `:dev` images, and each overlay patches what differs. Dev patches the images only, staging and prod also patch the replica counts and the config map, and prod adds two PodDisruptionBudgets.

The `minio` service runs Silo (`pgsty/silo`), a community fork of the MinIO server, because the official `minio/minio` image is no longer on Docker Hub. The bootstrap job runs `busybox` for the same reason: `minio/mc` is gone as well.

## Before you start

- Docker with the Compose plugin, for the comparison in step 2 and for Minikube's driver.
- Minikube and kubectl. This guide was tested with Minikube 1.38.0 and kubectl 1.35.0 on Kubernetes 1.35.
- Terraform 1.5 or newer for step 11, and membership of the `docker` group, because the provider talks to the Docker socket directly.
- Free ports 6379, 8000, 8080, 8088, 9000 and 9001 for the Compose and Terraform steps. The Kubernetes steps use the cluster address instead, so they do not need them.
- Around 6 GB of memory and 4 CPUs free for the cluster, and about 2 GB of disk for the images.
- For the tests: Python 3.12 and the virtual environment in `projects/.venv`.

The Docker commands below use `sudo docker`. If your user is in the `docker` group, leave out `sudo`.

## Step 1. Go to the project folder

```bash
cd projects/session-06
```

## Step 2. Run the stack on Compose first

Seeing the same system on Compose makes the Kubernetes objects easier to read later.

```bash
cd compose
sudo docker compose up --build -d
sudo docker compose ps
```

The six containers are named `session-06-api-1`, `session-06-gateway-1`, `session-06-minio-1`, `session-06-redis-1`, `session-06-web-1` and `session-06-worker-1`. Open http://localhost:8088/ and send a text, or use curl:

```bash
curl -X POST http://localhost:8088/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello from Compose"}'
curl http://localhost:8088/api/jobs/<job-id>
curl -o output.mp3 http://localhost:8088/mp3/<job-id>.mp3
```

Then stop it, because the Terraform step in this guide uses the same ports:

```bash
sudo docker compose down
cd ..
```

## Step 3. Run the tests

```bash
cd app
../../.venv/bin/pip install -r requirements-dev.txt
../../.venv/bin/python -m pytest -q tests/unit
```

```
......................................                                   [100%]
38 passed in 1.14s
```

The integration tests need a running stack and reach it through the gateway:

```bash
../../.venv/bin/python -m pytest -q tests/integration
cd ..
```

## Step 4. Start the cluster

```bash
minikube start --driver=docker --cpus=4 --memory=6g
minikube addons enable ingress
minikube addons enable metrics-server
kubectl get nodes
```

```
NAME       STATUS   ROLES           AGE   VERSION
minikube   Ready    control-plane   70s   v1.35.0
```

The ingress addon is what makes step 7 work, and the metrics-server addon is what lets the autoscaler read CPU in step 8. The ingress controller needs about a minute before it accepts new Ingress objects.

## Step 5. Put the application images into the cluster

The cluster has its own image store and cannot see the images on your machine. Build them once, then load them:

```bash
sudo docker build -t session05-api:dev app/services/api
sudo docker build -t session05-web:dev app/services/web
sudo docker build -t session05-worker:dev app/services/worker

minikube image load session05-api:dev
minikube image load session05-web:dev
minikube image load session05-worker:dev
minikube image ls | grep session05
```

Loading the worker image takes the longest, because it carries Piper and the voice. The names keep the `session05` prefix on purpose: these are the images built in session 5, now running on a cluster.

Each overlay asks for its own tag: `:dev`, `:staging` and `:prod`. Before applying one of the other two, give the cluster the tags it will ask for, or the rollout stalls and you end up with more pods than the overlay asked for, as the last row of the troubleshooting table explains:

```bash
for svc in api web worker; do
  sudo docker tag session05-$svc:dev session05-$svc:staging
  minikube image load session05-$svc:staging
done
```

## Step 6. Apply the dev overlay

```bash
cd iac/kubernetes
kubectl apply -k overlays/dev
kubectl get pods -n tts
```

Wait until every pod is ready:

```bash
kubectl wait --for=condition=available --timeout=300s \
  deploy/api deploy/web deploy/worker deploy/redis -n tts
kubectl rollout status statefulset/minio -n tts
```

```
NAME                      READY   STATUS      RESTARTS   AGE
api-6fcdb684f7-djk4w      1/1     Running     0          3m49s
minio-0                   1/1     Running     0          3m49s
minio-bootstrap-vd2zm     0/1     Completed   0          116s
redis-c46d5dffc-r9cnz     1/1     Running     0          3m49s
web-759b68d9c9-nltdz      1/1     Running     0          3m49s
worker-589d585c44-hnbl7   1/1     Running     0          3m49s
```

## Step 7. Open the application through the ingress

The ingresses answer on the host `dev.tts.local`, so the cluster address needs that name. Either add it to your hosts file:

```bash
echo "$(minikube ip) dev.tts.local" | sudo tee -a /etc/hosts
```

and open http://dev.tts.local/, or send the header yourself:

```bash
IP=$(minikube ip)
curl -H 'Host: dev.tts.local' http://$IP/healthz
curl -H 'Host: dev.tts.local' http://$IP/api/healthz
```

```
{"ok":true,"env":"dev","bucket":"tts-dev"}
{"ok":true,"env":"dev"}
```

The whole job runs the same way as on Compose, only through the cluster:

```bash
curl -H 'Host: dev.tts.local' -X POST http://$IP/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello from Kubernetes"}'
curl -H 'Host: dev.tts.local' http://$IP/api/jobs/<job-id>
curl -H 'Host: dev.tts.local' -o output.mp3 http://$IP/mp3/<job-id>.mp3
```

A short sentence finishes in a few seconds and its MP3 is around 15 KB.

## Step 8. Watch the autoscaler

```bash
kubectl get hpa -n tts
kubectl top pods -n tts
```

```
NAME     REFERENCE           TARGETS       MINPODS   MAXPODS   REPLICAS   AGE
worker   Deployment/worker   cpu: 0%/70%   1         5         1          5m51s
```

Right after the cluster starts, the column reads `cpu: <unknown>/70%`. The metrics-server needs a minute or two before the first values arrive.

That is the idle picture. To see the autoscaler work, give the worker more jobs than one pod can chew through. Open two more terminals and leave them watching:

```bash
watch -n 2 kubectl get hpa worker -n tts
watch -n 2 kubectl get pods -n tts -l app=worker
```

Then send the load from the project folder. `load_client.py` submits jobs, polls each one until it is DONE, and reports how long each took. It needs nothing but Python 3:

```bash
python3 app/client/load_client.py \
  --base-url http://$(minikube ip) --host dev.tts.local \
  --jobs 20 --concurrency 20
```

```
api says {"ok":true,"env":"dev"}
submitting 20 job(s), 20 at a time, 511 characters each
  job   4: DONE    in   16.1s
  ...
  job  18: DONE    in  176.7s

wall clock      : 176.8s
done            : 20/20
per job seconds : min 16.1, median 113.4, max 176.7
throughput      : 0.11 jobs/second
```

Each job there synthesises the client's built-in sentence four times over, 511 characters, which comes out as an MP3 of about 240 KB. `long_text_test.txt` in this folder is a longer passage to use when you want every job to cost more:

```bash
python3 app/client/load_client.py \
  --base-url http://$(minikube ip) --host dev.tts.local \
  --jobs 20 --concurrency 20 \
  --text-file long_text_test.txt --repeat 1
```

That gives 576 characters a job and MP3 files of about 278 KB. Keep `--repeat 1` with a file this size: the API rejects anything over 2000 characters, and the client stops before submitting rather than letting twenty jobs fail one at a time.

Here is what the two watch windows showed while the 20-job run went through, sampled every five seconds:

```
17:29:39  cpu 199%  desired 5  running 1
17:30:20  cpu 199%  desired 5  running 2
17:31:23  cpu 199%  desired 5  running 3
17:32:22  cpu 199%  desired 5  running 5
...  the queue drains, the client reports the last job  ...
17:35:16  cpu  20%  desired 4  running 4
17:35:32  cpu  24%  desired 3  running 3
17:35:47  cpu  32%  desired 2  running 2
17:36:02  cpu  24%  desired 1  running 1
17:36:18  cpu   0%  desired 1  running 1
```

Up in about three minutes, down in about one, and back to the idle line the step opened with.

Three things are worth pointing at while this happens.

`desired` jumps to 5 almost at once, while `running` climbs over about three minutes. The autoscaler decides immediately; the pods take their time, because each new worker pod has a 500 MB image to start and Piper to load. Scaling is not instant capacity.

CPU reads 199 percent, not 70. The number is the average across the pods as a share of the `requests` value, which is `500m` for the worker, so 199 percent means each pod is using about one whole core, which is also its limit. The autoscaler cannot push it lower than the work demands; all it can do is spread the queue over more pods.

Scaling down waits for the metric, not for the queue. The queue was already empty, and `kubectl top pods -n tts` showed the workers almost idle, a good minute before the HPA reduced the count. The averaging window in metrics-server is what you are waiting for.

The speed of both directions comes from the `behavior` block in `base/hpa-worker.yaml`: no stabilisation window going up, two pods or 70 percent every five seconds, and going down a five second window with one pod or 20 percent every five seconds. Those are deliberately impatient settings, chosen so the demo fits in a class. The Kubernetes defaults wait five minutes before scaling down.

## Step 9. Switch to another overlay, or start over

The three overlays differ in five places:

| | dev | staging | prod |
|---|---|---|---|
| Ingress host | `dev.tts.local` | `staging.tts.local` | `tts.local` |
| Image tags | `:dev` | `:staging` | `:prod` |
| api and web replicas | 1 each, straight from the base | 2 each | 3 each |
| `APP_ENV` and bucket | `dev`, `tts-dev` | `staging`, `tts-staging` | `prod`, `tts-prod` |
| Extra objects | none | none | two PodDisruptionBudgets |

The worker is missing from the replica row on purpose: the autoscaler owns its replica count in all three environments.

Switching is not just another `apply`. `kubectl apply -k` adds and updates objects but never removes what the previous overlay created, and each overlay names its ingresses after itself, so applying staging on top of dev leaves four:

```bash
kubectl get ingress -n tts -o custom-columns='NAME:.metadata.name,HOST:.spec.rules[0].host'
```

```
NAME              HOST
tts-api           dev.tts.local
tts-api-staging   staging.tts.local
tts-web           dev.tts.local
tts-web-staging   staging.tts.local
```

Both hostnames then reach pods that run the staging configuration, which is confusing in a demo and wrong in a test. So clear the namespace first:

```bash
kubectl delete -k overlays/dev
kubectl get ns tts
```

```
namespace "tts" deleted
...
Error from server (NotFound): namespaces "tts" not found
```

Every overlay includes `base/namespace.yaml`, so deleting an overlay deletes the namespace and cascades to everything inside it, including objects another overlay created and the `minio-data` claim with the MP3 files on it. When you no longer know which overlay is deployed, `kubectl delete namespace tts` does the same thing in one line. That is also the answer to `field is immutable`.

Then bring up the environment you want, from nothing:

```bash
for svc in api web worker; do
  sudo docker tag session05-$svc:dev session05-$svc:staging
  minikube image load session05-$svc:staging
done

kubectl apply -k overlays/staging
kubectl wait --for=condition=available --timeout=300s \
  deploy/api deploy/web deploy/worker deploy/redis -n tts
kubectl get deploy -n tts
```

```
NAME     READY   UP-TO-DATE   AVAILABLE   AGE
api      2/2     2            2           23m
redis    1/1     1            1           23m
web      2/2     2            2           23m
worker   1/1     1            1           23m
```

`UP-TO-DATE` matching `READY` is the column that says the rollout finished. Check that the environment really changed, rather than trusting the apply:

```bash
kubectl get configmap tts-config -n tts \
  -o jsonpath='{.data.APP_ENV}{"  "}{.data.MP3_BUCKET}{"\n"}'
curl -H 'Host: staging.tts.local' http://$(minikube ip)/api/healthz
```

```
staging  tts-staging
{"ok":true,"env":"staging"}
```

The bucket changed with the environment, so the MP3 files made under dev are in `tts-dev` and the staging stack will not serve them. Each environment starts with an empty bucket, which is the point of patching the name.

One trap worth knowing: every deployment reads the config map with `envFrom`, and that happens once at start-up. Changing a config map does not reach a running pod. Switching overlays gets away with it because the image tag changes at the same moment and every pod restarts. If you ever edit only the config map, restart them yourself:

```bash
kubectl rollout restart deploy -n tts
```

## Step 10. Change one service and roll the change out

A cluster is not a place to edit files. Changing one service of the stack means building a new image, handing it to the cluster, and letting the Deployment replace its own pods. The web service is the one to try it on, because the change shows up in a browser.

Change the heading on the page:

```bash
cd ../..                      # back to projects/session-06
sed -i 's|<h1>TTS Case Study</h1>|<h1>TTS Case Study v2</h1>|' \
  app/services/web/static/index.html
```

Build it under a new tag and load it. The new tag is what matters: rebuilding under `:dev` leaves the pod spec unchanged, so Kubernetes sees nothing to do and keeps the old pod running.

```bash
sudo docker build -t session05-web:v2 app/services/web
minikube image load session05-web:v2
```

Point the deployment at the new image and watch the replacement:

```bash
kubectl set image deploy/web web=session05-web:v2 -n tts
kubectl rollout status deploy/web -n tts
kubectl get pods -n tts -l app=web
```

```
deployment.apps/web image updated
Waiting for deployment "web" rollout to finish: 1 old replicas are pending termination...
deployment "web" successfully rolled out

NAME                   READY   STATUS        RESTARTS   AGE
web-64bc5754cd-x85ld   1/1     Running       0          1s
web-759b68d9c9-ppx99   1/1     Terminating   0          51s
```

In `web=session05-web:v2` the name on the left is the container, not the deployment. Here each deployment has one container named after itself, so the worker version is `kubectl set image deploy/worker worker=session05-worker:v2 -n tts`. Naming a container that does not exist is rejected with `unable to find container named ...`.

The new page is live:

```bash
curl -s -H 'Host: dev.tts.local' http://$(minikube ip)/ | grep '<h1>'
```

```
<h1>TTS Case Study v2</h1>
```

### Was it really without downtime?

Measure it instead of believing it. In a second terminal, poll the health endpoint a few times a second while the rollout runs:

```bash
IP=$(minikube ip)
while true; do
  printf '%s %s\n' "$(date +%H:%M:%S)" "$(curl -s -o /dev/null -w '%{http_code}' \
    --max-time 3 -H 'Host: dev.tts.local' http://$IP/healthz)"
  sleep 0.25
done
```

The dev overlay runs one web replica, and one replica cannot cover for itself. Rolling the image this way gave nine answers of 200 and one 502: for a moment the ingress had no ready endpoint behind it. Give the deployment a second replica and do it again:

```bash
kubectl rollout undo deploy/web -n tts            # back to :dev
kubectl scale deploy/web --replicas=2 -n tts
kubectl set image deploy/web web=session05-web:v2 -n tts
```

The same probe then gave 119 answers of 200 and one three-second timeout, in the window where the ingress controller was picking up the new endpoints. So a rolling update is only seamless when there are spare replicas to serve from. That is the reason staging patches api and web to 2 and prod to 3, and the reason prod adds PodDisruptionBudgets.

### Undo

Each image change is a revision, and the Deployment keeps them:

```bash
kubectl rollout history deploy/web -n tts
kubectl rollout undo deploy/web -n tts
kubectl scale deploy/web --replicas=1 -n tts
```

```
REVISION  CHANGE-CAUSE
1         <none>
2         <none>
3         <none>
```

`CHANGE-CAUSE` is empty unless you write it yourself:

```bash
kubectl annotate deploy/web -n tts \
  kubernetes.io/change-cause="web heading v2" --overwrite
```

Then put the file back, because this was an exercise:

```bash
git checkout -- app/services/web/static/index.html
```

One thing this exercise cannot show: a live reload. `index.html` is copied into the image when it is built, so a saved file never reaches a running pod on its own. Editing and seeing the result straight away is what the Compose stack in step 2 is for; on a cluster the loop is always build, load, roll out.

## Step 11. The same stack through Terraform

For comparison, `iac/terraform` builds the stack with containers on your own machine, exactly as in session 5:

Step 10 left you in the project folder, so the path starts there:

```bash
cd iac/terraform/dev
terraform init
terraform apply -var-file=dev.tfvars
```

It answers on http://localhost:8088/ as before. Remove it when you are done:

```bash
terraform destroy -var-file=dev.tfvars
```

Running this while the Compose stack is up fails, because both want the same ports.

## Step 12. Clean up

Step 11 left you in `iac/terraform/dev`, so go back to the project folder first:

```bash
cd ../../..
kubectl delete -k iac/kubernetes/overlays/dev
minikube stop
```

Use the overlay you actually applied, or `kubectl delete namespace tts` if you have switched between them.

`minikube stop` keeps the cluster and its image store, so the next session starts at step 6. `minikube delete` throws both away, and then step 5 has to be repeated.

## Changing the settings

| What to change | Where |
|---|---|
| Plain settings such as the bucket name or the environment label | `iac/kubernetes/base/configmap-app.yaml`, or the overlay's `patch-configmap.yaml` for staging and prod |
| Credentials and the internal token | `iac/kubernetes/base/secret-app.yaml` |
| Image tags per environment | `iac/kubernetes/overlays/<env>/patch-images.yaml` |
| Replica counts | `iac/kubernetes/overlays/<env>/patch-replicas.yaml`, and `hpa-worker.yaml` for the autoscaler range |
| Host names | `iac/kubernetes/overlays/<env>/ingress-api.yaml` and `ingress-web.yaml` |

After an edit, run `kubectl apply -k overlays/<env>` again. `kubectl diff -k overlays/<env>` shows what would change before you apply it.

The secret holds `dev-token-change-me` and the MinIO login `minioadmin`. Change both before the cluster is reachable by anyone else.

## Troubleshooting

| Problem | What to do |
|---|---|
| `failed calling webhook "validate.nginx.ingress.kubernetes.io"` | The ingress controller is still starting. Check `kubectl get pods -n ingress-nginx` and run the apply again once it is Running. |
| Pods stay `ContainerCreating` and events show `Pulling image "session05-..."` | The images are not in the cluster. Run the `minikube image load` commands from step 5 and wait for them to finish. |
| `ErrImagePull` or `ImagePullBackOff` on redis, minio or busybox | The cluster is pulling from Docker Hub and the network is slow or blocked. Pull the image on your machine and load it: `sudo docker pull busybox:1.36 && minikube image load busybox:1.36`. |
| `kubectl get hpa` keeps showing `cpu: <unknown>` | The metrics-server addon is missing or still starting. Run `minikube addons enable metrics-server`, then check `kubectl top pods -n tts`. |
| `port is already allocated` in step 2 or 11 | Another session's containers hold the ports. Stop them, for example with `sudo docker compose down` in the other folder, or `terraform destroy` in session 5. |
| The page loads but Synthesize fails | The request went to the web service without `/api` routing. Use the ingress host, not a port-forward to the web pod alone. |
| A job stays `QUEUED` | The worker cannot reach Redis or the queue name differs. Check `kubectl logs deploy/worker -n tts` and compare `QUEUE_KEY` with `APP_ENV` in the config map. |
| `field is immutable` when applying | An object from an earlier run differs in a field Kubernetes cannot change in place, usually a Job. Delete it first: `kubectl delete job minio-bootstrap -n tts`. |
| After `kubectl apply -k overlays/staging`, three api pods are running although the overlay asks for two, one of them in `ImagePullBackOff` | The overlay asked for `:staging` images the cluster does not have. Each deployment started a rolling update, and `maxSurge` of 25 percent on two replicas allows one extra pod, so the new ReplicaSet gets one pod that never pulls its image. Kubernetes then refuses to retire the working `:dev` pods, which is why the old ones stay. `kubectl get deploy -n tts` shows it as `READY 2/2` with `UP-TO-DATE 1`, and `kubectl rollout status deploy/api -n tts` hangs. Load the missing tags as in step 5 and the rollout finishes by itself, or run `kubectl rollout undo deploy/api -n tts` to go back. |
