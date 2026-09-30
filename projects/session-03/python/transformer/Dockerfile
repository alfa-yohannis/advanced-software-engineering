FROM python:3.12-slim

# ---- system deps (minimal, safe)
RUN apt-get update && apt-get install -y \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# ---- python deps (transformer only)
RUN pip install --no-cache-dir \
    pyzmq \
    pillow \
    prometheus-client \
    psutil \
    cryptography

# ---- app layout
WORKDIR /app

# copy shared module and transformer code
COPY shared/ ./shared/
COPY transformer/ ./transformer/

# ---- runtime
ENV PYTHONUNBUFFERED=1
# docker stop sends SIGINT, which Python turns into KeyboardInterrupt, so the node
# exits cleanly. As PID 1 it ignores SIGTERM and Docker would kill it after 10 s.
STOPSIGNAL SIGINT

CMD ["python", "transformer/transformer.py"]
