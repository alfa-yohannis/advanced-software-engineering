# SIAKAD Attendance Auto-Filler — Multi-Agent System

## Architecture

```
Claude (Orchestrator) ──[csv-reader MCP]──> Schedule CSV files
Claude (Orchestrator) ──[shared-state MCP]──> tasks/*.json <──[shared-state MCP]── Gemini (Worker)
                                                               Gemini (Worker) ──[browser MCP]──> SIAKAD
```

- **Claude** = Orchestrator: reads schedule data, plans execution, dispatches tasks
- **Gemini** = Browser Worker: executes browser automation, fills forms, validates results
- **Communication**: shared task queue via JSON files in `tasks/` directory

## MCP Servers

| Server | Used By | Purpose |
|--------|---------|---------|
| siakad-csv-reader | Claude | Read schedule CSV/Excel files |
| siakad-browser | Gemini | Playwright browser automation |
| shared-state | Both | Task queue for inter-agent communication |

## Schedule Data

Files in `schedule-data/`, one per course. Lowercase filenames with underscores.

CSV columns: `no`, `semester`, `tanggal`, `mata_kuliah`, `kelompok_kelas`, `topik`, `deskripsi`

## Configuration

All credentials and URLs are in `config.json`.

## Running

```bash
./run_multiagents.sh                   # Multi-agent mode (Claude + Gemini)
./run_claude_only.sh                   # Single-agent mode (Claude only)
./run_claude_interactive.sh            # Interactive single-agent mode (Claude)
./run_gemini_only.sh                   # Single-agent mode (Gemini only)
```
