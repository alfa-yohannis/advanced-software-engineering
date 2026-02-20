#!/usr/bin/env python3
"""
TTS load client for the FastAPI + Redis queue case study.

Usage:
  python client/load_client.py --base-url http://tts.local --concurrency 20 --jobs 200

Notes:
- Calls API via {base-url}/api (because your Ingress routes /api -> api service).
- Creates jobs via POST /api/jobs
- Polls GET /api/jobs/{job_id} until DONE/FAILED or timeout
- Optionally verifies MP3 endpoint by sending a HEAD request to {base-url}{mp3_url}
"""

from __future__ import annotations

import argparse
import asyncio
import json
import random
import statistics
import string
import time
from dataclasses import dataclass
from typing import Optional, List, Dict, Any, Tuple

import aiohttp


@dataclass
class Result:
    ok: bool
    status: str
    job_id: str
    create_ms: float
    total_ms: float
    polls: int
    mp3_ok: Optional[bool]
    error: Optional[str]


def _rand_text(min_words: int = 10, max_words: int = 40) -> str:
    words = random.randint(min_words, max_words)
    vocab = [
        "hello", "this", "is", "a", "real", "synthesised", "mp3", "scalability",
        "test", "for", "advanced", "software", "engineering", "and", "devops",
        "kubernetes", "autoscaling", "worker", "queue", "redis", "minio", "fastapi"
    ]
    return " ".join(random.choice(vocab) for _ in range(words)).capitalize() + "."


def _percentile(xs: List[float], p: float) -> float:
    if not xs:
        return float("nan")
    xs_sorted = sorted(xs)
    k = (len(xs_sorted) - 1) * (p / 100.0)
    f = int(k)
    c = min(f + 1, len(xs_sorted) - 1)
    if f == c:
        return xs_sorted[f]
    return xs_sorted[f] + (xs_sorted[c] - xs_sorted[f]) * (k - f)


async def create_job(session: aiohttp.ClientSession, api_base: str, text: str, voice: str, speed: float) -> Tuple[str, float]:
    url = f"{api_base}/jobs"
    payload = {"text": text, "voice": voice, "speed": speed}
    t0 = time.perf_counter()
    async with session.post(url, json=payload) as resp:
        body = await resp.text()
        dt = (time.perf_counter() - t0) * 1000.0
        if resp.status != 200:
            raise RuntimeError(f"CREATE failed {resp.status}: {body}")
        data = json.loads(body)
        return data["job_id"], dt


async def poll_job(session: aiohttp.ClientSession, api_base: str, job_id: str, poll_interval: float, timeout_s: float) -> Tuple[Dict[str, Any], int, float]:
    url = f"{api_base}/jobs/{job_id}"
    t0 = time.perf_counter()
    polls = 0
    while True:
        polls += 1
        async with session.get(url) as resp:
            data = await resp.json(content_type=None)
            if resp.status != 200:
                raise RuntimeError(f"GET job failed {resp.status}: {data}")
        st = data.get("status")
        if st in ("DONE", "FAILED"):
            total_ms = (time.perf_counter() - t0) * 1000.0
            return data, polls, total_ms

        if (time.perf_counter() - t0) > timeout_s:
            total_ms = (time.perf_counter() - t0) * 1000.0
            data["status"] = "TIMEOUT"
            return data, polls, total_ms

        await asyncio.sleep(poll_interval)


async def verify_mp3(session: aiohttp.ClientSession, base_url: str, mp3_url: str, method: str = "HEAD") -> bool:
    # mp3_url is like "/mp3/<id>.mp3" (path-only)
    url = f"{base_url}{mp3_url}"
    async with session.request(method, url, allow_redirects=True) as resp:
        return 200 <= resp.status < 300


async def one_job(
    sem: asyncio.Semaphore,
    session: aiohttp.ClientSession,
    base_url: str,
    api_base: str,
    voice: str,
    speed: float,
    poll_interval: float,
    timeout_s: float,
    check_mp3: bool,
    mp3_method: str,
) -> Result:
    async with sem:
        text = _rand_text()
        try:
            job_id, create_ms = await create_job(session, api_base, text, voice, speed)
            print(f"[job] CREATED job_id={job_id} create_ms={create_ms:.1f}")
        except Exception as e:
            return Result(
                ok=False, status="CREATE_FAILED", job_id="-", create_ms=0.0, total_ms=0.0, polls=0,
                mp3_ok=None, error=str(e)
            )

        try:
            data, polls, total_ms = await poll_job(session, api_base, job_id, poll_interval, timeout_s)
            st = data.get("status", "UNKNOWN")
            mp3_ok: Optional[bool] = None
            if check_mp3 and st == "DONE":
                mp3_url = data.get("mp3_url") or f"/mp3/{job_id}.mp3"
                mp3_ok = await verify_mp3(session, base_url, mp3_url, method=mp3_method)
            ok = (st == "DONE") and (mp3_ok is not False)
            print(
                f"[job] FINISHED job_id={job_id} "
                f"status={st} total_ms={total_ms:.1f} polls={polls}"
            )
            return Result(ok=ok, status=st, job_id=job_id, create_ms=create_ms, total_ms=total_ms, polls=polls, mp3_ok=mp3_ok, error=data.get("error"))
        except Exception as e:
            return Result(ok=False, status="POLL_FAILED", job_id=job_id, create_ms=create_ms, total_ms=0.0, polls=0, mp3_ok=None, error=str(e))


async def main_async(args: argparse.Namespace) -> int:
    base_url = args.base_url.rstrip("/")
    api_base = base_url + "/api"  # IMPORTANT: because ingress exposes API under /api

    timeout = aiohttp.ClientTimeout(total=args.http_timeout)
    connector = aiohttp.TCPConnector(limit=0, ttl_dns_cache=30)

    sem = asyncio.Semaphore(args.concurrency)

    headers = {"Accept": "application/json"}

    print(f"[info] base_url   = {base_url}")
    print(f"[info] api_base   = {api_base}")
    print(f"[info] jobs       = {args.jobs}")
    print(f"[info] concurrency= {args.concurrency}")
    print(f"[info] poll_intvl = {args.poll_interval}s, job_timeout={args.job_timeout}s")
    if args.check_mp3:
        print(f"[info] mp3_check  = enabled ({args.mp3_method})")

    t_start = time.perf_counter()

    async with aiohttp.ClientSession(timeout=timeout, connector=connector, headers=headers) as session:
        # quick health check
        try:
            async with session.get(f"{api_base}/healthz") as resp:
                body = await resp.text()
                print(f"[healthz] {resp.status} {body.strip()}")
        except Exception as e:
            print(f"[warn] healthz failed: {e}")

        tasks = [
            asyncio.create_task(
                one_job(
                    sem, session,
                    base_url=base_url,
                    api_base=api_base,
                    voice=args.voice,
                    speed=args.speed,
                    poll_interval=args.poll_interval,
                    timeout_s=args.job_timeout,
                    check_mp3=args.check_mp3,
                    mp3_method=args.mp3_method,
                )
            )
            for _ in range(args.jobs)
        ]

        results: List[Result] = await asyncio.gather(*tasks)

    elapsed_s = time.perf_counter() - t_start

    ok = [r for r in results if r.ok]
    failed = [r for r in results if not r.ok]

    create_lat = [r.create_ms for r in results if r.create_ms > 0]
    total_lat = [r.total_ms for r in results if r.total_ms > 0]

    done = sum(1 for r in results if r.status == "DONE")
    timeout_n = sum(1 for r in results if r.status == "TIMEOUT")
    failed_n = len(results) - done - timeout_n

    rps = (len(results) / elapsed_s) if elapsed_s > 0 else 0.0

    print("\n=== Summary ===")
    print(f"total_jobs     : {len(results)}")
    print(f"done           : {done}")
    print(f"timeout        : {timeout_n}")
    print(f"failed_other   : {failed_n}")
    print(f"wall_time_s    : {elapsed_s:.2f}")
    print(f"submit_rate_rps: {rps:.2f}")

    if create_lat:
        print("\n-- Create latency (ms) --")
        print(f"avg  : {statistics.mean(create_lat):.1f}")
        print(f"p50  : {_percentile(create_lat, 50):.1f}")
        print(f"p90  : {_percentile(create_lat, 90):.1f}")
        print(f"p95  : {_percentile(create_lat, 95):.1f}")
        print(f"p99  : {_percentile(create_lat, 99):.1f}")

    if total_lat:
        print("\n-- Total latency until DONE/FAILED/TIMEOUT (ms) --")
        print(f"avg  : {statistics.mean(total_lat):.1f}")
        print(f"p50  : {_percentile(total_lat, 50):.1f}")
        print(f"p90  : {_percentile(total_lat, 90):.1f}")
        print(f"p95  : {_percentile(total_lat, 95):.1f}")
        print(f"p99  : {_percentile(total_lat, 99):.1f}")

    if args.show_failures and failed:
        print("\n-- Failures (first 20) --")
        for r in failed[:20]:
            print(f"{r.status:12} job={r.job_id} err={r.error}")

    # exit code
    return 0 if done > 0 and (len(failed) / max(1, len(results))) < 0.5 else 2


def parse_args() -> argparse.Namespace:
    ap = argparse.ArgumentParser()
    ap.add_argument("--base-url", required=True, help="Base URL for ingress (e.g. http://tts.local)")
    ap.add_argument("--jobs", type=int, default=200, help="Total jobs to submit")
    ap.add_argument("--concurrency", type=int, default=20, help="Concurrent in-flight jobs")
    ap.add_argument("--voice", type=str, default="en_US", help="Voice name")
    ap.add_argument("--speed", type=float, default=1.0, help="TTS speed")
    ap.add_argument("--poll-interval", type=float, default=0.5, help="Seconds between polling job status")
    ap.add_argument("--job-timeout", type=float, default=120.0, help="Seconds to wait for a job to finish")
    ap.add_argument("--http-timeout", type=float, default=300.0, help="Total HTTP timeout per request")
    ap.add_argument("--check-mp3", action="store_true", help="Verify MP3 endpoint after DONE")
    ap.add_argument("--mp3-method", choices=["HEAD", "GET"], default="HEAD", help="Method to verify mp3")
    ap.add_argument("--show-failures", action="store_true", help="Print failure details")
    return ap.parse_args()


def main() -> None:
    args = parse_args()
    random.seed(42)
    rc = asyncio.run(main_async(args))
    raise SystemExit(rc)


if __name__ == "__main__":
    main()