# web_server.py

## Run locally with custom parameters
# BROKER_HOST=127.0.0.1 \
# SUB_PORT=5556 \
# SUB_TOPIC=processed \
# HTTP_HOST=0.0.0.0 \
# HTTP_PORT=9000 \
# HTTP_THREADS=8 \
# python web_server.py

## Run with Docker
# docker run --rm \
#   -p 8000:8000 \
#   -e BROKER_HOST=broker \
#   -e SUB_PORT=5556 \
#   -e SUB_TOPIC=processed \
#   -e HTTP_HOST=0.0.0.0 \
#   -e HTTP_PORT=8000 \
#   -e HTTP_THREADS=8 \
#   web_server

import json
import os
import threading
import time
from pathlib import Path

import zmq
from bottle import Bottle, static_file, response, HTTPResponse
from waitress import serve

def getenv_str(name: str, default: str) -> str:
    return os.getenv(name, default)

def getenv_int(name: str, default: int) -> int:
    try:
        return int(os.getenv(name, str(default)))
    except ValueError:
        return default

# -----------------------------
# Docker-friendly parameters
# -----------------------------
BROKER_HOST = getenv_str("BROKER_HOST", "broker")
SUB_PORT = getenv_int("SUB_PORT", 5556)
SUB_TOPIC = getenv_str("SUB_TOPIC", "processed").encode("utf-8")

HTTP_HOST = getenv_str("HTTP_HOST", "0.0.0.0")
HTTP_PORT = getenv_int("HTTP_PORT", 8000)

# Parallel request handling (multiple browser tabs / clients)
HTTP_THREADS = getenv_int("HTTP_THREADS", 8)

SUB_ENDPOINT = f"tcp://{BROKER_HOST}:{SUB_PORT}"

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

# Optional: keep the old single-frame endpoint (handy for testing)
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
    # Helps some reverse proxies (e.g., nginx) not buffer
    response.set_header("X-Accel-Buffering", "no")

    def gen():
        global latest_frame_seq
        last_sent_seq = -1

        try:
            while True:
                with frame_cond:
                    # Wait for a new frame (or wake up periodically)
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
            # client disconnected
            return
        except Exception:
            # don't crash server if one stream errors
            return

    return gen()

def zmq_receiver():
    global latest_jpeg, latest_meta, latest_frame_seq

    ctx = zmq.Context.instance()
    sub = ctx.socket(zmq.SUB)
    sub.connect(SUB_ENDPOINT)
    sub.setsockopt(zmq.SUBSCRIBE, SUB_TOPIC)

    # Real-time: keep queues tiny (prefer drop over latency growth)
    sub.setsockopt(zmq.RCVHWM, 2)

    print(f"Node C subscribed to {SUB_ENDPOINT} topic={SUB_TOPIC!r}")
    print(f"HTTP bound to http://{HTTP_HOST}:{HTTP_PORT}/ (threads={HTTP_THREADS})")

    last_log = time.time()
    frames = 0

    while True:
        topic, header_b, jpeg_in = sub.recv_multipart()
        header = json.loads(header_b.decode("utf-8"))

        with frame_cond:
            latest_jpeg = jpeg_in
            latest_meta = {
                "frame_id": header.get("frame_id"),
                "w": header.get("w"),
                "h": header.get("h"),
                "mode": header.get("mode"),
                "processed": header.get("processed"),
                "ts": header.get("ts"),
                "ts_processed": header.get("ts_processed"),
            }
            latest_frame_seq += 1
            frame_cond.notify_all()

        frames += 1
        now = time.time()
        if now - last_log >= 1.0:
            fps = frames / (now - last_log)
            print(
                f"FPS≈{fps:.1f} last_frame={header.get('frame_id')} "
                f"size={header.get('w')}x{header.get('h')} mode={header.get('mode')}"
            )
            frames = 0
            last_log = now

def main():
    t = threading.Thread(target=zmq_receiver, daemon=True)
    t.start()

    print(f"Serving static from: {STATIC_DIR}")
    print(f"Open UI:     http://{HTTP_HOST}:{HTTP_PORT}/")
    print(f"MJPEG:       http://{HTTP_HOST}:{HTTP_PORT}/stream.mjpg")
    print(f"ZMQ connect: {SUB_ENDPOINT} topic={SUB_TOPIC.decode('utf-8', 'ignore')}")
    print(f"Waitress:    threads={HTTP_THREADS}")

    serve(app, host=HTTP_HOST, port=HTTP_PORT, threads=HTTP_THREADS)

if __name__ == "__main__":
    main()
