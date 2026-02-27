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

## Apply Generated Output (example)

```bash
cd outputs/terraform/dev
terraform init
terraform apply -var-file=dev.tfvars
```

If you add a new env named `qa`, regenerate and then apply from `outputs/terraform/qa`.
