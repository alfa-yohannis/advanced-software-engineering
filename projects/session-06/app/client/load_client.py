#!/usr/bin/env python3
"""Submit synthesis jobs to the TTS stack and report how long they take.

Used in sessions 4 to 7 to put enough work on the worker to make the
Horizontal Pod Autoscaler react. Only the standard library is needed, so it
runs with any Python 3.9 or newer without installing anything.

    python load_client.py --base-url http://localhost:8088 --jobs 20 --concurrency 20
    python load_client.py --base-url http://$(minikube ip) --host dev.tts.local \
        --jobs 20 --concurrency 20 --check-mp3

The --host option sends a Host header without touching /etc/hosts, which is
how the Kubernetes ingress in sessions 6 and 7 is reached.
"""

from __future__ import annotations

import argparse
import json
import statistics
import sys
import threading
import time
import urllib.error
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed

DEFAULT_TEXT = (
    "The quick brown fox jumps over the lazy dog while the recording engineer "
    "checks every channel of the mixing desk one more time."
)

print_lock = threading.Lock()


def log(message: str) -> None:
    with print_lock:
        print(message, flush=True)


def call(url: str, host: str | None, timeout: float, method: str = "GET",
         payload: dict | None = None) -> tuple[int, bytes]:
    data = json.dumps(payload).encode() if payload is not None else None
    request = urllib.request.Request(url, data=data, method=method)
    if payload is not None:
        request.add_header("Content-Type", "application/json")
    if host:
        request.add_header("Host", host)
    try:
        with urllib.request.urlopen(request, timeout=timeout) as response:
            return response.status, response.read()
    except urllib.error.HTTPError as error:
        return error.code, error.read()


def submit(base_url: str, text: str, host: str | None, timeout: float) -> str:
    status, body = call(f"{base_url}/api/jobs", host, timeout, "POST", {"text": text})
    if status != 200:
        raise RuntimeError(f"POST /api/jobs returned {status}: {body[:200]!r}")
    return json.loads(body)["job_id"]


def wait_for(base_url: str, job_id: str, host: str | None, timeout: float,
             deadline: float, interval: float) -> tuple[str, str | None]:
    while time.monotonic() < deadline:
        status, body = call(f"{base_url}/api/jobs/{job_id}", host, timeout)
        if status != 200:
            raise RuntimeError(f"GET /api/jobs/{job_id} returned {status}")
        state = json.loads(body)
        if state["status"] in ("DONE", "FAILED"):
            return state["status"], state.get("mp3_url")
        time.sleep(interval)
    return "TIMEOUT", None


def fetch_mp3(base_url: str, job_id: str, host: str | None, timeout: float) -> int:
    status, body = call(f"{base_url}/mp3/{job_id}.mp3", host, timeout)
    if status != 200:
        raise RuntimeError(f"GET /mp3/{job_id}.mp3 returned {status}")
    return len(body)


def run_one(index: int, args: argparse.Namespace, text: str) -> dict:
    started = time.monotonic()
    result = {"index": index, "status": "ERROR", "seconds": 0.0, "bytes": 0, "error": None}
    try:
        job_id = submit(args.base_url, text, args.host, args.timeout)
        result["job_id"] = job_id
        deadline = started + args.job_timeout
        status, _ = wait_for(args.base_url, job_id, args.host, args.timeout,
                             deadline, args.poll_interval)
        result["status"] = status
        if status == "DONE" and args.check_mp3:
            result["bytes"] = fetch_mp3(args.base_url, job_id, args.host, args.timeout)
    except Exception as error:                      # noqa: BLE001 - reported, not raised
        result["error"] = f"{type(error).__name__}: {error}"
    result["seconds"] = time.monotonic() - started
    suffix = f", {result['bytes']} bytes" if result["bytes"] else ""
    detail = f" ({result['error']})" if result["error"] else ""
    log(f"  job {index:>3}: {result['status']:<7} in {result['seconds']:6.1f}s{suffix}{detail}")
    return result


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--base-url", default="http://localhost:8088",
                        help="gateway or ingress address (default: %(default)s)")
    parser.add_argument("--host", default=None,
                        help="Host header to send, e.g. dev.tts.local")
    parser.add_argument("--jobs", type=int, default=20,
                        help="how many jobs to submit (default: %(default)s)")
    parser.add_argument("--concurrency", type=int, default=20,
                        help="how many jobs in flight at once (default: %(default)s)")
    parser.add_argument("--repeat", type=int, default=4,
                        help="repeat the text this many times per job, to lengthen "
                             "synthesis (default: %(default)s)")
    parser.add_argument("--text", default=DEFAULT_TEXT, help="text to synthesise")
    parser.add_argument("--check-mp3", action="store_true",
                        help="download each MP3 and report its size")
    parser.add_argument("--timeout", type=float, default=30.0,
                        help="per-request timeout in seconds (default: %(default)s)")
    parser.add_argument("--job-timeout", type=float, default=600.0,
                        help="how long to wait for one job (default: %(default)s)")
    parser.add_argument("--poll-interval", type=float, default=2.0,
                        help="seconds between status checks (default: %(default)s)")
    args = parser.parse_args()

    args.base_url = args.base_url.rstrip("/")
    text = " ".join([args.text] * max(1, args.repeat))

    status, body = call(f"{args.base_url}/api/healthz", args.host, args.timeout)
    if status != 200:
        print(f"the API is not answering on {args.base_url}/api/healthz "
              f"(status {status})", file=sys.stderr)
        return 2
    log(f"api says {body.decode().strip()}")
    log(f"submitting {args.jobs} job(s), {args.concurrency} at a time, "
        f"{len(text)} characters each")

    started = time.monotonic()
    results = []
    with ThreadPoolExecutor(max_workers=args.concurrency) as pool:
        futures = [pool.submit(run_one, i + 1, args, text) for i in range(args.jobs)]
        for future in as_completed(futures):
            results.append(future.result())
    wall = time.monotonic() - started

    done = [r for r in results if r["status"] == "DONE"]
    log("")
    log(f"wall clock      : {wall:.1f}s")
    log(f"done            : {len(done)}/{len(results)}")
    for state in ("FAILED", "TIMEOUT", "ERROR"):
        count = sum(1 for r in results if r["status"] == state)
        if count:
            log(f"{state.lower():<16}: {count}")
    if done:
        seconds = sorted(r["seconds"] for r in done)
        log(f"per job seconds : min {seconds[0]:.1f}, "
            f"median {statistics.median(seconds):.1f}, max {seconds[-1]:.1f}")
        log(f"throughput      : {len(done) / wall:.2f} jobs/second")
    if args.check_mp3 and done:
        sizes = [r["bytes"] for r in done if r["bytes"]]
        if sizes:
            log(f"mp3 bytes       : min {min(sizes)}, max {max(sizes)}")

    return 0 if len(done) == len(results) else 1


if __name__ == "__main__":
    sys.exit(main())
