"""
Browser Automation MCP Server for SIAKAD Attendance System.

This MCP (Model Context Protocol) server provides browser automation tools
using Playwright to interact with the SIAKAD attendance system at Pradita University.
It exposes tools that an AI agent (Claude CLI / Gemini CLI) can call to:
  - Launch a browser and login to SIAKAD
  - Navigate to the daftar hadir (attendance) page
  - Set dates, find schedule rows, and click absensi buttons
  - Fill in topik and deskripsi fields in the attendance popup
  - Submit forms and take screenshots for debugging

The server runs over stdio transport and maintains a single browser session
across all tool calls via global state.
"""

import asyncio
import json
import logging
from datetime import datetime
from pathlib import Path

from mcp.server.fastmcp import FastMCP
from playwright.async_api import async_playwright, Browser, BrowserContext, Page

# Configure logging for debugging MCP server operations
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize the MCP server with a descriptive name
mcp = FastMCP("siakad-browser")

# ============================================================================
# Configuration
# Load URLs and credentials from config.json in the project root.
# The config file is located two directories up from this server script.
# ============================================================================
_config_path = Path(__file__).resolve().parent.parent.parent / "config.json"
with open(_config_path) as f:
    _config = json.load(f)

SIAKAD_LOGIN_URL = _config["siakad"]["login_url"]
SIAKAD_DAFTAR_HADIR_URL = _config["siakad"]["daftar_hadir_url"]
SIAKAD_USERNAME = _config["siakad"]["username"]
SIAKAD_PASSWORD = _config["siakad"]["password"]

# ============================================================================
# Global browser state
# These variables maintain a single browser session that persists across
# multiple tool calls within the same MCP server lifecycle.
# ============================================================================
_playwright = None
_browser: Browser | None = None
_context: BrowserContext | None = None
_page: Page | None = None


async def _ensure_browser() -> Page:
    """
    Lazily initialize the browser and return the active page.

    If no browser is running (or the page was closed), this function starts
    a new Playwright instance, launches Chromium in visible (non-headless) mode
    with a 500ms slow_mo for easier visual debugging, and creates a new page
    with Indonesian locale.
    
    The viewport width is set to 65% of the monitor's width.

    Returns:
        The active Playwright Page object.
    """
    global _playwright, _browser, _context, _page
    if _page is None or _page.is_closed():
        _playwright = await async_playwright().start()
        
        # We need to find the monitor width to calculate 65%.
        # We'll launch a temporary browser to get the screen size.
        temp_browser = await _playwright.chromium.launch(headless=True)
        temp_page = await temp_browser.new_page()
        screen_width = await temp_page.evaluate("window.screen.width")
        await temp_browser.close()
        
        # Fallback to 1920 if we couldn't get a valid width
        if not screen_width or screen_width < 800:
            screen_width = 1920
            
        target_width = int(screen_width * 0.65)
        # Use a standard height or 90% of screen height
        target_height = 900
        
        # headless=False so the user can watch the automation in real-time
        # slow_mo=100 adds 100ms delay between actions for faster execution
        _browser = await _playwright.chromium.launch(
            headless=False, 
            slow_mo=100,
            args=[
                f"--window-size={target_width},{target_height}",
                "--window-position=0,0"  # Position on the left side of the screen
            ]
        )
        _context = await _browser.new_context(
            viewport={"width": target_width, "height": target_height},
            locale="en-US",  # US locale as requested
        )
        _page = await _context.new_page()
    return _page


# ============================================================================
# Core SIAKAD workflow tools
# These tools implement the main attendance-filling workflow steps.
# ============================================================================


@mcp.tool()
async def launch_browser() -> str:
    """
    Launch a Chromium browser instance.

    This must be called first before any other browser action. Opens a visible
    browser window that the user can observe during automation.
    """
    page = await _ensure_browser()
    return "Browser launched successfully."


@mcp.tool()
async def login_siakad(username: str = "", password: str = "") -> str:
    """
    Login to SIAKAD system.

    Navigates to the login page, fills in the username and password fields,
    and clicks the submit button. Waits for the page to fully load after login.
    On error, saves a screenshot for debugging.

    If username/password are not provided, uses values from config.json.

    Args:
        username: The SIAKAD username/email (default: from config.json)
        password: The SIAKAD password (default: from config.json)
    """
    # Use config defaults if not provided
    username = username or SIAKAD_USERNAME
    password = password or SIAKAD_PASSWORD

    page = await _ensure_browser()
    try:
        # Navigate to login page and wait for full load
        await page.goto(SIAKAD_LOGIN_URL, wait_until="networkidle", timeout=30000)
        await page.wait_for_timeout(1000)

        # Fill login form - uses multiple selectors for robustness
        await page.fill('input[name="username"], input[type="email"], #username', username)
        await page.fill('input[name="password"], input[type="password"], #password', password)

        # Click login button and wait for redirect
        await page.click('button[type="submit"], input[type="submit"], .btn-login, #login-btn')
        await page.wait_for_load_state("networkidle", timeout=15000)
        await page.wait_for_timeout(2000)

        # Return current URL and title to confirm successful login
        current_url = page.url
        title = await page.title()
        return f"Login completed. Current URL: {current_url}, Title: {title}"
    except Exception as e:
        # Save screenshot on error for debugging
        screenshot_path = "/tmp/siakad_login_error.png"
        await page.screenshot(path=screenshot_path)
        return f"Login error: {str(e)}. Screenshot saved to {screenshot_path}"


@mcp.tool()
async def navigate_to_daftar_hadir() -> str:
    """
    Navigate to the daftar hadir (attendance list) page.

    Goes to the daftar hadir URL from config.json and returns
    the page title and visible text content for the agent to understand
    the current page state.
    """
    page = await _ensure_browser()
    try:
        await page.goto(SIAKAD_DAFTAR_HADIR_URL, wait_until="networkidle", timeout=30000)
        await page.wait_for_timeout(2000)
        title = await page.title()
        # Get visible text so the agent can understand the page layout
        text = await page.evaluate("() => document.body.innerText")
        truncated = text[:2000] if len(text) > 2000 else text
        return f"Navigated to daftar hadir. Title: {title}\n\nPage text:\n{truncated}"
    except Exception as e:
        return f"Navigation error: {str(e)}"


@mcp.tool()
async def set_date_picker(date_str: str) -> str:
    """
    Set the date in the date picker on the daftar hadir page.

    Tries multiple CSS selectors to find the date input field (since the exact
    HTML structure may vary). After filling, dispatches a 'change' event to
    trigger any JavaScript listeners that update the schedule list.

    Args:
        date_str: Date string in YYYY-MM-DD format (e.g., '2026-04-04')
    """
    page = await _ensure_browser()
    try:
        # Prepare US format (MM/DD/YYYY) just in case it's a text input
        # date_str is expected to be YYYY-MM-DD
        dt = datetime.strptime(date_str, "%Y-%m-%d")
        us_date = dt.strftime("%m/%d/%Y")
        id_date = dt.strftime("%d/%m/%Y")

        # Strategy 1: Try standard date input selectors
        date_input = await page.query_selector(
            'input[type="date"], input.datepicker, input[name*="tanggal"], input[name*="date"], .date-picker input'
        )
        if date_input:
            input_type = await date_input.get_attribute("type")
            if input_type == "date":
                # For input type="date", Playwright fill() MUST be YYYY-MM-DD
                await date_input.fill(date_str)
            else:
                # For text-based inputs, try the US format as requested
                await date_input.fill(us_date)
            
            await page.wait_for_timeout(500)
            # Dispatch change event to trigger any JS listeners (e.g., AJAX reload)
            await date_input.dispatch_event("change")
            await page.wait_for_timeout(1000)
            return f"Date set to {date_str} (sent as {us_date} to text input)"

        # Strategy 2: Try inputs with date-related placeholder text
        date_elements = await page.query_selector_all('input[placeholder*="tanggal"], input[placeholder*="date"]')
        if date_elements:
            await date_elements[0].click()
            await date_elements[0].fill(us_date)
            await page.wait_for_timeout(1000)
            return f"Date set to {date_str} via placeholder input (as {us_date})"

        # Strategy 3: Directly set the value via JavaScript for all date-related inputs
        # This is a broad fallback that works for most custom datepickers
        await page.evaluate(f"""(d_str, us_str) => {{
            const inputs = Array.from(document.querySelectorAll('input'));
            const dateInputs = inputs.filter(el => 
                el.type === 'date' || 
                el.id.toLowerCase().includes('tanggal') || 
                el.id.toLowerCase().includes('date') ||
                el.name.toLowerCase().includes('tanggal') ||
                el.name.toLowerCase().includes('date')
            );
            dateInputs.forEach(el => {{
                if (el.type === 'date') el.value = d_str;
                else el.value = us_str;
                el.dispatchEvent(new Event('change', {{ bubbles: true }}));
            }});
        }}""", [date_str, us_date])
        
        await page.wait_for_timeout(1000)
        return f"Date set to {date_str} and {us_date} using JavaScript fallback"
    except Exception as e:
        return f"Date picker error: {str(e)}"


@mcp.tool()
async def set_semester(semester_name: str) -> str:
    """
    Select the semester from the dropdown on the attendance page.

    Args:
        semester_name: The name or value of the semester (e.g., '2025/2026 GENAP')
    """
    page = await _ensure_browser()
    try:
        # Strategy 1: Find by ID seen in logs
        selectors = [
            '#daftar-semester-search',
            'select[name*="semester"]',
            'select[id*="semester"]'
        ]
        for sel in selectors:
            el = await page.query_selector(sel)
            if el and await el.is_visible():
                # Find matching option by visible text
                option_value = await page.evaluate(f"""(params) => {{
                    const [sel, text] = params;
                    const select = document.querySelector(sel);
                    if (!select) return null;
                    const option = Array.from(select.options).find(o => 
                        o.text.toLowerCase().includes(text.toLowerCase())
                    );
                    if (option) {{
                        select.value = option.value;
                        select.dispatchEvent(new Event('change'));
                        return option.text;
                    }}
                    return null;
                }}""", [sel, semester_name])
                
                if option_value:
                    await page.wait_for_timeout(1000)
                    return f"Semester set to '{option_value}'"

        # Fallback: List all available options for the agent to debug
        options = await page.evaluate("""() => {
            const select = document.querySelector('select[id*="semester"], select[name*="semester"]');
            if (!select) return [];
            return Array.from(select.options).map(o => o.text);
        }""")
        return f"Could not find matching semester '{semester_name}'. Available options: {options}"
    except Exception as e:
        return f"Semester selection error: {str(e)}"


@mcp.tool()
async def click_search_or_filter() -> str:
    """
    Click the search/filter/cari button after setting the date.

    After the date picker is set, this button triggers the page to load
    the schedule list for that date. Tries multiple common button selectors
    (Cari, Filter, Search, Tampilkan, submit buttons).

    Returns the updated page text so the agent can see the schedule list.
    """
    page = await _ensure_browser()
    try:
        # Fallback: Many forms submit on Enter
        await page.keyboard.press("Enter")
        await page.wait_for_timeout(1000)

        # Try common Indonesian and English button labels
        selectors = [
            'button:has-text("Cari")',
            'button:has-text("Filter")',
            'button:has-text("Search")',
            'button:has-text("Tampilkan")',
            '#search-addon',                   # Seen in logs
            'span:has-text("Cari")',           # Search icon often wrapped in span
            'input[type="submit"]',
            'button[type="submit"]',
            '.btn-search',
            '.btn-filter',
        ]
        for sel in selectors:
            btn = await page.query_selector(sel)
            if btn and await btn.is_visible():
                await btn.click()
                await page.wait_for_load_state("networkidle", timeout=15000)
                await page.wait_for_timeout(2000)
                text = await page.evaluate("() => document.body.innerText")
                truncated = text[:2000] if len(text) > 2000 else text
                return f"Clicked filter/search button ({sel}).\n\nPage text:\n{truncated}"

        # If we pressed Enter earlier, maybe it worked even if we didn't find a button
        await page.wait_for_timeout(2000)
        text = await page.evaluate("() => document.body.innerText")
        if "Data tidak ditemukan" not in text and "Mata Kuliah" in text:
             return f"Pressed Enter and found data.\n\nPage text:\n{text[:1000]}"

        screenshot_path = "/tmp/siakad_filter_debug.png"
        await page.screenshot(path=screenshot_path)
        return f"Could not find search/filter button. Screenshot: {screenshot_path}"
    except Exception as e:
        return f"Search/filter error: {str(e)}"


# ============================================================================
# Schedule table inspection tools
# These tools help the agent understand the schedule table and find the
# correct row to click when multiple classes appear on the same date.
#
# Example table structure on SIAKAD:
#   No | Mata Kuliah | Tanggal | Jam | Ruang | Prodi | Kelompok Kelas
#   1  | PBO         | 02/04   | 09:20-11:05 | A217 | Informatika | Kelas B
#   2  | PBO         | 02/04   | 13:55-15:40 | A216 | Informatika | Kelas A
# ============================================================================


@mcp.tool()
async def get_schedule_list() -> str:
    """
    Get the list of all schedules/classes visible on the daftar hadir page.

    Parses the HTML table and returns structured JSON data with each row's
    index, mata kuliah, tanggal, jam, ruang, prodi, and kelompok kelas.
    The row_index can then be passed to click_absensi_button.
    """
    page = await _ensure_browser()
    try:
        # Extract structured table data using JavaScript evaluation
        # Iterates over all <table> elements, skips header rows (no <td>),
        # and maps each data row to a structured object with row_index
        schedule = await page.evaluate("""() => {
            const tables = document.querySelectorAll('table');
            const results = [];
            tables.forEach(table => {
                const rows = table.querySelectorAll('tr');
                let dataRowIndex = 0;
                rows.forEach(row => {
                    const cells = row.querySelectorAll('td');
                    if (cells.length >= 6) {
                        results.push({
                            row_index: dataRowIndex,
                            no: cells[0] ? cells[0].innerText.trim() : '',
                            mata_kuliah: cells[1] ? cells[1].innerText.trim() : '',
                            tanggal: cells[2] ? cells[2].innerText.trim() : '',
                            jam: cells[3] ? cells[3].innerText.trim() : '',
                            ruang: cells[4] ? cells[4].innerText.trim() : '',
                            prodi: cells[5] ? cells[5].innerText.trim() : '',
                            kelompok_kelas: cells[6] ? cells[6].innerText.trim() : ''
                        });
                        dataRowIndex++;
                    }
                });
            });
            return results;
        }""")
        return json.dumps({
            "total_rows": len(schedule),
            "schedule": schedule
        }, indent=2, ensure_ascii=False)
    except Exception as e:
        return f"Error getting schedule list: {str(e)}"


@mcp.tool()
async def find_schedule_row(mata_kuliah: str, kelompok_kelas: str) -> str:
    """
    Find the correct table row index by matching mata kuliah and kelompok kelas.

    When multiple classes appear on the same date (e.g., Kelas A at 09:20 and
    Kelas B at 13:55), this tool identifies which row corresponds to the target
    class. Uses case-insensitive substring matching for flexibility.

    Returns the matching rows with their row_index, which should be passed
    to click_absensi_button.

    Args:
        mata_kuliah: Course name to match (e.g., 'Pemrograman Berorientasi Objek')
        kelompok_kelas: Class group to match (e.g., 'Kelas A' or 'Kelas B')
    """
    page = await _ensure_browser()
    try:
        # Search through all table rows, matching by mata kuliah and kelompok kelas
        # Uses case-insensitive includes() for flexible matching
        result = await page.evaluate("""(params) => {
            const [targetMK, targetKelas] = params;
            const tables = document.querySelectorAll('table');
            const matches = [];
            tables.forEach(table => {
                const rows = table.querySelectorAll('tr');
                let dataRowIndex = 0;
                rows.forEach(row => {
                    const cells = row.querySelectorAll('td');
                    if (cells.length >= 6) {
                        const mk = cells[1] ? cells[1].innerText.trim() : '';
                        const kelas = cells[6] ? cells[6].innerText.trim() : '';
                        if (mk.toLowerCase().includes(targetMK.toLowerCase()) &&
                            kelas.toLowerCase().includes(targetKelas.toLowerCase())) {
                            matches.push({
                                row_index: dataRowIndex,
                                mata_kuliah: mk,
                                kelompok_kelas: kelas,
                                tanggal: cells[2] ? cells[2].innerText.trim() : '',
                                jam: cells[3] ? cells[3].innerText.trim() : ''
                            });
                        }
                        dataRowIndex++;
                    }
                });
            });
            return matches;
        }""", [mata_kuliah, kelompok_kelas])

        if not result:
            return f"No matching row found for mata_kuliah='{mata_kuliah}', kelompok_kelas='{kelompok_kelas}'. Use get_schedule_list to see all rows."
        return json.dumps({
            "matches": result,
            "recommended_row_index": result[0]["row_index"]
        }, indent=2, ensure_ascii=False)
    except Exception as e:
        return f"Find row error: {str(e)}"


@mcp.tool()
async def click_absensi_button(row_index: int = 0) -> str:
    """
    Click the 'Absensi' button for a specific schedule row to open the attendance popup.

    Each row in the schedule table has an 'Absensi' button/link. This tool finds
    all such buttons and clicks the one at the specified index. Use find_schedule_row
    first to determine the correct row_index when multiple classes appear.

    Args:
        row_index: Zero-based index of the schedule row (default: 0 for first row).
                   Use find_schedule_row to get the correct index.
    """
    page = await _ensure_browser()
    try:
        # Try multiple selectors for the absensi button/link
        # SIAKAD may use <a> or <button> elements with various class names
        selectors = [
            'a:has-text("Absensi")',
            'button:has-text("Absensi")',
            'a:has-text("absensi")',
            'button:has-text("absensi")',
            '.btn-absensi',
            'a.absensi',
        ]
        for sel in selectors:
            buttons = await page.query_selector_all(sel)
            if buttons and len(buttons) > row_index:
                await buttons[row_index].click()
                # Wait for popup/modal to appear
                await page.wait_for_timeout(3000)
                text = await page.evaluate("() => document.body.innerText")
                truncated = text[:3000] if len(text) > 3000 else text
                return f"Clicked Absensi button (index {row_index} of {len(buttons)} buttons).\n\nPage text:\n{truncated}"

        # No button found - save screenshot for debugging
        screenshot_path = "/tmp/siakad_absensi_debug.png"
        await page.screenshot(path=screenshot_path)
        return f"Could not find Absensi button. Screenshot: {screenshot_path}"
    except Exception as e:
        return f"Absensi click error: {str(e)}"


# ============================================================================
# Form filling tools
# These tools handle the attendance popup/modal where topik and deskripsi
# are entered and submitted.
# ============================================================================


@mcp.tool()
async def fill_attendance_form(topik: str, deskripsi: str) -> str:
    """
    Fill the attendance form popup with topic and description.

    After clicking the Absensi button, a popup/modal appears with fields for
    'topik' (lecture topic) and 'deskripsi pembahasan' (lecture description).
    This tool tries multiple CSS selectors to find and fill both fields.

    If the standard selectors fail, it falls back to scanning all visible
    inputs/textareas in modals and returns debug info.

    Args:
        topik: The topic/subject of the lecture session
        deskripsi: The description/pembahasan of the lecture session
    """
    page = await _ensure_browser()
    try:
        # Wait for modal/popup animation to complete
        await page.wait_for_timeout(1000)

        # === Fill TOPIK field ===
        # Try multiple selectors since the exact field name may vary
        topik_selectors = [
            'input[name*="topik"]',
            'textarea[name*="topik"]',
            'input[name*="topic"]',
            'textarea[name*="topic"]',
            'input[placeholder*="topik"]',
            'textarea[placeholder*="topik"]',
            '#topik',
            '#topic',
        ]
        topik_filled = False
        for sel in topik_selectors:
            el = await page.query_selector(sel)
            if el and await el.is_visible():
                await el.fill(topik)
                topik_filled = True
                break

        # === Fill DESKRIPSI/PEMBAHASAN field ===
        desk_selectors = [
            'textarea[name*="deskripsi"]',
            'input[name*="deskripsi"]',
            'textarea[name*="pembahasan"]',
            'input[name*="pembahasan"]',
            'textarea[name*="description"]',
            'input[name*="description"]',
            'textarea[placeholder*="deskripsi"]',
            'textarea[placeholder*="pembahasan"]',
            '#deskripsi',
            '#pembahasan',
        ]
        desk_filled = False
        for sel in desk_selectors:
            el = await page.query_selector(sel)
            if el and await el.is_visible():
                await el.fill(deskripsi)
                desk_filled = True
                break

        # === Fallback: scan modals for any visible text inputs ===
        if not topik_filled or not desk_filled:
            modal_inputs = await page.evaluate("""() => {
                const modals = document.querySelectorAll('.modal, .popup, [role="dialog"], .modal-content');
                const inputs = [];
                modals.forEach(modal => {
                    modal.querySelectorAll('input[type="text"], textarea').forEach(el => {
                        inputs.push({
                            tag: el.tagName, name: el.name, id: el.id,
                            class: el.className, placeholder: el.placeholder,
                            visible: el.offsetParent !== null
                        });
                    });
                });
                // Also check all visible inputs/textareas on the page
                document.querySelectorAll('input[type="text"], textarea').forEach(el => {
                    if (el.offsetParent !== null) {
                        inputs.push({
                            tag: el.tagName, name: el.name, id: el.id,
                            class: el.className, placeholder: el.placeholder,
                            visible: true
                        });
                    }
                });
                return inputs;
            }""")

            screenshot_path = "/tmp/siakad_form_debug.png"
            await page.screenshot(path=screenshot_path)

            if not topik_filled and not desk_filled:
                return f"Could not find form fields. Screenshot: {screenshot_path}\nAvailable inputs: {json.dumps(modal_inputs, indent=2)}"

        result = f"Topik filled: {topik_filled}, Deskripsi filled: {desk_filled}"
        return result
    except Exception as e:
        return f"Form fill error: {str(e)}"


@mcp.tool()
async def submit_attendance_form() -> str:
    """
    Submit/save the attendance form after filling topik and deskripsi.

    Looks for a submit button in the modal (Simpan, Save, Submit, OK, etc.)
    and clicks it. Waits for the form submission to complete and returns
    the updated page text to confirm success.
    """
    page = await _ensure_browser()
    try:
        # Try common submit button selectors in order of specificity
        selectors = [
            'button:has-text("Save Pembahasan")', # Specifically seen in logs
            'button:has-text("Simpan")',         # Indonesian for "Save"
            'button:has-text("Save")',
            'button:has-text("Submit")',
            'input[type="submit"]',
            '.modal button.btn-primary',         # Bootstrap modal primary button
            '.modal button[type="submit"]',
            'button:has-text("OK")',
        ]
        for sel in selectors:
            btn = await page.query_selector(sel)
            if btn and await btn.is_visible():
                await btn.click()
                # Wait for form submission and page update
                await page.wait_for_timeout(3000)
                text = await page.evaluate("() => document.body.innerText")
                truncated = text[:2000] if len(text) > 2000 else text
                return f"Clicked submit ({sel}).\n\nPage text:\n{truncated}"

        # Fallback: Try calling the JavaScript function directly if it exists
        has_js_func = await page.evaluate("() => typeof save_pembahasan === 'function'")
        if has_js_func:
            await page.evaluate("() => save_pembahasan()")
            await page.wait_for_timeout(3000)
            text = await page.evaluate("() => document.body.innerText")
            return f"Called save_pembahasan() via JavaScript.\n\nPage text:\n{text[:1000]}"

        screenshot_path = "/tmp/siakad_submit_debug.png"
        await page.screenshot(path=screenshot_path)
        return f"Could not find submit button. Screenshot: {screenshot_path}"
    except Exception as e:
        return f"Submit error: {str(e)}"


# ============================================================================
# Debugging and utility tools
# These tools help the agent inspect and interact with the page when
# the standard workflow tools fail or the page structure is unexpected.
# ============================================================================


@mcp.tool()
async def take_screenshot(filename: str = "siakad_screenshot.png") -> str:
    """
    Take a full-page screenshot of the current browser state.

    Useful for debugging when a tool fails or the agent needs to understand
    the current page layout. Screenshots are saved to /tmp/.

    Args:
        filename: Filename for the screenshot (saved in /tmp/)
    """
    page = await _ensure_browser()
    path = f"/tmp/{filename}"
    await page.screenshot(path=path, full_page=True)
    return f"Screenshot saved to {path}"


@mcp.tool()
async def get_page_html() -> str:
    """
    Get the current page HTML content for debugging/analysis.

    Returns up to 5000 characters of the raw HTML. Useful when the agent
    needs to inspect element structure, class names, or IDs to construct
    specific CSS selectors for click_element or fill_input.
    """
    page = await _ensure_browser()
    html = await page.content()
    return html[:5000] if len(html) > 5000 else html


@mcp.tool()
async def run_js(script: str) -> str:
    """
    Run arbitrary JavaScript on the current page.

    A powerful escape hatch for advanced interactions that the other tools
    can't handle. Can be used to query DOM elements, trigger events,
    scroll, or manipulate the page in any way.

    Args:
        script: JavaScript code to evaluate in the browser context.
                The script should return a serializable value.
    """
    page = await _ensure_browser()
    try:
        result = await page.evaluate(script)
        return json.dumps(result, indent=2, ensure_ascii=False, default=str)
    except Exception as e:
        return f"JS execution error: {str(e)}"


@mcp.tool()
async def click_element(selector: str) -> str:
    """
    Click an element on the page using a CSS or Playwright selector.

    Use this when the standard workflow tools (click_absensi_button, etc.)
    fail to find the right element. The agent can inspect the HTML with
    get_page_html and construct a specific selector.

    Args:
        selector: CSS selector or Playwright selector (e.g., 'text=Submit',
                  '#my-button', '.btn-primary >> nth=2')
    """
    page = await _ensure_browser()
    try:
        await page.click(selector, timeout=5000)
        await page.wait_for_timeout(2000)
        return f"Clicked element: {selector}"
    except Exception as e:
        return f"Click error for '{selector}': {str(e)}"


@mcp.tool()
async def fill_input(selector: str, value: str) -> str:
    """
    Fill an input field on the page using a CSS selector.

    Use this when fill_attendance_form fails to find the right fields.
    The agent can inspect the HTML and target specific elements directly.

    Args:
        selector: CSS selector for the input element (e.g., '#topik', 'textarea.form-control')
        value: Value to fill into the field
    """
    page = await _ensure_browser()
    try:
        await page.fill(selector, value, timeout=5000)
        await page.wait_for_timeout(500)
        return f"Filled '{selector}' with '{value}'"
    except Exception as e:
        return f"Fill error for '{selector}': {str(e)}"


# ============================================================================
# Lifecycle management
# ============================================================================


@mcp.tool()
async def close_browser() -> str:
    """
    Close the browser instance and clean up all Playwright resources.

    Should be called when all attendance records have been filled,
    or when the agent needs to restart the browser session.
    """
    global _playwright, _browser, _context, _page
    try:
        if _browser:
            await _browser.close()
        if _playwright:
            await _playwright.stop()
        _browser = None
        _context = None
        _page = None
        _playwright = None
        return "Browser closed."
    except Exception as e:
        return f"Close error: {str(e)}"


# ============================================================================
# Entry point
# Run this file directly to start the MCP server over stdio transport.
# The server will be connected to by Claude CLI or any MCP-compatible client.
# ============================================================================

if __name__ == "__main__":
    mcp.run(transport="stdio")
