FROM python:3.12.3-slim

RUN pip install --no-cache-dir pyzmq pillow

WORKDIR /app
COPY transformer.py .

CMD ["python", "transformer.py"]
