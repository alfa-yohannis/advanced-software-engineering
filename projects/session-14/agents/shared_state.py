"""
Shared State MCP Server for multi-agent communication.

This MCP server provides a task queue mechanism for the orchestrator agent
(Claude) to dispatch tasks to the worker agent (Gemini), and for the worker
to report results back.

Communication flow:
  1. Orchestrator writes a task to tasks/ directory (e.g., task_001.json)
  2. Worker picks up pending tasks, executes them, and writes results
  3. Orchestrator polls for completed tasks and decides next steps

Task states: pending -> in_progress -> completed / failed

Each task file is a JSON object with:
  - id: Unique task identifier
  - type: Task type (e.g., "login", "fill_attendance")
  - status: pending | in_progress | completed | failed
  - params: Input parameters for the task
  - result: Output from the worker (filled after execution)
  - error: Error message if failed
"""

import json
import logging
import time
from pathlib import Path

from mcp.server.fastmcp import FastMCP

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

mcp = FastMCP("shared-state")

# Tasks directory — resolved relative to the project root (three levels up)
_project_root = Path(__file__).resolve().parent.parent
TASKS_DIR = _project_root / "tasks"
TASKS_DIR.mkdir(exist_ok=True)


def _task_path(task_id: str) -> Path:
    """Get the file path for a given task ID."""
    return TASKS_DIR / f"{task_id}.json"


@mcp.tool()
def create_task(task_id: str, task_type: str, params: str) -> str:
    """
    Create a new task for the worker agent to execute.

    Called by the orchestrator to dispatch work to the browser worker.
    The task is written as a JSON file in the tasks/ directory with
    status 'pending'.

    Args:
        task_id: Unique identifier for the task (e.g., 'task_001')
        task_type: Type of task to execute. Supported types:
            - 'login': Login to SIAKAD
            - 'fill_attendance': Fill attendance for a specific date/class
            - 'validate': Take screenshot and validate form submission
            - 'close_browser': Close the browser
        params: JSON string with task parameters (e.g., '{"topik": "...", "deskripsi": "..."}')
    """
    try:
        params_obj = json.loads(params)
    except json.JSONDecodeError:
        return f"Error: Invalid JSON in params: {params}"

    task = {
        "id": task_id,
        "type": task_type,
        "status": "pending",
        "params": params_obj,
        "result": None,
        "error": None,
        "created_at": time.strftime("%Y-%m-%d %H:%M:%S"),
        "updated_at": None,
    }

    path = _task_path(task_id)
    path.write_text(json.dumps(task, indent=2, ensure_ascii=False))
    return f"Task '{task_id}' created with type '{task_type}' and status 'pending'."


@mcp.tool()
def get_task(task_id: str) -> str:
    """
    Get the current state of a task by its ID.

    Called by either agent to check a task's status and result.

    Args:
        task_id: The task identifier to look up
    """
    path = _task_path(task_id)
    if not path.exists():
        return f"Error: Task '{task_id}' not found."

    task = json.loads(path.read_text())
    return json.dumps(task, indent=2, ensure_ascii=False)


@mcp.tool()
def update_task(task_id: str, status: str, result: str = "", error: str = "") -> str:
    """
    Update a task's status and result.

    Called by the worker agent to report progress or completion.

    Args:
        task_id: The task identifier to update
        status: New status ('in_progress', 'completed', or 'failed')
        result: Result data as a JSON string (for completed tasks)
        error: Error message (for failed tasks)
    """
    path = _task_path(task_id)
    if not path.exists():
        return f"Error: Task '{task_id}' not found."

    task = json.loads(path.read_text())
    task["status"] = status
    task["updated_at"] = time.strftime("%Y-%m-%d %H:%M:%S")

    if result:
        try:
            task["result"] = json.loads(result)
        except json.JSONDecodeError:
            task["result"] = result

    if error:
        task["error"] = error

    path.write_text(json.dumps(task, indent=2, ensure_ascii=False))
    return f"Task '{task_id}' updated to status '{status}'."


@mcp.tool()
def list_tasks(status_filter: str = "") -> str:
    """
    List all tasks, optionally filtered by status.

    Called by either agent to see the current task queue.

    Args:
        status_filter: Filter by status ('pending', 'in_progress', 'completed', 'failed').
                      Empty string returns all tasks.
    """
    tasks = []
    for path in sorted(TASKS_DIR.glob("*.json")):
        task = json.loads(path.read_text())
        if not status_filter or task["status"] == status_filter:
            tasks.append(task)

    return json.dumps({
        "total": len(tasks),
        "filter": status_filter or "all",
        "tasks": tasks
    }, indent=2, ensure_ascii=False)


@mcp.tool()
def get_next_pending_task() -> str:
    """
    Get the next pending task from the queue.

    Called by the worker agent to pick up the next task to execute.
    Returns the first task with status 'pending', sorted by filename.
    """
    for path in sorted(TASKS_DIR.glob("*.json")):
        task = json.loads(path.read_text())
        if task["status"] == "pending":
            return json.dumps(task, indent=2, ensure_ascii=False)

    return json.dumps({"message": "No pending tasks."})


@mcp.tool()
def clear_tasks() -> str:
    """
    Remove all task files from the tasks/ directory.

    Called by the orchestrator to reset the task queue before starting
    a new batch of attendance filling.
    """
    count = 0
    for path in TASKS_DIR.glob("*.json"):
        path.unlink()
        count += 1
    return f"Cleared {count} task(s)."


if __name__ == "__main__":
    mcp.run(transport="stdio")
