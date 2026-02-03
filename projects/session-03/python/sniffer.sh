sudo docker compose run --rm --no-deps broker \
python - <<'PY'
import zmq, binascii, os

BROKER_HOST = "broker"
SUB_PORT = 5556   # ini port SUB (tempat subscriber connect)
ENDPOINT = f"tcp://{BROKER_HOST}:{SUB_PORT}"

ctx = zmq.Context()
s = ctx.socket(zmq.SUB)
s.setsockopt(zmq.SUBSCRIBE, b"")  # subscribe semua topic
s.connect(ENDPOINT)

print("Sniffing from:", ENDPOINT)
print("Press Ctrl+C to stop\n")

for i in range(5):
    msg = s.recv_multipart()
    print(f"\n--- MESSAGE {i} ---")
    for idx, part in enumerate(msg):
        print(f"Part {idx}: length={len(part)}")

        # coba decode sebagai UTF-8 (JSON seharusnya kebaca)
        try:
            txt = part.decode("utf-8")
            print("  UTF-8 head:", repr(txt[:120]))
        except Exception:
            print("  UTF-8 head: <not utf8>")

        # cetak hex bytes awal (JPEG punya signature khas)
        print("  HEX head :", binascii.hexlify(part[:16]).decode())
PY
