# The TTS deployment generated from a model

Code for session 7 (Model-based Software Infrastructure) of IT30213 Advanced Software Engineering & DevOps. The application is the text-to-speech (TTS) system from sessions 4 to 6: you send a text, a worker turns it into speech, and a web page plays the MP3 file. What is new here is the folder `mde/`. It holds a model of the environments in YAML, Jinja2 templates of the Terraform and Kubernetes files, and a Python generator that combines the two. The generator writes the files of every environment into `outputs/`, and Terraform then deploys one environment to Minikube.

## How the pieces fit together

```
mde/models/environments.yaml --+
mde/models/files.yaml ---------+--> mde/transformers/generate.py --> outputs/terraform/modules/tts_stack/
mde/templates/**/*.j2 ---------+                                  --> outputs/terraform/<env>/
                                                                  --> outputs/kubernetes/base/
                                                                  --> outputs/kubernetes/overlays/<env>/

terraform apply in outputs/terraform/<env>
  --> minikube image build    session07-api:<env>, session07-web:<env>, session07-worker:<env>
  --> kubectl apply           namespace tts, ConfigMap tts-config, Secret tts-secrets
  --> kubectl apply -k        outputs/kubernetes/overlays/<env>, which holds the same ConfigMap and Secret
```

The generator works in four stages:

1. It reads the two models. `environments.yaml` holds `defaults` and a list of `environments`. `files.yaml` says which template becomes which file.
2. It renders every entry of `core_files` once, with the `defaults` as context. These are the Terraform module and the Kubernetes base, which all environments share.
3. For each environment it merges the `defaults` with the environment's own keys, adds the derived keys below, and renders every entry of `env_files` whose `when` key is true. The output path is a template as well, so `terraform/{{ name }}/{{ tfvars_filename }}` becomes `terraform/dev/dev.tfvars`.
4. If `terraform` is on your `PATH`, it runs `terraform fmt` on `outputs/terraform`, because a template cannot keep the alignment that Terraform's style asks for.

An environment needs only a `name`. The generator derives the rest from it, unless the environment sets a key itself:

| Key | Value for `name: dev` | Used for |
|---|---|---|
| `k8s_overlay` | `dev` | the overlay folder and the host name `dev.tts.local` |
| `env_value` | `dev` | `APP_ENV` of the services and the tag of the images that Terraform builds |
| `mp3_bucket` | `tts-dev` | the bucket for the MP3 files |
| `internal_token` | `changeme-dev` | the token the worker sends when it reports a job |
| `tfvars_filename` | `dev.tfvars` | the variable file of the environment |
| `k8s_root_path` | the absolute path of `outputs/kubernetes` | where Terraform finds the overlays |

The model in this folder has three environments. They differ only in the keys the model sets:

| Environment | Address | Worker pods (HPA maximum) | Extra files | MinIO login and token in Terraform |
|---|---|---|---|---|
| `dev` | http://dev.tts.local/ | 1 | none | written into `main.tf` |
| `staging` | http://staging.tts.local/ | 2 | none | written into `main.tf` |
| `prod` | http://prod.tts.local/ | 4 | `pdb-api.yaml`, `pdb-web.yaml` | `sensitive` variables, set in `prod.tfvars` |

All three deploy into the namespace `tts` with the same object names. One Minikube cluster therefore runs one environment at a time: destroy the one that runs before you apply another.

Inside Minikube the application looks like this:

```
browser --http://<env>.tts.local/api/*--> ingress-nginx --prefix /api removed--> api --job and status--> redis
browser --http://<env>.tts.local/*------> ingress-nginx ------------------------> web <--MP3 file------- minio

worker <--next job---- redis
worker --MP3 file----> minio
worker --job status--> api
```

| Object | What it does |
|---|---|
| Deployment `api` | FastAPI. Creates jobs, answers status requests and takes status updates from the worker. It keeps all of this in Redis. |
| Deployment `web` | FastAPI. Serves the page and the MP3 files, which it reads from MinIO. |
| Deployment `worker` | Takes jobs from Redis and makes the MP3 files with Piper (voice `en_GB-alan-medium`) and ffmpeg. |
| Deployment `redis` | The queue of waiting jobs and the status of every job. |
| StatefulSet `minio` | S3-compatible object storage for the MP3 files, on a 5 GiB volume. |
| Job `minio-bootstrap` | Prints one placeholder line and ends. The worker creates the bucket when it uploads its first file. |
| HorizontalPodAutoscaler `worker` | Runs between 1 and `worker_replicas` worker pods, depending on their CPU use. |
| Ingresses `tts-api` and `tts-web` | Route `<env>.tts.local/api/...` to `api`, with the `/api` prefix removed, and every other path to `web`. |
| ConfigMap `tts-config`, Secret `tts-secrets` | The settings and the passwords of the services. The overlay contains both, filled in from the model, so `kubectl apply -k` deploys a working environment by itself. The Terraform module also creates them from its variables before it applies the overlay. |

The `minio` StatefulSet runs Silo (`pgsty/silo`), a community fork of the MinIO server. The official `minio/minio` image is no longer on Docker Hub. Silo keeps MinIO's S3 API, its `MINIO_*` settings and its start command, so this guide and the course material still call it MinIO.

```
mde/models/environments.yaml        the defaults and the list of environments
mde/models/files.yaml               which template becomes which file, and under which condition (when)
mde/templates/core/                 rendered once: the Terraform module and the Kubernetes base
mde/templates/terraform/env/        main.tf and <env>.tfvars of one environment
mde/templates/kubernetes/overlay/   the Kubernetes overlay of one environment, with its ConfigMap and Secret
mde/transformers/generate.py        the generator
mde/transformers/requirements.txt   Jinja2 and PyYAML
outputs/                            what the generator writes (Git ignores it)
iac/                                hand-written files of the same kind, to compare with outputs/
app/                                the application from session 6: services, gateway and tests
compose/docker-compose.yml          the application with Docker Compose, without Kubernetes
```

## Before you start

- Docker. `docker version` should print a client and a server version. Your user has to be in the `docker` group (`sudo usermod -aG docker $USER`, then log out and in again), because Minikube's Docker driver refuses to run as root.
- Minikube. `minikube version` should print a version number. This guide was tested with Minikube 1.39.0, which runs Kubernetes 1.37.0.
- kubectl. `kubectl version --client` should print a version number. Tested with kubectl 1.30.0 and 1.37.0. Minikube warns that 1.30 is older than the cluster, which does not matter for the commands in this guide.
- Terraform 1.5 or newer. `terraform version` should print a version number. Tested with Terraform 1.17.2.
- bash. Terraform runs the deployment commands with `/bin/bash`. On Windows, work inside WSL 2.
- Python 3.12 and the virtual environment in `projects/.venv`, one folder up from this README. If you don't have it yet, create it from this folder:

  ```bash
  python3 -m venv ../.venv
  ```

  On Ubuntu, if `python3 -m venv` fails, run `sudo apt install python3-venv` first.
- About 4 CPUs, 8 GB of memory and 10 GB of disk for Minikube.
- Internet access for the first deployment. Minikube downloads its node image and Kubernetes. The cluster downloads `redis:7-alpine`, `pgsty/silo` and the ingress controller. The build of the worker image downloads ffmpeg, Piper (from GitHub) and the voice (from Hugging Face).

## Step 1. Go to the project folder

Open a terminal in the folder that contains this README:

```bash
cd projects/session-07
```

Every path in this guide starts there.

## Step 2. Install the generator's packages

```bash
../.venv/bin/pip install -r mde/transformers/requirements.txt
```

```
Successfully installed MarkupSafe-3.0.4 PyYAML-6.0.3 jinja2-3.1.6
```

## Step 3. Generate the files

```bash
../.venv/bin/python mde/transformers/generate.py
```

```
Generated artifacts in: /home/you/advanced-software-engineering/projects/session-07/outputs
```

`--output-root` writes somewhere else, for example `--output-root generated-iac`. The rest of this guide uses `outputs`. List what the generator wrote:

```bash
find outputs -type f | sort
```

```
outputs/kubernetes/base/deploy-api.yaml
outputs/kubernetes/base/deploy-redis.yaml
...
outputs/kubernetes/overlays/dev/configmap-app.yaml
outputs/kubernetes/overlays/dev/ingress-api.yaml
outputs/kubernetes/overlays/dev/ingress-web.yaml
outputs/kubernetes/overlays/dev/kustomization.yaml
outputs/kubernetes/overlays/dev/patch-hpa-worker.yaml
outputs/kubernetes/overlays/dev/patch-images.yaml
outputs/kubernetes/overlays/dev/secret-app.yaml
outputs/kubernetes/overlays/prod/...
outputs/kubernetes/overlays/staging/...
outputs/terraform/dev/dev.tfvars
outputs/terraform/dev/main.tf
outputs/terraform/modules/tts_stack/main.tf
outputs/terraform/modules/tts_stack/outputs.tf
outputs/terraform/modules/tts_stack/variables.tf
outputs/terraform/prod/main.tf
outputs/terraform/prod/prod.tfvars
outputs/terraform/staging/main.tf
outputs/terraform/staging/staging.tfvars
```

That is 47 files from 103 lines of model. The 15 files of the Kubernetes base and the 3 files of the Terraform module are the same for every environment. Each environment adds 2 Terraform files and 7 or more overlay files.

Each run first deletes what the previous run generated, so files whose template or environment is gone do not linger. It keeps the files Terraform writes into an environment folder (`.terraform/`, `.terraform.lock.hcl` and `terraform.tfstate`), so a deployed environment stays known to Terraform after you generate again.

## Step 4. Compare the environments

The model gives `prod` four workers, the PodDisruptionBudgets and sensitive variables, and the name `prod` gives it its own bucket, token and host. Look at what that one entry changed in the generated files:

```bash
diff -r outputs/kubernetes/overlays/dev outputs/kubernetes/overlays/prod
```

```
diff -r outputs/kubernetes/overlays/dev/configmap-app.yaml outputs/kubernetes/overlays/prod/configmap-app.yaml
6,7c6,7
<   APP_ENV: "dev"
<   MP3_BUCKET: "tts-dev"
---
>   APP_ENV: "prod"
>   MP3_BUCKET: "tts-prod"
diff -r outputs/kubernetes/overlays/dev/ingress-api.yaml outputs/kubernetes/overlays/prod/ingress-api.yaml
11c11
<     - host: dev.tts.local
---
>     - host: prod.tts.local
diff -r outputs/kubernetes/overlays/dev/ingress-web.yaml outputs/kubernetes/overlays/prod/ingress-web.yaml
8c8
<     - host: dev.tts.local
---
>     - host: prod.tts.local
diff -r outputs/kubernetes/overlays/dev/kustomization.yaml outputs/kubernetes/overlays/prod/kustomization.yaml
11a12,13
>   - pdb-api.yaml
>   - pdb-web.yaml
diff -r outputs/kubernetes/overlays/dev/patch-hpa-worker.yaml outputs/kubernetes/overlays/prod/patch-hpa-worker.yaml
7c7
<   maxReplicas: 1
---
>   maxReplicas: 4
diff -r outputs/kubernetes/overlays/dev/patch-images.yaml outputs/kubernetes/overlays/prod/patch-images.yaml
10c10
<           image: session07-api:dev
---
>           image: session07-api:prod
21c21
<           image: session07-web:dev
---
>           image: session07-web:prod
32c32
<           image: session07-worker:dev
---
>           image: session07-worker:prod
Only in outputs/kubernetes/overlays/prod: pdb-api.yaml
Only in outputs/kubernetes/overlays/prod: pdb-web.yaml
diff -r outputs/kubernetes/overlays/dev/secret-app.yaml outputs/kubernetes/overlays/prod/secret-app.yaml
9c9
<   INTERNAL_TOKEN: "changeme-dev"
---
>   INTERNAL_TOKEN: "changeme-prod"
```

```bash
diff outputs/terraform/dev/dev.tfvars outputs/terraform/prod/prod.tfvars
```

```
1,2c1,2
< mp3_bucket     = "tts-dev"
< internal_token = "changeme-dev"
---
> mp3_bucket     = "tts-prod"
> internal_token = "changeme-prod"
4c4,9
< worker_replicas = 1
---
> minio_root_user = "minioadmin"
>
> # MinIO's default, for this lab only. Change it in real production.
> minio_root_password = "minioadmin"
>
> worker_replicas = 4
```

`diff outputs/terraform/dev/main.tf outputs/terraform/prod/main.tf` shows the rest: in `prod` the MinIO login comes from variables, and those variables and `internal_token` are marked `sensitive`. To see the overlay exactly as Kubernetes will receive it, render it:

```bash
kubectl kustomize outputs/kubernetes/overlays/prod
```

It prints 19 objects for `prod` and 17 for the other two. The first line is a warning that `patchesStrategicMerge` is deprecated. Kustomize still applies those patches, so the warning does no harm.

## Step 5. Start Minikube

```bash
minikube start --driver=docker --cpus=4 --memory=8192
```

The first start downloads the node image and Kubernetes, which takes a few minutes. It ends with:

```
* Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default
```

Recent Minikube versions run the containers with containerd, not Docker, unless you add `--container-runtime=docker`. The Terraform module builds the images with `minikube image build`, which works with either runtime. It also turns on the ingress addon by itself, and it sends every `kubectl` command to the context of the Minikube profile, whichever context kubectl has selected.

## Step 6. Deploy the dev environment with Terraform

```bash
cd outputs/terraform/dev
terraform init
terraform plan -var-file=dev.tfvars
terraform apply -var-file=dev.tfvars
```

`init` prepares the folder. The configuration uses no provider plugins, so it downloads nothing. `plan` shows one resource to add, `module.tts.terraform_data.deploy`, and `apply` asks for confirmation. Type `yes`. Terraform then runs the commands of that resource in the project folder:

1. `minikube image build` builds `session07-api:dev`, `session07-web:dev` and `session07-worker:dev` inside the cluster, so the pods find them without a registry.
2. `kubectl` creates the namespace `tts`, the ConfigMap `tts-config` with the settings of the environment, and the Secret `tts-secrets` with the MinIO login and the token, all from the Terraform variables.
3. `kubectl apply -k` applies `outputs/kubernetes/overlays/dev`. The overlay holds the same ConfigMap and Secret, filled in from the model, so their values stay the same.
4. `kubectl patch` sets the HPA's maximum to `worker_replicas`, and `kubectl scale` starts the worker with one pod.

The first apply took 6 minutes in one test run and 16 in another, over a slower connection. Most of that time goes to the worker image, which installs ffmpeg and downloads Piper and the voice. Later applies reuse the image layers and take seconds. It ends with:

```
Apply complete! Resources: 1 added, 0 changed, 0 destroyed.

Outputs:

urls = {
  "api" = "http://dev.tts.local/api"
  "ingress_host" = "dev.tts.local"
  "web" = "http://dev.tts.local/"
}
```

Terraform keeps what it created in `terraform.tfstate` in this folder. Generating again (step 3) leaves that file alone.

## Step 7. Check the pods

The apply returns as soon as Kubernetes has accepted the objects. The cluster still has to pull `redis:7-alpine` and `pgsty/silo` the first time. Wait until everything is up:

```bash
kubectl -n tts wait --for=condition=available deployment --all --timeout=5m
kubectl -n tts rollout status statefulset/minio --timeout=5m
kubectl get pods -n tts
```

```
NAME                     READY   STATUS      RESTARTS   AGE
api-fc79ff46d-dz6pf      1/1     Running     0          82s
minio-0                  1/1     Running     0          82s
minio-bootstrap-k5vgw    0/1     Completed   0          82s
redis-5d85cc97f8-x8n9z   1/1     Running     0          82s
web-bc967878-drs2s       1/1     Running     0          82s
worker-7f8848ff7-xtvsq   1/1     Running     0          82s
```

`minio-bootstrap` is a Job, so `Completed` is its final state. Then read the startup messages of the worker:

```bash
kubectl logs -n tts deploy/worker
```

```
Worker starting...
ENV: dev
QUEUE: jobs:queue:dev
API: http://api:8000
S3: http://minio:9000 BUCKET: tts-dev
PIPER_MODEL: /models/en_GB-alan-medium.onnx
PIPER_LENGTH_SCALE: 1.0
[FATAL LOOP ERROR] Error 111 connecting to redis:6379. Connection refused.
```

The `Connection refused` lines come from the seconds before Redis was ready. The worker tries again every second and stops printing them once it gets through. After that it prints one `[DONE]` line for every job it finishes.

`kubectl get hpa -n tts` shows the worker HPA with `cpu: <unknown>/70%`. The HPA reads CPU use from the metrics server, which Minikube leaves off. `minikube addons enable metrics-server` turns it on, and after a minute the column shows a percentage.

## Step 8. Open the application

The ingress answers on the Minikube IP and chooses the environment by host name. Tell your machine where `dev.tts.local` is:

```bash
minikube ip
echo "$(minikube ip) dev.tts.local staging.tts.local prod.tts.local qa.tts.local" | sudo tee -a /etc/hosts
```

The IP is usually `192.168.49.2`. On macOS and Windows the Docker driver does not make that IP reachable from your machine. There, run `minikube tunnel` in a second terminal and put `127.0.0.1` in the hosts file instead.

| Address | What to check |
|---|---|
| http://dev.tts.local/ | The page "TTS Case Study". Type a text or keep the example and click Synthesize. The status goes from `QUEUED` to `RUNNING` to `DONE` within a few seconds, and the player plays the speech. |
| http://dev.tts.local/healthz | `{"ok":true,"env":"dev","bucket":"tts-dev"}`, the answer of the web service. |
| http://dev.tts.local/api/healthz | `{"ok":true,"env":"dev"}`, the answer of the API. |

Right after an apply, nginx may answer `404 Not Found` for a few seconds, until the ingress controller has loaded the new rules. The same job with curl:

```bash
curl -X POST http://dev.tts.local/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello world this is a test"}'
```

```
{"job_id":"b8a139dc-8f5f-48c7-97fd-0ee2b8b0907d","status":"QUEUED"}
```

Your `job_id` is different. Put it in place of `<job-id>`:

```bash
curl http://dev.tts.local/api/jobs/<job-id>
curl -I http://dev.tts.local/mp3/<job-id>.mp3
```

```
{"job_id":"b8a139dc-8f5f-48c7-97fd-0ee2b8b0907d","status":"DONE","mp3_url":"/mp3/b8a139dc-8f5f-48c7-97fd-0ee2b8b0907d.mp3","error":null}
HTTP/1.1 200 OK
Date: Thu, 19 Nov 2026 09:06:16 GMT
Content-Type: audio/mpeg
Content-Length: 14902
Connection: keep-alive
```

Without the hosts entry, curl can still reach the ingress if you tell it the IP: `curl --resolve dev.tts.local:80:$(minikube ip) http://dev.tts.local/healthz`.

## Step 9. Run the tests

The unit tests import the code of the three services and need no cluster. The integration tests send a job through the ingress, as you did by hand in step 8. From the environment folder:

```bash
cd ../../../app
../../.venv/bin/pip install -r requirements-dev.txt
../../.venv/bin/python -m pytest -q tests/unit
../../.venv/bin/python -m pytest -q tests/integration --base-url http://dev.tts.local
```

```
38 passed, 1 warning in 0.79s
..                                                                       [100%]
2 passed in 1.64s
```

The warning is a `DeprecationWarning` from Starlette's test client, a package that FastAPI installs. It does not affect the tests.

## Step 10. Change the model and deploy again

Give `dev` two workers. In `mde/models/environments.yaml`, change the entry of `dev` to:

```yaml
  - name: dev
    worker_replicas: 2
```

Generate again and ask Terraform what it would do:

```bash
cd ..
../.venv/bin/python mde/transformers/generate.py
cat outputs/kubernetes/overlays/dev/patch-hpa-worker.yaml
cd outputs/terraform/dev
terraform plan -var-file=dev.tfvars
```

```
  # module.tts.terraform_data.deploy must be replaced
...
Plan: 1 to add, 0 to change, 1 to destroy.
```

The resource remembers a hash of the model's values, of every Kubernetes file and of the code of the three services. Any change to one of them replaces the resource. A replacement first runs the destroy step, `kubectl delete -k` on the overlay, which deletes the whole namespace `tts`, and then deploys everything again. The MP3 files and the job records go with it, and the application is unreachable for the minute it takes to come back. Apply, wait as in step 7, and check the HPA:

```bash
terraform apply -var-file=dev.tfvars
kubectl get hpa -n tts
```

`MAXPODS` is now 2. The same happens when you edit a template, or a file under `app/`: generate again if you changed a template, then `terraform apply`, which rebuilds the images with the new code. Set `dev` back to the plain `- name: dev` when you are done, and generate and apply once more.

## Step 11. Add an environment

This is the scenario from the chapter: a new environment `qa` is one line in the model. Add it at the end of `environments` in `mde/models/environments.yaml`:

```yaml
  - name: qa
```

Generate again and look at what appeared:

```bash
cd ../../..
../.venv/bin/python mde/transformers/generate.py
ls outputs/terraform outputs/kubernetes/overlays
cat outputs/terraform/qa/qa.tfvars
```

```
outputs/kubernetes/overlays:
dev  prod  qa  staging

outputs/terraform:
dev  modules  prod  qa  staging
```

```
mp3_bucket     = "tts-qa"
internal_token = "changeme-qa"

worker_replicas = 1
```

`qa` uses the namespace `tts` as well, so destroy `dev` first, then deploy `qa`:

```bash
cd outputs/terraform/dev
terraform destroy -var-file=dev.tfvars
cd ../qa
terraform init
terraform apply -var-file=qa.tfvars
```

After the wait from step 7, http://qa.tts.local/healthz answers `{"ok":true,"env":"qa","bucket":"tts-qa"}`. Destroy `qa` with `terraform destroy -var-file=qa.tfvars` before you remove it from the model. Once its `main.tf` is gone, Terraform can no longer run the destroy step.

## Step 12. Run staging or production instead

Each environment folder works the same way, with its own variable file. Destroy the environment that runs, then, from the project folder:

```bash
cd outputs/terraform/staging
terraform init
terraform apply -var-file=staging.tfvars
```

It answers on http://staging.tts.local/, and `/healthz` reports `{"ok":true,"env":"staging","bucket":"tts-staging"}`. `kubectl get hpa -n tts` shows a maximum of 2 workers. For production:

```bash
cd ../prod
terraform init
terraform apply -var-file=prod.tfvars
```

`prod` marks the token and the MinIO login as sensitive. Terraform keeps sensitive values out of its output, and because the deployment commands contain them, it prints `(output suppressed due to sensitive value in config)` for every line those commands write. The apply still ends with `Apply complete!` and the URLs. http://prod.tts.local/healthz reports `{"ok":true,"env":"prod","bucket":"tts-prod"}`, and `kubectl get pdb -n tts` lists `api-pdb` and `web-pdb`.

## Step 13. Destroy and stop

In the folder of the environment that runs:

```bash
terraform destroy -var-file=dev.tfvars
```

```
Destroy complete! Resources: 1 destroyed.
```

That deletes the namespace `tts` with everything in it. The images stay in the cluster. Stop Minikube with `minikube stop`, which keeps the cluster for next time, or remove it with `minikube delete`. Remove the `tts.local` line from `/etc/hosts` when you no longer need it.

## Deploy an overlay without Terraform

The generated overlay carries its own ConfigMap and Secret, so `kubectl` can deploy it without Terraform. The images still have to be in the cluster. With Minikube running and no environment deployed, from the project folder:

```bash
minikube addons enable ingress
minikube image build -t session07-api:dev app/services/api
minikube image build -t session07-web:dev app/services/web
minikube image build -t session07-worker:dev app/services/worker
kubectl apply -k outputs/kubernetes/overlays/dev
```

Each build prints the steps of the image build. When Terraform built the same images before, every step comes from the cache and the three builds take a few seconds. `kubectl apply -k` prints one line per object:

```
# Warning: 'patchesStrategicMerge' is deprecated. Please use 'patches' instead. Run 'kustomize edit fix' to update your Kustomization automatically.
namespace/tts created
configmap/tts-config created
secret/tts-secrets created
service/api created
...
ingress.networking.k8s.io/tts-api created
ingress.networking.k8s.io/tts-web created
```

Wait for the pods as in step 7. http://dev.tts.local/healthz then answers `{"ok":true,"env":"dev","bucket":"tts-dev"}`, and a job goes through as in step 8. Remove the environment with `kubectl delete namespace tts`. If you run `terraform apply` while these objects exist, Terraform takes them over, and a later `terraform destroy` deletes them.

## The hand-written files in `iac/`

`iac/` holds files of the same kind written by hand: a Terraform module, three environments and the Kubernetes base and overlays. They show what the generator saves you from writing and keeping in sync. They also deploy:

```bash
cd iac/terraform/dev
terraform init
terraform apply -var-file=dev.tfvars
```

The differences from `outputs/` are small. `diff -ru iac/kubernetes/base outputs/kubernetes/base` prints nothing, because the generated base is the hand-written one. The module applies `iac/kubernetes/overlays/<env>` and sets the number of worker pods directly with `kubectl scale` instead of patching the HPA. The hand-written overlays do not contain the ConfigMap and the Secret, so they need the module, which creates both. Production is named `production`, so its images are tagged `production` and `/healthz` reports `"env":"production"`, while its host is `prod.tts.local` as in the generated files. The same rule applies: one environment at a time.

## Run the application with Docker Compose

`compose/docker-compose.yml` starts the same application without Kubernetes, as in session 5, with the gateway on http://localhost:8088/. It needs the ports 6379, 8000, 8080, 8088, 9000 and 9001.

```bash
cd compose
sudo docker compose up --build -d
```

The file begins with `name: session-07`, so its containers are called `session-07-api-1` and so on. The integration tests from step 9 run against it without `--base-url`. Stop it with `sudo docker compose down`, or `sudo docker compose down -v` to delete the MP3 files as well.

## Changing the settings

Change the model, not the generated files: the next run of the generator overwrites `outputs/`. A key under `defaults` applies to every environment, and a key in an environment's entry overrides it for that environment. After a change, generate (step 3), check the result with `kubectl kustomize` as in step 4, then run `terraform plan` and `terraform apply` in the environment's folder.

| Key | Value in the model | Meaning |
|---|---|---|
| `name` | `dev`, `staging`, `prod` | The only key an environment needs. The derived keys in the first section come from it. |
| `worker_replicas` | 1, `staging` 2, `prod` 4 | Maximum number of worker pods for the HPA, and the default of the Terraform variable `worker_replicas`. |
| `include_pdb` | `false`, `prod` `true` | Adds `pdb-api.yaml` and `pdb-web.yaml` to the overlay: PodDisruptionBudgets that keep at least one `api` and one `web` pod during a voluntary disruption, such as draining a node. |
| `include_replicas_patch` | `false` | Adds `patch-replicas.yaml`, which sets the number of `api` and `web` pods to `api_replicas` and `web_replicas`. |
| `api_replicas`, `web_replicas` | 1 | Used only with `include_replicas_patch`. |
| `internal_token_sensitive` | `false`, `prod` `true` | Marks the Terraform variable `internal_token` as sensitive. |
| `minio_root_user_from_var`, `minio_root_password_from_var` | `false`, `prod` `true` | Puts the MinIO login into `<env>.tfvars` and passes it through variables, instead of writing it into `main.tf`. |
| `minio_root_user_sensitive`, `minio_root_password_sensitive` | `true` | Marks those variables as sensitive. They matter only when the matching `_from_var` key is `true`. |
| `minio_root_user`, `minio_root_password` | `minioadmin` | The MinIO login. It goes into the Secret `tts-secrets`, from which MinIO, `web` and `worker` all read it, so changing it here changes it everywhere. |
| `image_registry_prefix` | `session07` | First part of the image names, as in `session07-api:dev`. |
| `api_image_tag`, `web_image_tag`, `worker_image_tag` | `"{{ env_name }}"` | Image tags in `patch-images.yaml`. Terraform tags the images it builds with `env_value`, so keep the two equal. Otherwise the pods look for an image that does not exist. |
| `minikube_profile` | `minikube` | The Minikube profile, and the kubectl context of the same name, that the module deploys to. |
| `api_base_url` | `http://api:8000` | Where the worker reports the status of a job. |
| `redis_url`, `s3_endpoint`, `s3_region` | `redis://redis:6379/0`, `http://minio:9000`, `us-east-1` | Where the services find Redis and MinIO, and the region their S3 client uses. They go into the ConfigMap `tts-config`. |
| `terraform_module_source`, `project_root_expr` | `../modules/tts_stack`, `abspath("${path.module}/../../..")` | Written into `main.tf`: where the module is, and the project folder whose `app/` Terraform builds. |

The token `changeme-<env>` and the login `minioadmin` suit a cluster on your own machine. Change them before an environment runs where other people can reach it.

## Troubleshooting

| Problem | What to do |
|---|---|
| `Missing dependency: jinja2` or `Missing dependency: PyYAML` | Do step 2 with the same `../.venv` that runs the generator. |
| `jinja2.exceptions.UndefinedError: '...' is undefined` | A template uses a key that the model does not define for that environment. Add the key under `defaults` or to the environment. The templates of `core_files` see only the `defaults`, so a key that differs per environment belongs in a template of `env_files`. |
| `Module not installed` from `terraform plan` or `apply` | Run `terraform init` in that environment folder. A new environment, or an `outputs/` folder you deleted, needs it once. |
| `minikube status` fails at the start of the apply, or `context "minikube" does not exist` | Minikube is not running, or runs under another profile. Start it as in step 5, or set `minikube_profile` in the model. |
| `The "docker" driver should not be used with root privileges` | Run `minikube` without `sudo`, as a user in the `docker` group. |
| `Could not resolve host: dev.tts.local` | The hosts entry from step 8 is missing. |
| nginx answers `404 Not Found` | The ingress was created a few seconds ago, or you used the host name of an environment that is not deployed. `kubectl get ingress -n tts` shows the host that is. |
| Pods stay in `CreateContainerConfigError` | The ConfigMap `tts-config` or the Secret `tts-secrets` is missing, for example after `kubectl apply -k` of a hand-written overlay from `iac/`, which contains neither. `kubectl get configmap,secret -n tts` shows what is there. Deploy those overlays with the Terraform module. |
| A pod shows `ErrImageNeverPull` or `ImagePullBackOff` for a `session07-` image | The image was built in another Minikube profile or with another tag. Check `minikube image ls` and the `_image_tag` keys. |
| A job stays `QUEUED` or ends `FAILED` | Read `kubectl logs -n tts deploy/worker`. `curl http://dev.tts.local/api/jobs/<job-id>` shows the reason of a failure in `error`. |
| Applying a second environment replaced the first | All environments use the namespace `tts`. Destroy one before you apply another. If two were applied, the first `terraform destroy` deletes the objects of both. |
| `(output suppressed due to sensitive value in config)` during the apply of `prod` | Expected, see step 12. |
| `port is already allocated` with Docker Compose | Another program or another session's containers hold one of the six ports. Stop them, for example with `sudo docker compose down` in the other session's folder. |
