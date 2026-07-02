import mongomock
import pytest
from fastapi.testclient import TestClient

from app.db.client import get_db
from app.main import app


@pytest.fixture
def db():
    """A fresh in-memory database for each test."""
    client = mongomock.MongoClient()
    return client["test_db"]


@pytest.fixture
def client():
    """
    Creates a TestClient for API testing.
    """
    with TestClient(app) as test_client:
        yield test_client
