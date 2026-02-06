# tests/unit/worker/test_worker.py
#
# Unit tests for services/worker/worker.py
# - No Docker needed
# - No Redis/MinIO/Piper/ffmpeg needed
# - We patch external calls (requests, boto3, subprocess, shutil.which)
#
# Run:
#   PYTHONPATH=. pytest -q tests/unit/worker/test_worker.py

from __future__ import annotations

import json
import os
import subprocess
from types import SimpleNamespace
from typing import Any, Dict, List

import pytest

import services.worker.worker as worker_mod  # type: ignore


@pytest.fixture()
def calls(monkeypatch):
    """
    Capture api_update calls and other side-effects.
    Also set stable config values for tests.
    """
    captured: Dict[str, Any] = {
        "api_updates": [],          # list[(job_id, status, error)]
        "ensure_bucket": [],        # list[bucket]
        "upload_mp3": [],           # list[(bucket, key, path)]
        "run_piper": [],            # list[(text, wav_path)]
        "wav_to_mp3": [],           # list[(wav_path, mp3_path)]
    }

    def _api_update(job_id: str, status: str, error: str | None = None):
        captured["api_updates"].append((job_id, status, error))

    def _ensure_bucket(bucket: str):
        captured["ensure_bucket"].append(bucket)

    def _upload_mp3(bucket: str, key: str, mp3_path: str):
        captured["upload_mp3"].append((bucket, key, mp3_path))

    def _run_piper_to_wav(text: str, wav_path: str):
        captured["run_piper"].append((text, wav_path))
        # create a fake WAV file (size > 1000 requirement is inside run_piper_to_wav,
        # but we are patching it here, so only downstream uses the path)
        os.makedirs(os.path.dirname(wav_path), exist_ok=True)
        with open(wav_path, "wb") as f:
            f.write(b"0" * 2001)

    def _wav_to_mp3(wav_path: str, mp3_path: str):
        captured["wav_to_mp3"].append((wav_path, mp3_path))
        with open(mp3_path, "wb") as f:
            f.write(b"ID3" + b"\x00" * 100)  # looks like MP3 header

    monkeypatch.setattr(worker_mod, "api_update", _api_update, raising=True)
    monkeypatch.setattr(worker_mod, "ensure_bucket", _ensure_bucket, raising=True)
    monkeypatch.setattr(worker_mod, "upload_mp3", _upload_mp3, raising=True)
    monkeypatch.setattr(worker_mod, "run_piper_to_wav", _run_piper_to_wav, raising=True)
    monkeypatch.setattr(worker_mod, "wav_to_mp3", _wav_to_mp3, raising=True)

    # Make MAX_TEXT_LEN deterministic for tests
    monkeypatch.setattr(worker_mod, "MAX_TEXT_LEN", 2000, raising=True)
    monkeypatch.setattr(worker_mod, "S3_BUCKET", "tts-dev", raising=True)

    return captured


# -------------------------
# process_job() unit tests
# -------------------------

def test_process_job_fails_on_empty_text(calls):
    raw = json.dumps({"job_id": "j1", "text": "   ", "bucket": "tts-dev", "mp3_key": "j1.mp3"})
    worker_mod.process_job(raw)

    assert calls["api_updates"] == [
        ("j1", "FAILED", "empty text"),
    ]
    assert calls["run_piper"] == []
    assert calls["wav_to_mp3"] == []
    assert calls["upload_mp3"] == []


def test_process_job_fails_on_text_too_long(calls, monkeypatch):
    monkeypatch.setattr(worker_mod, "MAX_TEXT_LEN", 5, raising=True)

    raw = json.dumps({"job_id": "j2", "text": "abcdef", "bucket": "tts-dev", "mp3_key": "j2.mp3"})
    worker_mod.process_job(raw)

    assert calls["api_updates"] == [
        ("j2", "FAILED", "text too long (> 5)"),
    ]
    assert calls["run_piper"] == []
    assert calls["upload_mp3"] == []


def test_process_job_success_happy_path(calls):
    raw = json.dumps({"job_id": "j3", "text": "hello", "bucket": "tts-dev", "mp3_key": "j3.mp3"})
    worker_mod.process_job(raw)

    # status sequence: RUNNING then DONE (no error)
    assert calls["api_updates"][0] == ("j3", "RUNNING", None)
    assert calls["api_updates"][-1] == ("j3", "DONE", None)

    # pipeline steps executed
    assert len(calls["run_piper"]) == 1
    assert len(calls["wav_to_mp3"]) == 1
    assert calls["ensure_bucket"] == ["tts-dev"]
    assert len(calls["upload_mp3"]) == 1

    bucket, key, mp3_path = calls["upload_mp3"][0]
    assert bucket == "tts-dev"
    assert key == "j3.mp3"
    assert mp3_path.endswith("j3.mp3")


def test_process_job_uses_defaults_for_bucket_and_key(calls):
    raw = json.dumps({"job_id": "j4", "text": "hello"})  # no bucket, no mp3_key
    worker_mod.process_job(raw)

    # ensure_bucket should use module default S3_BUCKET
    assert calls["ensure_bucket"] == ["tts-dev"]
    # upload key defaults to "{job_id}.mp3"
    bucket, key, _ = calls["upload_mp3"][0]
    assert bucket == "tts-dev"
    assert key == "j4.mp3"


def test_process_job_marks_failed_if_piper_raises(calls, monkeypatch):
    def boom(*args, **kwargs):
        raise RuntimeError("piper exploded")

    monkeypatch.setattr(worker_mod, "run_piper_to_wav", boom, raising=True)

    raw = json.dumps({"job_id": "j5", "text": "hello"})
    with pytest.raises(RuntimeError):
        # process_job does not catch; main loop catches. This is unit-level behavior.
        worker_mod.process_job(raw)

    # It should have already set RUNNING before failing
    assert calls["api_updates"][0] == ("j5", "RUNNING", None)
    # DONE should not be sent
    assert all(st != "DONE" for _, st, _ in calls["api_updates"])


# -------------------------
# run_piper_to_wav() tests
# -------------------------

def test_run_piper_to_wav_raises_if_piper_missing(monkeypatch, tmp_path):
    # Ensure which("piper") returns None
    monkeypatch.setattr(worker_mod.shutil, "which", lambda _: None, raising=True)

    with pytest.raises(RuntimeError, match="piper binary not found"):
        worker_mod.run_piper_to_wav("hi", str(tmp_path / "out.wav"))


def test_run_piper_to_wav_tries_candidates_until_success(monkeypatch, tmp_path):
    """
    Simulate first candidate failing, second succeeding by creating wav file.
    We patch:
      - shutil.which -> found
      - worker_mod._run -> returns returncode and stderr
      - os.path.exists/getsize controlled by actually writing file on success
    """
    monkeypatch.setattr(worker_mod.shutil, "which", lambda _: "/usr/bin/piper", raising=True)

    out_wav = tmp_path / "out.wav"
    seen_cmds: List[List[str]] = []

    def fake_run(cmd: List[str], stdin_text: str):
        seen_cmds.append(cmd)
        # fail first candidate
        if len(seen_cmds) == 1:
            return SimpleNamespace(returncode=1, stderr=b"bad flag", stdout=b"")
        # succeed second candidate by writing a valid wav (>1000 bytes)
        out_wav.write_bytes(b"0" * 2001)
        return SimpleNamespace(returncode=0, stderr=b"", stdout=b"")

    monkeypatch.setattr(worker_mod, "_run", fake_run, raising=True)

    worker_mod.run_piper_to_wav("hello", str(out_wav))

    assert len(seen_cmds) >= 2
    assert out_wav.exists()
    assert out_wav.stat().st_size > 1000


def test_run_piper_to_wav_raises_with_last_stderr(monkeypatch, tmp_path):
    monkeypatch.setattr(worker_mod.shutil, "which", lambda _: "/usr/bin/piper", raising=True)

    out_wav = tmp_path / "out.wav"

    def always_fail(cmd: List[str], stdin_text: str):
        return SimpleNamespace(returncode=1, stderr=b"nope", stdout=b"")

    monkeypatch.setattr(worker_mod, "_run", always_fail, raising=True)

    with pytest.raises(RuntimeError, match="piper failed"):
        worker_mod.run_piper_to_wav("hello", str(out_wav))


# -------------------------
# wav_to_mp3() tests
# -------------------------

def test_wav_to_mp3_raises_on_ffmpeg_failure(monkeypatch, tmp_path):
    wav = tmp_path / "in.wav"
    mp3 = tmp_path / "out.mp3"
    wav.write_bytes(b"0" * 10)

    def fail_run(cmd, stdout, stderr, check):
        return SimpleNamespace(returncode=1, stderr=b"ffmpeg error", stdout=b"")

    monkeypatch.setattr(worker_mod.subprocess, "run", fail_run, raising=True)

    with pytest.raises(RuntimeError, match="ffmpeg failed"):
        worker_mod.wav_to_mp3(str(wav), str(mp3))


# -------------------------
# upload_mp3() tests
# -------------------------

def test_upload_mp3_calls_s3_upload_file(monkeypatch, tmp_path):
    mp3 = tmp_path / "a.mp3"
    mp3.write_bytes(b"ID3" + b"\x00" * 10)

    called = {}

    class FakeS3:
        def upload_file(self, Filename, Bucket, Key, ExtraArgs):
            called["args"] = (Filename, Bucket, Key, ExtraArgs)

    monkeypatch.setattr(worker_mod, "s3", FakeS3(), raising=True)

    worker_mod.upload_mp3("bkt", "k.mp3", str(mp3))

    Filename, Bucket, Key, ExtraArgs = called["args"]
    assert Filename == str(mp3)
    assert Bucket == "bkt"
    assert Key == "k.mp3"
    assert ExtraArgs == {"ContentType": "audio/mpeg"}
