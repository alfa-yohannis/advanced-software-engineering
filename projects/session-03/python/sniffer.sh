#!/usr/bin/env bash
# Jalankan sniffer.py di dalam jaringan Docker Compose, sebagai pihak yang bisa
# menjangkau broker tetapi tidak punya PAYLOAD_KEY. Container memakai image
# broker (sudah berisi pyzmq). Jalankan pipeline dulu, lalu:
#
#   sudo bash sniffer.sh        # 10 message
#   sudo N=4 bash sniffer.sh    # 4 message
#
cd "$(dirname "$0")" || exit 1

docker compose run --rm --no-deps -T \
  -e BROKER_HOST=broker-session03 \
  -e N="${N:-10}" \
  broker-session03 python - < sniffer.py
