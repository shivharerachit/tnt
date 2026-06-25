from pydantic import BaseModel, EmailStr, Field
from typing import Optional
from enum import Enum

class UserRole(str, Enum):
    ADMIN = "Admin"
    PROJECT_MANAGER = "Project Manager"
    DEVELOPER = "Developer"
    VIEWER = "Viewer"

class UserCreate(BaseModel):
    name: str = Field(
        ..., 
        min_length=2, 
        max_length=100, 
        description="Full name of the user"
    )
    
    email: EmailStr = Field(
        ..., 
        description="Valid email address"
    )
    
    password: str = Field(
        ..., 
        min_length=8, 
        description="Password must be at least 8 characters"
    )
    
    role: UserRole = Field(
        ..., 
        description="User role in the system"
    )

class UserResponse(BaseModel):
    id: str
    name: str
    email: str
    role: UserRole
    
    class Config:
        from_attributes = True

class UserLogin(BaseModel):
    email: EmailStr
    password: str