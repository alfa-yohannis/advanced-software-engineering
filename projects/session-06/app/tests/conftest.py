import os
import pytest

# Options of the integration tests. By default they reach the stack through the
# gateway (--base-url). --api-url and --web-url send them straight to the API
# and web services instead.
def pytest_addoption(parser):
    parser.addoption("--base-url", action="store", default=os.getenv("BASE_URL", "http://localhost:8088"))
    parser.addoption("--api-url", action="store", default=os.getenv("API_URL", ""))  # optional override
    parser.addoption("--web-url", action="store", default=os.getenv("WEB_URL", ""))  # optional override

@pytest.fixture
def base_url(request) -> str:
    return request.config.getoption("--base-url").rstrip("/")

@pytest.fixture
def api_url(request, base_url: str) -> str:
    v = (request.config.getoption("--api-url") or "").strip()
    # default: via gateway /api
    return (v.rstrip("/") if v else f"{base_url}/api")

@pytest.fixture
def web_url(request, base_url: str) -> str:
    v = (request.config.getoption("--web-url") or "").strip()
    # default: via gateway root
    return (v.rstrip("/") if v else base_url)
