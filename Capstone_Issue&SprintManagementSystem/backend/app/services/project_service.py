"""
Project service: CRUD and team management.
"""

from datetime import datetime, timezone

from ..constants import USER_ROLE
from ..core.exceptions import NotFoundError
from ..db.utils import new_id, serialize


def list_projects(db, current_user: dict) -> list[dict]:
    """Admins see all projects; members see only their assigned projects."""
    if current_user["role"] == USER_ROLE["ADMIN"]:
        query = {}
    else:
        query = {"memberIds": current_user["id"]}
    return [serialize(p) for p in db.projects.find(query)]


def get_project(db, project_id: str) -> dict:
    project = db.projects.find_one({"_id": project_id})
    if not project:
        raise NotFoundError("Project not found.")
    return serialize(project)


def create_project(db, data) -> dict:
    project = {
        "_id": new_id(),
        "name": data.name,
        "description": data.description,
        "key": data.key.upper(),
        "memberIds": data.member_ids,
        "createdAt": datetime.now(timezone.utc).isoformat(),
    }
    db.projects.insert_one(project)
    return serialize(project)


def update_members(db, project_id: str, member_ids: list[str]) -> dict:
    project = db.projects.find_one({"_id": project_id})
    if not project:
        raise NotFoundError("Project not found.")
    db.projects.update_one(
        {"_id": project_id}, {"$set": {"memberIds": member_ids}}
    )
    project["memberIds"] = member_ids
    return serialize(project)
