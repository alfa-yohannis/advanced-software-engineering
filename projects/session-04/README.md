# Text-to-speech stack with CI/CD

Code for session 4 (Continuous Integration and Deployment) of IT30213 Advanced Software Engineering & DevOps. It is a small text-to-speech (TTS) system: you send a text, a worker turns it into speech, and a web page plays the MP3 file. Docker Compose builds and starts its six containers with one command. Two GitHub Actions workflows test this folder on every push and, when the tests pass, start the stack on a self-hosted runner.

## How the pieces fit together

```
browser --HTTP--> gateway --/api/*--------> api --job and status--> redis
                  gateway --/ and /mp3/*--> web <--MP3 file-------- minio

worker <--next job---- redis
worker --MP3 file----> minio
worker --job status--> api
```

A text becomes an MP3 file in three stages:

1. The client sends `POST /api/jobs` with the text. The API stores the job in Redis with the status `QUEUED`, adds it to the list `jobs:queue:dev` and answers at once with a `job_id`. It does not wait for the speech.
2. The worker takes the next job from that list and reports it to the API as `RUNNING`. Piper turns the text into a WAV file, ffmpeg turns the WAV file into an MP3 file, and the worker uploads it to the bucket `tts-dev` in MinIO as `<job_id>.mp3`. Then it reports the job as `DONE`. If a step fails, it reports `FAILED` with the error message.
3. The client asks `GET /api/jobs/<job_id>` until the status is `DONE`, and then gets `/mp3/<job_id>.mp3` from the web service, which reads the file from MinIO.

| Service | What it does | Ports on your machine |
|---|---|---|
| `gateway` | nginx reverse proxy. It sends `/api/*` to `api` with the `/api` prefix removed, and every other path to `web`. The page and the API then share one address. | 8088 |
| `api` | FastAPI. Creates jobs, answers status requests and takes status updates from the worker. It keeps all of this in Redis. | 8000 |
| `web` | FastAPI. Serves the page and the MP3 files, which it reads from MinIO. | 8080 |
| `worker` | Takes jobs from Redis and makes the MP3 files with Piper (voice `en_GB-alan-medium`) and ffmpeg. | none |
| `redis` | The queue of waiting jobs (a list) and the status of every job (one hash per job). | 6379 |
| `minio` | S3-compatible object storage for the MP3 files. | 9000 (S3 API), 9001 (console) |

Compose names the containers `session-04-<service>-1`, for example `session-04-api-1`. Inside Docker the containers reach each other by service name: the worker uses `redis://redis:6379/0`, `http://api:8000` and `http://minio:9000`. From your own browser you use `localhost` with the ports in the table.

The `minio` service runs Silo (`pgsty/silo`), a community fork of the MinIO server. The official `minio/minio` image is no longer on Docker Hub. Silo keeps MinIO's S3 API, its `MINIO_*` settings and its start command, so this guide and the course material still call the service MinIO.

```
docker-compose.yml     the six containers and their settings
gateway/nginx.conf     the two routing rules of the gateway
services/api/          api.py, requirements.txt and the Dockerfile
services/web/          web.py, static/index.html, requirements.txt and the Dockerfile
services/worker/       worker.py, requirements.txt and the Dockerfile, which downloads Piper and the voice
tests/unit/            tests of the three services that need no container
tests/integration/     test_e2e.py, which sends a job through the running stack
tests/conftest.py      the --base-url, --api-url and --web-url options of the integration tests
pytest.ini             pytest settings
requirements-dev.txt   the Python packages the tests need
client/, scripts/      empty placeholder files that this session does not use
```

The two workflows are in `.github/workflows/` at the root of the repository, because GitHub reads workflows only from there:

```
.github/workflows/tests.yml         CI Tests (session-04): the unit tests, then the integration tests
.github/workflows/deploy-local.yml  deploy-local: starts the stack on a self-hosted runner after the CI tests pass
```

## Before you start

- Docker with the Compose plugin. `docker compose version` should print a version number. On Windows or macOS, install Docker Desktop.
- Internet access for the first build. Docker downloads `python:3.12.3-slim`, `redis:7-alpine`, `nginx:1.27-alpine` and `pgsty/silo`. The build of the worker image also downloads ffmpeg, Piper (from GitHub) and the voice (from Hugging Face).
- Free ports: 6379, 8000, 8080, 8088, 9000 and 9001. Sessions 1 to 3 use port 8000 as well, so stop them first with `sudo docker compose down` in their folders.
- For the tests in steps 5 and 6: Python 3.12 and the virtual environment in `projects/.venv`, one folder up from this README. If you don't have it yet, create it from this folder:

  ```bash
  python3 -m venv ../.venv
  ```

  On Ubuntu, if `python3 -m venv` fails, run `sudo apt install python3-venv` first.
- For steps 7 and 8: a GitHub repository that you can push to, such as your own fork of this one, and the GitHub CLI (`gh`) logged in with `gh auth login`. GitHub turns workflows off in a new fork. Open the Actions tab of the fork to enable them.

The commands below use `sudo docker`. If your user is in the `docker` group, or you use Docker Desktop, leave out `sudo`.

## Step 1. Go to the project folder

Open a terminal in the folder that contains this README and `docker-compose.yml`, for example:

```bash
cd projects/session-04
```

Run every command in this guide from this folder.

## Step 2. Build and start the containers

```bash
sudo docker compose up --build -d
```

The first build takes a few minutes. Most of that time goes to the worker image, which installs ffmpeg and downloads Piper and the voice. Later builds reuse those layers and take seconds. `-d` runs the containers in the background and gives your terminal back.

## Step 3. Check the containers

```bash
sudo docker compose ps
```

You should see six containers, `session-04-api-1`, `session-04-gateway-1`, `session-04-minio-1`, `session-04-redis-1`, `session-04-web-1` and `session-04-worker-1`, each with a STATUS that starts with `Up`. The API has a health check in `docker-compose.yml` that asks `/healthz` every 10 seconds, so its status also shows `(health: starting)` and, after the first check, `(healthy)`.

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

After that the worker prints one `[DONE]` line for every job it finishes. To follow the requests as they arrive, run `sudo docker compose logs -f gateway api worker` and press Ctrl+C to stop watching.

## Step 4. Open each address and check it

Open these addresses in a browser on the machine that runs Docker.

### The page

| Address | What to check |
|---|---|
| http://localhost:8088/ | The page "TTS Case Study". Type a text or keep the example and click Synthesize. The status next to the button goes from `QUEUED` to `RUNNING` to `DONE` within a few seconds, `job_id` and `mp3_url` fill in, and the player under "2) Play MP3" plays the speech. |

Use port 8088. The page calls the API at `/api` on the address it was loaded from, and only the gateway knows that path.

### The same job with curl

Send a text:

```bash
curl -X POST http://localhost:8088/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"text":"Hello world this is a test"}'
```

```
{"job_id":"fabe836a-f080-4448-880a-5046ea39fc73","status":"QUEUED"}
```

Your `job_id` is different. Put it in place of `<job-id>` in the next commands. Ask for the status:

```bash
curl http://localhost:8088/api/jobs/<job-id>
```

```
{"job_id":"fabe836a-f080-4448-880a-5046ea39fc73","status":"DONE","mp3_url":"/mp3/fabe836a-f080-4448-880a-5046ea39fc73.mp3","error":null}
```

A sentence this short is `DONE` about a second after you send it. A text of 2000 characters, the longest the API accepts, took 21 seconds in a test run and showed `RUNNING` in between. Then look at the headers of the MP3 file and download it:

```bash
curl -I http://localhost:8088/mp3/<job-id>.mp3
curl -o output.mp3 http://localhost:8088/mp3/<job-id>.mp3
```

```
HTTP/1.1 200 OK
Server: nginx/1.27.5
Date: Thu, 01 Oct 2026 07:46:40 GMT
Content-Type: audio/mpeg
Content-Length: 15635
Connection: keep-alive
```

`output.mp3` holds about 2.4 seconds of speech in about 16 KB. You can also paste `http://localhost:8088/mp3/<job-id>.mp3` into the browser's address bar.

The API answers with status 422 when `text` is missing, empty or longer than 2000 characters, and with 404 for a `job_id` it does not know. It also accepts `voice` and `speed` and stores them with the job, but the worker does not read them. The voice and the speaking rate are settings of the worker (see [Changing the settings](#changing-the-settings)).

### The other addresses

| Address | What to check |
|---|---|
| http://localhost:8088/healthz | `{"ok":true,"env":"dev","bucket":"tts-dev"}`, the answer of the web service. |
| http://localhost:8088/api/healthz | `{"ok":true,"env":"dev"}`, the answer of the API. |
| http://localhost:8000/docs | The API on its own port, without the gateway. FastAPI lists the routes here and lets you try them. The paths have no `/api` prefix, for example `/jobs`. Do not use `http://localhost:8088/api/docs` for this: that page loads the route list of the web service. |
| http://localhost:8080/healthz | The web service on its own port. The page at http://localhost:8080/ loads too, but its Synthesize button fails with "Create job failed", because port 8080 has no `/api`. |
| http://localhost:9001 | The console of the object storage, titled SILO. Log in as `minioadmin` with password `minioadmin`. Object Browser lists the bucket `tts-dev`, and inside it one `.mp3` file per finished job. The worker creates the bucket when it uploads its first file, so the list is empty before the first job. |

### The queue and the job records in Redis

```bash
sudo docker compose exec redis redis-cli LLEN jobs:queue:dev
sudo docker compose exec redis redis-cli HGETALL job:<job-id>
```

`LLEN` prints the number of jobs that wait for the worker, which is 0 when the worker keeps up. `HGETALL` prints what the API stored for one job: `job_id`, `status`, `voice`, `speed`, `bucket`, `mp3_key` and `error`.

## Step 5. Run the unit tests

The unit tests import the code of the three services and replace Redis, MinIO, Piper and ffmpeg with stand-ins, so they need no container. Install the packages once, then run the tests:

```bash
../.venv/bin/pip install -r requirements-dev.txt
../.venv/bin/python -m pytest -q tests/unit
```

```
......................................                                   [100%]
38 passed, 1 warning in 1.04s
```

The warning is a `DeprecationWarning` from Starlette's test client, a package that FastAPI installs. It does not affect the tests.

`requirements-dev.txt` installs the packages of the three services with the versions their images use, plus pytest and the other test tools. pytest finds the services because `pytest.ini` puts this folder on the import path.

## Step 6. Run the integration tests

The integration tests need the running stack from step 2. They do what you did by hand in step 4:

```bash
../.venv/bin/python -m pytest -q tests/integration/test_e2e.py --base-url http://localhost:8088
```

```
..                                                                       [100%]
2 passed in 1.69s
```

`test_e2e_compose_pipeline` checks both `/healthz` addresses, creates a job, waits up to 180 seconds for `DONE`, downloads the MP3 file and checks its type, its size and the headers that `curl -I` shows. `test_e2e_invalid_job_rejected` sends an empty text and expects the API to refuse it. Every request goes through the gateway at `--base-url`.

To test the API and the web service on their own ports instead, replace `--base-url ...` with `--api-url http://localhost:8000 --web-url http://localhost:8080`.

## Step 7. Run the tests on GitHub (CI)
 
`.github/workflows/tests.yml` defines the workflow `CI Tests (session-04)`. GitHub runs it on every push and every pull request, on its own runners (`ubuntu-latest`). Every step runs in `projects/session-04`. The workflow has two jobs:

| Job | What it runs |
|---|---|
| Unit Tests | Installs `requirements-dev.txt` and runs `pytest -q tests/unit`, as in step 5. |
| Integration / E2E Tests (docker-compose) | Starts only when the unit tests passed. It runs `docker compose up -d --build`, waits until `/api/healthz` and `/healthz` answer through the gateway, and runs the command from step 6. If a step fails it prints the logs of all containers. It always ends with `docker compose down -v`. |

Push a change and watch the run from the terminal:

```bash
git add .
git commit -m "Change the page title"
git push origin main

gh run list --workflow "CI Tests (session-04)" --limit 3
gh run watch --exit-status
```

`gh run watch` asks which run to follow and then shows its jobs and steps until the run ends. With `--exit-status` its exit code is 0 only if the run succeeded. A run takes about two minutes, most of it for the image build in the second job. After a failed run, `gh run view --log-failed` prints the output of the step that failed.

To start the workflow without a new commit:

```bash
gh workflow run tests.yml --ref main
```

## Step 8. Deploy after CI succeeds (CD)

`.github/workflows/deploy-local.yml` defines the workflow `deploy-local`. GitHub starts it each time a run of `CI Tests (session-04)` completes, and skips its job unless that run succeeded. The job asks for a runner with the labels `self-hosted`, `Linux` and `X64`. That is a machine of your own with Docker, which you register with the repository. On it the job checks out branch `main` and runs, from the root of the repository:

```bash
docker compose -f projects/session-04/docker-compose.yml down || true
docker compose -f projects/session-04/docker-compose.yml up -d --build
docker compose -f projects/session-04/docker-compose.yml ps
```

The stack then runs on that machine, at http://localhost:8088/, built from the code that passed the tests. You can run these three commands yourself from the root of the repository to see what the job does. Put `sudo` in front of them if your user is not in the `docker` group.

To set up the runner:

1. On GitHub, open the repository's Settings > Actions > Runners and click New self-hosted runner. Choose Linux and x64. The page shows the commands that download the runner and register it, with a token that is valid for one hour.
2. Run those commands in a folder outside the repository, such as `~/actions-runner`. Accept the default labels.
3. The workflow calls `docker` without `sudo`, so the user that starts the runner must be in the `docker` group: `sudo usermod -aG docker $USER`, then log out and in again.
4. Start the runner with `./run.sh`. It prints `Listening for Jobs`.
5. Push a commit. When the CI run ends with success, the runner prints the name of the deploy job, and `gh run list --workflow deploy-local --limit 3` shows the run.

Without a runner online, the deploy run waits in the queue and GitHub cancels it after 24 hours.

The runner checks the repository out into its own folder, `actions-runner/_work/`. Compose names the project `session-04` after the folder of `docker-compose.yml` in both copies, so the deployment replaces the containers you started in step 2. `sudo docker compose ps` in this folder shows the deployed containers, and step 9 stops them.

## Step 9. Stop

```bash
sudo docker compose down
```

This removes the six containers and their network within a few seconds. The MP3 files stay in the Docker volume `session-04_minio_data`. The job records were in Redis and go with its container. After the next start, `/api/jobs/<job-id>` answers 404 for an old job, while `/mp3/<job-id>.mp3` still plays. To delete the MP3 files as well, run `sudo docker compose down -v`.

## Changing the settings

All settings are environment variables in `docker-compose.yml`. After you edit that file, run `sudo docker compose up -d` again, and Compose recreates only the containers whose settings changed. After you edit Python code, the page or a Dockerfile, run `sudo docker compose up --build -d`. The gateway finds a recreated container by itself. For a second or two it may answer `502 Bad Gateway`.

| Variable | Service | Value | Meaning |
|---|---|---|---|
| `APP_ENV` | api, web, worker | `dev` | Name of the environment. `/healthz` reports it, and the API puts new jobs on the list `jobs:queue:<APP_ENV>`. |
| `QUEUE_KEY` | worker | `jobs:queue:dev` | The Redis list the worker reads. It must be the list the API writes to. |
| `REDIS_URL` | api, worker | `redis://redis:6379/0` | Address of Redis |
| `MP3_BUCKET` | api, web, worker | `tts-dev` | Bucket for the MP3 files. All three need the same value. |
| `INTERNAL_TOKEN` | api, worker | `changeme` | The worker sends it in the header `X-Internal-Token` when it reports the status of a job. The API refuses the report with 401 if the two values differ. |
| `API_BASE_URL` | worker | `http://api:8000` | Where the worker reports the status of a job |
| `PUBLIC_BASE_URL` | api | empty | Put in front of `mp3_url` in the status of a job. Empty gives the relative address `/mp3/<job-id>.mp3`. |
| `S3_ENDPOINT`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, `S3_REGION` | web, worker | `http://minio:9000`, `minioadmin`, `minioadmin`, `us-east-1` | Address and login of MinIO. The two keys must equal `MINIO_ROOT_USER` and `MINIO_ROOT_PASSWORD` of the `minio` service. |
| `PIPER_MODEL_PATH` | worker | `/models/en_GB-alan-medium.onnx` | The voice. The worker's Dockerfile downloads this file into the image. |
| `PIPER_LENGTH_SCALE` | worker | `"1.0"` | Speaking rate. A higher value is slower: with `"1.5"` the sentence from step 4 takes 3.3 seconds instead of 2.4. |
| `replicas` (under `deploy`) | worker | `1` | Number of worker containers |

Several workers can share the queue, and each job goes to one of them. To try it without editing the file, run `sudo docker compose up -d --scale worker=3`. `sudo docker compose logs worker` then shows `[DONE]` lines from `worker-1`, `worker-2` and `worker-3`.

The token `changeme`, the login `minioadmin` and the published ports of Redis and MinIO suit a stack on your own machine. Change them before the stack runs on a machine that other people can reach.

## Troubleshooting

| Problem | What to do |
|---|---|
| `port is already allocated` or `address already in use` | Another program or another session's containers use one of the six ports. Sessions 1 to 3 use port 8000, and a Redis server installed on your machine uses 6379. Stop them, for example with `sudo docker compose down` in the other session's folder, then run step 2 again. |
| The page shows `ERROR` and the message "Create job failed" | The page was opened on port 8080. Use http://localhost:8088/. If it happens on 8088, the API is not running: check `sudo docker compose ps` and `sudo docker compose logs api`. |
| The page shows `ERROR` and the message "Job failed" | The worker reported `FAILED`. `curl http://localhost:8088/api/jobs/<job-id>` shows the reason in `error`, for example `empty text` for a text of spaces only. For other errors read `sudo docker compose logs worker`. |
| A job stays `QUEUED` | No worker is running. Check `sudo docker compose ps` and `sudo docker compose logs worker`. |
| A job stays `RUNNING` | The worker was stopped while it worked on that job. Send the text again. |
| `502 Bad Gateway` on port 8088 | The `api` or `web` container is not up. Right after a start this passes within a second or two. If it stays, check `sudo docker compose ps` and the logs of that service. |
| The build of the worker image stops at a `curl` command | The download of Piper or of the voice failed. Check your internet connection and run step 2 again. |
| `ModuleNotFoundError: No module named 'fastapi'` in step 5 | The packages are not in the environment that runs pytest. Run the `pip install` command from step 5 with the same `../.venv`. |
| `ConnectionError` or `Connection refused` in step 6 | The stack is not running. Do step 2, wait until http://localhost:8088/healthz answers, and run the tests again. |
| The `deploy-local` run stays queued | No self-hosted runner is online. Start it with `./run.sh` (step 8). |
| The deploy job fails with `permission denied` on `/var/run/docker.sock` | The user that runs the runner is not in the `docker` group (step 8, point 3). |
