# web_server/web_server.py
# ------------------------------------------------------------
# Web Server (Node C): subscribes to processed frames and serves
# - /            static UI
# - /frame.jpg   latest JPEG
# - /meta.json   latest metadata
# - /stream.mjpg MJPEG stream
# Exposes Prometheus metrics via shared/observability.py
# ------------------------------------------------------------

## Run locally (from project root)
# BROKER_HOST=127.0.0.1 \
# SUB_PORT=5556 \
# SUB_TOPIC=processed \
# HTTP_HOST=0.0.0.0 \
# HTTP_PORT=9000 \
# HTTP_THREADS=8 \
# METRICS_PORT=9104 \
# SERVICE=web_server \
# python web_server/web_server.py
#
# (recommended alternative)
# python -m web_server.web_server

## Run with Docker (named container)
# docker run --rm --name web_server \
#   -p 8000:8000 \
#   -p 9104:9104 \
#   -e BROKER_HOST=broker \
#   -e SUB_PORT=5556 \
#   -e SUB_TOPIC=processed \
#   -e HTTP_HOST=0.0.0.0 \
#   -e HTTP_PORT=8000 \
#   -e HTTP_THREADS=8 \
#   -e METRICS_PORT=9104 \
#   -e SERVICE=web_server \
#   web_server

import json
import os
import sys
import threading
import time
from pathlib import Path

import zmq
from bottle import Bottle, HTTPResponse, response, static_file
from waitress import serve

# ------------------------------------------------------------
# Ensure project root is on PYTHONPATH so shared/observability.py works
# ------------------------------------------------------------
THIS_FILE = Path(__file__).resolve()
PROJECT_ROOT = THIS_FILE.parents[1]  # parent of web_server/
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from shared.observability import (  # noqa: E402
    init as obs_init,
    SERVICE as OBS_SERVICE,
    INSTANCE as OBS_INSTANCE,
    BYTES_IN,
    E2E_SECONDS,
    ERRORS,
    FRAMES_IN,
    JPEG_BYTES,
    MJPEG_CLIENTS,
    STAGE_SECONDS,
)


def getenv_str(name: str, default: str) -> str:
    return os.getenv(name, default)


def getenv_int(name: str, default: int) -> int:
    try:
        return int(os.getenv(name, str(default)))
    except ValueError:
        return default


def validate_port(port: int, name: str) -> int:
    if not (1 <= port <= 65535):
        raise ValueError(f"{name} must be in [1..65535], got {port}")
    return port


# -----------------------------
# Docker-friendly parameters
# -----------------------------
BROKER_HOST = getenv_str("BROKER_HOST", "broker")
SUB_PORT = validate_port(getenv_int("SUB_PORT", 5556), "SUB_PORT")
SUB_TOPIC = getenv_str("SUB_TOPIC", "processed").encode("utf-8")

HTTP_HOST = getenv_str("HTTP_HOST", "0.0.0.0")
HTTP_PORT = validate_port(getenv_int("HTTP_PORT", 8000), "HTTP_PORT")

HTTP_THREADS = getenv_int("HTTP_THREADS", 8)

# Prometheus metrics
METRICS_PORT = validate_port(getenv_int("METRICS_PORT", 9104), "METRICS_PORT")

SUB_ENDPOINT = f"tcp://{BROKER_HOST}:{SUB_PORT}"
SUB_TOPIC_STR = SUB_TOPIC.decode("utf-8", "ignore")

# -----------------------------
# Static hosting
# -----------------------------
STATIC_DIR = Path(__file__).parent / "static"
INDEX_NAME = "index.html"

latest_jpeg = None
latest_meta = {}
lock = threading.Lock()

# Used to notify MJPEG streamers when a new frame arrives
frame_cond = threading.Condition(lock)
latest_frame_seq = 0  # increments for each new frame

app = Bottle()


@app.get("/")
def index():
    return static_file(INDEX_NAME, root=str(STATIC_DIR))


@app.get("/frame.jpg")
def frame():
    with lock:
        data = latest_jpeg
    if not data:
        return HTTPResponse("No frame yet", status=503)

    response.content_type = "image/jpeg"
    response.set_header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
    response.set_header("Pragma", "no-cache")
    response.set_header("Expires", "0")
    return data


@app.get("/meta.json")
def meta():
    with lock:
        m = dict(latest_meta) if latest_meta else {}
    response.content_type = "application/json"
    response.set_header("Cache-Control", "no-store")
    return json.dumps(m)


@app.get("/stream.mjpg")
def stream_mjpg():
    """
    MJPEG streaming endpoint.
    Browser loads: <img src="/stream.mjpg">
    """
    boundary = "frame"

    response.content_type = f"multipart/x-mixed-replace; boundary={boundary}"
    response.set_header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
    response.set_header("Pragma", "no-cache")
    response.set_header("Expires", "0")
    response.set_header("X-Accel-Buffering", "no")  # reverse proxies should not buffer

    def gen():
        global latest_frame_seq
        last_sent_seq = -1

        # Prometheus: active clients gauge
        MJPEG_CLIENTS.labels(OBS_SERVICE, OBS_INSTANCE).inc()
        try:
            while True:
                with frame_cond:
                    frame_cond.wait_for(
                        lambda: latest_jpeg is not None and latest_frame_seq != last_sent_seq,
                        timeout=2.0,
                    )
                    if latest_jpeg is None:
                        continue

                    jpeg = latest_jpeg
                    last_sent_seq = latest_frame_seq

                part_header = (
                    f"--{boundary}\r\n"
                    f"Content-Type: image/jpeg\r\n"
                    f"Content-Length: {len(jpeg)}\r\n"
                    f"\r\n"
                ).encode("utf-8")

                yield part_header
                yield jpeg
                yield b"\r\n"

        except GeneratorExit:
            return
        except Exception:
            return
        finally:
            MJPEG_CLIENTS.labels(OBS_SERVICE, OBS_INSTANCE).dec()

    return gen()


def zmq_receiver():
    global latest_jpeg, latest_meta, latest_frame_seq

    ctx = zmq.Context.instance()
    sub = ctx.socket(zmq.SUB)
    sub.setsockopt(zmq.LINGER, 0)
    sub.connect(SUB_ENDPOINT)
    sub.setsockopt(zmq.SUBSCRIBE, SUB_TOPIC)

    # Real-time: keep queues tiny (prefer drop over latency growth)
    sub.setsockopt(zmq.RCVHWM, 2)

    print(f"[web_server] Subscribed to {SUB_ENDPOINT} topic={SUB_TOPIC_STR}")
    print(f"[web_server] HTTP bound to http://{HTTP_HOST}:{HTTP_PORT}/ (threads={HTTP_THREADS})")
    print(f"[web_server] Metrics on :{METRICS_PORT}/metrics")

    while True:
        try:
            t_recv = time.time()
            topic, header_b, jpeg_in = sub.recv_multipart()
            STAGE_SECONDS.labels(OBS_SERVICE, OBS_INSTANCE, "web_recv_zmq").observe(time.time() - t_recv)

            FRAMES_IN.labels(OBS_SERVICE, OBS_INSTANCE, SUB_TOPIC_STR).inc()
            BYTES_IN.labels(OBS_SERVICE, OBS_INSTANCE, SUB_TOPIC_STR).inc(len(jpeg_in))
            JPEG_BYTES.labels(OBS_SERVICE, OBS_INSTANCE, "in").observe(len(jpeg_in))

            t_hdr = time.time()
            header = json.loads(header_b.decode("utf-8"))
            STAGE_SECONDS.labels(OBS_SERVICE, OBS_INSTANCE, "web_parse_header").observe(time.time() - t_hdr)

            # End-to-end latency: capture (Node A) -> ingest here (Node C)
            ts_cap = header.get("ts_capture") or header.get("ts")
            if ts_cap is not None:
                try:
                    E2E_SECONDS.labels(OBS_SERVICE, OBS_INSTANCE).observe(time.time() - float(ts_cap))
                except Exception:
                    pass

            with frame_cond:
                latest_jpeg = jpeg_in
                latest_meta = {
                    "frame_id": header.get("frame_id"),
                    "w": header.get("w"),
                    "h": header.get("h"),
                    "mode": header.get("mode"),
                    "processed": header.get("processed"),
                    "ts_capture": header.get("ts_capture") or header.get("ts"),
                    "ts_processed": header.get("ts_processed"),
                }
                latest_frame_seq += 1
                frame_cond.notify_all()

        except Exception as e:
            ERRORS.labels(OBS_SERVICE, OBS_INSTANCE, "web_zmq_receiver", type(e).__name__).inc()
            # Keep the server alive even if one frame is malformed
            continue


def main():
    # Start Prometheus metrics server
    obs_init(service="web_server", metrics_port=METRICS_PORT)

    t = threading.Thread(target=zmq_receiver, daemon=True)
    t.start()

    print(f"[web_server] Serving static from: {STATIC_DIR}")
    print(f"[web_server] Open UI:     http://{HTTP_HOST}:{HTTP_PORT}/")
    print(f"[web_server] MJPEG:       http://{HTTP_HOST}:{HTTP_PORT}/stream.mjpg")
    print(f"[web_server] ZMQ connect: {SUB_ENDPOINT} topic={SUB_TOPIC_STR}")
    print(f"[web_server] Waitress:    threads={HTTP_THREADS}")

    serve(app, host=HTTP_HOST, port=HTTP_PORT, threads=HTTP_THREADS)


if __name__ == "__main__":
    main()
