"""
Small helpers shared by the service layer.
"""

import uuid


def new_id() -> str:
    """Generate a short, unique string id used as the MongoDB _id."""
    return uuid.uuid4().hex


def serialize(document: dict | None) -> dict | None:
    """
    Convert a MongoDB document into an API-friendly dict.
    MongoDB stores the primary key under "_id"; the frontend expects "id".
    """
    if document is None:
        return None
    result = dict(document)
    result["id"] = result.pop("_id")
    return result
