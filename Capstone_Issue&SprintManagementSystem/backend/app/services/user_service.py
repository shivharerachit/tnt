"""
User service: registration, login and lookups.
"""

from ..constants import USER_ROLE
from ..core.exceptions import (
    BadRequestError,
    ConflictError,
    NotFoundError,
    UnauthorizedError,
)
from ..core.security import create_access_token, hash_password, verify_password
from ..db.utils import new_id


def _public(user: dict) -> dict:
    """Return a user without the password hash."""
    return {
        "id": str(user["_id"]),
        "name": user["name"],
        "email": user["email"],
        "role": user["role"],
    }


def register(db, data) -> dict:
    """Create a new user and return a token + public user."""
    existing = db.users.find_one({"email": data.email.lower()})
    if existing:
        raise ConflictError("A user with this email already exists.")

    role = USER_ROLE["VIEWER"]

    user = {
        "_id": new_id(),
        "name": data.name,
        "email": data.email.lower(),
        "passwordHash": hash_password(data.password),
        "role": role,
    }
    db.users.insert_one(user)

    token = create_access_token(user["_id"], user["email"], user["role"])
    return {"access_token": token, "token_type": "bearer", "user": _public(user)}


def login(db, data) -> dict:
    """Verify credentials and return a token + public user."""
    user = db.users.find_one({"email": data.email.lower()})
    if not user or not verify_password(data.password, user["passwordHash"]):
        raise UnauthorizedError("Invalid email or password.")

    token = create_access_token(user["_id"], user["email"], user["role"])
    return {
        "access_token": token,
        "token_type": "bearer",
        "user": _public(user),
    }


def list_users(db, page: int = 1, page_size: int = 20, search: str = "") -> dict:
    """
    Return a paginated, optionally-searched list of users.
    """
    if page < 1:
        page = 1
    if page_size < 1:
        page_size = 20
    if page_size > 100:
        page_size = 100

    query = {}
    term = (search or "").strip()
    if term:
        query = {
            "$or": [
                {"name": {"$regex": term, "$options": "i"}},
                {"email": {"$regex": term, "$options": "i"}},
            ]
        }

    total = db.users.count_documents(query)
    skip = (page - 1) * page_size
    cursor = db.users.find(query).skip(skip).limit(page_size)

    return {
        "items": [_public(u) for u in cursor],
        "total": total,
        "page": page,
        "pageSize": page_size,
    }


def _find_or_404(db, user_id: str) -> dict:
    user = db.users.find_one({"_id": user_id})
    if not user:
        raise NotFoundError("User not found.")
    return user


def get_user(db, user_id: str) -> dict:
    """Return a single user, or raise 404."""
    return _public(_find_or_404(db, user_id))


def update_user(db, user_id: str, data) -> dict:
    """Update a user's name and/or email. Email must remain unique."""
    user = _find_or_404(db, user_id)

    updates = {}
    if data.name is not None:
        updates["name"] = data.name
    if data.email is not None:
        new_email = data.email.lower()
        if new_email != user["email"]:
            clash = db.users.find_one({"email": new_email})
            if clash:
                raise ConflictError("A user with this email already exists.")
            updates["email"] = new_email

    if updates:
        db.users.update_one({"_id": user_id}, {"$set": updates})

    return _public(_find_or_404(db, user_id))


def change_role(db, user_id: str, role: str) -> dict:
    """Change a user's role (admin only)."""
    if role not in USER_ROLE.values():
        raise BadRequestError(f"Role must be one of: {', '.join(USER_ROLE.values())}.")
    _find_or_404(db, user_id)
    db.users.update_one({"_id": user_id}, {"$set": {"role": role}})
    return _public(_find_or_404(db, user_id))


def delete_user(db, user_id: str) -> None:
    """Delete a user (admin only)."""
    _find_or_404(db, user_id)
    db.users.delete_one({"_id": user_id})
