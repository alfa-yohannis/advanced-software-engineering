# node_b.py
import time, json, io
import zmq
import numpy as np
from PIL import Image

BROKER_IP = "127.0.0.1"  # set to broker machine IP
SUB_ENDPOINT = f"tcp://{BROKER_IP}:5556"
PUB_ENDPOINT = f"tcp://{BROKER_IP}:5555"

SUB_TOPIC = b"raw"
PUB_TOPIC = b"processed"

def jpeg_bytes_to_pil(b: bytes) -> Image.Image:
    return Image.open(io.BytesIO(b))

def pil_to_jpeg_bytes(img: Image.Image, quality: int = 85) -> bytes:
    buf = io.BytesIO()
    img.save(buf, format="JPEG", quality=quality, optimize=True)
    return buf.getvalue()

def to_grayscale(img_rgb: Image.Image) -> Image.Image:
    # Convert to grayscale (L) then back to RGB if you want consistent display pipelines
    # Here we keep it as 'L' (single channel).
    return img_rgb.convert("L")

def main(jpeg_quality_out: int = 85):
    ctx = zmq.Context.instance()

    sub = ctx.socket(zmq.SUB)
    sub.connect(SUB_ENDPOINT)
    sub.setsockopt(zmq.SUBSCRIBE, SUB_TOPIC)

    pub = ctx.socket(zmq.PUB)
    pub.connect(PUB_ENDPOINT)

    time.sleep(0.5)  # let pub connect
    print(f"Node B subscribed to {SUB_ENDPOINT} topic={SUB_TOPIC!r}")
    print(f"Node B publishing to {PUB_ENDPOINT} topic={PUB_TOPIC!r}")

    while True:
        topic, header_b, jpeg_in = sub.recv_multipart()
        header = json.loads(header_b.decode("utf-8"))

        img = jpeg_bytes_to_pil(jpeg_in).convert("RGB")
        gray = to_grayscale(img)

        jpeg_out = pil_to_jpeg_bytes(gray, quality=jpeg_quality_out)

        out_header = {
            **header,
            "processed": "grayscale",
            "mode": gray.mode,      # "L"
            "w": gray.width,
            "h": gray.height,
            "ts_processed": time.time(),
        }

        pub.send_multipart([PUB_TOPIC, json.dumps(out_header).encode("utf-8"), jpeg_out])

if __name__ == "__main__":
    main(jpeg_quality_out=85)
