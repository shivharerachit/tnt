"""
Base schema configuration.

The frontend uses camelCase keys (projectId, memberIds, ...). We define a
base model that accepts and produces camelCase so request/response bodies
match the frontend exactly, while our Python code stays snake_case-friendly.
"""

from pydantic import BaseModel, ConfigDict

def to_camel(field_name: str) -> str:
    """Convert snake_case to camelCase (e.g. project_id -> projectId)."""
    parts = field_name.split("_")
    return parts[0] + "".join(word.capitalize() for word in parts[1:])


class CamelModel(BaseModel):
    """All request/response schemas inherit from this."""

    model_config = ConfigDict(
        alias_generator=to_camel,
        populate_by_name=True,
    )
