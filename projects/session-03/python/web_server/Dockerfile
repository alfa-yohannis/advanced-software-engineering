FROM python:3.12-slim

# ---- system deps (minimal, safe)
RUN apt-get update && apt-get install -y \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# ---- python deps (web server only)
RUN pip install --no-cache-dir \
    pyzmq \
    bottle \
    waitress \
    prometheus-client \
    psutil \
    cryptography

# ---- app layout
WORKDIR /app

# copy shared module and web server code + static assets
COPY shared/ ./shared/
COPY web_server/ ./web_server/

# ---- runtime
ENV PYTHONUNBUFFERED=1
# docker stop sends SIGINT, which Python turns into KeyboardInterrupt, so the node
# exits cleanly. As PID 1 it ignores SIGTERM and Docker would kill it after 10 s.
STOPSIGNAL SIGINT

CMD ["python", "web_server/web_server.py"]
