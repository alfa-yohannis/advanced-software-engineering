# Dockerfile (services/api/Dockerfile)
FROM python:3.12.3-slim

# (Optional but recommended) reduce image issues with TLS/certs + health tooling
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates curl \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Install deps first (better layer caching)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy app code (assume your file is named main.py)
COPY api.py .

# Runtime defaults (override in k8s if needed)
ENV PORT=8000
EXPOSE 8000

# Start API
CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
