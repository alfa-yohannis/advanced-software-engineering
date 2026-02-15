from fastapi import FastAPI, HTTPException, Header, Response
from pydantic import BaseModel, Field
from typing import Optional, Literal, Dict, Any
import os, uuid, json
import redis

APP_ENV = os.getenv("APP_ENV", "dev")
REDIS_URL = os.getenv("REDIS_URL", "redis://redis:6379/0")
BUCKET = os.getenv("MP3_BUCKET", f"tts-{APP_ENV}")
PUBLIC_BASE_URL = os.getenv("PUBLIC_BASE_URL", "")  # e.g. https://tts-dev.example.com
INTERNAL_TOKEN = os.getenv("INTERNAL_TOKEN", "changeme")  # simple protection for class demo

r = redis.Redis.from_url(REDIS_URL, decode_responses=True)

app = FastAPI(title="TTS Case Study API", version="0.1")

JobStatus = Literal["QUEUED", "RUNNING", "DONE", "FAILED"]

class CreateJobReq(BaseModel):
    text: str = Field(min_length=1, max_length=2000)
    voice: Optional[str] = "en_US"
    speed: Optional[float] = 1.0

class CreateJobRes(BaseModel):
    job_id: str
    status: JobStatus

class JobState(BaseModel):
    job_id: str
    status: JobStatus
    mp3_url: Optional[str] = None
    error: Optional[str] = None

def job_key(job_id: str) -> str:
    return f"job:{job_id}"

def jobs_queue_key() -> str:
    return f"jobs:queue:{APP_ENV}"

def build_mp3_url(job_id: str) -> str:
    path = f"/mp3/{job_id}.mp3"
    return f"{PUBLIC_BASE_URL}{path}" if PUBLIC_BASE_URL else path

@app.get("/healthz")
def healthz():
    return {"ok": True, "env": APP_ENV}

@app.post("/jobs", response_model=CreateJobRes)
def create_job(req: CreateJobReq):
    job_id = str(uuid.uuid4())

    # Store state
    state = {
        "job_id": job_id,
        "status": "QUEUED",
        "voice": req.voice,
        "speed": req.speed,
        "bucket": BUCKET,
        "mp3_key": f"{job_id}.mp3",
        "error": "",
    }
    r.hset(job_key(job_id), mapping=state)

    # Enqueue work item (simple JSON)
    work = {"job_id": job_id, "text": req.text, "voice": req.voice, "speed": req.speed, "bucket": BUCKET, "mp3_key": state["mp3_key"]}
    r.rpush(jobs_queue_key(), json.dumps(work))

    return {"job_id": job_id, "status": "QUEUED"}

@app.get("/jobs/{job_id}", response_model=JobState)
def get_job(job_id: str):
    if not r.exists(job_key(job_id)):
        raise HTTPException(status_code=404, detail="job_id not found")

    h = r.hgetall(job_key(job_id))
    st = h.get("status", "QUEUED")
    mp3_url = build_mp3_url(job_id) if st == "DONE" else None
    err = h.get("error") or None
    return {"job_id": job_id, "status": st, "mp3_url": mp3_url, "error": err}

# Worker updates state (internal)
class UpdateJobReq(BaseModel):
    status: JobStatus
    error: Optional[str] = None

@app.put("/internal/jobs/{job_id}")
def update_job(job_id: str, req: UpdateJobReq, x_internal_token: Optional[str] = Header(default=None)):
    if x_internal_token != INTERNAL_TOKEN:
        raise HTTPException(status_code=401, detail="unauthorized")

    if not r.exists(job_key(job_id)):
        raise HTTPException(status_code=404, detail="job_id not found")

    mapping: Dict[str, Any] = {"status": req.status}
    if req.error is not None:
        mapping["error"] = req.error
    r.hset(job_key(job_id), mapping=mapping)
    return {"ok": True}

@app.get("/mp3/{job_id}.mp3")
def get_mp3(job_id: str):
    # For simplicity, API just returns a pointer. In real impl:
    # - proxy bytes from MinIO OR
    # - redirect to presigned URL (recommended)
    #
    # Here we signal DONE-only; actual MP3 serving can be done by web service.
    if not r.exists(job_key(job_id)):
        raise HTTPException(status_code=404, detail="job_id not found")
    h = r.hgetall(job_key(job_id))
    if h.get("status") != "DONE":
        raise HTTPException(status_code=409, detail="mp3 not ready")

    # In the minimal design, let web service serve MP3.
    # So API returns 302 redirect to web path.
    mp3_url = build_mp3_url(job_id)
    return Response(status_code=302, headers={"Location": mp3_url})
