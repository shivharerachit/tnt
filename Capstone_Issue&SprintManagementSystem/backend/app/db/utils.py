"""
Small helpers shared by the service layer.
"""

import uuid


def new_id() -> str:
    """Generate a short, unique string id used as the MongoDB _id."""
    return uuid.uuid4().hex