from pydantic import BaseModel, ConfigDict, EmailStr
from typing import Optional


class UserOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: Optional[str]
    name: str
    email: EmailStr
    role: str