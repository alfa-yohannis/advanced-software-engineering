FROM python:3.12.3-slim

RUN apt-get update && apt-get install -y \
    libgl1 libglib2.0-0 \
 && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir pyzmq opencv-python-headless

WORKDIR /app
COPY capturer.py .

CMD ["python", "capturer.py"]
