"""
MongoDB connection management.

A single MongoClient is created on startup.
"""
from pymongo import MongoClient

from ..core.config import settings

_client: MongoClient | None = None
_db = None

def connect_to_mongo() -> None:
    """Open the MongoDB connection and create indexes."""
    global _client, _db
    _client = MongoClient(settings.MONGODB_URI)
    _db = _client[settings.DATABASE_NAME]

def close_mongo_connection() -> None:
    """Close the MongoDB connection on shutdown."""
    global _client
    if _client is not None:
        _client.close()

def get_db():
    """FastAPI dependency that returns the active database handle."""
    global _db
    if _db is None:
        connect_to_mongo()
    return _db
