"""
User service: registration, login and lookups.
"""

from ..constants import USER_ROLE
from ..core.exceptions import ConflictError, UnauthorizedError
from ..core.security import create_access_token, hash_password, verify_password
from ..db.utils import new_id


def _public(user: dict) -> dict:
    """Return a user without the password hash."""
    return {
        "id": user["_id"],
        "name": user["name"],
        "email": user["email"],
        "role": user["role"],
    }


def register(db, data) -> dict:
    """Create a new user and return a token + public user."""
    existing = db.users.find_one({"email": data.email.lower()})
    if existing:
        raise ConflictError("A user with this email already exists.")
    
    if data.role not in USER_ROLE.values():
        raise 

    role = data.role if data.role in USER_ROLE.values() else USER_ROLE["MEMBER"]
    user = {
        "_id": new_id(),
        "name": data.name,
        "email": data.email.lower(),
        "passwordHash": hash_password(data.password),
        "role": role,
    }
    db.users.insert_one(user)

    token = create_access_token(user["_id"], user["email"], user["role"])
    return {"token": token, "user": _public(user)}


def login(db, data) -> dict:
    """Verify credentials and return a token + public user."""
    user = db.users.find_one({"email": data.email.lower()})
    if not user or not verify_password(data.password, user["passwordHash"]):
        raise UnauthorizedError("Invalid email or password.")

    token = create_access_token(user["_id"], user["email"], user["role"])
    return {"token": token, "user": _public(user)}