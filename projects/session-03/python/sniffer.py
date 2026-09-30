#!/usr/bin/env python3
# Jalankan perintah berikut untuk sniffing dari host (port 5556 broker dibuka ke host):
#
# BROKER_HOST=127.0.0.1 SUB_PORT=5556 N=10 ../../.venv/bin/python sniffer.py
#
# Atau dari dalam jaringan Docker Compose, tanpa Python di host:
#
# bash sniffer.sh
#

import json
import os
import sys
import time
import binascii
import zmq

BROKER_HOST = os.getenv("BROKER_HOST", "broker-session03")  # nama service di docker-compose
SUB_PORT = int(os.getenv("SUB_PORT", "5556"))  # port SUB side (xpub bind)
TOPIC = os.getenv("TOPIC", "")                 # "" = semua topic
N = int(os.getenv("N", "10"))                  # berapa message
TIMEOUT_SEC = int(os.getenv("TIMEOUT_SEC", "10"))  # berhenti bila tidak ada message

ENDPOINT = f"tcp://{BROKER_HOST}:{SUB_PORT}"

def is_probably_jpeg(b: bytes) -> bool:
    return len(b) >= 3 and b[0] == 0xFF and b[1] == 0xD8 and b[2] == 0xFF

def is_json_object(b: bytes) -> bool:
    try:
        return isinstance(json.loads(b), dict)
    except ValueError:  # termasuk UnicodeDecodeError dan JSONDecodeError
        return False

def preview_utf8(b: bytes, limit: int = 120) -> str:
    try:
        s = b.decode("utf-8")
        return repr(s[:limit])
    except Exception:
        return "<not utf8>"

def main():
    ctx = zmq.Context.instance()
    s = ctx.socket(zmq.SUB)
    s.setsockopt(zmq.LINGER, 0)
    # ZMQ tidak melapor bila host salah atau broker mati, recv() hanya menunggu
    s.setsockopt(zmq.RCVTIMEO, TIMEOUT_SEC * 1000)

    s.setsockopt(zmq.SUBSCRIBE, TOPIC.encode("utf-8"))
    s.connect(ENDPOINT)

    topic_label = TOPIC if TOPIC else "(all)"
    print(f"[sniffer] endpoint={ENDPOINT} subscribe={topic_label} messages={N}")
    print("[sniffer] Ctrl+C to stop\n")

    for i in range(N):
        try:
            parts = s.recv_multipart()
        except zmq.Again:
            print(f"[sniffer] no message within {TIMEOUT_SEC}s from {ENDPOINT}.")
            print("[sniffer] Is the pipeline running? From the host, use BROKER_HOST=127.0.0.1.")
            return 1
        print(f"--- msg {i} parts={len(parts)} ---")

        for idx, p in enumerate(parts):
            head_hex = binascii.hexlify(p[:16]).decode()
            print(f"  part[{idx}] len={len(p)} hex16={head_hex} utf8={preview_utf8(p)}")

        # quick heuristics for the common 3-part pattern: [topic, header, jpeg]
        if len(parts) >= 3:
            header_part = parts[1]
            payload_part = parts[2]
            looks_json = is_json_object(header_part)
            looks_jpeg = is_probably_jpeg(payload_part)

            if looks_json:
                print("  => header looks like JSON (PLAINTEXT)")
            if looks_jpeg:
                print("  => payload looks like JPEG (PLAINTEXT ffd8ff)")
            if (not looks_json) and (not looks_jpeg):
                print("  => header+payload do NOT look like JSON/JPEG (LIKELY ENCRYPTED)")
        print()

        time.sleep(0.05)

    return 0

if __name__ == "__main__":
    sys.exit(main())
