FROM python:3.12-slim

# ---- system deps (small, safe defaults)
RUN apt-get update && apt-get install -y \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# ---- python deps (broker only)
RUN pip install --no-cache-dir \
    pyzmq \
    prometheus-client \
    psutil

# ---- app layout
WORKDIR /app

# copy shared module and broker code
COPY shared/ ./shared/
COPY broker/ ./broker/

# ---- runtime
ENV PYTHONUNBUFFERED=1

CMD ["python", "broker/broker.py"]
