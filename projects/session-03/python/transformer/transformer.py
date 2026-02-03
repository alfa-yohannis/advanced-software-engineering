# transformer/transformer.py
# ------------------------------------------------------------
# Transformer: subscribes to raw frames, processes, republishes
# ------------------------------------------------------------

import io
import json
import os
import sys
import time
from pathlib import Path

import zmq
from PIL import Image

# ------------------------------------------------------------
# Ensure project root is on PYTHONPATH
# ------------------------------------------------------------
THIS_FILE = Path(__file__).resolve()
PROJECT_ROOT = THIS_FILE.parents[1]  # parent of transformer/
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from shared.crypto import decrypt_bytes, encrypt_bytes  # noqa: E402
from shared.observability import (  # noqa: E402
    init as obs_init,
    SERVICE as OBS_SERVICE,
    INSTANCE as OBS_INSTANCE,
    FRAMES_IN,
    FRAMES_OUT,
    BYTES_IN,
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


def validate_port(port: int, name: str) -> int:
    if not (1 <= port <= 65535):
        raise ValueError(f"{name} must be in [1..65535], got {port}")
    return port


# -----------------------------
# Configuration
# -----------------------------
BROKER_HOST = getenv_str("BROKER_HOST", "broker")

SUB_PORT = validate_port(getenv_int("SUB_PORT", 5556), "SUB_PORT")
PUB_PORT = validate_port(getenv_int("PUB_PORT", 5555), "PUB_PORT")

SUB_TOPIC = getenv_str("SUB_TOPIC", "raw").encode("utf-8")
PUB_TOPIC = getenv_str("PUB_TOPIC", "processed").encode("utf-8")

JPEG_QUALITY_OUT = getenv_int("JPEG_QUALITY_OUT", 85)

METRICS_PORT = validate_port(getenv_int("METRICS_PORT", 9103), "METRICS_PORT")

SUB_ENDPOINT = f"tcp://{BROKER_HOST}:{SUB_PORT}"
PUB_ENDPOINT = f"tcp://{BROKER_HOST}:{PUB_PORT}"


def jpeg_bytes_to_pil(b: bytes) -> Image.Image:
    return Image.open(io.BytesIO(b))


def pil_to_jpeg_bytes(img: Image.Image, quality: int = 85) -> bytes:
    buf = io.BytesIO()
    img.save(buf, format="JPEG", quality=quality, optimize=True)
    return buf.getvalue()


def to_grayscale(img_rgb: Image.Image) -> Image.Image:
    return img_rgb.convert("L")  # grayscale


def main() -> int:
    # Start Prometheus metrics
    obs_init(service="transformer", metrics_port=METRICS_PORT)

    ctx = zmq.Context.instance()

    sub = ctx.socket(zmq.SUB)
    sub.setsockopt(zmq.LINGER, 0)
    sub.connect(SUB_ENDPOINT)
    sub.setsockopt(zmq.SUBSCRIBE, SUB_TOPIC)

    pub = ctx.socket(zmq.PUB)
    pub.setsockopt(zmq.LINGER, 0)
    pub.connect(PUB_ENDPOINT)

    time.sleep(0.5)  # PUB/SUB slow joiner

    sub_topic_str = SUB_TOPIC.decode("utf-8", "ignore")
    pub_topic_str = PUB_TOPIC.decode("utf-8", "ignore")

    print(f"[transformer] Subscribed to {SUB_ENDPOINT} topic={sub_topic_str}")
    print(f"[transformer] Publishing to {PUB_ENDPOINT} topic={pub_topic_str}")
    print(f"[transformer] JPEG_QUALITY_OUT={JPEG_QUALITY_OUT}")
    print(f"[transformer] Metrics on :{METRICS_PORT}/metrics")

    try:
        while True:
            # -------------------------
            # Receive (encrypted parts)
            # -------------------------
            t_recv = time.time()
            topic, enc_header_b, enc_jpeg_in = sub.recv_multipart()
            STAGE_SECONDS.labels(
                OBS_SERVICE, OBS_INSTANCE, "transformer_recv"
            ).observe(time.time() - t_recv)

            FRAMES_IN.labels(OBS_SERVICE, OBS_INSTANCE, sub_topic_str).inc()
            wire_in_bytes = len(enc_header_b) + len(enc_jpeg_in)
            BYTES_IN.labels(OBS_SERVICE, OBS_INSTANCE, sub_topic_str).inc(wire_in_bytes)

            # -------------------------
            # Decrypt header + payload
            # -------------------------
            t_decsec = time.time()
            header_b = decrypt_bytes(enc_header_b)
            jpeg_in = decrypt_bytes(enc_jpeg_in)
            STAGE_SECONDS.labels(
                OBS_SERVICE, OBS_INSTANCE, "transformer_decrypt"
            ).observe(time.time() - t_decsec)

            JPEG_BYTES.labels(OBS_SERVICE, OBS_INSTANCE, "in").observe(len(jpeg_in))

            # -------------------------
            # Parse header
            # -------------------------
            t_hdr = time.time()
            header = json.loads(header_b.decode("utf-8"))
            STAGE_SECONDS.labels(
                OBS_SERVICE, OBS_INSTANCE, "transformer_parse_header"
            ).observe(time.time() - t_hdr)

            # -------------------------
            # Decode
            # -------------------------
            t_dec = time.time()
            img = jpeg_bytes_to_pil(jpeg_in).convert("RGB")
            STAGE_SECONDS.labels(
                OBS_SERVICE, OBS_INSTANCE, "transformer_decode"
            ).observe(time.time() - t_dec)

            # -------------------------
            # Transform
            # -------------------------
            t_tr = time.time()
            gray = to_grayscale(img)
            STAGE_SECONDS.labels(
                OBS_SERVICE, OBS_INSTANCE, "transformer_grayscale"
            ).observe(time.time() - t_tr)

            # -------------------------
            # Encode
            # -------------------------
            t_enc = time.time()
            jpeg_out = pil_to_jpeg_bytes(gray, quality=JPEG_QUALITY_OUT)
            STAGE_SECONDS.labels(
                OBS_SERVICE, OBS_INSTANCE, "transformer_encode_jpeg"
            ).observe(time.time() - t_enc)

            JPEG_BYTES.labels(OBS_SERVICE, OBS_INSTANCE, "out").observe(len(jpeg_out))

            # -------------------------
            # Publish (encrypt header + payload)
            # -------------------------
            out_header = {
                **header,
                "processed": "grayscale",
                "mode": gray.mode,  # "L"
                "w": gray.width,
                "h": gray.height,
                "ts_processed": time.time(),
            }

            out_header_b = json.dumps(out_header).encode("utf-8")

            t_encsec = time.time()
            enc_out_header = encrypt_bytes(out_header_b)
            enc_jpeg_out = encrypt_bytes(jpeg_out)
            STAGE_SECONDS.labels(
                OBS_SERVICE, OBS_INSTANCE, "transformer_encrypt"
            ).observe(time.time() - t_encsec)

            pub.send_multipart([PUB_TOPIC, enc_out_header, enc_jpeg_out])

            FRAMES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, pub_topic_str).inc()
            wire_out_bytes = len(enc_out_header) + len(enc_jpeg_out)
            BYTES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, pub_topic_str).inc(wire_out_bytes)

    except KeyboardInterrupt:
        print("[transformer] Stopped")
        return 0
    except Exception as e:
        ERRORS.labels(OBS_SERVICE, OBS_INSTANCE, "transformer_main", type(e).__name__).inc()
        raise
    finally:
        try:
            sub.close()
        except Exception:
            pass
        try:
            pub.close()
        except Exception:
            pass
        try:
            ctx.term()
        except Exception:
            pass

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
