"""User-related request/response schemas."""

from pydantic import EmailStr, Field

from . import CamelModel

class RegisterRequest(CamelModel):
    name: str = Field(min_length=1)
    email: EmailStr
    password: str = Field(min_length=6)
    

class LoginRequest(CamelModel):
    email: EmailStr
    password: str = Field(min_length=1)

class UserPublic(CamelModel):
    id: str
    name: str
    email: EmailStr
    role: str

class AuthResponse(CamelModel):
    access_token: str = Field(alias="access_token", serialization_alias="access_token")
    token_type: str = Field(alias="token_type", serialization_alias="token_type")
    user: UserPublic

class UpdateUserRequest(CamelModel):
    name: str | None = Field(default=None, min_length=1)
    email: EmailStr | None = None

class RoleUpdateRequest(CamelModel):
    role: str

class UserListResponse(CamelModel):
    items: list[UserPublic]
    total: int
    page: int
    page_size: int