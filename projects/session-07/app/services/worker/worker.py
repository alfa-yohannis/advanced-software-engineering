# worker.py (updated for Piper 2023.11.14-2 + HF model en_GB-alan-medium.onnx)
# Key updates:
# - Default PIPER_MODEL_PATH now points to /models/en_GB-alan-medium.onnx
# - Piper CLI flags differ across releases; this tries common variants safely
# - Logs more helpful info if piper invocation fails

import os, json, time, tempfile, subprocess, traceback, shutil
import requests
import redis
import boto3
from botocore.config import Config
from botocore.exceptions import BotoCoreError, ClientError

# ---- Env ----
APP_ENV = os.getenv("APP_ENV", "dev")

REDIS_URL = os.getenv("REDIS_URL", "redis://redis:6379/0")
QUEUE_KEY = os.getenv("QUEUE_KEY", f"jobs:queue:{APP_ENV}")

API_BASE_URL = os.getenv("API_BASE_URL", "http://api:8000")
INTERNAL_TOKEN = os.getenv("INTERNAL_TOKEN", "changeme")

S3_ENDPOINT = os.getenv("S3_ENDPOINT", "http://minio:9000")
S3_ACCESS_KEY = os.getenv("S3_ACCESS_KEY", "minioadmin")
S3_SECRET_KEY = os.getenv("S3_SECRET_KEY", "minioadmin")
S3_REGION = os.getenv("S3_REGION", "us-east-1")
S3_BUCKET = os.getenv("MP3_BUCKET", f"tts-{APP_ENV}")

# Piper
# Default now matches your Hugging Face model choice:
# https://huggingface.co/rhasspy/piper-voices/.../en_GB-alan-medium.onnx
PIPER_MODEL_PATH = os.getenv("PIPER_MODEL_PATH", "/models/en_GB-alan-medium.onnx")

# Some Piper builds use --length_scale, some support --speaker, etc.
# We'll keep length_scale optional.
PIPER_LENGTH_SCALE = float(os.getenv("PIPER_LENGTH_SCALE", "1.0"))  # higher = slower speech

# Worker
POLL_TIMEOUT_S = int(os.getenv("POLL_TIMEOUT_S", "5"))
MAX_TEXT_LEN = int(os.getenv("MAX_TEXT_LEN", "2000"))

# ---- Clients ----
r = redis.Redis.from_url(REDIS_URL, decode_responses=True)

s3 = boto3.client(
    "s3",
    endpoint_url=S3_ENDPOINT,
    aws_access_key_id=S3_ACCESS_KEY,
    aws_secret_access_key=S3_SECRET_KEY,
    region_name=S3_REGION,
    config=Config(signature_version="s3v4"),
)

def ensure_bucket(bucket: str):
    """Idempotent bucket creation for MinIO."""
    try:
        s3.head_bucket(Bucket=bucket)
        return
    except Exception:
        pass
    try:
        s3.create_bucket(Bucket=bucket)
    except Exception:
        pass  # ignore race / already-exists


def api_update(job_id: str, status: str, error: str | None = None):
    url = f"{API_BASE_URL}/internal/jobs/{job_id}"
    headers = {"X-Internal-Token": INTERNAL_TOKEN}
    payload = {"status": status}
    if error is not None:
        payload["error"] = error[:500]
    resp = requests.put(url, json=payload, headers=headers, timeout=10)
    resp.raise_for_status()


def _run(cmd: list[str], stdin_text: str):
    return subprocess.run(
        cmd,
        input=stdin_text.encode("utf-8"),
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        check=False,
    )


def run_piper_to_wav(text: str, wav_path: str):
    """
    Piper CLI differences across releases:
    - Some use: piper --model M --output_file out.wav
    - Others use: piper -m M -f out.wav
    We'll attempt a small set of common variants.
    """
    if shutil.which("piper") is None:
        raise RuntimeError("piper binary not found in PATH")

    # Candidate command variants (most likely first)
    candidates = [
        ["piper", "--model", PIPER_MODEL_PATH, "--output_file", wav_path, "--length_scale", str(PIPER_LENGTH_SCALE)],
        ["piper", "--model", PIPER_MODEL_PATH, "--output_file", wav_path],
        ["piper", "-m", PIPER_MODEL_PATH, "-f", wav_path, "--length_scale", str(PIPER_LENGTH_SCALE)],
        ["piper", "-m", PIPER_MODEL_PATH, "-f", wav_path],
    ]

    last_err = None
    for cmd in candidates:
        p = _run(cmd, text)
        if p.returncode == 0 and os.path.exists(wav_path) and os.path.getsize(wav_path) > 1000:
            return
        last_err = p.stderr.decode("utf-8", errors="ignore")[:400]

    raise RuntimeError(f"piper failed for all known CLI variants. stderr={last_err}")


def wav_to_mp3(wav_path: str, mp3_path: str):
    cmd = [
        "ffmpeg",
        "-y",
        "-i", wav_path,
        "-vn",
        "-codec:a", "libmp3lame",
        "-q:a", "4",
        mp3_path,
    ]
    p = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=False)
    if p.returncode != 0:
        raise RuntimeError(f"ffmpeg failed: {p.stderr.decode('utf-8', errors='ignore')[:400]}")


def upload_mp3(bucket: str, key: str, mp3_path: str):
    try:
        s3.upload_file(
            Filename=mp3_path,
            Bucket=bucket,
            Key=key,
            ExtraArgs={"ContentType": "audio/mpeg"},
        )
    except (BotoCoreError, ClientError) as e:
        raise RuntimeError(f"s3 upload failed: {e}")


def process_job(raw: str):
    job = json.loads(raw)
    job_id = job["job_id"]
    text = job.get("text", "")
    bucket = job.get("bucket", S3_BUCKET)
    mp3_key = job.get("mp3_key", f"{job_id}.mp3")

    if not isinstance(text, str) or not text.strip():
        api_update(job_id, "FAILED", "empty text")
        return

    if len(text) > MAX_TEXT_LEN:
        api_update(job_id, "FAILED", f"text too long (> {MAX_TEXT_LEN})")
        return

    api_update(job_id, "RUNNING")

    t0 = time.time()
    with tempfile.TemporaryDirectory(prefix="tts_worker_") as d:
        wav_path = os.path.join(d, f"{job_id}.wav")
        mp3_path = os.path.join(d, f"{job_id}.mp3")

        run_piper_to_wav(text, wav_path)
        wav_to_mp3(wav_path, mp3_path)

        ensure_bucket(bucket)
        upload_mp3(bucket, mp3_key, mp3_path)

    dt = time.time() - t0
    api_update(job_id, "DONE")
    print(f"[DONE] job_id={job_id} in {dt:.2f}s bucket={bucket} key={mp3_key}")


def main():
    print("Worker starting...")
    print("ENV:", APP_ENV)
    print("QUEUE:", QUEUE_KEY)
    print("API:", API_BASE_URL)
    print("S3:", S3_ENDPOINT, "BUCKET:", S3_BUCKET)
    print("PIPER_MODEL:", PIPER_MODEL_PATH)
    print("PIPER_LENGTH_SCALE:", PIPER_LENGTH_SCALE)

    while True:
        try:
            item = r.blpop(QUEUE_KEY, timeout=POLL_TIMEOUT_S)
            if item is None:
                continue
            _, raw = item
            try:
                process_job(raw)
            except Exception as e:
                # Try to mark FAILED if possible
                try:
                    job = json.loads(raw)
                    jid = job.get("job_id", "unknown")
                    api_update(jid, "FAILED", f"{type(e).__name__}: {str(e)[:300]}")
                except Exception:
                    pass
                print("[ERROR]", e)
                print(traceback.format_exc(limit=3))
        except Exception as outer:
            print("[FATAL LOOP ERROR]", outer)
            time.sleep(1)


if __name__ == "__main__":
    main()
