# SIAKAD Attendance Auto-Filler — Multi-Agent System

## Architecture (Sequential Two-Phase)

```
Phase 1: Claude ──[csv-reader MCP]──> reads CSV ──[shared-state MCP]──> creates tasks/*.json
Phase 2: Gemini ──[shared-state MCP]──> reads tasks ──[browser MCP]──> fills SIAKAD forms
```

- **Claude** = Orchestrator: reads schedule data, creates ALL task files, then exits
- **Gemini** = Browser Worker: processes all pre-created tasks sequentially
- **Communication**: task JSON files in `tasks/` directory (no polling)

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
