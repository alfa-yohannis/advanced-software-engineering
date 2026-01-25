# capturer/capturer.py
# ------------------------------------------------------------
# Capturer: reads video and publishes JPEG frames to ZMQ broker
# ------------------------------------------------------------

## Run locally (from project root)
# BROKER_HOST=127.0.0.1 \
# PUB_PORT=5555 \
# TOPIC=raw \
# VIDEO_PATH=input.mp4 \
# PUBLISH_EVERY_SEC=0.1 \
# JPEG_QUALITY=80 \
# LOOP=true \
# FALLBACK_FPS=25 \
# METRICS_PORT=9101 \
# SERVICE=capturer \
# python capturer/capturer.py
#
# (recommended alternative)
# python -m capturer.capturer

## Run with Docker (named container)
# docker run --rm --name capturer \
#   -e BROKER_HOST=broker \
#   -e PUB_PORT=5555 \
#   -e TOPIC=raw \
#   -e VIDEO_PATH=/data/input.mp4 \
#   -e PUBLISH_EVERY_SEC=0.1 \
#   -e JPEG_QUALITY=80 \
#   -e LOOP=true \
#   -e FALLBACK_FPS=25 \
#   -e METRICS_PORT=9101 \
#   -e SERVICE=capturer \
#   -p 9101:9101 \
#   -v "$PWD:/data:ro" \
#   capturer

import json
import os
import sys
import time
from pathlib import Path

import cv2
import zmq

# ------------------------------------------------------------
# Ensure project root is on PYTHONPATH
# ------------------------------------------------------------
THIS_FILE = Path(__file__).resolve()
PROJECT_ROOT = THIS_FILE.parents[1]  # parent of capturer/
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from shared.observability import (  # noqa: E402
    init as obs_init,
    SERVICE as OBS_SERVICE,
    INSTANCE as OBS_INSTANCE,
    FRAMES_OUT,
    BYTES_OUT,
    JPEG_BYTES,
    STAGE_SECONDS,
    ERRORS,
)


def getenv_str(name: str, default: str) -> str:
    return os.getenv(name, default)


def getenv_int(name: str, default: int) -> int:
    try:
        return int(os.getenv(name, str(default)))
    except ValueError:
        return default


def getenv_float(name: str, default: float) -> float:
    try:
        return float(os.getenv(name, str(default)))
    except ValueError:
        return default


def getenv_bool(name: str, default: bool) -> bool:
    v = os.getenv(name)
    if v is None:
        return default
    return v.strip().lower() in ("1", "true", "yes", "y", "on")


def validate_port(port: int, name: str) -> int:
    if not (1 <= port <= 65535):
        raise ValueError(f"{name} must be in [1..65535], got {port}")
    return port


# -----------------------------
# Configuration
# -----------------------------
BROKER_HOST = getenv_str("BROKER_HOST", "broker")
PUB_PORT = validate_port(getenv_int("PUB_PORT", 5555), "PUB_PORT")
TOPIC = getenv_str("TOPIC", "raw").encode("utf-8")

VIDEO_PATH = getenv_str("VIDEO_PATH", "input.mp4")
PUBLISH_EVERY_SEC = getenv_float("PUBLISH_EVERY_SEC", 2.0)
JPEG_QUALITY = getenv_int("JPEG_QUALITY", 80)
LOOP = getenv_bool("LOOP", True)
FALLBACK_FPS = getenv_float("FALLBACK_FPS", 25.0)

METRICS_PORT = validate_port(getenv_int("METRICS_PORT", 9101), "METRICS_PORT")

PUB_ENDPOINT = f"tcp://{BROKER_HOST}:{PUB_PORT}"


def main() -> int:
    # Start Prometheus metrics
    obs_init(service="capturer", metrics_port=METRICS_PORT)

    ctx = zmq.Context.instance()
    pub = ctx.socket(zmq.PUB)
    pub.setsockopt(zmq.LINGER, 0)
    pub.connect(PUB_ENDPOINT)

    cap = cv2.VideoCapture(VIDEO_PATH)
    if not cap.isOpened():
        ERRORS.labels(OBS_SERVICE, OBS_INSTANCE, "capturer", "video_open_fail").inc()
        raise RuntimeError(f"Cannot open {VIDEO_PATH}")

    video_fps = cap.get(cv2.CAP_PROP_FPS)
    if not video_fps or video_fps <= 0:
        video_fps = FALLBACK_FPS

    period = 1.0 / video_fps
    encode_params = [cv2.IMWRITE_JPEG_QUALITY, int(JPEG_QUALITY)]

    frame_id = 0
    next_publish_time = time.time()
    time.sleep(0.5)  # PUB/SUB slow joiner

    topic_str = TOPIC.decode("utf-8", "ignore")

    print(f"[capturer] Video={VIDEO_PATH} FPS≈{video_fps:.2f}")
    print(f"[capturer] Publish every {PUBLISH_EVERY_SEC:.3f}s → {PUB_ENDPOINT} topic={topic_str}")
    print(f"[capturer] Metrics on :{METRICS_PORT}/metrics")

    try:
        while True:
            t_loop = time.time()

            ok, frame = cap.read()
            if not ok:
                if LOOP:
                    cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
                    continue
                print("[capturer] End of video")
                break

            now = time.time()
            if now >= next_publish_time:
                h, w = frame.shape[:2]

                t_enc = time.time()
                ok2, jpg = cv2.imencode(".jpg", frame, encode_params)
                STAGE_SECONDS.labels(
                    OBS_SERVICE, OBS_INSTANCE, "capturer_encode_jpeg"
                ).observe(time.time() - t_enc)

                if ok2:
                    payload = jpg.tobytes()
                    JPEG_BYTES.labels(OBS_SERVICE, OBS_INSTANCE, "out").observe(len(payload))

                    header = {
                        "frame_id": frame_id,
                        "ts_capture": now,
                        "encoding": "jpeg",
                        "mode": "BGR",
                        "w": int(w),
                        "h": int(h),
                        "src": "video",
                        "video": VIDEO_PATH,
                        "video_fps": float(video_fps),
                        "publish_every_sec": float(PUBLISH_EVERY_SEC),
                        "jpeg_quality": int(JPEG_QUALITY),
                    }

                    pub.send_multipart([TOPIC, json.dumps(header).encode("utf-8"), payload])

                    FRAMES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, topic_str).inc()
                    BYTES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, topic_str).inc(len(payload))
                    frame_id += 1
                else:
                    ERRORS.labels(OBS_SERVICE, OBS_INSTANCE, "capturer", "imencode_fail").inc()

                next_publish_time += PUBLISH_EVERY_SEC
                if now - next_publish_time > PUBLISH_EVERY_SEC * 5:
                    next_publish_time = now + PUBLISH_EVERY_SEC

            elapsed = time.time() - t_loop
            if elapsed < period:
                time.sleep(period - elapsed)

    except KeyboardInterrupt:
        print("[capturer] Stopped")
    finally:
        cap.release()
        pub.close()
        ctx.term()

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
