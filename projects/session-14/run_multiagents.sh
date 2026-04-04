#!/bin/bash
# ============================================================================
# Multi-Agent SIAKAD Attendance Auto-Filler
# ============================================================================
# Usage: ./run_multiagents.sh [path_to_schedule_dir]
#
# This script launches a multi-agent system with:
#   - Claude (Orchestrator): Reads schedule data, plans execution, dispatches tasks
#   - Gemini (Browser Worker): Executes browser automation, fills forms, validates
#
# Communication between agents is via a shared task queue (JSON files in tasks/).
#
# Architecture:
#   Claude ──[csv-reader MCP]──> Schedule CSV files
#   Claude ──[shared-state MCP]──> tasks/*.json <──[shared-state MCP]── Gemini
#   Gemini ──[browser MCP]──> SIAKAD Website
#
# Configuration (credentials, URLs) is in config.json.

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
    echo "Usage: ./run_multiagents.sh [path_to_schedule_dir]"
    exit 1
fi

# Clean up previous tasks
rm -f "$SCRIPT_DIR/tasks/"*.json 2>/dev/null
mkdir -p "$SCRIPT_DIR/tasks"

echo "============================================================"
echo "  SIAKAD Multi-Agent Attendance Auto-Filler"
echo "============================================================"
echo ""
echo "  Orchestrator : Claude (reads CSV, plans, dispatches tasks)"
echo "  Browser Worker: Gemini (browser automation, form filling)"
echo "  Communication : Shared task queue (tasks/*.json)"
echo ""
echo "  Schedule Dir  : $SCHEDULE_DIR"
echo "  Files found   :"
ls -1 "$SCHEDULE_DIR"/*.csv "$SCHEDULE_DIR"/*.xlsx 2>/dev/null | sed 's/^/    /'
echo ""
echo "============================================================"
echo ""

cd "$SCRIPT_DIR"

# Read the worker and orchestrator prompts
WORKER_PROMPT=$(cat "$SCRIPT_DIR/agents/worker_prompt.md")
ORCHESTRATOR_PROMPT=$(cat "$SCRIPT_DIR/agents/orchestrator_prompt.md")

# Ensure Gemini MCP servers are registered for the worker
echo "Registering Gemini MCP servers (worker)..."
gemini mcp add --scope project siakad-browser "$SCRIPT_DIR/mcp-servers/browser-automation/run.sh" -e DISPLAY=:0
gemini mcp add --scope project shared-state "$SCRIPT_DIR/agents/run_shared_state.sh"
echo ""

# --- Step 1: Start Gemini (Browser Worker) in the background ---
echo "[1/2] Starting Gemini (Browser Worker)..."

#model options: https://ai.google.dev/gemini/docs/models/overview
# /model
# 1. gemini-3.1-pro-preview                                             │
# 2. gemini-3-flash-preview                                             │
# 3. gemini-3.1-flash-lite-preview                                      │
# 4. gemini-2.5-pro                                                     │
# 5. gemini-2.5-flash                                                   │
# 6. gemini-2.5-flash-lite
# gemini --yolo --model gemini-3.1-pro-preview  --prompt "
# gemini --yolo --model gemini-1.5-flash --prompt "

gemini --yolo --model gemini-3-flash-preview  --prompt "

$WORKER_PROMPT

You are now running as the browser worker agent. Start processing tasks immediately.

Instructions:
1. Call get_next_pending_task to check for tasks.
2. If no tasks yet, wait a moment and try again (the orchestrator is creating tasks).
3. Process each task as described in your instructions.
4. After completing a task, immediately check for the next one.
5. Keep polling until you see a 'close_browser' task, execute it, then stop.

Begin now — poll for tasks.
" &
GEMINI_PID=$!
echo "  Gemini PID: $GEMINI_PID"

# Give Gemini a moment to start up and connect to MCP servers
sleep 3

# --- Step 2: Start Claude (Orchestrator) ---
echo "[2/2] Starting Claude (Orchestrator)..."

# Generate MCP config file with absolute paths for Claude.
# Claude CLI resolves .claude/settings.json from the git root, which may not
# be session-11/. We use --mcp-config to pass a temp config file explicitly.
MCP_CONFIG_FILE=$(mktemp /tmp/claude_mcp_XXXXXX.json)
cat > "$MCP_CONFIG_FILE" <<MCPEOF
{
  "mcpServers": {
    "siakad-csv-reader": {
      "command": "$SCRIPT_DIR/mcp-servers/csv-reader/run.sh"
    },
    "shared-state": {
      "command": "$SCRIPT_DIR/agents/run_shared_state.sh"
    }
  }
}
MCPEOF
trap "rm -f '$MCP_CONFIG_FILE'" EXIT

cat <<CLAUPROMPT | claude -p --mcp-config "$MCP_CONFIG_FILE" --dangerously-skip-permissions --verbose
$ORCHESTRATOR_PROMPT

You are now running as the orchestrator agent. The browser worker (Gemini) is already
running and polling for tasks.

Schedule directory: $SCHEDULE_DIR

Execute the workflow:
1. Clear previous tasks with clear_tasks.
2. List and read all schedule files from the directory above.
3. Create a 'login' task (task_001) and wait for it to complete.
4. For each attendance record, create a 'fill_attendance' task and wait for completion.
5. After all records are processed, create a 'close_browser' task.
6. Report a final summary of all tasks (completed, failed, skipped).

Start now.
CLAUPROMPT

# --- Cleanup ---
echo ""
echo "Orchestrator finished. Waiting for Gemini worker to complete..."
wait $GEMINI_PID 2>/dev/null
echo "Done. All agents finished."
