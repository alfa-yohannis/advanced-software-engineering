FROM python:3.12.3-slim

RUN pip install --no-cache-dir pyzmq bottle waitress

WORKDIR /app
COPY web_server.py .
COPY static ./static

CMD ["python", "web_server.py"]
