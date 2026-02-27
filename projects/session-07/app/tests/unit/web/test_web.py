# tests/unit/web/test_web.py
#
# Unit tests for services/web/web.py
# - No Docker needed
# - No MinIO needed (S3 client is mocked)
#
# Run:
#   PYTHONPATH=. pytest -q tests/unit/web/test_web.py

from __future__ import annotations

import os
from typing import Any, Dict, Iterator

import pytest
from botocore.exceptions import ClientError
from fastapi.testclient import TestClient

import services.web.web as web_mod  # type: ignore


class FakeBody:
    """Minimal streaming body for StreamingResponse."""
    def __init__(self, chunks):
        self._chunks = list(chunks)

    def __iter__(self):
        yield from self._chunks

    # Some callers may use .read(); provide it too.
    def read(self, amt: int = -1) -> bytes:
        data = b"".join(self._chunks)
        if amt == -1:
            return data
        return data[:amt]


class FakeS3:
    def __init__(self):
        self.objects: Dict[tuple[str, str], Any] = {}
        self.raise_client_error_code: str | None = None
        self.last_get_object_args = None

    def put_mp3(self, bucket: str, key: str, content: bytes):
        self.objects[(bucket, key)] = {"Body": FakeBody([content])}

    def get_object(self, Bucket: str, Key: str):
        self.last_get_object_args = {"Bucket": Bucket, "Key": Key}

        if self.raise_client_error_code is not None:
            code = self.raise_client_error_code
            raise ClientError(
                error_response={"Error": {"Code": code, "Message": "boom"}},
                operation_name="GetObject",
            )

        obj = self.objects.get((Bucket, Key))
        if obj is None:
            raise ClientError(
                error_response={"Error": {"Code": "NoSuchKey", "Message": "not found"}},
                operation_name="GetObject",
            )
        return obj


@pytest.fixture()
def fake_s3(monkeypatch) -> FakeS3:
    fs3 = FakeS3()
    monkeypatch.setattr(web_mod, "s3", fs3, raising=True)
    # Make env-derived globals deterministic in tests
    monkeypatch.setattr(web_mod, "APP_ENV", "dev", raising=True)
    monkeypatch.setattr(web_mod, "S3_BUCKET", "tts-dev", raising=True)
    return fs3


@pytest.fixture()
def client(fake_s3) -> TestClient:
    return TestClient(web_mod.app)


def test_healthz_ok(client: TestClient):
    r = client.get("/healthz")
    assert r.status_code == 200
    assert r.headers.get("content-type", "").startswith("application/json")
    assert r.json() == {"ok": True, "env": web_mod.APP_ENV, "bucket": web_mod.S3_BUCKET}


def test_index_serves_html_file(client: TestClient):
    r = client.get("/")
    assert r.status_code == 200
    # FileResponse sets a content-type; should be html
    assert "text/html" in r.headers.get("content-type", "")

    # Basic sanity check that it's your page
    body = r.text
    assert "<!doctype html>" in body.lower()
    assert "tts case study" in body.lower()


def test_mp3_streams_audio_from_s3(client: TestClient, fake_s3: FakeS3):
    # Arrange: store an mp3-like payload
    fake_s3.put_mp3("tts-dev", "job123.mp3", b"ID3" + b"\x00" * 50)

    # Act
    r = client.get("/mp3/job123.mp3")

    # Assert
    assert r.status_code == 200
    assert r.headers.get("content-type", "").startswith("audio/mpeg")
    assert r.content[:3] == b"ID3"
    assert fake_s3.last_get_object_args == {"Bucket": "tts-dev", "Key": "job123.mp3"}


def test_mp3_404_when_key_missing(client: TestClient, fake_s3: FakeS3):
    # FakeS3 defaults to raising NoSuchKey if not found
    r = client.get("/mp3/missing.mp3")
    assert r.status_code == 404
    assert r.json()["detail"] == "mp3 not found"


@pytest.mark.parametrize("code", ["404", "NoSuchKey"])
def test_mp3_404_when_s3_returns_not_found_codes(client: TestClient, fake_s3: FakeS3, code: str):
    fake_s3.raise_client_error_code = code
    r = client.get("/mp3/whatever.mp3")
    assert r.status_code == 404
    assert r.json()["detail"] == "mp3 not found"


@pytest.mark.parametrize("code", ["AccessDenied", "InvalidAccessKeyId", "SlowDown", "500"])
def test_mp3_502_on_other_s3_errors(client: TestClient, fake_s3: FakeS3, code: str):
    fake_s3.raise_client_error_code = code
    r = client.get("/mp3/whatever.mp3")
    assert r.status_code == 502
    assert r.json()["detail"] == "storage error"
