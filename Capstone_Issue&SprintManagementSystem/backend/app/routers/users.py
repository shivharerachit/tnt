"""User routes (read-only list of users for assignment dropdowns)."""

from fastapi import APIRouter, Depends, Query, status

from ..constants import USER_ROLE
from ..core.dependencies import get_current_user, require_admin
from ..core.exceptions import BadRequestError, ForbiddenError
from ..db.client import get_db
from ..schemas.user import (
    RoleUpdateRequest,
    UpdateUserRequest,
    UserListResponse,
    UserPublic,
)
from ..services import user_service

router = APIRouter(prefix="/users", tags=["users"])


def _ensure_admin_or_self(current_user: dict, user_id: str) -> None:
    """Allow admins, or the user acting on their own record."""
    is_admin = current_user["role"] == USER_ROLE["ADMIN"]
    if not is_admin and current_user["id"] != user_id:
        raise ForbiddenError("You can only access your own account.")


@router.get("", response_model=UserListResponse)
def list_users(
    page: int = Query(default=1, ge=1),
    page_size: int = Query(default=20, ge=1, le=100, alias="pageSize"),
    search: str = Query(default=""),
    db=Depends(get_db),
    _: dict = Depends(require_admin),
):
    """List all users with pagination and search (admin only)."""
    return user_service.list_users(db, page=page, page_size=page_size, search=search)


@router.get("/{user_id}", response_model=UserPublic)
def get_user(
    user_id: str,
    db=Depends(get_db),
    current_user: dict = Depends(get_current_user),
):
    """Get a single user (admin, or the user themselves)."""
    _ensure_admin_or_self(current_user, user_id)
    return user_service.get_user(db, user_id)


@router.put("/{user_id}", response_model=UserPublic)
def update_user(
    user_id: str,
    body: UpdateUserRequest,
    db=Depends(get_db),
    current_user: dict = Depends(get_current_user),
):
    """Update a user's name and/or email (admin, or the user themselves)."""
    _ensure_admin_or_self(current_user, user_id)
    return user_service.update_user(db, user_id, body)


@router.patch("/{user_id}/role", response_model=UserPublic)
def change_role(
    user_id: str,
    body: RoleUpdateRequest,
    db=Depends(get_db),
    current_user: dict = Depends(require_admin),
):
    """Change a user's role (admin only)."""
    if current_user["id"] == user_id:
        raise BadRequestError("You cannot change your own role.")
    return user_service.change_role(db, user_id, body.role)


@router.delete("/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(
    user_id: str,
    db=Depends(get_db),
    current_user: dict = Depends(require_admin),
):
    """Delete a user (admin only). Admins cannot delete themselves."""
    if current_user["id"] == user_id:
        raise BadRequestError("You cannot delete your own account.")
    user_service.delete_user(db, user_id)
    return None
