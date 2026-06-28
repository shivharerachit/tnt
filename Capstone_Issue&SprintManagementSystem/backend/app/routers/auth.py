from fastapi import APIRouter, HTTPException, status

from backend.app.db.client import db
from backend.app.models.user import UserCreate
from backend.app.schemas.user import UserOut
from backend.app.services.user_service import create_user, get_user_by_email

router = APIRouter(prefix="/auth")

@router.post("/register", response_model=UserOut, status_code=status.HTTP_201_CREATED)
def register(user_data: UserCreate):
    if get_user_by_email(db.users, user_data.email):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Email already registered")
    
    user = create_user(db.users, user_data)
    return UserOut(id=str(user.id), name=user.name, email=user.email, role=user.role)