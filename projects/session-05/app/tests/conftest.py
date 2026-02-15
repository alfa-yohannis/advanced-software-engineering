import os
import pytest

def pytest_addoption(parser):
    parser.addoption("--base-url", action="store", default=os.getenv("BASE_URL", "http://localhost:8088"))
    parser.addoption("--api-url", action="store", default=os.getenv("API_URL", "http://localhost:8000"))
    parser.addoption("--web-url", action="store", default=os.getenv("WEB_URL", "http://localhost:8080"))

@pytest.fixture
def base_url(request):
    return request.config.getoption("--base-url").rstrip("/")

@pytest.fixture
def api_url(request):
    return request.config.getoption("--api-url").rstrip("/")

@pytest.fixture
def web_url(request):
    return request.config.getoption("--web-url").rstrip("/")
