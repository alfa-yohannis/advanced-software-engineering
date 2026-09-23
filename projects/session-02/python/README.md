# ZeroMQ video pipeline with Prometheus and Grafana

Code for session 2 (Observability) of IT30213 Advanced Software Engineering & DevOps. It is the ZeroMQ video pipeline from session 1 with a Prometheus `/metrics` endpoint on every node. Prometheus collects those metrics and Grafana charts them. Docker Compose builds and starts all six containers with one command.

## How the pieces fit together

```
capturer (Node A) ----raw------> broker ----raw------> transformer (Node B)
transformer (Node B) --processed--> broker --processed--> web_server (Node C) --MJPEG--> browser

prometheus --reads /metrics every 2 s--> capturer, broker, transformer, web_server
grafana ---queries---------------------> prometheus
```

| Container | What it does | Ports on your machine |
|---|---|---|
| `broker` | ZeroMQ XSUB/XPUB proxy. Publishers connect to port 5555, subscribers to port 5556. | 5555, 5556, 9102 (metrics) |
| `capturer` (Node A) | Reads `data/input.mp4` (1280x720, 30 fps, 17 seconds, played in a loop) and publishes one JPEG frame every 0.1 s on topic `raw`. | 9101 (metrics) |
| `transformer` (Node B) | Receives `raw` frames, converts them to grayscale and publishes them on topic `processed`. | 9103 (metrics) |
| `web_server` (Node C) | Receives `processed` frames and serves them to the browser as an MJPEG stream. | 8000 (web page), 9104 (metrics) |
| `prometheus` | Scrapes the four `/metrics` endpoints every 2 s, as listed in `prometheus.yml`. | 9090 |
| `grafana` | Charts the data stored in Prometheus. | 3000 |

Inside Docker the containers reach each other by service name. The nodes connect to `tcp://broker:5555` and `tcp://broker:5556`, Prometheus scrapes `capturer:9101` and the other three nodes, and Grafana reads from `http://prometheus:9090`. From your own browser you use `localhost` with the ports in the table.

```
broker/             broker.py and its Dockerfile
capturer/           capturer.py and its Dockerfile
transformer/        transformer.py and its Dockerfile
web_server/         web_server.py, static/index.html and the Dockerfile
shared/             observability.py, the metrics every node exports
data/input.mp4      the video the capturer reads
prometheus.yml      the targets Prometheus scrapes
docker-compose.yml  the six containers and their settings
logs/               /metrics snapshots from an earlier run, used in the slides
```

## Before you start

- Docker with the Compose plugin. `docker compose version` should print a version number. On Windows or macOS, install Docker Desktop.
- Internet access for the first build. Docker downloads `python:3.12-slim`, `prom/prometheus`, `grafana/grafana` and the Python packages.
- Free ports: 3000, 5555, 5556, 8000, 9090, 9101, 9102, 9103 and 9104. Sessions 1 and 3 use the same ports and some of the same container names, so stop them first with `sudo docker compose down` in their folders.

The commands below use `sudo docker`. If your user is in the `docker` group, or you use Docker Desktop, leave out `sudo`.

## Step 1. Go to the project folder

Open a terminal in the folder that contains this README and `docker-compose.yml`, for example:

```bash
cd projects/session-02/python
```

Run every command in this guide from this folder.

## Step 2. Build and start the containers

```bash
sudo docker compose up --build -d
```

The first build takes a few minutes. `-d` runs the containers in the background and gives your terminal back. Keep `--build`: sessions 1 to 3 use the same image names (`broker`, `capturer`, `transformer`, `web_server`), and without it Docker may start an image built from another session's code.

## Step 3. Check the containers

```bash
sudo docker compose ps
```

You should see six containers, `broker`, `capturer`, `transformer`, `web_server`, `prometheus` and `grafana`, each with a STATUS that starts with `Up`.

Then read the startup messages of the four pipeline nodes:

```bash
sudo docker compose logs broker capturer transformer web_server
```

Look for these lines:

```
broker       | Broker running:
broker       |   PUB -> tcp://0.0.0.0:5555  (publishers connect here)
broker       |   SUB -> tcp://0.0.0.0:5556  (subscribers connect here)
broker       |   METRICS -> 0.0.0.0:9102/metrics
capturer     | [capturer] Video=/data/input.mp4 FPS≈30.00
capturer     | [capturer] Publish every 0.100s → tcp://broker:5555 topic=raw
capturer     | [capturer] Metrics on :9101/metrics
transformer  | [transformer] Subscribed to tcp://broker:5556 topic=raw
transformer  | [transformer] Publishing to tcp://broker:5555 topic=processed
transformer  | [transformer] Metrics on :9103/metrics
web_server   | [web_server] Subscribed to tcp://broker:5556 topic=processed
web_server   | [web_server] Metrics on :9104/metrics
```

Two threads in the web server print at the same time, so some of its lines come out joined together. After startup the nodes print nothing per frame. Step 4 shows how to see the frames moving.

The web server log also says `http://0.0.0.0:8000/`. That is the address it listens on. In your browser, use `http://localhost:8000/`.

## Step 4. Open each address and check it

Open these addresses in a browser on the machine that runs Docker.

### The application (web_server, Node C)

| Address | What to check |
|---|---|
| http://localhost:8000/ | The page plays the video in grayscale. The clip is 17 seconds long and starts again when it ends. |
| http://localhost:8000/stream.mjpg | The same video without the page around it. This is the MJPEG stream that the page embeds. |
| http://localhost:8000/frame.jpg | A single grayscale frame, 1280x720. Reload to get a newer one. `No frame yet` means no frame has reached the web server yet. |
| http://localhost:8000/meta.json | Data about the latest frame. `frame_id` grows by about 10 per second, `processed` is `grayscale`, `mode` is `L` (8-bit grayscale), `w` is 1280 and `h` is 720. |

### The metrics of each node

Each node serves its metrics as plain text. Reload a page after a few seconds and check that the counters went up.

| Address | Node | What to check |
|---|---|---|
| http://localhost:9101/metrics | capturer | `frames_out_total{...,topic="raw"}` grows by about 10 per second. |
| http://localhost:9102/metrics | broker | `frames_in_total{...,topic="xsub_in"}` and `frames_out_total{...,topic="xpub_out"}` grow by about 20 per second, because both `raw` and `processed` frames pass through the broker. The counters with `topic="subs"` show 2: one subscription from the transformer and one from the web server. |
| http://localhost:9103/metrics | transformer | `frames_in_total{...,topic="raw"}` and `frames_out_total{...,topic="processed"}` grow at the same rate, about 10 per second. |
| http://localhost:9104/metrics | web_server | `frames_in_total{...,topic="processed"}` and `e2e_seconds_count` grow by about 10 per second. `mjpeg_clients` equals the number of browser tabs showing the video. |

On all four pages, `errors_total` should have only its `# HELP` and `# TYPE` lines. A line with a value means that node counted an error.

### Prometheus

| Address | What to check |
|---|---|
| http://localhost:9090/targets | Four targets, `broker`, `capturer`, `transformer` and `web_server`, each showing `1 / 1 up` and the state `UP`. |
| http://localhost:9090/query | The queries in step 5 return data. |

### Grafana

| Address | What to check |
|---|---|
| http://localhost:3000 | The Grafana login page. Step 6 connects Grafana to Prometheus. |

## Step 5. Query the metrics in Prometheus

Open http://localhost:9090/query, paste a query into the expression box and click Execute. The Table tab shows the current value and the Graph tab shows it over time.

| Question | Query | Expected result |
|---|---|---|
| Frames per second sent by each node | `sum by (service) (rate(frames_out_total[1m]))` | capturer and transformer about 10, broker about 20 |
| Frames per second received by each node | `sum by (service) (rate(frames_in_total[1m]))` | transformer and web_server about 10, broker about 20 |
| Average time from capture to the web server, in seconds | `rate(e2e_seconds_sum[1m]) / rate(e2e_seconds_count[1m])` | about 0.03 |
| 95th percentile of that time, in seconds | `histogram_quantile(0.95, sum by (le) (rate(e2e_seconds_bucket[1m])))` | about 0.05 |
| Average time per processing stage, in seconds | `sum by (stage) (rate(stage_seconds_sum[1m])) / sum by (stage) (rate(stage_seconds_count[1m]))` | JPEG decode and encode about 0.01, grayscale about 0.002, broker stages about 0.0001 |
| CPU per node, in percent | `process_cpu_percent` | capturer highest (about 50), then transformer (about 20), broker and web_server a few percent |
| Memory per node, in MB | `process_rss_bytes / 1024 / 1024` | capturer about 115, the others 30 to 50 |
| Average JPEG size, in bytes | `sum by (service, direction) (rate(jpeg_bytes_sum[1m])) / sum by (service, direction) (rate(jpeg_bytes_count[1m]))` | about 100000 |
| Open video streams | `mjpeg_clients` | one per open tab |
| Errors | `errors_total` | Empty query result |

The expected values come from one test run. CPU and memory depend on your machine.

When you read the results:

- The queries average over the last minute (`[1m]`), so give the pipeline a minute after startup before you compare numbers.
- `transformer_recv` and `web_recv_zmq` measure how long the node waited for the next frame, so they stay near 0.1 s, the capturer's publish interval. The other stages measure actual work.
- `broker_recv_xpub` and `broker_send_xsub` show `NaN`. They run only when a subscriber connects or leaves, so there is nothing to average.
- Each node labels its metrics with `service` and with its own `instance` (the container ID). Prometheus sets `instance` to the address it scraped, such as `capturer:9101`, and renames the node's label to `exported_instance`.

## Step 6. Connect Grafana to Prometheus

1. Open http://localhost:3000 and log in as `admin` with password `admin`. Grafana then asks for a new password. Set one or click Skip.
2. In the left menu, open Connections > Data sources and click Add data source.
3. Choose Prometheus.
4. Under Connection, type `http://prometheus:9090` in Prometheus server URL. Leave the other fields as they are.
5. Scroll to the bottom and click Save & test. Grafana should answer "Successfully queried the Prometheus API."

Use the service name `prometheus`, not `localhost`. Grafana runs in its own container, where `localhost` means the Grafana container itself, and the test fails with "connection refused".

## Step 7. Build a dashboard

These steps match Grafana 13, the version of `grafana/grafana:latest` when this guide was tested. Other versions name some buttons differently.

1. In the left menu, open Dashboards and click New > New dashboard.
2. In the Add pane on the right, click the Panel tile ("Drag or click to add a panel"). A new panel appears and the pane on the right shows its Title. Type a name there, then click Configure visualization on the panel.
3. The data source is already `prometheus`. In query A, switch from Builder to Code, paste a query from step 5 and click Run queries. The chart appears above the query.
4. Click Back at the top right to return to the dashboard.
5. Repeat steps 2 to 4 for each chart you want, for example frames per second, capture-to-web latency, CPU and memory.
6. Click Save at the top right, type a title for the dashboard and click Save.

For a quick look at a single query without building a dashboard, use Explore in the left menu.

## Step 8 (optional). Repeat the experiment from the slides

The slides compare the web server with one viewer and with six viewers.

1. Keep one browser tab on http://localhost:8000/ for a few minutes. Note the frames per second, the capture-to-web latency, and the CPU and memory of `web_server`, using the queries in step 5 or your dashboard.
2. Open five more tabs with the same address. `mjpeg_clients` should rise to 6.
3. Wait a few minutes and compare the numbers with those from point 1.

The files in `logs/` are copies of the four `/metrics` pages from an earlier run, and the numbers in the slides' component table come from them. To save your own copies:

```bash
mkdir -p logs/my-run
curl -s http://localhost:9101/metrics > logs/my-run/capturer.txt
curl -s http://localhost:9102/metrics > logs/my-run/broker.txt
curl -s http://localhost:9103/metrics > logs/my-run/transformer.txt
curl -s http://localhost:9104/metrics > logs/my-run/web_server.txt
```

## Step 9. Stop

```bash
sudo docker compose down
```

This removes the six containers and their network. The Grafana data source and dashboards go with them, so next time you repeat steps 6 and 7. To pause and keep them, run `sudo docker compose stop`, and later `sudo docker compose start`. Add `-v` to `down` to also delete the metrics Prometheus stored.

## Changing the settings

All settings are environment variables in `docker-compose.yml`. After you edit that file, run `sudo docker compose up -d` again, and Compose recreates only the containers whose settings changed. After you edit Python code, run `sudo docker compose up --build -d`.

| Variable | Container | Value | Meaning |
|---|---|---|---|
| `PUBLISH_EVERY_SEC` | capturer | `0.1` | Seconds between published frames. `0.1` gives 10 frames per second. |
| `JPEG_QUALITY` | capturer | `80` | JPEG quality (1 to 100) of the `raw` frames |
| `LOOP` | capturer | `"true"` | Start the video again when it ends |
| `JPEG_QUALITY_OUT` | transformer | `85` | JPEG quality of the `processed` frames |
| `HTTP_THREADS` | web_server | `8` | Web server worker threads. Each open video stream keeps one busy. |
| `METRICS_PORT` | all four nodes | `9101` to `9104` | Port of the node's `/metrics` endpoint |
| `SERVICE` | all four nodes | the node name | Value of the `service` label on the node's metrics |

If you change a `METRICS_PORT`, change the matching `ports` entry in `docker-compose.yml` and the target in `prometheus.yml` as well.

## Troubleshooting

| Problem | What to do |
|---|---|
| `port is already allocated` or `address already in use` | Another program or another session's containers use that port. Stop them, for example with `sudo docker compose down` in the other session's folder, then run step 2 again. |
| `Conflict. The container name "/broker" is already in use` | Containers with the same names are left over from another session. Run `sudo docker compose down` in that session's folder, or remove them with `sudo docker rm -f broker capturer transformer web_server prometheus grafana`. |
| The page at http://localhost:8000/ stays black, or `/frame.jpg` says `No frame yet` | Run `sudo docker compose ps`. If `capturer` is not `Up`, read `sudo docker compose logs capturer`. `Cannot open /data/input.mp4` means the video is missing from `data/`. |
| A target on http://localhost:9090/targets is `DOWN` | That node's container stopped. Check it with `sudo docker compose ps` and `sudo docker compose logs <name>`. |
| Save & test in Grafana fails with "connection refused" | The URL points to `localhost`. Use `http://prometheus:9090`. |
