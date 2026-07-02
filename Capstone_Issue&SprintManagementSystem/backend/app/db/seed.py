from datetime import datetime, timezone
from ..constants import USER_ROLE
from ..core.config import settings
from ..core.security import hash_password
from ..db.utils import new_id


def seed_default_admin(db) -> None:
    """Ensure a single default admin account always exists on startup."""
    existing_admin = db.users.find_one({"role": USER_ROLE["ADMIN"]})
    if existing_admin:
        return

    email = settings.DEFAULT_ADMIN_EMAIL.lower()
    if db.users.find_one({"email": email}):
        return

    db.users.insert_one(
        {
            "_id": new_id(),
            "name": settings.DEFAULT_ADMIN_NAME,
            "email": email,
            "passwordHash": hash_password(settings.DEFAULT_ADMIN_PASSWORD),
            "role": USER_ROLE["ADMIN"],
        }
    )


def seed_demo_data(db) -> None:
    if db.users.count_documents({}) > 1:
        return

    now = datetime.now(timezone.utc).isoformat()

    member_id = "user_member"
    viewer_id = "user_viewer"
    project_id = "project_demo"

    db.users.insert_many(
        [
            {
                "_id": member_id,
                "name": "Member",
                "email": "member@company.com",
                "passwordHash": hash_password("Member@123"),
                "role": USER_ROLE["MEMBER"],
            },
            {
                "_id": viewer_id,
                "name": "Viewer",
                "email": "viewer@demo.com",
                "passwordHash": hash_password("Viewer@123"),
                "role": USER_ROLE["VIEWER"],
            },
        ]
    )

    db.projects.insert_one(
        {
            "_id": project_id,
            "name": "Website Revamp",
            "description": "Rebuild the marketing website with a new design system.",
            "key": "WEB",
            "memberIds": [member_id, viewer_id],
            "createdAt": now,
        }
    )
