import os
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse, StreamingResponse
import boto3
from botocore.config import Config
from botocore.exceptions import ClientError

APP_ENV = os.getenv("APP_ENV", "dev")

S3_ENDPOINT = os.getenv("S3_ENDPOINT", "http://minio:9000")
S3_ACCESS_KEY = os.getenv("S3_ACCESS_KEY", "minioadmin")
S3_SECRET_KEY = os.getenv("S3_SECRET_KEY", "minioadmin")
S3_REGION = os.getenv("S3_REGION", "us-east-1")
S3_BUCKET = os.getenv("MP3_BUCKET", f"tts-{APP_ENV}")

BASE_DIR = os.path.dirname(__file__)
STATIC_DIR = os.path.join(BASE_DIR, "static")

s3 = boto3.client(
    "s3",
    endpoint_url=S3_ENDPOINT,
    aws_access_key_id=S3_ACCESS_KEY,
    aws_secret_access_key=S3_SECRET_KEY,
    region_name=S3_REGION,
    config=Config(signature_version="s3v4"),
)

app = FastAPI(title="TTS Web Player", version="0.1")


@app.get("/healthz")
def healthz():
    return {"ok": True, "env": APP_ENV, "bucket": S3_BUCKET}


@app.get("/")
def index():
    return FileResponse(os.path.join(STATIC_DIR, "index.html"))


@app.get("/mp3/{job_id}.mp3")
def serve_mp3(job_id: str):
    key = f"{job_id}.mp3"

    try:
        obj = s3.get_object(Bucket=S3_BUCKET, Key=key)
    except ClientError as e:
        code = e.response.get("Error", {}).get("Code", "")
        if code in ("NoSuchKey", "404"):
            raise HTTPException(status_code=404, detail="mp3 not found")
        raise HTTPException(status_code=502, detail="storage error")

    return StreamingResponse(
        obj["Body"],
        media_type="audio/mpeg"
    )
