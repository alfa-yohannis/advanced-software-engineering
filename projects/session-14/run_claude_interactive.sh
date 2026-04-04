#!/bin/bash
# ============================================================================
# Interactive version - launches Claude CLI in chat mode with MCP servers
# ============================================================================
# Usage: ./run_claude_interactive.sh [path_to_schedule_dir]
#
# Launches Claude CLI in interactive mode so you can guide the agent
# step by step through the attendance filling process.
#
# Configuration (credentials, URLs) is read from config.json by the MCP servers.

# Resolve the project root relative to this script's location
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Read schedule_dir from config.json, fallback to ./schedule-data
CONFIG_FILE="$SCRIPT_DIR/config.json"
if [ -f "$CONFIG_FILE" ] && command -v python3 &>/dev/null; then
    DEFAULT_SCHEDULE_DIR=$(python3 -c "import json,os; c=json.load(open('$CONFIG_FILE')); d=c.get('schedule_dir','./schedule-data'); print(os.path.realpath(os.path.join('$SCRIPT_DIR',d)))")
else
    DEFAULT_SCHEDULE_DIR="$SCRIPT_DIR/schedule-data"
fi

SCHEDULE_DIR="${1:-$DEFAULT_SCHEDULE_DIR}"
SCHEDULE_DIR=$(realpath "$SCHEDULE_DIR")

if [ ! -d "$SCHEDULE_DIR" ]; then
    echo "Error: Schedule directory not found: $SCHEDULE_DIR"
    exit 1
fi

echo "================================================"
echo "  SIAKAD Attendance Auto-Filler (Interactive)"
echo "================================================"
echo "Schedule Dir: $SCHEDULE_DIR"
echo ""
echo "Launching Claude CLI in interactive mode..."
echo "You can guide the agent step by step."
echo ""

cd "$SCRIPT_DIR"

# Generate MCP config file with absolute paths.
MCP_CONFIG_FILE=$(mktemp /tmp/claude_mcp_XXXXXX.json)
cat > "$MCP_CONFIG_FILE" <<EOF
{
  "mcpServers": {
    "siakad-browser": {
      "command": "$SCRIPT_DIR/mcp-servers/browser-automation/run.sh",
      "env": { "DISPLAY": "${DISPLAY:-:0}" }
    },
    "siakad-csv-reader": {
      "command": "$SCRIPT_DIR/mcp-servers/csv-reader/run.sh"
    },
    "shared-state": {
      "command": "$SCRIPT_DIR/agents/run_shared_state.sh"
    }
  }
}
EOF
trap "rm -f '$MCP_CONFIG_FILE'" EXIT

# Use --prompt-interactive (-i) to pass the initial prompt and stay in chat mode.
claude --mcp-config "$MCP_CONFIG_FILE" --dangerously-skip-permissions -i "
I need to fill Berita Acara Perkuliahan on SIAKAD.
The schedule files are in: ${SCHEDULE_DIR}
Credentials and URLs are pre-configured in config.json — just call login_siakad without arguments.

IMPORTANT RULES:
- One step at a time: wait for each browser action result before the next.
- After setting semester or date, wait for the loader (div.box_loader) to disappear.
- Always OVERWRITE existing topik and deskripsi values.
- If submit_attendance_form fails, use run_js to call save_pembahasan().
- After saving, verify 'Sukses update topik pembahasan' message appears.

Please start by listing all schedule files, then launch browser and login to SIAKAD.
"
