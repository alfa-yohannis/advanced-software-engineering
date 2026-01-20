# broker.py

## Run locally with custom parameters
# BROKER_BIND_HOST=127.0.0.1 \
# PUB_PORT=5555 \
# SUB_PORT=5556 \
# python broker.py


## Run with Docker
# docker run --rm \
#   -p 5555:5555 \
#   -p 5556:5556 \
#   -e BROKER_BIND_HOST=0.0.0.0 \
#   -e PUB_PORT=5555 \
#   -e SUB_PORT=5556 \
#   broker

import os
import zmq


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
BROKER_BIND_HOST = getenv_str("BROKER_BIND_HOST", "0.0.0.0")
PUB_PORT = getenv_int("PUB_PORT", 5555)  # publishers connect here
SUB_PORT = getenv_int("SUB_PORT", 5556)  # subscribers connect here

PUB_BIND = f"tcp://{BROKER_BIND_HOST}:{PUB_PORT}"
SUB_BIND = f"tcp://{BROKER_BIND_HOST}:{SUB_PORT}"


def main():
    ctx = zmq.Context.instance()

    # Publishers connect here
    xsub = ctx.socket(zmq.XSUB)
    xsub.bind(PUB_BIND)

    # Subscribers connect here
    xpub = ctx.socket(zmq.XPUB)
    xpub.bind(SUB_BIND)

    print("Broker running:")
    print(f"  PUB -> {PUB_BIND}  (publishers connect here)")
    print(f"  SUB -> {SUB_BIND}  (subscribers connect here)")

    zmq.proxy(xsub, xpub)


if __name__ == "__main__":
    main()
