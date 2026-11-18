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
- Terraform 1.5 or newer for step 9, and membership of the `docker` group, because the provider talks to the Docker socket directly.
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

Right after the cluster starts, the column reads `cpu: <unknown>/70%`. The metrics-server needs a minute or two before the first values arrive. To see the autoscaler react, send a long text or several jobs at once and watch the replica count.

## Step 9. The same stack through Terraform

For comparison, `iac/terraform` builds the stack with containers on your own machine, exactly as in session 5:

```bash
cd ../terraform/dev
terraform init
terraform apply -var-file=dev.tfvars
```

It answers on http://localhost:8088/ as before. Remove it when you are done:

```bash
terraform destroy -var-file=dev.tfvars
```

Running this while the Compose stack is up fails, because both want the same ports.

## Step 10. Clean up

Step 9 left you in `iac/terraform/dev`, so go back to the project folder first:

```bash
cd ../../..
kubectl delete -k iac/kubernetes/overlays/dev
minikube stop
```

`minikube delete` removes the cluster and its image store as well, which means step 5 has to be repeated next time.

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
| `port is already allocated` in step 2 or 9 | Another session's containers hold the ports. Stop them, for example with `sudo docker compose down` in the other folder, or `terraform destroy` in session 5. |
| The page loads but Synthesize fails | The request went to the web service without `/api` routing. Use the ingress host, not a port-forward to the web pod alone. |
| A job stays `QUEUED` | The worker cannot reach Redis or the queue name differs. Check `kubectl logs deploy/worker -n tts` and compare `QUEUE_KEY` with `APP_ENV` in the config map. |
| `field is immutable` when applying | An object from an earlier run differs in a field Kubernetes cannot change in place, usually a Job. Delete it first: `kubectl delete job minio-bootstrap -n tts`. |
