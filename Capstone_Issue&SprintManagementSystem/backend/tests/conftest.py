import pytest

from fastapi.testclient import TestClient

from backend.app.main import app


@pytest.fixture
def client():
    """
    Creates a TestClient for API testing.
    """
    return TestClient(app)