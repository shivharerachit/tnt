"""
Reusable FastAPI dependencies.
"""

from fastapi import Depends, Header

from ..db.client import get_db
from .exceptions import ForbiddenError, UnauthorizedError
from .security import decode_access_token
from ..constants import USER_ROLE


def get_current_user(
    authorization: str | None = Header(default=None),
    db=Depends(get_db),
) -> dict:
    """Return the logged-in user based on the Bearer token, or raise 401."""
    if not authorization:
        raise UnauthorizedError("Authentication required.")

    token = authorization.strip()
    if token.lower().startswith("bearer "):
        token = token.split(" ", 1)[1].strip()

    if not token:
        raise UnauthorizedError("Authentication required.")

    payload = decode_access_token(token)
    user_id = payload.get("sub")
    if not user_id:
        raise UnauthorizedError("Invalid authentication token.")

    user = db.users.find_one({"_id": user_id})
    if not user:
        raise UnauthorizedError("User no longer exists.")

    return {
        "id": user["_id"],
        "name": user["name"],
        "email": user["email"],
        "role": user["role"],
    }


def require_admin(current_user: dict = Depends(get_current_user)) -> dict:
    """Allow only ADMIN users through."""
    if current_user["role"] != USER_ROLE["ADMIN"]:
        raise ForbiddenError("Only an admin can perform this action.")
    return current_user


def require_editor(current_user: dict = Depends(get_current_user)) -> dict:
    """Allow ADMIN and MEMBER through; block VIEWER (read-only) from writes."""
    if current_user["role"] == USER_ROLE["VIEWER"]:
        raise ForbiddenError("Viewers have read-only access.")
    return current_user
