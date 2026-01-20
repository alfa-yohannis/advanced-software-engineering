FROM python:3.12.3-slim

RUN pip install --no-cache-dir pyzmq

WORKDIR /app
COPY broker.py .

CMD ["python", "broker.py"]
