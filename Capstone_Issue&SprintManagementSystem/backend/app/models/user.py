from bson import ObjectId
from pydantic import BaseModel, EmailStr, Field, field_validator


class User(BaseModel):
    id: str = Field(..., alias="_id")
    name: str
    email: EmailStr
    hashed_password: str
    role: str = "member"
    
    @field_validator("id", mode="before")
    @classmethod
    def convert_object_id(cls, value):
        if isinstance(value, ObjectId):
            return str(value)
        return value


class UserCreate(BaseModel):
    name: str
    email: EmailStr
    password: str
    role: str = "member"


class UserLogin(BaseModel):
    email: EmailStr
    password: str
