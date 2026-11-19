#!/bin/bash
# Launcher script for the shared state MCP server
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Python of the course's virtual environment, projects/.venv, which has the
# packages of requirements.txt. Set SIAKAD_PYTHON to use another Python.
PYTHON="${SIAKAD_PYTHON:-$SCRIPT_DIR/../../.venv/bin/python}"
exec "$PYTHON" "$SCRIPT_DIR/shared_state.py"
