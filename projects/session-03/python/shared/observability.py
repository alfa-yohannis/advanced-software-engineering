# obs_prom.py
"""
Shared Prometheus observability module for your ZMQ video pipeline.

Usage (in each service):
  import obs_prom as obs
  obs.init(service="node_a")   # or "broker" / "transformer" / "web_server"
  # then use obs.FRAMES_OUT.labels(...).inc(), etc.

Env vars (optional):
  METRICS_PORT=9101
  INSTANCE=<name>   (defaults to HOSTNAME or "local")
"""

from __future__ import annotations

import os
import threading
import time
from typing import Optional

from prometheus_client import Counter, Gauge, Histogram, start_http_server

try:
    import psutil  # optional but recommended
except Exception:  # pragma: no cover
    psutil = None  # type: ignore


# -----------------------------
# Labels / runtime identity
# -----------------------------
SERVICE: str = os.getenv("SERVICE", "unknown")
INSTANCE: str = os.getenv("INSTANCE", os.getenv("HOSTNAME", "local"))
METRICS_PORT: int = int(os.getenv("METRICS_PORT", "9100"))

_started = False


# -----------------------------
# Core metrics (shared)
# -----------------------------
FRAMES_IN = Counter(
    "frames_in_total",
    "Frames received (events/messages)",
    ["service", "instance", "topic"],
)
FRAMES_OUT = Counter(
    "frames_out_total",
    "Frames sent (events/messages)",
    ["service", "instance", "topic"],
)

BYTES_IN = Counter(
    "bytes_in_total",
    "Bytes received (payload bytes; best-effort)",
    ["service", "instance", "topic"],
)
BYTES_OUT = Counter(
    "bytes_out_total",
    "Bytes sent (payload bytes; best-effort)",
    ["service", "instance", "topic"],
)

ERRORS = Counter(
    "errors_total",
    "Errors (count)",
    ["service", "instance", "where", "type"],
)

STAGE_SECONDS = Histogram(
    "stage_seconds",
    "Stage latency in seconds",
    ["service", "instance", "stage"],
    buckets=(0.001, 0.003, 0.005, 0.01, 0.02, 0.05, 0.1, 0.2, 0.5, 1, 2, 5),
)

JPEG_BYTES = Histogram(
    "jpeg_bytes",
    "JPEG payload size in bytes",
    ["service", "instance", "direction"],  # direction: in|out
    buckets=(10_000, 30_000, 60_000, 120_000, 250_000, 500_000, 1_000_000, 2_000_000),
)

# End-to-end latency measured at Node C using Node A header ts_capture
E2E_SECONDS = Histogram(
    "e2e_seconds",
    "End-to-end latency seconds (capture->node_c_ingest)",
    ["service", "instance"],
    buckets=(0.01, 0.02, 0.05, 0.1, 0.2, 0.5, 1, 2, 5),
)

# Web server only (optional)
MJPEG_CLIENTS = Gauge(
    "mjpeg_clients",
    "Active MJPEG clients",
    ["service", "instance"],
)

# Process stats (optional)
PROC_RSS_BYTES = Gauge(
    "process_rss_bytes",
    "Process RSS bytes",
    ["service", "instance"],
)
PROC_CPU_PERCENT = Gauge(
    "process_cpu_percent",
    "Process CPU percent",
    ["service", "instance"],
)


def init(
    service: Optional[str] = None,
    instance: Optional[str] = None,
    metrics_port: Optional[int] = None,
    enable_process_stats: bool = True,
) -> None:
    """
    Start /metrics HTTP server and set service identity.

    Call once per process, early in main().
    """
    global SERVICE, INSTANCE, METRICS_PORT, _started

    if service:
        SERVICE = service
    if instance:
        INSTANCE = instance
    if metrics_port is not None:
        METRICS_PORT = int(metrics_port)

    if _started:
        return

    start_http_server(METRICS_PORT)
    _started = True

    if enable_process_stats and psutil is not None:
        _start_process_heartbeat()


def _start_process_heartbeat() -> None:
    """
    Background thread exporting cpu% and rss.
    """
    def run():
        try:
            p = psutil.Process()
            # prime cpu measurement
            p.cpu_percent(interval=None)
            while True:
                try:
                    PROC_RSS_BYTES.labels(SERVICE, INSTANCE).set(p.memory_info().rss)
                    PROC_CPU_PERCENT.labels(SERVICE, INSTANCE).set(p.cpu_percent(interval=None))
                except Exception:
                    pass
                time.sleep(1.0)
        except Exception:
            return

    t = threading.Thread(target=run, daemon=True)
    t.start()


# -----------------------------
# Convenience helpers (optional)
# -----------------------------
def topic_str(topic: bytes | str) -> str:
    if isinstance(topic, bytes):
        return topic.decode("utf-8", "ignore")
    return topic


def observe_stage(stage: str, seconds: float) -> None:
    STAGE_SECONDS.labels(SERVICE, INSTANCE, stage).observe(seconds)


def inc_error(where: str, err_type: str, n: int = 1) -> None:
    ERRORS.labels(SERVICE, INSTANCE, where, err_type).inc(n)
