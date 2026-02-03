# Jalankan perintah berikut untuk sniffing:
#
# BROKER_HOST=127.0.0.1 SUB_PORT=5556 N=10 python3 sniffer.py
#

#!/usr/bin/env python3
import os
import time
import binascii
import zmq

BROKER_HOST = os.getenv("BROKER_HOST", "broker")
SUB_PORT = int(os.getenv("SUB_PORT", "5556"))  # port SUB side (xpub bind)
TOPIC = os.getenv("TOPIC", "")                 # "" = semua topic
N = int(os.getenv("N", "10"))                  # berapa message

ENDPOINT = f"tcp://{BROKER_HOST}:{SUB_PORT}"

def is_probably_jpeg(b: bytes) -> bool:
    return len(b) >= 3 and b[0] == 0xFF and b[1] == 0xD8 and b[2] == 0xFF

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

    s.setsockopt(zmq.SUBSCRIBE, TOPIC.encode("utf-8"))
    s.connect(ENDPOINT)

    topic_label = TOPIC if TOPIC else "(all)"
    print(f"[sniffer] endpoint={ENDPOINT} subscribe={topic_label} messages={N}")
    print("[sniffer] Ctrl+C to stop\n")

    for i in range(N):
        parts = s.recv_multipart()
        print(f"--- msg {i} parts={len(parts)} ---")

        for idx, p in enumerate(parts):
            head_hex = binascii.hexlify(p[:16]).decode()
            print(f"  part[{idx}] len={len(p)} hex16={head_hex} utf8={preview_utf8(p)}")

        # quick heuristics for the common 3-part pattern: [topic, header, jpeg]
        if len(parts) >= 3:
            header_part = parts[1]
            payload_part = parts[2]
            looks_json = preview_utf8(header_part).startswith("'{'") or preview_utf8(header_part).startswith('"{')
            looks_jpeg = is_probably_jpeg(payload_part)

            if looks_json:
                print("  => header looks like JSON (PLAINTEXT)")
            if looks_jpeg:
                print("  => payload looks like JPEG (PLAINTEXT ffd8ff)")
            if (not looks_json) and (not looks_jpeg):
                print("  => header+payload do NOT look like JSON/JPEG (LIKELY ENCRYPTED)")
        print()

        time.sleep(0.05)

if __name__ == "__main__":
    main()

