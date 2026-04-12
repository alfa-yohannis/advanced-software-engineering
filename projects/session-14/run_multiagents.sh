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

# Write Gemini MCP settings directly (gemini mcp add overwrites on each call,
# so only the last server survives — we must write both servers at once).
echo "Registering Gemini MCP servers (worker)..."
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
    "shared-state": {
      "command": "$SCRIPT_DIR/agents/run_shared_state.sh",
      "args": []
    }
  }
}
GEMINI_MCP_EOF
echo "  Wrote .gemini/settings.json with both siakad-browser and shared-state"
echo ""

# ============================================================================
# Phase 1: Claude (Orchestrator) — batch-create all tasks
# ============================================================================
echo "[Phase 1/2] Claude (Orchestrator) — reading CSV and creating tasks..."
echo ""

# Generate MCP config file with absolute paths for Claude.
# Claude CLI resolves .claude/settings.json from the git root, which may not
# be session-14/. We use --mcp-config to pass a temp config file explicitly.
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

You are now running as the orchestrator agent.

Schedule directory: $SCHEDULE_DIR

Execute the workflow:
1. Clear previous tasks with clear_tasks.
2. List and read all schedule files from the directory above.
3. Create a 'login' task (task_001).
4. For each attendance record, create a 'fill_attendance' task (task_002, task_003, ...).
5. Create a 'close_browser' task as the final task.
6. Report the full task manifest (ID, type, params summary).

Do NOT poll or wait. Just create all tasks and finish.

Start now.
CLAUPROMPT

CLAUDE_EXIT=$?
echo ""

if [ $CLAUDE_EXIT -ne 0 ]; then
    echo "Error: Claude orchestrator failed (exit $CLAUDE_EXIT). Aborting."
    exit 1
fi

# Verify tasks were created
TASK_COUNT=$(ls -1 "$SCRIPT_DIR/tasks/"*.json 2>/dev/null | wc -l)
echo "Phase 1 complete: $TASK_COUNT task(s) created."
echo ""

if [ "$TASK_COUNT" -eq 0 ]; then
    echo "Error: No tasks were created. Check orchestrator output above."
    exit 1
fi

# ============================================================================
# Phase 2: Gemini (Browser Worker) — process all tasks sequentially
# ============================================================================
echo "[Phase 2/2] Gemini (Browser Worker) — processing tasks..."
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
# gemini --yolo --model gemini-1.5-flash --prompt "

gemini --yolo --model gemini-3-flash-preview  --prompt "

$WORKER_PROMPT

You are now running as the browser worker agent.
All tasks have been pre-created by the orchestrator.
Explain Before Acting: ALWAYS provide a short, one-sentence explanation of what you are doing right before you call a tool. This acts as a progress log for the user. Do not call tools in silence.

Instructions:
1. Call get_all_tasks_ordered to get the full list of tasks.
2. Process each task IN ORDER (login first, then fill_attendance tasks, then close_browser).
3. For each task: mark as in_progress, execute it, then mark as completed or failed.
4. If a fill_attendance task fails, retry up to 3 times before moving on.
5. After the last task (close_browser), report a summary of results.

Begin now — get the task list and start processing.
"

# --- Cleanup ---
echo ""
echo "Done. All agents finished."

