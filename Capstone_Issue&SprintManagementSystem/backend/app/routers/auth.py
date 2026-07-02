"""Authentication routes: register and login."""

from fastapi import APIRouter, Depends, status

from ..db.client import get_db
from ..schemas.user import AuthResponse, RegisterRequest, LoginRequest
from ..services import user_service

router = APIRouter(prefix="/auth", tags=["auth"])

@router.post("/register", response_model=AuthResponse, status_code=status.HTTP_201_CREATED)
def register(data: RegisterRequest, db=Depends(get_db)):
    return user_service.register(db, data)


@router.post("/login", response_model=AuthResponse)
def login(data: LoginRequest, db=Depends(get_db)):
    return user_service.login(db, data)