"""User routes (read-only list of users for assignment dropdowns)."""

from fastapi import APIRouter, Depends

from ..core.dependencies import get_current_user
from ..db.client import get_db
from ..schemas.user import UserPublic
from ..services import user_service

router = APIRouter(prefix="/users", tags=["users"])


@router.get("", response_model=list[UserPublic])
def list_users(db=Depends(get_db), _: dict = Depends(get_current_user)):
    return user_service.list_users(db)
