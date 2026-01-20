# transformer.py

## Run locally with custom parameters
# BROKER_HOST=127.0.0.1 \
# SUB_PORT=5556 \
# PUB_PORT=5555 \
# SUB_TOPIC=raw \
# PUB_TOPIC=processed \
# JPEG_QUALITY_OUT=85 \
# python transformer.py


## Run with Docker
# docker run --rm \
#   -e BROKER_HOST=broker \
#   -e SUB_PORT=5556 \
#   -e PUB_PORT=5555 \
#   -e SUB_TOPIC=raw \
#   -e PUB_TOPIC=processed \
#   -e JPEG_QUALITY_OUT=85 \
#   transformer


import io
import json
import os
import time

import zmq
from PIL import Image


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

SUB_PORT = getenv_int("SUB_PORT", 5556)   # subscribe to raw frames from broker
PUB_PORT = getenv_int("PUB_PORT", 5555)   # publish processed frames to broker

SUB_TOPIC = getenv_str("SUB_TOPIC", "raw").encode("utf-8")
PUB_TOPIC = getenv_str("PUB_TOPIC", "processed").encode("utf-8")

JPEG_QUALITY_OUT = getenv_int("JPEG_QUALITY_OUT", 85)

SUB_ENDPOINT = f"tcp://{BROKER_HOST}:{SUB_PORT}"
PUB_ENDPOINT = f"tcp://{BROKER_HOST}:{PUB_PORT}"


def jpeg_bytes_to_pil(b: bytes) -> Image.Image:
    return Image.open(io.BytesIO(b))


def pil_to_jpeg_bytes(img: Image.Image, quality: int = 85) -> bytes:
    buf = io.BytesIO()
    img.save(buf, format="JPEG", quality=quality, optimize=True)
    return buf.getvalue()


def to_grayscale(img_rgb: Image.Image) -> Image.Image:
    # Convert to grayscale (L). Keeping 'L' is fine for MJPEG viewers.
    return img_rgb.convert("L")


def main():
    ctx = zmq.Context.instance()

    sub = ctx.socket(zmq.SUB)
    sub.connect(SUB_ENDPOINT)
    sub.setsockopt(zmq.SUBSCRIBE, SUB_TOPIC)

    pub = ctx.socket(zmq.PUB)
    pub.connect(PUB_ENDPOINT)

    time.sleep(0.5)  # let pub connect

    print(f"Node B subscribed to {SUB_ENDPOINT} topic={SUB_TOPIC!r}")
    print(f"Node B publishing to {PUB_ENDPOINT} topic={PUB_TOPIC!r}")
    print(f"JPEG_QUALITY_OUT={JPEG_QUALITY_OUT}")

    while True:
        topic, header_b, jpeg_in = sub.recv_multipart()
        header = json.loads(header_b.decode("utf-8"))

        img = jpeg_bytes_to_pil(jpeg_in).convert("RGB")
        gray = to_grayscale(img)

        jpeg_out = pil_to_jpeg_bytes(gray, quality=JPEG_QUALITY_OUT)

        out_header = {
            **header,
            "processed": "grayscale",
            "mode": gray.mode,  # "L"
            "w": gray.width,
            "h": gray.height,
            "ts_processed": time.time(),
        }

        pub.send_multipart([PUB_TOPIC, json.dumps(out_header).encode("utf-8"), jpeg_out])


if __name__ == "__main__":
    main()
