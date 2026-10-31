import os
import pytest
import pytest_asyncio
import httpx

API_BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8080")

@pytest_asyncio.fixture
async def api_client():
    """Async HTTP client for functional API testing."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=15.0) as client:
        yield client

