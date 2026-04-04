#!/bin/bash
# Launcher script for the shared state MCP server
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$HOME/venv/bin/python" "$SCRIPT_DIR/shared_state.py"
