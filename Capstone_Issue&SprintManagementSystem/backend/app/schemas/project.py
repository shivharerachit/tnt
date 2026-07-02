"""Project-related request/response schemas."""

from pydantic import Field

from . import CamelModel


class ProjectCreate(CamelModel):
    name: str = Field(min_length=1)
    description: str = ""
    key: str = Field(min_length=1, max_length=10)
    member_ids: list[str] = Field(default_factory=list)


class MembersUpdate(CamelModel):
    member_ids: list[str] = Field(default_factory=list)


class ProjectOut(CamelModel):
    id: str
    name: str
    description: str
    key: str
    member_ids: list[str]
    created_at: str
