# ZeroMQ video pipeline with payload encryption

Code for session 3 (Security in Software Systems) of IT30213 Advanced Software Engineering & DevOps. It is the pipeline from session 2, with Prometheus and Grafana, plus one runtime security control: every frame is encrypted while it travels through the broker. The broker forwards the messages without the key, and a sniffer shows what any other program that connects to the broker can see. Docker Compose builds and starts all six containers with one command.

## How the pieces fit together

```
capturer (Node A) ----raw------> broker ----raw------> transformer (Node B)
transformer (Node B) --processed--> broker --processed--> web_server (Node C) --MJPEG--> browser
                                    broker --raw, processed--> sniffer (no key)

prometheus --reads /metrics every 2 s--> capturer, broker, transformer, web_server
grafana ---queries---------------------> prometheus
```

Each message has three parts: the topic, a JSON header and a JPEG frame. The capturer encrypts the header and the frame before it publishes them. The transformer decrypts both, converts the frame to grayscale and encrypts both again. The web server decrypts them before it serves the frame. The topic stays readable because the broker routes messages by topic. The encryption is Fernet from the `cryptography` package (`shared/crypto.py`), and only the three nodes that need the key get it.

| Container | What it does | Has the key | Ports on your machine |
|---|---|---|---|
| `broker-session03` | ZeroMQ XSUB/XPUB proxy. Publishers connect to port 5555, subscribers to port 5556. | no | 5555, 5556, 9102 (metrics) |
| `capturer-session03` (Node A) | Reads `data/input.mp4` (1280x720, 30 fps, 17 seconds, played in a loop), encrypts one JPEG frame every 0.1 s and publishes it on topic `raw`. | yes | 9101 (metrics) |
| `transformer-session03` (Node B) | Decrypts `raw` frames, converts them to grayscale, encrypts them again and publishes them on topic `processed`. | yes | 9103 (metrics) |
| `web_server-session03` (Node C) | Decrypts `processed` frames and serves them to the browser as an MJPEG stream. | yes | 8000 (web page), 9104 (metrics) |
| `prometheus` | Scrapes the four `/metrics` endpoints every 2 s, as listed in `prometheus.yml`. | no | 9090 |
| `grafana` | Charts the data stored in Prometheus. | no | 3000 |

Inside Docker the containers reach each other by service name. The nodes connect to `tcp://broker-session03:5555` and `tcp://broker-session03:5556`, Prometheus scrapes `capturer-session03:9101` and the other three nodes, and Grafana reads from `http://prometheus:9090`. From your own browser you use `localhost` with the ports in the table.

```
broker/             broker.py and its Dockerfile
capturer/           capturer.py and its Dockerfile
transformer/        transformer.py and its Dockerfile
web_server/         web_server.py, static/index.html and the Dockerfile
shared/             observability.py (the metrics every node exports) and crypto.py (encrypt_bytes, decrypt_bytes)
sniffer.py          subscribes to every topic at the broker and prints the first bytes of each message
sniffer.sh          runs sniffer.py in a container on the Compose network
data/input.mp4      the video the capturer reads
prometheus.yml      the targets Prometheus scrapes
docker-compose.yml  the six containers and their settings
.env                the encryption key, which you create in step 2 (Git ignores this file)
logs/               /metrics snapshots copied from a session 2 run, before encryption was added
```

## Before you start

- Docker with the Compose plugin. `docker compose version` should print a version number. On Windows or macOS, install Docker Desktop.
- The Python virtual environment in `projects/.venv`, two folders up from this README, with `pyzmq` and `cryptography` installed. It creates the key and runs the sniffer on your machine. If you don't have it yet, create it from this folder:

  ```bash
  python3 -m venv ../../.venv
  ../../.venv/bin/pip install pyzmq cryptography
  ```

  On Ubuntu, if `python3 -m venv` fails, run `sudo apt install python3-venv` first.
- Internet access for the first build. Docker downloads `python:3.12-slim`, `prom/prometheus`, `grafana/grafana` and the Python packages.
- Free ports: 3000, 5555, 5556, 8000, 9090, 9101, 9102, 9103 and 9104. Sessions 1 and 2 use the same ports, so stop them first with `sudo docker compose down` in their folders.

The commands below use `sudo docker`. If your user is in the `docker` group, or you use Docker Desktop, leave out `sudo`.

## Step 1. Go to the project folder

Open a terminal in the folder that contains this README and `docker-compose.yml`, for example:

```bash
cd projects/session-03/python
```

Run every command in this guide from this folder.

## Step 2. Create the encryption key

The capturer, transformer and web server read the same key from the environment variable `PAYLOAD_KEY`. `docker-compose.yml` takes its value from a file named `.env` in this folder. Git ignores `.env`, so the key never goes into the repository and every copy of the project gets its own. Create the file once:

```bash
../../.venv/bin/python -c "from cryptography.fernet import Fernet; print('PAYLOAD_KEY=' + Fernet.generate_key().decode())" > .env
```

`.env` now holds one line, `PAYLOAD_KEY=` followed by 44 characters. `docker compose down` leaves the file alone, so you create it again only when you want a new key (see [Changing the settings](#changing-the-settings)).

## Step 3. Build and start the containers

```bash
sudo docker compose up --build -d
```

The first build takes a few minutes. `-d` runs the containers in the background and gives your terminal back. Keep `--build`: sessions 2 and 3 use the same image names (`broker`, `capturer`, `transformer`, `web_server`), and without it Docker may start an image built from the other session's code.

Compose may warn about `orphan containers (web_server, transformer, capturer, broker)`. Those are stopped containers from session 1 or 2. The three sessions sit in folders named `python`, so Compose treats them as one project. The warning is harmless, and `sudo docker compose down` in the other session's folder removes them.

## Step 4. Check the containers

```bash
sudo docker compose ps
```

You should see six containers, `broker-session03`, `capturer-session03`, `transformer-session03`, `web_server-session03`, `prometheus` and `grafana`, each with a STATUS that starts with `Up`.

Then read the startup messages of the four pipeline nodes:

```bash
sudo docker compose logs broker-session03 capturer-session03 transformer-session03 web_server-session03
```

Look for these lines:

```
broker-session03       | Broker running:
broker-session03       |   PUB -> tcp://0.0.0.0:5555  (publishers connect here)
broker-session03       |   SUB -> tcp://0.0.0.0:5556  (subscribers connect here)
broker-session03       |   METRICS -> 0.0.0.0:9102/metrics
capturer-session03     | [capturer] Video=/data/input.mp4 FPS≈30.00
capturer-session03     | [capturer] Publish every 0.100s → tcp://broker-session03:5555 topic=raw
capturer-session03     | [capturer] Metrics on :9101/metrics
transformer-session03  | [transformer] Subscribed to tcp://broker-session03:5556 topic=raw
transformer-session03  | [transformer] Publishing to tcp://broker-session03:5555 topic=processed
transformer-session03  | [transformer] Metrics on :9103/metrics
web_server-session03   | [web_server] Subscribed to tcp://broker-session03:5556 topic=processed
web_server-session03   | [web_server] Metrics on :9104/metrics
```

Two threads in the web server print at the same time, so some of its lines come out joined together. After startup the nodes print nothing per frame. The web server log also says `http://0.0.0.0:8000/`. That is the address it listens on. In your browser, use `http://localhost:8000/`.

If the capturer, transformer or web server is not `Up`, its last log line names the problem. The usual cause is a missing or broken `.env` (see [Troubleshooting](#troubleshooting)).

## Step 5. Open each address and check it

Open these addresses in a browser on the machine that runs Docker.

### The application (web_server, Node C)

| Address | What to check |
|---|---|
| http://localhost:8000/ | The page plays the video in grayscale. The clip is 17 seconds long and starts again when it ends. |
| http://localhost:8000/stream.mjpg | The same video without the page around it. This is the MJPEG stream that the page embeds. |
| http://localhost:8000/frame.jpg | A single grayscale frame, 1280x720. Reload to get a newer one. `No frame yet` means no frame has reached the web server yet. |
| http://localhost:8000/meta.json | Data about the latest frame. `frame_id` grows by about 10 per second, `processed` is `grayscale`, `mode` is `L` (8-bit grayscale), `w` is 1280 and `h` is 720. |

The browser receives ordinary JPEG frames, because the web server decrypts them first. The encryption protects only the path through the broker. The connection from the web server to the browser is plain HTTP.

### The metrics of each node

Each node serves its metrics as plain text. Reload a page after a few seconds and check that the counters went up. The `service` label of every metric is the node's container name, for example `service="capturer-session03"`.

| Address | Node | What to check |
|---|---|---|
| http://localhost:9101/metrics | capturer | `frames_out_total{...,topic="raw"}` grows by about 10 per second. |
| http://localhost:9102/metrics | broker | `frames_in_total{...,topic="xsub_in"}` and `frames_out_total{...,topic="xpub_out"}` grow by about 20 per second, because both `raw` and `processed` frames pass through the broker. The counters with `topic="subs"` show 2: one subscription from the transformer and one from the web server. Each sniffer run in step 6 adds 2 more, one when it subscribes and one when it leaves. |
| http://localhost:9103/metrics | transformer | `frames_in_total{...,topic="raw"}` and `frames_out_total{...,topic="processed"}` grow at the same rate, about 10 per second. |
| http://localhost:9104/metrics | web_server | `frames_in_total{...,topic="processed"}` and `e2e_seconds_count` grow by about 10 per second. `mjpeg_clients` equals the number of browser tabs showing the video. |

On all four pages, `errors_total` should have only its `# HELP` and `# TYPE` lines. A line with a value means that node counted an error, for example a message it could not decrypt (step 8).

### Prometheus

| Address | What to check |
|---|---|
| http://localhost:9090/targets | Four targets, `broker`, `capturer`, `transformer` and `web_server`, each showing `1 / 1 up` and the state `UP`. |
| http://localhost:9090/query | The queries in step 9 return data. |

### Grafana

| Address | What to check |
|---|---|
| http://localhost:3000 | The Grafana login page. Step 9 explains how to connect Grafana to Prometheus. |

## Step 6. Sniff the traffic at the broker

The sniffer plays the outsider. It connects to the broker's subscriber port, as any program that can reach port 5556 could, subscribes to every topic and prints the first bytes of each part of 10 messages. It has no key.

```bash
BROKER_HOST=127.0.0.1 ../../.venv/bin/python sniffer.py
```

`127.0.0.1` works because `docker-compose.yml` publishes the broker's port 5556 on your machine. To run the same sniffer from inside the Compose network instead, without Python on your machine, use `sudo bash sniffer.sh`.

The output looks like this (long lines shortened):

```
[sniffer] endpoint=tcp://127.0.0.1:5556 subscribe=(all) messages=10
[sniffer] Ctrl+C to stop

--- msg 0 parts=3 ---
  part[0] len=3 hex16=726177 utf8='raw'
  part[1] len=376 hex16=6741414141414271764e6873714c3249 utf8='gAAAAABqvNhsqL2I4ZbxZhVvg9oGL6Jw...'
  part[2] len=141028 hex16=6741414141414271764e687361587765 utf8='gAAAAABqvNhsaXwe2P0TTI58dwEsOGm9...'
  => header+payload do NOT look like JSON/JPEG (LIKELY ENCRYPTED)

--- msg 1 parts=3 ---
  part[0] len=9 hex16=70726f636573736564 utf8='processed'
  part[1] len=460 hex16=6741414141414271764e687330597554 utf8='gAAAAABqvNhs0YuTrDbUS9Z3sS6x9cqW...'
  part[2] len=136184 hex16=6741414141414271764e68737a702d79 utf8='gAAAAABqvNhszp-ykZlalmYDbtqymQ65...'
  => header+payload do NOT look like JSON/JPEG (LIKELY ENCRYPTED)
```

What the sniffer can and cannot read:

- `part[0]`, the topic, is readable (`raw` or `processed`). The broker needs it to route the message.
- `part[1]` (the header) and `part[2]` (the frame) are Fernet tokens. A token is base64 text and always starts with `gAAAAA` (hex `674141414141`), which encodes the token version and the start of a timestamp. The frame number, the timestamps and the image itself stay unreadable without the key.
- The sniffer still sees that messages flow, how often they come and how large they are, because the encryption covers only their content.
- Because a token is base64 text, it is about a third larger than the data inside. A JPEG of about 100 KB travels as a part of about 134 KB.

If the sniffer prints `no message within 10s`, the pipeline is not running or `BROKER_HOST` points to the wrong machine.

## Step 7 (optional). Compare with session 2

Session 2 runs the same pipeline without encryption. To sniff it with the same program:

```bash
sudo docker compose down
cd ../../session-02/python
sudo docker compose up --build -d
BROKER_HOST=127.0.0.1 ../../.venv/bin/python ../../session-03/python/sniffer.py
```

This time the header is readable JSON and the frame starts with `ffd8ff`, the signature of a JPEG file:

```
--- msg 0 parts=3 ---
  part[0] len=3 hex16=726177 utf8='raw'
  part[1] len=213 hex16=7b226672616d655f6964223a2033372c utf8='{"frame_id": 37, "ts_capture": 1790760951.073174, "encoding": "jpeg", ...'
  part[2] len=107045 hex16=ffd8ffe000104a464946000101000001 utf8=<not utf8>
  => header looks like JSON (PLAINTEXT)
  => payload looks like JPEG (PLAINTEXT ffd8ff)
```

Anyone who can reach port 5556 reads the frame number and timestamps, and can save the payload as a `.jpg` file and open it. Go back to session 3 afterwards:

```bash
sudo docker compose down
cd ../../session-03/python
sudo docker compose up --build -d
```

## Step 8 (optional). Give the web server the wrong key

Restart only the web server with a different key, as if it were a program that receives the messages but does not hold the right key:

```bash
WRONG_KEY=$(../../.venv/bin/python -c "from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())")
sudo PAYLOAD_KEY=$WRONG_KEY docker compose up -d --no-deps web_server-session03
```

A variable given on the command line takes priority over `.env`. `--no-deps` stops Compose from recreating the transformer with the wrong key as well.

Then check:

- http://localhost:8000/frame.jpg answers `No frame yet`, and the page stays black after a reload.
- On http://localhost:9104/metrics, `errors_total{...,type="InvalidToken",where="web_zmq_receiver"}` grows by about 10 per second, as fast as `frames_in_total`. The web server receives every frame and can read none of them.

Fernet also checks a signature in every token, so a message that someone changed or made up fails the same way. The transformer and the web server drop such a message, count it in `errors_total` and keep running.

Put the right key back:

```bash
sudo docker compose up -d
```

## Step 9. Query the metrics in Prometheus

Open http://localhost:9090/query, paste a query into the expression box and click Execute. The Table tab shows the current value and the Graph tab shows it over time.

| Question | Query | Expected result |
|---|---|---|
| Frames per second sent by each node | `sum by (service) (rate(frames_out_total[1m]))` | capturer-session03 and transformer-session03 about 10, broker-session03 about 20 |
| Frames per second received by each node | `sum by (service) (rate(frames_in_total[1m]))` | transformer-session03 and web_server-session03 about 10, broker-session03 about 20 |
| Average time from capture to the web server, in seconds | `rate(e2e_seconds_sum[1m]) / rate(e2e_seconds_count[1m])` | about 0.02 |
| 95th percentile of that time, in seconds | `histogram_quantile(0.95, sum by (le) (rate(e2e_seconds_bucket[1m])))` | about 0.05 |
| Average time per processing stage, in seconds | `sum by (stage) (rate(stage_seconds_sum[1m])) / sum by (stage) (rate(stage_seconds_count[1m]))` | `transformer_decrypt` about 0.001, `transformer_encrypt` and `web_decrypt` about 0.0005, JPEG encode and decode about 0.005, grayscale about 0.001, broker stages below 0.0001 |
| Bytes on the wire per message | `sum by (service) (rate(bytes_out_total[1m])) / sum by (service) (rate(frames_out_total[1m]))` | about 134000 |
| Average JPEG size, in bytes | `sum by (service, direction) (rate(jpeg_bytes_sum[1m])) / sum by (service, direction) (rate(jpeg_bytes_count[1m]))` | about 100000 |
| CPU per node, in percent | `process_cpu_percent` | capturer highest (25 to 35), then transformer (about 10), broker and web_server a few percent |
| Memory per node, in MB | `process_rss_bytes / 1024 / 1024` | capturer about 120, transformer about 60, broker and web_server 30 to 45 |
| Errors per second, by node and type | `sum by (service, type) (rate(errors_total[1m]))` | Empty query result. During step 8, web_server-session03 with type InvalidToken, about 10 |
| Open video streams | `mjpeg_clients` | one per open tab |

The expected values come from one test run. CPU and memory depend on your machine.

When you read the results:

- The queries average over the last minute (`[1m]`), so give the pipeline a minute after startup before you compare numbers.
- The encryption costs about 1 ms per frame in the transformer and half of that in the web server, little next to JPEG decoding and encoding. Its cost on the network is larger: compare bytes on the wire per message with the JPEG size.
- `transformer_recv` and `web_recv_zmq` measure how long the node waited for the next frame, so they stay near 0.1 s, the capturer's publish interval. The other stages measure actual work.
- `broker_recv_xpub` and `broker_send_xsub` run only when a subscriber connects or leaves, such as the sniffer. With none of that in the last minute they show `NaN`.
- Prometheus sets `instance` to the address it scraped, such as `capturer-session03:9101`, and renames the node's own label to `exported_instance`. The `job` label is the name on the targets page, such as `capturer`.

Grafana charts the same queries. Log in at http://localhost:3000 as `admin` with password `admin`, add a Prometheus data source with the URL `http://prometheus:9090`, and build a dashboard from the queries above. Steps 6 and 7 of the session 2 README list every click. When this guide was tested, `grafana/grafana:latest` was Grafana 13.2.2.

## Step 10. Stop

```bash
sudo docker compose down
```

This removes the six containers and their network. `.env` stays, so the next start uses the same key. The Grafana data source and dashboards go with the containers. To pause and keep them, run `sudo docker compose stop`, and later `sudo docker compose start`. The nodes stop within a second or two. With a browser tab still showing the video, the web server takes about 5 seconds and logs `WARNING:waitress:1 thread(s) still running`.

## Run without Docker

The four nodes also run as plain Python processes with the virtual environment in `projects/.venv`. Prometheus and Grafana are not part of this setup. Stop the containers first with `sudo docker compose down`, because the nodes use the same ports.

Install the packages the nodes import, the same ones the Dockerfiles install:

```bash
../../.venv/bin/pip install pyzmq opencv-python-headless pillow bottle waitress prometheus-client psutil cryptography
```

If the environment already has `opencv-python`, leave out `opencv-python-headless`. Both provide the `cv2` module.

Open four terminals in this folder. The capturer, transformer and web server need the key, so in their terminals load `.env` first:

```bash
set -a; source .env; set +a
```

Terminal 1, broker:

```bash
BROKER_BIND_HOST=127.0.0.1 ../../.venv/bin/python broker/broker.py
```

Terminal 2, capturer (Node A):

```bash
BROKER_HOST=127.0.0.1 VIDEO_PATH=data/input.mp4 PUBLISH_EVERY_SEC=0.1 ../../.venv/bin/python capturer/capturer.py
```

Terminal 3, transformer (Node B):

```bash
BROKER_HOST=127.0.0.1 ../../.venv/bin/python transformer/transformer.py
```

Terminal 4, web server (Node C):

```bash
BROKER_HOST=127.0.0.1 ../../.venv/bin/python web_server/web_server.py
```

Open http://localhost:8000/ and run the sniffer command from step 6. The metrics are on ports 9101 to 9104 as before, with the `service` labels `capturer`, `broker`, `transformer` and `web_server`. Press Ctrl+C in each terminal to stop.

## Changing the settings

All settings are environment variables in `docker-compose.yml`, except the key, which comes from `.env`. After you edit either file, run `sudo docker compose up -d` again, and Compose recreates only the containers whose settings changed. After you edit Python code, run `sudo docker compose up --build -d`.

| Variable | Container | Value | Meaning |
|---|---|---|---|
| `PAYLOAD_KEY` | capturer, transformer, web_server | from `.env` | Fernet key. All three need the same key, and the broker gets none. |
| `PUBLISH_EVERY_SEC` | capturer | `0.1` | Seconds between published frames. `0.1` gives 10 frames per second. |
| `JPEG_QUALITY` | capturer | `80` | JPEG quality (1 to 100) of the `raw` frames |
| `LOOP` | capturer | `"true"` | Start the video again when it ends |
| `JPEG_QUALITY_OUT` | transformer | `85` | JPEG quality of the `processed` frames |
| `HTTP_THREADS` | web_server | `8` | Web server worker threads. Each open video stream keeps one busy. |
| `METRICS_PORT` | all four nodes | `9101` to `9104` | Port of the node's `/metrics` endpoint |
| `SERVICE` | all four nodes | the container name | Value of the `service` label on the node's metrics |

To change the key, run the command from step 2 again, which overwrites `.env`, and then `sudo docker compose up -d`. Compose recreates the capturer, transformer and web server with the new key. Replace the key whenever it may have leaked. A key that was ever committed to Git stays in the repository's history even after you delete it from the files.

If you change a `METRICS_PORT`, change the matching `ports` entry in `docker-compose.yml` and the target in `prometheus.yml` as well. After you edit `prometheus.yml`, run `sudo docker compose restart prometheus`.

## Troubleshooting

| Problem | What to do |
|---|---|
| Compose warns `The "PAYLOAD_KEY" variable is not set. Defaulting to a blank string.`, and the capturer, transformer and web server stop with `Missing env var PAYLOAD_KEY` | `.env` is missing from this folder. Do step 2, then run `sudo docker compose up -d`. |
| A node stops with `ValueError: Fernet key must be 32 url-safe base64-encoded bytes.` | The value in `.env` is not a Fernet key, for example because it was cut short. Create the file again with the command from step 2. |
| `errors_total` counts `InvalidToken` errors | The nodes do not share one key, for example after step 8. Run `sudo docker compose up -d` so that all three read `.env` again. A few such errors can also come from a program outside the pipeline that publishes to the broker. The nodes drop those messages. |
| The sniffer prints `no message within 10s` | The pipeline is not running, or `BROKER_HOST` is wrong. From your machine, use `BROKER_HOST=127.0.0.1`. `sniffer.sh` sets the host itself. |
| `port is already allocated` or `address already in use` | Another program or another session's containers use that port. Stop them, for example with `sudo docker compose down` in the other session's folder, then run step 3 again. |
| The page at http://localhost:8000/ stays black, or `/frame.jpg` says `No frame yet` | Run `sudo docker compose ps`. If `capturer-session03` is not `Up`, read `sudo docker compose logs capturer-session03`. `Cannot open /data/input.mp4` means the video is missing from `data/`. If all containers are up, look for `InvalidToken` in `errors_total` on http://localhost:9104/metrics. |
| A target on http://localhost:9090/targets is `DOWN` | That node's container stopped. Check it with `sudo docker compose ps` and `sudo docker compose logs <name>`. |
| Save & test in Grafana fails with "connection refused" | The URL points to `localhost`. Use `http://prometheus:9090`. |
