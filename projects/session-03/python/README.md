# ZeroMQ Video Pipeline — Run Guide

This document explains how to run the 4-node pipeline:

- broker.py
- capturer.py (Node A)
- transformer.py (Node B)
- web_server.py (Node C)

in three ways:
1. Manually with Python (virtual environment)
2. With Docker
3. With Docker Compose (recommended)

All configuration is done via environment variables.

----------------------------------------------------------------------
**1. Run Manually with Python (Virtual Environment)**
----------------------------------------------------------------------

**1.1 Prerequisites**

- Python 3.12.3
- pip
- venv
- OS packages for OpenCV (Linux):
```bash
sudo apt install -y libgl1 libglib2.0-0
```

----------------------------------------------------------------------
1.2 Create and Activate Virtual Environment
----------------------------------------------------------------------

Create a virtual environment using Python 3.12:
```bash
python3 -m venv .venv
```

Activate the virtual environment:
```bash
source .venv/bin/activate
```
----------------------------------------------------------------------
1.3 Install Dependencies
----------------------------------------------------------------------
```bash
pip install \
  pyzmq \
  opencv-python-headless \
  pillow \
  bottle \
  waitress
```
----------------------------------------------------------------------
1.4 Run Each Node (use 4 terminals)
----------------------------------------------------------------------

Terminal 1 — Broker
```bash
BROKER_BIND_HOST=127.0.0.1 \
PUB_PORT=5555 \
SUB_PORT=5556 \
python broker.py
```
----------------------------------------------------------------------
Terminal 2 — Capturer (Node A)
```bash
BROKER_HOST=127.0.0.1 \
PUB_PORT=5555 \
TOPIC=raw \
VIDEO_PATH=input.mp4 \
PUBLISH_EVERY_SEC=0.1 \
JPEG_QUALITY=80 \
LOOP=true \
python capturer.py
```
----------------------------------------------------------------------
Terminal 3 — Transformer (Node B)
```bash
BROKER_HOST=127.0.0.1 \
SUB_PORT=5556 \
PUB_PORT=5555 \
SUB_TOPIC=raw \
PUB_TOPIC=processed \
JPEG_QUALITY_OUT=85 \
python transformer.py
```
----------------------------------------------------------------------
Terminal 4 — Web Server (Node C)
```bash
BROKER_HOST=127.0.0.1 \
SUB_PORT=5556 \
SUB_TOPIC=processed \
HTTP_HOST=0.0.0.0 \
HTTP_PORT=8000 \
python web_server.py
```
Open browser:

http://localhost:8000/
http://localhost:8000/stream.mjpg

----------------------------------------------------------------------
**2. Run with Docker (Individual Containers)**
----------------------------------------------------------------------

**2.1 Build Images**

```bash
sudo docker build -t broker ./broker
sudo docker build -t capturer ./capturer
sudo docker build -t transformer ./transformer
sudo docker build -t web_server ./web_server
```
----------------------------------------------------------------------

**2.2 Run Containers**

Broker
```bash
sudo docker run --rm \
  -p 5555:5555 \
  -p 5556:5556 \
  -e BROKER_BIND_HOST=0.0.0.0 \
  -e PUB_PORT=5555 \
  -e SUB_PORT=5556 \
  broker
```
----------------------------------------------------------------------
Capturer
```bash
sudo docker run --rm \
  -e BROKER_HOST=broker \
  -e PUB_PORT=5555 \
  -e TOPIC=raw \
  -e VIDEO_PATH=/data/input.mp4 \
  -e PUBLISH_EVERY_SEC=0.1 \
  -e JPEG_QUALITY=80 \
  -e LOOP=true \
  -v "$PWD/input.mp4:/data/input.mp4:ro" \
  capturer
```
----------------------------------------------------------------------
Transformer
```bash
sudo docker run --rm \
  -e BROKER_HOST=broker \
  -e SUB_PORT=5556 \
  -e PUB_PORT=5555 \
  -e SUB_TOPIC=raw \
  -e PUB_TOPIC=processed \
  -e JPEG_QUALITY_OUT=85 \
  transformer
```
----------------------------------------------------------------------
Web Server
```bash
sudo docker run --rm \
  -p 8000:8000 \
  -e BROKER_HOST=broker \
  -e SUB_PORT=5556 \
  -e SUB_TOPIC=processed \
  -e HTTP_HOST=0.0.0.0 \
  -e HTTP_PORT=8000 \
  web_server
```
----------------------------------------------------------------------
**3. Run with Docker Compose (Recommended)**
----------------------------------------------------------------------

Start everything:
```bash
sudo docker compose up --build
```
Stop everything:
```bash
sudo docker compose down
```
----------------------------------------------------------------------
**4. Notes and Best Practices**
----------------------------------------------------------------------

- Always use service names such as broker inside Docker
- Never use 127.0.0.1 between containers
- Node B does not expose ports
- Node C exposes HTTP only
- Configuration is environment-variable driven (12-factor)

----------------------------------------------------------------------
**5. Quick Health Checklist**
----------------------------------------------------------------------

- Broker logs show ports bound
- Capturer publishes frames
- Transformer logs FPS
- Web server shows live MJPEG stream

----------------------------------------------------------------------

