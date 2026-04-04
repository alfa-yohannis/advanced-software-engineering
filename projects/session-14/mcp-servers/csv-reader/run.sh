#!/bin/bash
# Launcher script for the CSV reader MCP server
# Resolves paths relative to this script's location
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$HOME/venv/bin/python" "$SCRIPT_DIR/server.py"
