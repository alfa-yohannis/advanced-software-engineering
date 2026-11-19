"""Start the three MCP servers the way the agents do, and check them without SIAKAD.

Each server is started through its run.sh launcher and spoken to over stdio,
like Claude CLI and Gemini CLI do. The script lists the tools of every server,
reads the schedule with the CSV reader, and creates, updates and deletes two
tasks in the shared task queue. It never opens the browser or SIAKAD.

Usage, from the session-14 folder:
    ../.venv/bin/python check_mcp_servers.py [schedule_file]
"""

import asyncio
import json
import sys
from pathlib import Path

from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

ROOT = Path(__file__).resolve().parent
SCHEDULE = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else (
    ROOT / "schedule-data" / "advanced_software_engineering_and_devops.csv")


async def start(launcher: str, check) -> None:
    params = StdioServerParameters(command=str(ROOT / launcher), args=[], cwd=str(ROOT))
    async with stdio_client(params) as (read, write):
        async with ClientSession(read, write) as session:
            info = await session.initialize()
            tools = (await session.list_tools()).tools
            print(f"{info.serverInfo.name}: {len(tools)} tools")
            print("  " + ", ".join(tool.name for tool in tools))
            if check:
                await check(session)


async def call(session: ClientSession, tool: str, **args) -> str:
    result = await session.call_tool(tool, args)
    return result.content[0].text if result.content else ""


async def check_csv_reader(session: ClientSession) -> None:
    data = json.loads(await call(session, "read_attendance_data", file_path=str(SCHEDULE)))
    print(f"  {SCHEDULE.name}: {data['total_records']} records")
    for record in data["data"][:3]:
        print(f"    {record['tanggal']}  {record['topik']}")
    if data["total_records"] > 3:
        print("    ...")


async def check_shared_state(session: ClientSession) -> None:
    print("  " + await call(session, "clear_tasks"))
    print("  " + await call(session, "create_task", task_id="task_001", task_type="login", params="{}"))
    print("  " + await call(session, "create_task", task_id="task_002", task_type="close_browser", params="{}"))
    print("  " + await call(session, "update_task", task_id="task_001", status="completed", result="check only"))
    pending = json.loads(await call(session, "get_next_pending_task"))
    print(f"  next pending task: {pending['id']} ({pending['type']})")
    print("  " + await call(session, "clear_tasks"))


async def main() -> None:
    await start("mcp-servers/csv-reader/run.sh", check_csv_reader)
    await start("agents/run_shared_state.sh", check_shared_state)
    await start("mcp-servers/browser-automation/run.sh", None)


if __name__ == "__main__":
    asyncio.run(main())
