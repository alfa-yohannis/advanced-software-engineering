# tests/integration/test_e2e.py
#
# End-to-end integration test for the full docker-compose stack:
#   Text -> API (/api/jobs) -> Redis queue -> Worker -> MinIO -> Web (/mp3/<id>.mp3)
#
# Prereqs:
#   docker compose up -d --build
#
# Run:
#   pytest -q tests/integration/test_e2e.py --base-url http://localhost:8088
#
# Notes:
# - This test assumes you use the nginx gateway so the API is reachable at /api/*
# - If you don't use gateway, run with:
#     --api-url http://localhost:8000 --web-url http://localhost:8080
# - The options and the api_url and web_url fixtures are in tests/conftest.py.
#   pytest reads pytest_addoption only from conftest.py files and plugins.

from __future__ import annotations

import requests
from tenacity import retry, stop_after_delay, wait_fixed


def _create_job(api_url: str, text: str) -> str:
    r = requests.post(f"{api_url}/jobs", json={"text": text}, timeout=15)
    r.raise_for_status()
    data = r.json()
    job_id = data.get("job_id")
    if not job_id:
        raise AssertionError(f"Missing job_id in response: {data}")
    return job_id


def _get_job(api_url: str, job_id: str) -> dict:
    r = requests.get(f"{api_url}/jobs/{job_id}", timeout=15)
    r.raise_for_status()
    return r.json()


@retry(stop=stop_after_delay(180), wait=wait_fixed(1.5))
def _wait_done(api_url: str, job_id: str) -> dict:
    st = _get_job(api_url, job_id)
    status = (st.get("status") or "").upper()

    if status == "DONE":
        return st
    if status == "FAILED":
        raise AssertionError(f"Job FAILED: {st}")
    if status in ("QUEUED", "RUNNING", ""):
        raise AssertionError(f"Not done yet: {status}")
    raise AssertionError(f"Unexpected status value: {st}")


def _fetch_mp3(web_url: str, job_id: str) -> requests.Response:
    # Web service serves from MinIO at /mp3/{job_id}.mp3
    return requests.get(f"{web_url}/mp3/{job_id}.mp3", timeout=30)


def test_e2e_compose_pipeline(api_url: str, web_url: str):
    """
    Creates a job via API, waits until worker finishes, then downloads MP3 from web.
    """
    # Smoke checks
    api_h = requests.get(f"{api_url}/healthz", timeout=10)
    api_h.raise_for_status()
    web_h = requests.get(f"{web_url}/healthz", timeout=10)
    web_h.raise_for_status()

    # 1) submit
    job_id = _create_job(api_url, "Hello from e2e test. This should become an MP3.")

    # 2) wait until DONE (worker + minio)
    st = _wait_done(api_url, job_id)
    assert st["job_id"] == job_id
    assert st["status"] == "DONE"

    # 3) fetch mp3 from web
    mp3 = _fetch_mp3(web_url, job_id)
    assert mp3.status_code == 200, f"MP3 fetch failed: {mp3.status_code} {mp3.text[:200]}"
    ctype = mp3.headers.get("content-type", "")
    assert ctype.startswith("audio/mpeg") or mp3.content[:3] == b"ID3", f"Not MP3. content-type={ctype}"
    assert len(mp3.content) > 500, f"MP3 too small: {len(mp3.content)} bytes"

    # 4) headers only, as curl -I does
    head = requests.head(f"{web_url}/mp3/{job_id}.mp3", timeout=30)
    assert head.status_code == 200, f"MP3 HEAD failed: {head.status_code}"
    assert int(head.headers["content-length"]) == len(mp3.content)


def test_e2e_invalid_job_rejected(api_url: str):
    """
    Empty text should be rejected by API validation (Pydantic min_length=1),
    typically 422.
    """
    r = requests.post(f"{api_url}/jobs", json={"text": ""}, timeout=15)
    assert r.status_code in (400, 422), f"Expected validation error, got {r.status_code}: {r.text[:200]}"
