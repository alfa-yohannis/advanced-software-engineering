#!/bin/bash
# ============================================================================
# Single-Agent SIAKAD Attendance Auto-Filler (Claude only)
# ============================================================================
# Usage: ./run_claude_only.sh [path_to_schedule_dir]
#
# This script runs in single-agent mode where Claude handles everything
# (reading CSV, browser automation, form filling) using all MCP servers.
#
# For multi-agent mode (Claude + Gemini), use ./run_multiagents.sh instead.
# For Gemini-only mode, use ./run_gemini_only.sh instead.

# Resolve paths relative to this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Read schedule_dir from config.json
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
    echo "Usage: ./run_claude_only.sh [path_to_schedule_dir]"
    exit 1
fi

echo "================================================"
echo "  SIAKAD Attendance Auto-Filler (Claude Only)"
echo "================================================"
echo "Schedule Dir: $SCHEDULE_DIR"
echo "Files found:"
ls -1 "$SCHEDULE_DIR"/*.csv "$SCHEDULE_DIR"/*.xlsx 2>/dev/null | sed 's/^/  /'
echo ""
echo "Starting Claude CLI agent..."
echo ""

cd "$SCRIPT_DIR"

# Generate MCP config file with absolute paths.
# Claude CLI resolves .claude/settings.json from the git root, which may not
# be session-11/. We use --mcp-config to pass a temp config file explicitly.
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

# Pipe the prompt via stdin. --mcp-config is a variadic flag that consumes
# all following positional arguments, so the prompt must come via stdin.
cat <<PROMPT | claude -p --mcp-config "$MCP_CONFIG_FILE" --dangerously-skip-permissions --verbose
You are an attendance-filling agent running in single-agent mode.
You have direct access to both the CSV reader and browser automation tools.

Schedule directory: ${SCHEDULE_DIR}
Credentials and URLs are loaded from config.json by the browser MCP server.

CRITICAL RELIABILITY RULES:
1. One Step at a Time: Do NOT chain multiple browser interaction tools in a single turn. Wait for the result of each step before proceeding to the next.
2. Date Format: Dates in the CSV might be DD/MM/YYYY or YYYY-MM-DD. Always convert them to YYYY-MM-DD for the 'set_date_picker' tool.
3. Handle Loader: After setting a date or a semester, ALWAYS check if a loader (e.g., div.box_loader) is visible. Use 'run_js' to wait for it to disappear. The page refreshes automatically when these fields change.
4. Overwrite Policy: ALWAYS replace existing Topic (Topik) and Description (Deskripsi/Pembahasan) values with the ones from the schedule data. Tools like 'fill_attendance_form' or 'fill_input' automatically clear existing text before typing.
5. Saving: The 'submit_attendance_form' tool may fail if the button is hidden. If it fails, use 'run_js' to call 'save_pembahasan()' or click the button with text 'Save Pembahasan'.
6. Display on the prompt the step you are currently working on, and the filename and record details (e.g., date, course) being processed

Follow these steps for EACH record:
1. navigate_to_daftar_hadir (Start fresh for each record to avoid modal/context issues).
2. set_semester (Select the semester if specified). Wait for loader to disappear as the page refreshes automatically.
3. set_date_picker (Format: YYYY-MM-DD). Wait for loader to disappear as the page refreshes automatically.
4. get_schedule_list to verify the table has loaded correctly.
5. find_schedule_row and then click_absensi_button.
6. fill_attendance_form. This will OVERWRITE any existing values. If fields aren't found, use 'fill_input' with IDs 'topik_pembahasan' and 'deskripsi_pembahasan'.
7. Save the form (use submit_attendance_form, and if it fails, use 'run_js' to call 'save_pembahasan()').
8. VERIFY SUCCESS: After saving, wait and check for the notification message 'Sukses update topik pembahasan' appearing on the page. Use 'get_page_html' or 'run_js' to confirm the text is present.
9. Take a screenshot to verify success and documentation.

Start by listing and reading the schedule files. Report a summary when all records are processed.
PROMPT
