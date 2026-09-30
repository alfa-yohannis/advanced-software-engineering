FROM python:3.12-slim

# ---- system deps (needed for OpenCV even in headless mode)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libglib2.0-0 \
    libgl1 \
 && rm -rf /var/lib/apt/lists/*

# ---- python deps (capturer needs OpenCV + metrics + encryption)
RUN pip install --no-cache-dir \
    pyzmq \
    opencv-python-headless \
    prometheus-client \
    psutil \
    cryptography

# ---- app layout
WORKDIR /app

# copy shared module and capturer code
COPY shared/ ./shared/
COPY capturer/ ./capturer/

# ---- runtime
ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app
# docker stop sends SIGINT, which Python turns into KeyboardInterrupt, so the node
# exits cleanly. As PID 1 it ignores SIGTERM and Docker would kill it after 10 s.
STOPSIGNAL SIGINT

CMD ["python", "-m", "capturer.capturer"]
