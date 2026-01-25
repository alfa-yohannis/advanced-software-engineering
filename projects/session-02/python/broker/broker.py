# broker/broker.py

## Run locally with custom parameters (from project root)
# METRICS_PORT=9102 \
# SERVICE=broker \
# BROKER_BIND_HOST=127.0.0.1 \
# PUB_PORT=5555 \
# SUB_PORT=5556 \
# python -m broker.broker
#
# Or (if you insist running the file directly):
# python broker/broker.py

## Run with Docker (named container)
# docker run --rm --name zmq-broker \
#   -p 5555:5555 \
#   -p 5556:5556 \
#   -p 9102:9102 \
#   -e BROKER_BIND_HOST=0.0.0.0 \
#   -e PUB_PORT=5555 \
#   -e SUB_PORT=5556 \
#   -e METRICS_PORT=9102 \
#   -e SERVICE=broker \
#   broker

import os
import sys
import time
from pathlib import Path

import zmq

# ------------------------------------------------------------
# Make project root importable so "shared.observability" works
# ------------------------------------------------------------
THIS_FILE = Path(__file__).resolve()
PROJECT_ROOT = THIS_FILE.parents[1]  # .../python (parent of broker/)
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

# Shared observability module (saved as shared/observability.py)
from shared.observability import (  # noqa: E402
    init as obs_init,
    SERVICE as OBS_SERVICE,
    INSTANCE as OBS_INSTANCE,
    FRAMES_IN,
    FRAMES_OUT,
    BYTES_IN,
    BYTES_OUT,
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
# Docker-friendly parameters
# -----------------------------
BROKER_BIND_HOST = getenv_str("BROKER_BIND_HOST", "0.0.0.0")
PUB_PORT = validate_port(getenv_int("PUB_PORT", 5555), "PUB_PORT")  # publishers connect here
SUB_PORT = validate_port(getenv_int("SUB_PORT", 5556), "SUB_PORT")  # subscribers connect here

# Optional: log subscribe/unsubscribe frames (1=yes, 0=no)
XPUB_VERBOSE = getenv_int("XPUB_VERBOSE", 0)

# Prometheus metrics
METRICS_PORT = validate_port(getenv_int("METRICS_PORT", 9102), "METRICS_PORT")

PUB_BIND = f"tcp://{BROKER_BIND_HOST}:{PUB_PORT}"
SUB_BIND = f"tcp://{BROKER_BIND_HOST}:{SUB_PORT}"


def main() -> int:
    # Start metrics endpoint
    obs_init(service="broker", metrics_port=METRICS_PORT)

    ctx = zmq.Context.instance()

    xsub = ctx.socket(zmq.XSUB)
    xsub.setsockopt(zmq.LINGER, 0)
    xsub.bind(PUB_BIND)

    xpub = ctx.socket(zmq.XPUB)
    xpub.setsockopt(zmq.LINGER, 0)
    if XPUB_VERBOSE:
        xpub.setsockopt(zmq.XPUB_VERBOSE, 1)
    xpub.bind(SUB_BIND)

    print("Broker running:")
    print(f"  PUB -> {PUB_BIND}  (publishers connect here)")
    print(f"  SUB -> {SUB_BIND}  (subscribers connect here)")
    print(f"  METRICS -> 0.0.0.0:{METRICS_PORT}/metrics")
    if XPUB_VERBOSE:
        print("  XPUB_VERBOSE=1 (subscription logging enabled)")

    poller = zmq.Poller()
    poller.register(xsub, zmq.POLLIN)
    poller.register(xpub, zmq.POLLIN)

    TOPIC_XSUB = "xsub_in"
    TOPIC_XPUB = "xpub_out"
    TOPIC_SUBS = "subs"

    try:
        while True:
            events = dict(poller.poll(250))

            # Publisher -> Subscriber (data frames)
            if xsub in events:
                t0 = time.time()
                msg = xsub.recv_multipart()
                STAGE_SECONDS.labels(OBS_SERVICE, OBS_INSTANCE, "broker_recv_xsub").observe(time.time() - t0)

                total_bytes = sum(len(p) for p in msg) if msg else 0
                FRAMES_IN.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_XSUB).inc()
                BYTES_IN.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_XSUB).inc(total_bytes)

                t1 = time.time()
                xpub.send_multipart(msg)
                STAGE_SECONDS.labels(OBS_SERVICE, OBS_INSTANCE, "broker_send_xpub").observe(time.time() - t1)

                FRAMES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_XPUB).inc()
                BYTES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_XPUB).inc(total_bytes)

            # Subscriber subscription frames -> Publisher side (topic subscriptions)
            if xpub in events:
                t2 = time.time()
                sub_msg = xpub.recv_multipart()
                STAGE_SECONDS.labels(OBS_SERVICE, OBS_INSTANCE, "broker_recv_xpub").observe(time.time() - t2)

                sub_bytes = sum(len(p) for p in sub_msg) if sub_msg else 0
                FRAMES_IN.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_SUBS).inc()
                BYTES_IN.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_SUBS).inc(sub_bytes)

                t3 = time.time()
                xsub.send_multipart(sub_msg)
                STAGE_SECONDS.labels(OBS_SERVICE, OBS_INSTANCE, "broker_send_xsub").observe(time.time() - t3)

                FRAMES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_SUBS).inc()
                BYTES_OUT.labels(OBS_SERVICE, OBS_INSTANCE, TOPIC_SUBS).inc(sub_bytes)

                if XPUB_VERBOSE and sub_msg and sub_msg[0] and len(sub_msg[0]) >= 1:
                    action = "SUB" if sub_msg[0][0] == 1 else "UNSUB"
                    topic = sub_msg[0][1:].decode("utf-8", errors="replace")
                    print(f"[{action}] topic='{topic}'")

    except KeyboardInterrupt:
        print("Broker stopping (KeyboardInterrupt).")
        return 0
    except Exception as e:
        ERRORS.labels(OBS_SERVICE, OBS_INSTANCE, "broker_main", type(e).__name__).inc()
        raise
    finally:
        try:
            xsub.close()
        except Exception:
            pass
        try:
            xpub.close()
        except Exception:
            pass
        try:
            ctx.term()
        except Exception:
            pass

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
