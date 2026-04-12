#!/bin/bash
# ============================================================================
# Single-Agent SIAKAD Attendance Auto-Filler (Gemini only)
# ============================================================================
# Usage: ./run_gemini_only.sh [path_to_schedule_dir]
#
# This script runs in single-agent mode where Gemini handles everything
# (reading CSV, browser automation, form filling) using its tools.
#
# For multi-agent mode (Claude + Gemini), use ./run_multiagent.sh instead.

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
    echo "Usage: ./run_gemini_only.sh [path_to_schedule_dir]"
    exit 1
fi

echo "================================================"
echo "  SIAKAD Attendance Auto-Filler (Gemini Only)"
echo "================================================"
echo "Schedule Dir: $SCHEDULE_DIR"
echo "Files found:"
ls -1 "$SCHEDULE_DIR"/*.csv "$SCHEDULE_DIR"/*.xlsx 2>/dev/null | sed 's/^/  /'
echo ""
echo "Starting Gemini CLI agent..."
echo ""

cd "$SCRIPT_DIR"

# Write Gemini MCP settings directly (gemini mcp add overwrites on each call,
# so only the last server survives — we must write both servers at once).
echo "Ensuring Gemini MCP servers are registered..."
mkdir -p "$SCRIPT_DIR/.gemini"
cat > "$SCRIPT_DIR/.gemini/settings.json" <<GEMINI_MCP_EOF
{
  "mcpServers": {
    "siakad-browser": {
      "command": "$SCRIPT_DIR/mcp-servers/browser-automation/run.sh",
      "args": [],
      "env": {
        "DISPLAY": ":0"
      }
    },
    "siakad-csv-reader": {
      "command": "$SCRIPT_DIR/mcp-servers/csv-reader/run.sh",
      "args": []
    }
  }
}
GEMINI_MCP_EOF
echo "  Wrote .gemini/settings.json with both siakad-browser and siakad-csv-reader"
echo ""

#model options: https://ai.google.dev/gemini/docs/models/overview
# /model
# 1. gemini-3.1-pro-preview                                             │
# 2. gemini-3-flash-preview                                             │
# 3. gemini-3.1-flash-lite-preview                                      │
# 4. gemini-2.5-pro                                                     │
# 5. gemini-2.5-flash                                                   │
# 6. gemini-2.5-flash-lite
# gemini --yolo --model gemini-3.1-pro-preview  --prompt "
gemini --yolo --model gemini-3-flash-preview  --prompt "
You are an attendance-filling agent running in single-agent mode.
You have direct access to both the CSV reader and browser automation tools.

Schedule directory: ${SCHEDULE_DIR}
Credentials and URLs are loaded from config.json by the browser MCP server.

CRITICAL RELIABILITY RULES:
1. Explain Before Acting: ALWAYS provide a short, one-sentence explanation of what you are doing right before you call a tool. This acts as a progress log for the user. Do not call tools in silence.
2. One Step at a Time: Do NOT chain multiple browser interaction tools in a single turn. Wait for the result of each step before proceeding to the next.
3. Date Format: Dates in the CSV might be DD/MM/YYYY or YYYY-MM-DD. Always convert them to YYYY-MM-DD for the 'set_date_picker' tool.
4. Handle Loader: After setting a date or a semester, ALWAYS check if a loader (e.g., div.box_loader) is visible. Use 'run_js' to wait for it to disappear: 'while(document.querySelector(\".box_loader\")) { await new Promise(r => setTimeout(r, 500)); }'.
5. Overwrite Policy: ALWAYS replace existing Topic (Topik) and Description (Deskripsi/Pembahasan) values with the ones from the schedule data. Tools like 'fill_attendance_form' or 'fill_input' automatically clear existing text before typing.
6. Saving: The 'submit_attendance_form' tool may fail if the button is hidden. If it fails, use 'run_js' to call 'save_pembahasan()' or click the button with text 'Save Pembahasan'.

Follow these steps for EACH record:
1. navigate_to_daftar_hadir.
2. SELECT SEMESTER: Use 'set_semester' with the semester name (e.g., '2025/2026 GENAP'). After calling, ALWAYS wait for the loader (div.box_loader) max 3 seconds to disappear using 'run_js'.
3. SELECT DATE: Use 'set_date_picker' with the date (YYYY-MM-DD). After calling, ALWAYS wait for the loader (div.box_loader) max 3 seconds to disappear using 'run_js'.
4. get_schedule_list and find_schedule_row.
5. click_absensi_button.
6. fill_attendance_form and Save.
7. VERIFY SUCCESS: Check for the 'Sukses update topik pembahasan' notification.
#8. Take Screenshot to document success.

Start by listing and reading the schedule files. Report a summary when all records are processed.
"
