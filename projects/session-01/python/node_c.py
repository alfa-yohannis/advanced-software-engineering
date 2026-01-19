# node_c_bottle.py
import json
import threading
import time
from pathlib import Path

import zmq
from bottle import Bottle, static_file, response, HTTPResponse, run

BROKER_IP = "127.0.0.1"
SUB_ENDPOINT = f"tcp://{BROKER_IP}:5556"
SUB_TOPIC = b"processed"

HOST = "0.0.0.0"
PORT = 8000

STATIC_DIR = Path(__file__).parent / "static"
INDEX_NAME = "index.html"

latest_jpeg = None
latest_meta = {}
lock = threading.Lock()

app = Bottle()

@app.get("/")
def index():
    # serve separate HTML file
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

def zmq_receiver():
    global latest_jpeg, latest_meta

    ctx = zmq.Context.instance()
    sub = ctx.socket(zmq.SUB)
    sub.connect(SUB_ENDPOINT)
    sub.setsockopt(zmq.SUBSCRIBE, SUB_TOPIC)

    # Real-time: keep queues tiny (prefer drop over latency growth)
    sub.setsockopt(zmq.RCVHWM, 2)

    print(f"Node C (Bottle) subscribed to {SUB_ENDPOINT} topic={SUB_TOPIC!r}")

    last_log = time.time()
    frames = 0

    while True:
        topic, header_b, jpeg_in = sub.recv_multipart()
        header = json.loads(header_b.decode("utf-8"))

        with lock:
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

        frames += 1
        now = time.time()
        if now - last_log >= 1.0:
            fps = frames / (now - last_log)
            print(f"FPS≈{fps:.1f} last_frame={header.get('frame_id')} size={header.get('w')}x{header.get('h')} mode={header.get('mode')}")
            frames = 0
            last_log = now

def main():
    t = threading.Thread(target=zmq_receiver, daemon=True)
    t.start()

    print(f"HTTP: http://{HOST}:{PORT}/  (open in browser)")
    print(f"Serving static from: {STATIC_DIR}")
    run(app, host=HOST, port=PORT, quiet=True)

if __name__ == "__main__":
    main()
