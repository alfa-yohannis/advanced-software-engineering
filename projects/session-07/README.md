# Generating the TTS infrastructure from a model

Code for session 7 (Model-Based Approach to Infrastructure Automation) of IT30213 Advanced Software Engineering & DevOps. The application is the same text-to-speech (TTS) system as sessions 4 to 6. What is new here is that its Terraform and Kubernetes files are no longer written by hand. A small model describes the environments, templates describe the shape of each artefact, and a generator renders one from the other.

## How the pieces fit together

```
mde/models/environments.yaml ─┐
mde/models/files.yaml ────────┤
                              ├─> mde/transformers/generate.py ─> outputs/terraform/...
mde/templates/*.j2 ───────────┘                                   outputs/kubernetes/...
```

| Part | What it holds |
|---|---|
| `mde/models/environments.yaml` | The environments (`dev`, `qa`, `staging`, `prod`) and the defaults they share: bucket names, tokens, replica counts, image prefixes, service addresses. |
| `mde/models/files.yaml` | Which template produces which file. `core_files` are rendered once, `env_files` once per environment, and `when:` keeps a file out of environments that do not need it. |
| `mde/templates/core/` | The parts that are the same everywhere: the Terraform module and the Kubernetes base. |
| `mde/templates/kubernetes/overlay/` | The parts that differ per environment: the overlay, its config map and secret, the image and replica patches, the ingresses. |
| `mde/transformers/generate.py` | Reads both models, renders every record with Jinja2, writes `outputs/`, and runs `terraform fmt` on the result. |
| `outputs/` | The generated artefacts. Everything here is overwritten on each run, so never edit it by hand. |
| `iac/` | The hand-written version of the same artefacts, kept for comparison. |

The generated Terraform is not a set of Docker resources like session 5. It is a `terraform_data` resource whose provisioner builds the images inside Minikube's own Docker daemon, creates the config map and secret from the model values, and applies the matching Kustomize overlay. Terraform here orchestrates the cluster rather than talking to an API of its own.

```
app/                        the same application as sessions 4 to 6
compose/docker-compose.yml  the stack on Docker Compose, for comparison
mde/                        models, templates and the generator
outputs/                    generated Terraform and Kubernetes artefacts
iac/                        the hand-written artefacts the generator replaces
```

## Before you start

- Python 3.12 and the virtual environment in `projects/.venv`.
- Docker, Minikube and kubectl, as in session 6. This guide was tested with Minikube 1.38.0 and kubectl 1.35.0.
- Terraform 1.5 or newer.
- Free ports 6379, 8000, 8080, 8088, 9000 and 9001 for the Compose step.
- Around 6 GB of memory and 4 CPUs free for the cluster.

The Docker commands below use `sudo docker`. If your user is in the `docker` group, leave out `sudo`.

## Step 1. Go to the project folder

```bash
cd projects/session-07
```

## Step 2. Install what the generator needs

```bash
../.venv/bin/pip install -r mde/transformers/requirements.txt
```

It needs Jinja2 and PyYAML, nothing else.

## Step 3. Read the model before running anything

Open `mde/models/environments.yaml`. The `defaults` block holds every value shared by all environments, and each entry under `environments` only states what differs:

```yaml
environments:
  - name: dev

  - name: qa
    worker_replicas: 10
```

Most fields are derived from the name, so a new environment needs one line. `dev` becomes the bucket `tts-dev`, the token `changeme-dev`, the overlay `kubernetes/overlays/dev` and the variable file `dev.tfvars`.

## Step 4. Generate the artefacts

```bash
../.venv/bin/python mde/transformers/generate.py
```

```
Generated artifacts in: /…/projects/session-07/outputs
```

The run is idempotent: `outputs/terraform` and `outputs/kubernetes` are removed and rebuilt each time. Look at what came out:

```bash
find outputs -type f | sort
```

Four environments produce 56 files: the Terraform module in three files, the Kubernetes base in fifteen, nine per environment, and two PodDisruptionBudgets that only prod asks for. Check that the result is valid before trusting it:

```bash
kubectl kustomize outputs/kubernetes/overlays/dev | grep -c '^kind:'
cd outputs/terraform/dev && terraform init && terraform validate && cd ../../..
```

```
17
Success! The configuration is valid.
```

## Step 5. Compare the generated files with the hand-written ones

```bash
diff -ru iac/kubernetes/base outputs/kubernetes/base | head -40
```

This is the point of the session. The hand-written tree in `iac/` and the generated tree in `outputs/` describe the same system, but only one of them has to be edited when an environment changes.

## Step 6. Add an environment and regenerate

Add two lines to `mde/models/environments.yaml`:

```yaml
  - name: alice
    worker_replicas: 2
```

Then run the generator again and count what appeared:

```bash
../.venv/bin/python mde/transformers/generate.py
ls outputs/kubernetes/overlays/
ls outputs/terraform/
```

Those two lines produce nine files: a Terraform folder with its `main.tf` and `alice.tfvars`, and a Kustomize overlay with its kustomization, config map, secret, image patch, autoscaler patch and two ingresses. Remove the lines and regenerate to put the model back.

## Step 7. Deploy the generated artefacts

Start the cluster as in session 6:

```bash
minikube start --driver=docker --cpus=4 --memory=6g
minikube addons enable ingress
minikube addons enable metrics-server
```

Build the images with the names the model expects and load them:

```bash
sudo docker build -t session07-api:dev app/services/api
sudo docker build -t session07-web:dev app/services/web
sudo docker build -t session07-worker:dev app/services/worker

minikube image load session07-api:dev
minikube image load session07-web:dev
minikube image load session07-worker:dev
```

Then apply the generated overlay:

```bash
kubectl apply -k outputs/kubernetes/overlays/dev
kubectl wait --for=condition=available --timeout=300s \
  deploy/api deploy/web deploy/worker deploy/redis -n tts
```

The generated overlay carries its own config map and secret, so it works on its own:

```bash
IP=$(minikube ip)
curl -H 'Host: dev.tts.local' http://$IP/healthz
curl -H 'Host: dev.tts.local' http://$IP/api/healthz
curl -H 'Host: dev.tts.local' -X POST http://$IP/api/jobs \
  -H "Content-Type: application/json" -d '{"text":"Hello from the generator"}'
```

## Step 8. Let Terraform do the whole deployment

The Terraform in `iac/terraform` and the one in `outputs/terraform` both drive the cluster through the same steps: check Minikube, enable the ingress addon, build the three images inside the cluster's Docker daemon, create the config map and secret from the variables, apply the overlay, and scale the worker.

```bash
cd iac/terraform/dev
terraform init
terraform apply -var-file=dev.tfvars
```

Because it builds inside the cluster, no `minikube image load` is needed on this path. The first run takes several minutes while the worker image is built. When it finishes, the application answers on the same addresses as step 7.

```bash
terraform destroy -var-file=dev.tfvars
kubectl delete namespace tts
```

A deployment that was applied with `kubectl apply -k` first can make this run fail with `field is immutable`, because a Job cannot be changed in place. Delete the namespace before switching between the two paths.

## Step 9. The stack on Compose, for comparison

Step 8 left you in `iac/terraform/dev`, so the path back up is part of the command:

```bash
cd ../../../compose
sudo docker compose up --build -d
curl http://localhost:8088/api/healthz
sudo docker compose down
```

The tests are the same as in the earlier sessions:

```bash
cd ../app
../../.venv/bin/pip install -r requirements-dev.txt
../../.venv/bin/python -m pytest -q tests/unit tests/integration
```

```
........................................                                 [100%]
40 passed in 2.77s
```

## Step 10. Clean up

```bash
kubectl delete namespace tts
minikube stop
```

## Changing the settings

| What to change | Where |
|---|---|
| A value for every environment, such as the Redis address or the image prefix | `mde/models/environments.yaml`, under `defaults` |
| A value for one environment | the entry for that environment in the same file |
| Which files are produced | `mde/models/files.yaml` |
| The shape of an artefact | the matching template under `mde/templates/` |
| Anything in `outputs/` | nothing: edit the model or the template and regenerate |

After every change, run the generator again and check the result with `kubectl kustomize` and `terraform validate` before applying it.

The model keeps `minioadmin` and `changeme-<env>` as credentials. Change them before anything leaves your machine.

## Troubleshooting

| Problem | What to do |
|---|---|
| `Missing dependency: jinja2` or `PyYAML` | Run the install command from step 2 with the same interpreter you use to run the generator. |
| `'something' is undefined` while generating | A template uses a field the model does not define. Add it under `defaults` in `environments.yaml`, or give it a default in the template. Core templates see only `defaults`, so environment-specific fields belong to `env_files`. |
| The generated overlay has no config map | The entry is missing from `env_files` in `files.yaml`, or the overlay template does not list it under `resources`. |
| `kubectl apply -k` leaves pods in `CreateContainerConfigError` | The config map or secret is missing in the namespace. Check `kubectl get configmap,secret -n tts`. |
| `field is immutable` during `terraform apply` | The namespace still holds objects from an earlier `kubectl apply`. Run `kubectl delete namespace tts` and apply again. |
| Terraform reports `minikube status` failed | The cluster is not running. Start it with `minikube start` before `terraform apply`. |
| Pods stay `ContainerCreating` with `Pulling image "session07-..."` | The images are not in the cluster. Either load them as in step 7 or use the Terraform path, which builds them inside the cluster. |
| `port is already allocated` in step 9 | Another session's containers hold the ports. Stop them first. |
