# Captor
## Run locally with custom parameters
# BROKER_HOST=127.0.0.1 \
# PUB_PORT=5555 \
# TOPIC=raw \
# VIDEO_PATH=input.mp4 \
# PUBLISH_EVERY_SEC=0.1 \
# JPEG_QUALITY=80 \
# LOOP=true \
# FALLBACK_FPS=25 \
# python node_a.py


## Run with Docker
# docker run --rm \
#   -e BROKER_HOST=broker \
#   -e PUB_PORT=5555 \
#   -e TOPIC=raw \
#   -e VIDEO_PATH=/data/input.mp4 \
#   -e PUBLISH_EVERY_SEC=0.1 \
#   -e JPEG_QUALITY=80 \
#   -e LOOP=true \
#   -e FALLBACK_FPS=25 \
#   -v "$PWD:/data:ro" \
#   node-a

import json
import os
import time

import cv2
import zmq


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
    v = v.strip().lower()
    return v in ("1", "true", "yes", "y", "on")


# -----------------------------
# Docker-friendly parameters
# -----------------------------
BROKER_HOST = getenv_str("BROKER_HOST", "broker")
PUB_PORT = getenv_int("PUB_PORT", 5555)
TOPIC = getenv_str("TOPIC", "raw").encode("utf-8")

VIDEO_PATH = getenv_str("VIDEO_PATH", "input.mp4")
PUBLISH_EVERY_SEC = getenv_float("PUBLISH_EVERY_SEC", 2.0)
JPEG_QUALITY = getenv_int("JPEG_QUALITY", 80)
LOOP = getenv_bool("LOOP", True)

# Optional: if video reports 0 FPS, fall back here
FALLBACK_FPS = getenv_float("FALLBACK_FPS", 25.0)

PUB_ENDPOINT = f"tcp://{BROKER_HOST}:{PUB_PORT}"


def main():
    ctx = zmq.Context.instance()
    pub = ctx.socket(zmq.PUB)
    pub.connect(PUB_ENDPOINT)

    cap = cv2.VideoCapture(VIDEO_PATH)
    if not cap.isOpened():
        raise RuntimeError(f"Cannot open {VIDEO_PATH}")

    video_fps = cap.get(cv2.CAP_PROP_FPS)
    if not video_fps or video_fps <= 0:
        video_fps = FALLBACK_FPS

    period = 1.0 / float(video_fps)  # playback speed (normal)
    encode_params = [cv2.IMWRITE_JPEG_QUALITY, int(JPEG_QUALITY)]

    frame_id = 0
    next_publish_time = time.time()  # wall-clock schedule
    time.sleep(0.5)  # PUB/SUB slow joiner mitigation

    print(f"Playing {VIDEO_PATH} at ~{video_fps:.2f} FPS (fallback={FALLBACK_FPS})")
    print(f"Publishing 1 frame every {PUBLISH_EVERY_SEC:.3f} seconds to {PUB_ENDPOINT} topic={TOPIC!r}")
    print(f"JPEG_QUALITY={JPEG_QUALITY} LOOP={LOOP}")

    while True:
        t0 = time.time()

        ok, frame_bgr = cap.read()
        if not ok:
            if LOOP:
                cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
                continue
            else:
                print("End of video.")
                break

        # video plays normally because we keep reading frames at video FPS
        now = time.time()
        if now >= next_publish_time:
            h, w = frame_bgr.shape[:2]
            ok2, jpg = cv2.imencode(".jpg", frame_bgr, encode_params)
            if ok2:
                header = {
                    "frame_id": frame_id,
                    "ts": now,
                    "encoding": "jpeg",
                    "mode": "BGR",
                    "w": int(w),
                    "h": int(h),
                    "src": "video",
                    "video": VIDEO_PATH,
                    "video_fps": float(video_fps),
                    "publish_every_sec": float(PUBLISH_EVERY_SEC),
                }
                pub.send_multipart([TOPIC, json.dumps(header).encode("utf-8"), jpg.tobytes()])
                frame_id += 1

            # schedule next capture
            next_publish_time += PUBLISH_EVERY_SEC

            # if we fell behind (e.g., pause), jump forward to avoid burst publishes
            if now - next_publish_time > PUBLISH_EVERY_SEC * 5:
                next_publish_time = now + PUBLISH_EVERY_SEC

        # playback pacing (normal speed)
        elapsed = time.time() - t0
        if elapsed < period:
            time.sleep(period - elapsed)

    cap.release()


if __name__ == "__main__":
    main()
