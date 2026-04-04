"""
CSV/Excel Reader MCP Server for SIAKAD Attendance Data.

This MCP (Model Context Protocol) server provides tools for reading and
querying attendance data from CSV or Excel files. It is used alongside the
browser automation MCP server to supply the data that gets filled into SIAKAD.

The expected CSV/Excel format has these columns:
  - no: Session number (optional, for reference)
  - tanggal: Date of the class session (YYYY-MM-DD or DD/MM/YYYY)
  - mata_kuliah: Course name (must match what appears on SIAKAD)
  - kelompok_kelas: Class group (e.g., "Kelas A", "Kelas B", or "-" if not applicable)
  - topik: Lecture topic to fill in the attendance form
  - deskripsi: Lecture description/pembahasan to fill in the attendance form

Column names are matched flexibly (e.g., "date" maps to "tanggal",
"course" maps to "mata_kuliah", etc.).

The file should be stored in the schedule-data/ directory and named after
the course using underscores (e.g., Advanced_Software_Engineering_&_DevOps.csv).

The server runs over stdio transport and is stateless — each tool call
reads the file fresh from disk.
"""

import json
import logging
from pathlib import Path

import pandas as pd
from mcp.server.fastmcp import FastMCP

# Configure logging for debugging MCP server operations
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize the MCP server with a descriptive name
mcp = FastMCP("siakad-csv-reader")


@mcp.tool()
def read_attendance_data(file_path: str) -> str:
    """
    Read all attendance data from a CSV or Excel file.

    Parses the file, normalizes column names, validates that all required
    columns are present, and converts dates to YYYY-MM-DD format.

    Supports flexible column naming:
      - tanggal/date -> tanggal
      - mata_kuliah/mata kuliah/course/matakuliah -> mata_kuliah
      - kelompok_kelas/kelompok kelas/kelas/class_group -> kelompok_kelas
      - topik/topic -> topik
      - deskripsi/description/pembahasan -> deskripsi

    Returns JSON with total_records, columns list, and data array.

    Args:
        file_path: Absolute path to the CSV or Excel file
    """
    path = Path(file_path)
    if not path.exists():
        return f"Error: File not found at {file_path}"

    try:
        # Read file based on extension
        if path.suffix.lower() in (".xlsx", ".xls"):
            df = pd.read_excel(file_path)
        elif path.suffix.lower() == ".csv":
            df = pd.read_csv(file_path)
        else:
            return f"Error: Unsupported file format '{path.suffix}'. Use .csv, .xlsx, or .xls"

        # Normalize column names to lowercase and strip whitespace
        df.columns = [col.strip().lower() for col in df.columns]

        # Build a mapping from standard names to actual column names
        # This allows flexible column naming in the input file
        col_mapping = {}
        for col in df.columns:
            if col in ("no", "number", "nomor"):
                col_mapping["no"] = col
            elif "tanggal" in col or "date" in col:
                col_mapping["tanggal"] = col
            elif col in ("mata_kuliah", "mata kuliah", "course", "matakuliah"):
                col_mapping["mata_kuliah"] = col
            elif col in ("kelompok_kelas", "kelompok kelas", "kelas", "class_group"):
                col_mapping["kelompok_kelas"] = col
            elif "topik" in col or "topic" in col:
                col_mapping["topik"] = col
            elif "deskripsi" in col or "description" in col or "pembahasan" in col:
                col_mapping["deskripsi"] = col
            elif "semester" in col:
                col_mapping["semester"] = col

        # Validate required columns are present
        # 'no', 'kelompok_kelas', and 'semester' are optional
        missing = []
        for required in ["tanggal", "mata_kuliah", "topik", "deskripsi"]:
            if required not in col_mapping:
                missing.append(required)

        if missing:
            return (
                f"Error: Missing required columns: {missing}. "
                f"Found columns: {list(df.columns)}. "
                f"Expected: tanggal/date, mata_kuliah/course, topik/topic, deskripsi/description/pembahasan "
                f"(optional: no, kelompok_kelas/kelas)"
            )

        # Rename columns to standard names for consistent output
        rename_map = {v: k for k, v in col_mapping.items()}
        df = df.rename(columns=rename_map)

        # Convert date column to consistent YYYY-MM-DD string format
        # dayfirst=True handles DD/MM/YYYY input; format="mixed" allows various formats
        df["tanggal"] = pd.to_datetime(df["tanggal"], dayfirst=True, format="mixed").dt.strftime("%Y-%m-%d")

        # Normalize kelompok_kelas: treat "-" and empty/NaN as None (no specific class group)
        if "kelompok_kelas" in df.columns:
            df["kelompok_kelas"] = df["kelompok_kelas"].fillna("").astype(str).str.strip()
            df["kelompok_kelas"] = df["kelompok_kelas"].replace({"": None, "-": None})

        # Convert to list of dictionaries for JSON output
        records = df.to_dict(orient="records")
        return json.dumps({
            "total_records": len(records),
            "columns": list(df.columns),
            "data": records
        }, indent=2, ensure_ascii=False, default=str)
    except Exception as e:
        return f"Error reading file: {str(e)}"


@mcp.tool()
def list_schedule_files(directory: str) -> str:
    """
    List all CSV/Excel schedule files in a directory.

    Each file in the schedule-data/ directory represents a schedule for one
    course (and optionally one class group). The filename convention uses
    underscores for spaces (e.g., Advanced_Software_Engineering_&_DevOps.csv).

    The agent should iterate through all returned files and process each one.

    Args:
        directory: Absolute path to the schedule-data directory
    """
    dir_path = Path(directory)
    if not dir_path.exists():
        return f"Error: Directory not found at {directory}"
    if not dir_path.is_dir():
        return f"Error: {directory} is not a directory"

    files = []
    for ext in ("*.csv", "*.xlsx", "*.xls"):
        for f in sorted(dir_path.glob(ext)):
            files.append({
                "file_path": str(f),
                "filename": f.name,
                "course_name": f.stem.replace("_", " ")
            })

    return json.dumps({
        "directory": directory,
        "total_files": len(files),
        "files": files
    }, indent=2, ensure_ascii=False)


@mcp.tool()
def get_attendance_for_date(file_path: str, date: str) -> str:
    """
    Get attendance records for a specific date.

    Reads the full file and filters to only return records matching the
    given date. Useful when processing one date at a time in the workflow.

    If no records match, returns the list of available dates for guidance.

    Args:
        file_path: Absolute path to the CSV or Excel file
        date: Date to filter by in YYYY-MM-DD format (e.g., '2026-03-02')
    """
    # Reuse read_attendance_data for parsing and validation
    result = read_attendance_data(file_path)
    if result.startswith("Error"):
        return result

    data = json.loads(result)
    # Filter records matching the target date
    matching = [r for r in data["data"] if r["tanggal"] == date]

    if not matching:
        # Return available dates to help the agent correct the date
        available = sorted(set(r['tanggal'] for r in data['data']))
        return f"No attendance records found for date {date}. Available dates: {available}"

    return json.dumps({
        "date": date,
        "records": matching,
        "count": len(matching)
    }, indent=2, ensure_ascii=False, default=str)


@mcp.tool()
def list_attendance_dates(file_path: str) -> str:
    """
    List all unique dates in the attendance file.

    Returns a sorted list of all dates found in the file. Useful for the
    agent to know which dates need to be processed, and to iterate over
    them one by one.

    Args:
        file_path: Absolute path to the CSV or Excel file
    """
    # Reuse read_attendance_data for parsing and validation
    result = read_attendance_data(file_path)
    if result.startswith("Error"):
        return result

    data = json.loads(result)
    # Extract unique dates and sort them chronologically
    dates = sorted(set(r["tanggal"] for r in data["data"]))
    return json.dumps({
        "total_dates": len(dates),
        "dates": dates
    }, indent=2)


# ============================================================================
# Entry point
# Run this file directly to start the MCP server over stdio transport.
# The server will be connected to by Claude CLI or any MCP-compatible client.
# ============================================================================

if __name__ == "__main__":
    mcp.run(transport="stdio")
