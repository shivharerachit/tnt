import bcrypt
from datetime import datetime, timedelta
from typing import Optional

from jose import JWTError, jwt

from backend.app.core.config import SECRET_KEY, ACCESS_TOKEN_EXPIRE_MINUTES


ALGORITHM = "HS256"

salt = bcrypt.gensalt(12)

def verify_password(plain_password: str, hashed_password: str) -> bool:
    return bcrypt.checkpw(plain_password.encode(), hashed_password.encode())

def get_password_hash(password: str) -> str:
    return bcrypt.hashpw(password.encode(), salt).decode()

def create_access_token(data: dict[str, str]) -> str:
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def decode_access_token(token:str) -> Optional[dict[str, str]]:
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except JWTError:
        return None