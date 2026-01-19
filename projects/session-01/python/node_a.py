# node_a.py
import time, json
import zmq
from PIL import Image
import io

BROKER_IP = "127.0.0.1"   # set to broker machine IP
PUB_ENDPOINT = f"tcp://{BROKER_IP}:5555"

TOPIC = b"raw"

def pil_to_jpeg_bytes(img: Image.Image, quality: int = 85) -> bytes:
    buf = io.BytesIO()
    img.save(buf, format="JPEG", quality=quality, optimize=True)
    return buf.getvalue()

def main(image_path: str = "input.jpg", fps: float = 10.0, jpeg_quality: int = 85):
    ctx = zmq.Context.instance()
    pub = ctx.socket(zmq.PUB)
    pub.connect(PUB_ENDPOINT)

    img = Image.open(image_path).convert("RGB")  # keep RGB for input
    period = 1.0 / max(fps, 0.1)

    frame_id = 0
    time.sleep(0.5)  # allow SUBs to connect (PUB/SUB slow-joiner)

    print(f"Node A publishing {image_path} at ~{fps} FPS to {PUB_ENDPOINT}")
    while True:
        ts = time.time()
        jpeg = pil_to_jpeg_bytes(img, quality=jpeg_quality)

        header = {
            "frame_id": frame_id,
            "ts": ts,
            "encoding": "jpeg",
            "mode": "RGB",
            "w": img.width,
            "h": img.height,
        }
        pub.send_multipart([TOPIC, json.dumps(header).encode("utf-8"), jpeg])

        frame_id += 1
        # crude fps control
        elapsed = time.time() - ts
        if elapsed < period:
            time.sleep(period - elapsed)

if __name__ == "__main__":
    # usage: python node_a.py
    main(image_path="input.jpg", fps=10.0, jpeg_quality=85)
