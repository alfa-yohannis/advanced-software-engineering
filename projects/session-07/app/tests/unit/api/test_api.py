# tests/unit/api/test_api.py
#
# Unit tests for services/api/api.py (FastAPI + Redis state/queue)
#
# These tests patch the module-level Redis client `r` with an in-memory fake,
# so they do NOT require a real Redis container.

from __future__ import annotations

import json
from typing import Any, Dict, List, Optional

import pytest
from fastapi.testclient import TestClient

import services.api.api as api_mod  # type: ignore


class FakeRedis:
    """
    Minimal Redis fake to support the calls used in services/api/api.py:
    - exists(key)
    - hset(key, mapping=dict)
    - hgetall(key)
    - rpush(key, value)
    """

    def __init__(self) -> None:
        self._hashes: Dict[str, Dict[str, str]] = {}
        self._lists: Dict[str, List[str]] = {}

    def exists(self, key: str) -> int:
        return 1 if (key in self._hashes) else 0

    def hset(self, key: str, mapping: Dict[str, Any]) -> int:
        # Redis stores strings; module uses decode_responses=True.
        self._hashes.setdefault(key, {})
        for k, v in mapping.items():
            self._hashes[key][str(k)] = "" if v is None else str(v)
        return len(mapping)

    def hgetall(self, key: str) -> Dict[str, str]:
        return dict(self._hashes.get(key, {}))

    def rpush(self, key: str, value: str) -> int:
        self._lists.setdefault(key, [])
        self._lists[key].append(value)
        return len(self._lists[key])

    # helpers for assertions
    def list_getall(self, key: str) -> List[str]:
        return list(self._lists.get(key, []))


@pytest.fixture()
def fake_redis(monkeypatch) -> FakeRedis:
    fr = FakeRedis()
    monkeypatch.setattr(api_mod, "r", fr, raising=True)
    # Make globals deterministic for tests (module reads these at import time, but endpoints use them dynamically)
    monkeypatch.setattr(api_mod, "APP_ENV", "dev", raising=True)
    monkeypatch.setattr(api_mod, "BUCKET", "tts-dev", raising=True)
    monkeypatch.setattr(api_mod, "PUBLIC_BASE_URL", "", raising=True)  # relative /mp3/...
    monkeypatch.setattr(api_mod, "INTERNAL_TOKEN", "changeme", raising=True)
    return fr


@pytest.fixture()
def client(fake_redis) -> TestClient:
    return TestClient(api_mod.app)


def test_healthz_ok(client: TestClient):
    r = client.get("/healthz")
    assert r.status_code == 200
    assert r.headers.get("content-type", "").startswith("application/json")
    assert r.json() == {"ok": True, "env": api_mod.APP_ENV}


def test_get_job_404_when_missing(client: TestClient):
    r = client.get("/jobs/not-found")
    assert r.status_code == 404
    assert r.json()["detail"] == "job_id not found"


def test_create_job_stores_state_and_enqueues_work(client: TestClient, fake_redis: FakeRedis, monkeypatch):
    # Force deterministic UUID
    class _UUID:
        def __init__(self, v: str):
            self._v = v

        def __str__(self) -> str:
            return self._v

    monkeypatch.setattr(api_mod.uuid, "uuid4", lambda: _UUID("00000000-0000-0000-0000-000000000000"))

    payload = {"text": "hello", "voice": "en_US", "speed": 1.0}
    r = client.post("/jobs", json=payload)
    assert r.status_code == 200
    assert r.json() == {"job_id": "00000000-0000-0000-0000-000000000000", "status": "QUEUED"}

    # Verify state stored in Redis hash
    state = fake_redis.hgetall(api_mod.job_key("00000000-0000-0000-0000-000000000000"))
    assert state["job_id"] == "00000000-0000-0000-0000-000000000000"
    assert state["status"] == "QUEUED"
    assert state["voice"] == "en_US"
    assert state["speed"] == "1.0"  # stored as string
    assert state["bucket"] == api_mod.BUCKET
    assert state["mp3_key"] == "00000000-0000-0000-0000-000000000000.mp3"

    # Verify work item pushed to queue
    qkey = api_mod.jobs_queue_key()
    items = fake_redis.list_getall(qkey)
    assert len(items) == 1
    work = json.loads(items[0])
    assert work["job_id"] == "00000000-0000-0000-0000-000000000000"
    assert work["text"] == "hello"
    assert work["voice"] == "en_US"
    assert work["speed"] == 1.0
    assert work["bucket"] == api_mod.BUCKET
    assert work["mp3_key"] == "00000000-0000-0000-0000-000000000000.mp3"


def test_get_job_returns_queued_and_no_mp3_url(client: TestClient, fake_redis: FakeRedis):
    job_id = "j1"
    fake_redis.hset(api_mod.job_key(job_id), mapping={"job_id": job_id, "status": "QUEUED", "error": ""})

    r = client.get(f"/jobs/{job_id}")
    assert r.status_code == 200
    data = r.json()
    assert data["job_id"] == job_id
    assert data["status"] == "QUEUED"
    assert data["mp3_url"] is None
    assert data["error"] is None  # empty string -> None per implementation


def test_update_job_requires_token(client: TestClient, fake_redis: FakeRedis):
    job_id = "j2"
    fake_redis.hset(api_mod.job_key(job_id), mapping={"job_id": job_id, "status": "QUEUED", "error": ""})

    r = client.put(f"/internal/jobs/{job_id}", json={"status": "RUNNING"})
    assert r.status_code == 401
    assert r.json()["detail"] == "unauthorized"

    r2 = client.put(
        f"/internal/jobs/{job_id}",
        json={"status": "RUNNING"},
        headers={"x-internal-token": "wrong"},
    )
    assert r2.status_code == 401
    assert r2.json()["detail"] == "unauthorized"


def test_update_job_404_if_missing(client: TestClient):
    r = client.put(
        "/internal/jobs/missing",
        json={"status": "RUNNING"},
        headers={"x-internal-token": api_mod.INTERNAL_TOKEN},
    )
    assert r.status_code == 404
    assert r.json()["detail"] == "job_id not found"


def test_update_job_success_updates_hash(client: TestClient, fake_redis: FakeRedis):
    job_id = "j3"
    fake_redis.hset(api_mod.job_key(job_id), mapping={"job_id": job_id, "status": "QUEUED", "error": ""})

    r = client.put(
        f"/internal/jobs/{job_id}",
        json={"status": "DONE", "error": ""},
        headers={"x-internal-token": api_mod.INTERNAL_TOKEN},
    )
    assert r.status_code == 200
    assert r.json() == {"ok": True}

    h = fake_redis.hgetall(api_mod.job_key(job_id))
    assert h["status"] == "DONE"
    assert h["error"] == ""


def test_get_mp3_404_when_missing(client: TestClient):
    r = client.get("/mp3/nope.mp3")
    assert r.status_code == 404
    assert r.json()["detail"] == "job_id not found"


def test_get_mp3_409_when_not_ready(client: TestClient, fake_redis: FakeRedis):
    job_id = "j4"
    fake_redis.hset(api_mod.job_key(job_id), mapping={"job_id": job_id, "status": "RUNNING", "error": ""})

    r = client.get(f"/mp3/{job_id}.mp3")
    assert r.status_code == 409
    assert r.json()["detail"] == "mp3 not ready"


def test_get_mp3_redirects_302_when_done_relative(client: TestClient, fake_redis: FakeRedis, monkeypatch):
    job_id = "j5"
    fake_redis.hset(api_mod.job_key(job_id), mapping={"job_id": job_id, "status": "DONE", "error": ""})
    monkeypatch.setattr(api_mod, "PUBLIC_BASE_URL", "", raising=True)

    r = client.get(f"/mp3/{job_id}.mp3", follow_redirects=False)
    assert r.status_code == 302
    assert r.headers["Location"] == f"/mp3/{job_id}.mp3"


def test_get_mp3_redirects_302_when_done_with_public_base_url(client: TestClient, fake_redis: FakeRedis, monkeypatch):
    job_id = "j6"
    fake_redis.hset(api_mod.job_key(job_id), mapping={"job_id": job_id, "status": "DONE", "error": ""})
    monkeypatch.setattr(api_mod, "PUBLIC_BASE_URL", "https://example.test", raising=True)

    r = client.get(f"/mp3/{job_id}.mp3", follow_redirects=False)
    assert r.status_code == 302
    assert r.headers["Location"] == f"https://example.test/mp3/{job_id}.mp3"
