"""User-related request/response schemas."""

from pydantic import EmailStr, Field

from ..constants import USER_ROLE
from . import CamelModel

class RegisterRequest(CamelModel):
    name: str = Field(min_length=1)
    email: EmailStr
    password: str = Field(min_length=6)
    role: str = Field(default=USER_ROLE["MEMBER"])
    

class LoginRequest(CamelModel):
    email: EmailStr
    password: str = Field(min_length=1)

class UserPublic(CamelModel):
    id: str
    name: str
    email: EmailStr
    role: str

class AuthResponse(CamelModel):
    token: str
    user: UserPublic