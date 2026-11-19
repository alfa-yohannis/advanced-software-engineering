#!/usr/bin/env bash
# Runs sniffer.py inside the Docker Compose network, as a party that can reach
# the broker but does not have PAYLOAD_KEY. The container uses the broker's
# image, which already contains pyzmq. Start the pipeline first, then:
#
#   sudo bash sniffer.sh        # 10 messages
#   sudo N=4 bash sniffer.sh    # 4 messages
#
cd "$(dirname "$0")" || exit 1

docker compose run --rm --no-deps -T \
  -e BROKER_HOST=broker-session03 \
  -e N="${N:-10}" \
  broker-session03 python - < sniffer.py
