# MDE Generator

This folder implements a small Model-Driven Engineering flow for infrastructure environments.

## Directory Layout

- `models/`: environment model definitions
- `transformers/`: generation scripts
- `templates/`: Jinja templates used by transformers
- `../outputs/`: generated Terraform + Kubernetes artifacts

## Model File

Edit `models/environments.yaml` and add a new environment object under `environments`.
Edit `models/files.yaml` to control which template files are rendered and where.

Minimum fields for a new environment:

- `name`

Derived automatically from `name` (unless overridden):

- `k8s_overlay`
- `env_value`
- `mp3_bucket` (`tts-<name>`)
- `internal_token` (`changeme-<name>`)
- `tfvars_filename` (`<name>.tfvars`)

## Generate

Install template dependency (in your active `~/venv` or any virtualenv):

```bash
pip install -r mde/transformers/requirements.txt
```

Run from repository root:

```bash
python3 mde/transformers/generate.py
```

Generate to a different target directory:

```bash
python3 mde/transformers/generate.py --output-root generated-iac
```

This generates:

- `outputs/terraform/modules/*` (generated from templates)
- `outputs/terraform/<env>/*` (generated env Terraform)
- `outputs/kubernetes/base/*` (generated from templates)
- `outputs/kubernetes/overlays/<overlay>/*` (generated overlay files)

## Runtime Prerequisites

Generated Terraform applies workloads to Kubernetes via `kubectl apply -k` and expects Minikube.

- `minikube`
- `kubectl`
- `docker`
- `terraform`

Quick check:

```bash
minikube status -p minikube
kubectl cluster-info
```

## Restart / Refresh Kubernetes (Minikube)

If your cluster is in a bad state or you want a clean restart:

```bash
minikube stop -p minikube
minikube start -p minikube
minikube addons enable ingress -p minikube
kubectl cluster-info
```

If you want a full reset (delete cluster and recreate):

```bash
minikube delete -p minikube
minikube start -p minikube
minikube addons enable ingress -p minikube
```

## Apply Generated Output (example)

```bash
cd outputs/terraform/dev
terraform init
terraform apply -var-file=dev.tfvars
```

Notes:

- This applies Kubernetes manifests from `outputs/kubernetes/...`.
- Ensure Minikube is running before `terraform apply`.

## End-to-End Steps (Generated Artifacts)

From repository root:

1. Install generator dependencies
```bash
pip install -r mde/transformers/requirements.txt
```
2. Generate artifacts
```bash
python3 mde/transformers/generate.py --output-root outputs
```
3. Start/ensure Minikube
```bash
minikube start -p minikube
minikube addons enable ingress -p minikube
```
4. Deploy one environment (example: `dev`)
```bash
cd outputs/terraform/dev
terraform init
terraform apply -var-file=dev.tfvars
```
5. Verify Kubernetes resources
```bash
kubectl get pods -n tts
kubectl get svc -n tts
kubectl get ingress -n tts
kubectl get hpa -n tts
```
6. Re-apply after model/template changes
```bash
cd /data2/projects/advanced-software-engineering/projects/session-07
python3 mde/transformers/generate.py --output-root outputs
cd outputs/terraform/dev
terraform apply -var-file=dev.tfvars -replace=module.tts.terraform_data.deploy
```

If you add a new env named `qa`, regenerate and then apply from `outputs/terraform/qa`.

## Example: Add a New Environment

Edit `mde/models/environments.yaml` and add a new entry under `environments`.

Minimal example (`qa`):

```yaml
environments:
  - name: qa
```

Extended example (`qa` with overrides):

```yaml
environments:
  - name: qa
    worker_replicas: 3
    include_pdb: true
    internal_token_sensitive: true
```

Then generate and deploy:

```bash
cd /data2/projects/advanced-software-engineering/projects/session-07
python3 mde/transformers/generate.py --output-root outputs

cd outputs/terraform/qa
terraform init
terraform apply -var-file=qa.tfvars
```

What gets auto-derived from `name: qa` (unless overridden):

- `k8s_overlay: qa`
- `env_value: qa`
- `mp3_bucket: tts-qa`
- `internal_token: changeme-qa`
- `tfvars_filename: qa.tfvars`
