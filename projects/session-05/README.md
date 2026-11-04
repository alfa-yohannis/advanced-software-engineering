# The TTS stack as Infrastructure as Code

Code for session 5 (Infrastructure as Code) of IT30213 Advanced Software Engineering & DevOps. The application is the text-to-speech (TTS) system from session 4: you send a text, a worker turns it into speech, and a web page plays the MP3 file. What is new here is how the system is started. Docker Compose builds one stack from one file, and Terraform builds the same stack from a module that three environments reuse with different ports, different numbers of workers and different settings.

## How the pieces fit together

```
browser --HTTP--> gateway --/api/*--------> api --job and status--> redis
                  gateway --/ and /mp3/*--> web <--MP3 file-------- minio

worker <--next job---- redis
worker --MP3 file----> minio
worker --job status--> api
```

A text becomes an MP3 file in three stages:

1. The client sends `POST /api/jobs` with the text. The API stores the job in Redis with the status `QUEUED`, adds it to the list `jobs:queue:<env>` and answers at once with a `job_id`.
2. A worker takes the next job from that list and reports it to the API as `RUNNING`. Piper turns the text into a WAV file, ffmpeg turns the WAV file into an MP3 file, and the worker uploads it to the bucket in MinIO as `<job_id>.mp3`. Then it reports the job as `DONE`, or `FAILED` with the error message.
3. The client asks `GET /api/jobs/<job_id>` until the status is `DONE`, and then gets `/mp3/<job_id>.mp3` from the web service, which reads the file from MinIO.

| Service | What it does | Ports in the dev environment |
|---|---|---|
| `gateway` | nginx reverse proxy. It sends `/api/*` to `api` with the `/api` prefix removed, and every other path to `web`. | 8088 |
| `api` | FastAPI. Creates jobs, answers status requests and takes status updates from the worker. It keeps all of this in Redis. | 8000 |
| `web` | FastAPI. Serves the page and the MP3 files, which it reads from MinIO. | 8080 |
| `worker` | Takes jobs from Redis and makes the MP3 files with Piper (voice `en_GB-alan-medium`) and ffmpeg. | none |
| `redis` | The queue of waiting jobs (a list) and the status of every job (one hash per job). | 6379 |
| `minio` | S3-compatible object storage for the MP3 files. | 9000 (S3 API), 9001 (console) |

The two ways of starting the stack name their containers differently, which lets you tell them apart in `docker ps`:

| Started by | Containers | Images | Network | Volume |
|---|---|---|---|---|
| Docker Compose | `session-05-api-1`, `session-05-worker-1`, … | `session-05-api:latest`, … | `session-05_default` | `session-05_minio_data` |
| Terraform | `tts-dev-api`, `tts-dev-worker-0`, … | `session05-api:dev`, … | `tts-dev` | `minio_data_dev` |

Inside Docker the containers reach each other by name: the worker uses `redis://redis:6379/0`, `http://api:8000` and `http://minio:9000`. Terraform keeps those names working with network aliases, so the same application code runs under both tools. From your own browser you use `localhost` with the ports in the table.

The `minio` service runs Silo (`pgsty/silo`), a community fork of the MinIO server. The official `minio/minio` image is no longer on Docker Hub. Silo keeps MinIO's S3 API, its `MINIO_*` settings and its start command, so this guide and the course material still call the service MinIO.

```
app/services/api/        api.py, requirements.txt and the Dockerfile
app/services/web/        web.py, static/index.html, requirements.txt and the Dockerfile
app/services/worker/     worker.py, requirements.txt and the Dockerfile, which downloads Piper and the voice
app/gateway/nginx.conf   the two routing rules of the gateway
app/models/              the voices on the host, for the environments that mount them
app/tests/unit/          tests of the three services that need no container
app/tests/integration/   test_e2e.py, which sends a job through the running stack
app/pytest.ini           pytest settings
app/requirements-dev.txt the Python packages the tests need
compose/docker-compose.yml   the six containers in one environment
iac/terraform/modules/tts_stack/  the module that builds the whole stack: main.tf, variables.tf, outputs.tf
iac/terraform/dev/       dev environment: main.tf and dev.tfvars
iac/terraform/staging/   staging environment: main.tf and staging.tfvars
iac/terraform/prod/      production environment: main.tf and production.tfvars
```

The three environments call the same module with different values, so they can all run at the same time:

| Environment | Gateway | Web | API | MinIO API / console | Redis | Workers | Voice from | Bucket |
|---|---|---|---|---|---|---|---|---|
| `dev` | 8088 | 8080 | 8000 | 9000 / 9001 | 6379 | 1 | the image | `tts-dev` |
| `staging` | 18088 | 18080 | 18000 | 19000 / 19001 | 16379 | 2 | `app/models` on the host | `tts-staging` |
| `production` | 28088 | 28080 | 28000 | 29000 / 29001 | 26379 | 4 | the image | `tts-prod` |

Compose and the dev environment use the same ports, so run one or the other, not both.

## Before you start

- Docker with the Compose plugin. `docker compose version` should print a version number.
- Terraform 1.5 or newer for the steps from step 8 on. `terraform version` should print a version number. This guide was tested with Terraform 1.17.1 and the provider `kreuzwerker/docker` 3.x.
- Your user has to be in the `docker` group for the Terraform part: `sudo usermod -aG docker $USER`, then log out and in again. Terraform's Docker provider talks to `/var/run/docker.sock` by itself, and there is no way to put `sudo` in front of that.
- Free ports. The dev environment and Compose need 6379, 8000, 8080, 8088, 9000 and 9001. Port 8080 is a common one: an IPFS node, a Tomcat, or another session's containers may hold it. Stop whatever uses it, or use the staging environment, whose ports are 1xxxx.
- Internet access for the first build. Docker downloads `python:3.12.3-slim`, `redis:7-alpine`, `nginx:1.27-alpine` and `pgsty/silo`. The build of the worker image also downloads ffmpeg, Piper (from GitHub) and the voice (from Hugging Face), which takes a few minutes.
- For the tests: Python 3.12 and the virtual environment in `projects/.venv`, two folders up from `app`. If you don't have it yet, create it from the `projects` folder:

  ```bash
  python3 -m venv .venv
  ```

  On Ubuntu, if `python3 -m venv` fails, run `sudo apt install python3-venv` first.
- The staging environment mounts `app/models` into the worker, so the two voice files `en_GB-alan-medium.onnx` and `en_GB-alan-medium.onnx.json` have to be there. They are in the repository, and Terraform stops with a clear message if they are missing.

The Docker commands below use `sudo docker`. If your user is in the `docker` group, or you use Docker Desktop, leave out `sudo`.

## Step 1. Go to the project folder

Open a terminal in the folder that contains this README:

```bash
cd projects/session-05
```

Every path in this guide starts there.

## Step 2. Start the stack with Docker Compose

```bash
cd compose
sudo docker compose up --build -d
```

The first build takes a few minutes, most of it for the worker image. Later builds reuse those layers and take seconds. `-d` runs the containers in the background and gives your terminal back.

The file begins with `name: session-05`, so Compose calls the project `session-05` instead of `compose`, the name of the folder. Sessions 6 and 7 have a folder with the same name, and without that line all three would fight over one project name.

## Step 3. Check the containers

```bash
sudo docker compose ps
```

You should see six containers, `session-05-api-1`, `session-05-gateway-1`, `session-05-minio-1`, `session-05-redis-1`, `session-05-web-1` and `session-05-worker-1`, each with a STATUS that starts with `Up`. The API has a health check that asks `/healthz` every 10 seconds, so its status also shows `(health: starting)` and, after the first check, `(healthy)`.

Then read the startup messages of the worker:

```bash
sudo docker compose logs worker
```

```
worker-1  | Worker starting...
worker-1  | ENV: dev
worker-1  | QUEUE: jobs:queue:dev
worker-1  | API: http://api:8000
worker-1  | S3: http://minio:9000 BUCKET: tts-dev
worker-1  | PIPER_MODEL: /models/en_GB-alan-medium.onnx
worker-1  | PIPER_LENGTH_SCALE: 1.0
```

After that the worker prints one `[DONE]` line for every job it finishes.

## Step 4. Open each address and check it

| Address | What to check |
|---|---|
| http://localhost:8088/ | The page "TTS Case Study". Type a text or keep the example and click Synthesize. The status goes from `QUEUED` to `RUNNING` to `DONE` within a few seconds, and the player below plays the speech. |
| http://localhost:8088/healthz | `{"ok":true,"env":"dev","bucket":"tts-dev"}`, the answer of the web service. |
| http://localhost:8088/api/healthz | `{"ok":true,"env":"dev"}`, the answer of the API. |
| http://localhost:8000/docs | The API on its own port, without the gateway. The paths have no `/api` prefix there, for example `/jobs`. |
| http://localhost:8080/healthz | The web service on its own port. The page at http://localhost:8080/ loads too, but its Synthesize button fails, because port 8080 has no `/api`. |
| http://localhost:9001 | The console of the object storage, titled SILO. Log in as `minioadmin` with password `minioadmin`. Object Browser lists the bucket `tts-dev` with one `.mp3` file per finished job. The worker creates the bucket when it uploads its first file. |

The same job with curl:

```bash
curl -X POST http://localhost:8088/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello world this is a test"}'
```

```
{"job_id":"780f4125-6da2-4650-98a7-0bb2e25a94d9","status":"QUEUED"}
```

Your `job_id` is different. Put it in place of `<job-id>`:

```bash
curl http://localhost:8088/api/jobs/<job-id>
curl -o output.mp3 http://localhost:8088/mp3/<job-id>.mp3
```

```
{"job_id":"780f4125-6da2-4650-98a7-0bb2e25a94d9","status":"DONE","mp3_url":"/mp3/780f4125-6da2-4650-98a7-0bb2e25a94d9.mp3","error":null}
```

A short sentence is `DONE` about a second after you send it, and its MP3 file is around 20 KB. The API has a `/mp3/<job-id>.mp3` route of its own, but it only answers 302 and points at the web service, which serves the bytes.

The queue and the job records in Redis:

```bash
sudo docker compose exec redis redis-cli LLEN jobs:queue:dev
sudo docker compose exec redis redis-cli HGETALL job:<job-id>
```

## Step 5. Run the unit tests

The unit tests import the code of the three services and replace Redis, MinIO, Piper and ffmpeg with stand-ins, so they need no container. Install the packages once, then run the tests from the `app` folder:

```bash
cd ../app
../../.venv/bin/pip install -r requirements-dev.txt
../../.venv/bin/python -m pytest -q tests/unit
```

```
......................................                                   [100%]
38 passed in 1.18s
```

## Step 6. Run the integration tests

The integration tests need a running stack. They do what you did by hand in step 4:

```bash
../../.venv/bin/python -m pytest -q tests/integration
```

```
..                                                                       [100%]
2 passed in 1.66s
```

`test_e2e_compose_pipeline` checks both `/healthz` addresses, creates a job, waits up to 180 seconds for `DONE`, downloads the MP3 file and checks its type and size. `test_e2e_invalid_job_rejected` sends an empty text and expects the API to refuse it.

The tests reach the stack through the gateway at http://localhost:8088 unless you say otherwise. For another environment, add `--base-url`, for example `--base-url http://localhost:18088` for staging. To test the API and the web service on their own ports, use `--api-url http://localhost:8000 --web-url http://localhost:8080`.

## Step 7. Stop the Compose stack

```bash
cd ../compose
sudo docker compose down
```

The MP3 files stay in the volume `session-05_minio_data`. `sudo docker compose down -v` deletes them as well. Stop this stack before you go on, because the dev environment of step 8 uses the same ports.

## Step 8. Start the same stack with Terraform

The Terraform module in `iac/terraform/modules/tts_stack` builds every piece the Compose file builds: the network, the volume, the three images, and the six containers. The three environment folders only choose values. Start with `dev`:

```bash
cd ../iac/terraform/dev
terraform init
terraform plan -var-file=dev.tfvars
terraform apply -var-file=dev.tfvars
```

`init` downloads the Docker provider into `.terraform/` and writes `.terraform.lock.hcl`. `plan` prints what Terraform would do without doing it, and `apply` asks for confirmation and then does it. Type `yes`. The first apply builds the three images under their own names (`session05-api:dev` and so on), so it takes as long as the first Compose build.

```
Apply complete! Resources: 11 added, 0 changed, 0 destroyed.

Outputs:

urls = {
  "api" = "http://localhost:8000"
  "gateway" = "http://localhost:8088"
  "minio" = "http://localhost:9001"
  "web" = "http://localhost:8080"
}
```

`terraform output urls` prints that map again at any time. The addresses are the ones from step 4, so check the page and send a job the same way. The containers are now named `tts-dev-api`, `tts-dev-gateway`, `tts-dev-minio`, `tts-dev-redis`, `tts-dev-web` and `tts-dev-worker-0`:

```bash
sudo docker ps --format '{{.Names}}\t{{.Status}}'
```

Run the integration tests against this stack as well, from the `app` folder:

```bash
cd ../../../app
../../.venv/bin/python -m pytest -q tests/integration
```

Terraform wrote what it created into `terraform.tfstate` in the environment folder. That file is how `plan` knows what already exists, which is why it must not be deleted by hand. It is in `.gitignore`, so every machine builds its own.

## Step 9. Run another environment

Each environment folder works the same way, with its own variable file. Staging runs two workers and mounts the voices from `app/models` instead of using the ones in the image:

```bash
cd ../iac/terraform/staging
terraform init
terraform apply -var-file=staging.tfvars
```

```
Apply complete! Resources: 12 added, 0 changed, 0 destroyed.
```

It answers on http://localhost:18088/. Send three jobs, then look at how they were spread:

```bash
sudo docker logs tts-staging-worker-0 | grep DONE
sudo docker logs tts-staging-worker-1 | grep DONE
```

Both workers read the same Redis list, so each job goes to whichever is free. Production is the same step with four workers and ports that start with 2:

```bash
cd ../prod
terraform init
terraform apply -var-file=production.tfvars
```

```
Apply complete! Resources: 14 added, 0 changed, 0 destroyed.
```

It answers on http://localhost:28088/, and `/healthz` reports `{"ok":true,"env":"production","bucket":"tts-prod"}`. Dev, staging and production use different ports, container names, networks and volumes, so all three can run next to each other. The application code and the module are the same in all three. Only the values differ, which is the point of this session.

## Step 10. Destroy

In each environment folder you used:

```bash
terraform destroy -var-file=dev.tfvars
```

```
Destroy complete! Resources: 11 destroyed.
```

That removes the containers, the network and the volume of that environment, and leaves the images. `sudo docker ps` should no longer list any `tts-` container.

## Compose and Terraform side by side

| | Docker Compose | Terraform |
|---|---|---|
| What you describe | the services of one stack | the desired state of the same stack, as a module with variables |
| Another environment | copy the file and edit it | one folder with a few values, reusing the module |
| What it remembers | nothing; it reads the running containers | `terraform.tfstate`, which it compares against the real containers |
| Before changing | `up` and see | `plan` shows every create, change and destroy first |
| In this project | `compose/docker-compose.yml` | `iac/terraform/` |

## Changing the settings

Compose keeps its settings as environment variables in `compose/docker-compose.yml`. Terraform takes them from the variable file of the environment and passes them to the module. These are the ones worth trying:

| Variable | Where | Value in dev | Meaning |
|---|---|---|---|
| `worker_replicas` | `*.tfvars` | 1 | Number of worker containers. Terraform names them `tts-<env>-worker-0`, `-1`, and so on. |
| `mount_models` | `*.tfvars` | `false` | `true` mounts `app/models` into the worker as `/models`, so you can swap the voice without rebuilding the image. The repository also has `id_ID-news_tts-medium.onnx` for Indonesian. |
| `mp3_bucket` | `*.tfvars` | `tts-dev` | Bucket for the MP3 files. The worker creates it on the first upload. |
| `internal_token` | `*.tfvars` | `changeme-dev` | The worker sends it in the header `X-Internal-Token` when it reports a job. The API answers 401 if the two differ. |
| `ports` | `<env>/main.tf` | the table above | The host ports of the six containers. |
| `piper_length_scale` | `<env>/main.tf` | `"1.0"` | Speaking rate. A higher value is slower. |
| `piper_model_path`, `piper_model_config_path` | `<env>/main.tf` | `/models/en_GB-alan-medium.onnx` | The voice the worker uses inside the container. |
| `minio_root_user`, `minio_root_password` | `prod/production.tfvars` | `minioadmin` | The MinIO login. In `dev` and `staging` these sit in `main.tf`; `prod` marks them as `sensitive` so Terraform does not print them. |

After an edit, run `terraform plan -var-file=...` first. It tells you whether Terraform will change a container in place or destroy and recreate it, which is the habit this session is about. Then `terraform apply -var-file=...`.

The token `changeme-dev`, the login `minioadmin` and the published ports of Redis and MinIO suit a stack on your own machine. Change them before the stack runs on a machine that other people can reach.

## Troubleshooting

| Problem | What to do |
|---|---|
| `port is already allocated` or `address already in use` | Another program or another session's containers hold one of the ports. Port 8080 is a frequent one. Run `sudo docker ps` and `ss -ltn` to find out what has it, stop that, and try again. Compose and the dev environment cannot run at the same time. |
| Terraform: `permission denied while trying to connect to the Docker daemon socket` | Your user is not in the `docker` group. Add it as described in "Before you start" and log out and in again. |
| Terraform: `mount_models is true, but ... has no en_GB-alan-medium.onnx` | The voice files are missing from `app/models`. Without them Docker would create an empty folder and hide the voice inside the image, so the module refuses to start the worker. Restore the files, or set `mount_models = false`. |
| `terraform plan` wants to create things that already run | The state file of that environment is gone or belongs to another machine. Run `terraform destroy -var-file=...` if the containers are still around, or remove them with `sudo docker rm -f`, then apply again. |
| The page shows `ERROR` and the message "Create job failed" | The page was opened on the web port (8080, 18080, 28080) instead of the gateway port. Use 8088, 18088 or 28088. If it happens there, check `sudo docker ps` and the logs of the API. |
| A job stays `QUEUED` | No worker is running, or the worker reads another queue. Check `sudo docker logs tts-dev-worker-0`: the `QUEUE:` line has to match the `APP_ENV` of the API. |
| A job ends `FAILED` | `curl http://localhost:8088/api/jobs/<job-id>` shows the reason in `error`. For anything else read the worker log. |
| `502 Bad Gateway` from the gateway | The `api` or `web` container is not up yet. It passes within a second or two after a start. The nginx config looks the names up again every second, so a recreated container is found without restarting the gateway. |
| The build of the worker image stops at a `curl` command | The download of Piper or of the voice failed. Check your internet connection and build again. |
| `ModuleNotFoundError` in the tests | The packages are not in the environment that runs pytest. Run the `pip install` command from step 5 with the same `../../.venv`. |
| `ConnectionError` or `Connection refused` in the integration tests | No stack is running on that address. Start one (step 2 or step 8), wait until `/healthz` answers, and run the tests again. |
