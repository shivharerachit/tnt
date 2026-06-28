from fastapi import APIRouter, HTTPException, status

from backend.app.db.client import db
from backend.app.models.user import UserCreate, UserLogin
from backend.app.schemas.user import Token, UserOut
from backend.app.services.user_service import authenticate_user, create_user, get_user_by_email
from backend.app.core.security import create_access_token

router = APIRouter(prefix="/auth")

@router.post("/register", response_model=UserOut, status_code=status.HTTP_201_CREATED)
def register(user_data: UserCreate):
    if get_user_by_email(db.users, user_data.email):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Email already registered")
    
    user = create_user(db.users, user_data)
    return UserOut(id=str(user.id), name=user.name, email=user.email, role=user.role)


@router.post("/login", response_model=Token)
def login(user_data: UserLogin):
    user = authenticate_user(db.users, user_data.email, user_data.password)
    if not user:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials")
    
    token = create_access_token({"sub": user.email, "role": user.role})
    return Token(access_token=token)