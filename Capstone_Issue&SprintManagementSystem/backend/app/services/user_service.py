from typing import Optional

from pymongo.collection import Collection

from backend.app.core.security import get_password_hash
from backend.app.models.user import User, UserCreate


def get_user_by_email(users: Collection, email: str) -> Optional[User]:
    result = users.find_one({"email": email})
    return User(**result) if result else None


def create_user(users: Collection, user_data: UserCreate) -> User:
    hashed_password = str(get_password_hash(user_data.password))
    user_dict = user_data.dict(exclude={"password"})
    user_dict["hashed_password"] = hashed_password
    result = users.insert_one(user_dict)
    created = users.find_one({"_id": result.inserted_id})
    if not created:
        raise RuntimeError("Failed to find newly created user")
    return User(**created)