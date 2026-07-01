"""
Security helpers: password hashing and JWT token creation/decoding.

Passwords are hashed with bcrypt, and login returns a signed JWT 
that contains the user id, email, role and expiry.
"""
from datetime import datetime, timedelta, timezone

import bcrypt
from jose import JWTError, jwt

from .config import settings
from .exceptions import UnauthorizedError


def _normalize_password(password: str) -> str:
    """Encode and Truncate passwords to bcrypt's 72-byte limit."""
    return password.encode("utf-8")[:72]


def hash_password(password: str) -> str:
    """Hash a plain-text password for safe storage."""
    salt = bcrypt.gensalt()
    return bcrypt.hashpw(_normalize_password(password), salt).decode("utf-8")


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Check a plain-text password against a stored hash."""
    return bcrypt.checkpw(
        _normalize_password(plain_password),
        hashed_password.encode("utf-8"),
    )


def create_access_token(user_id: str, email: str, role: str) -> str:
    """Create a signed JWT containing user_id, email, role and expiry."""
    expire = datetime.now(timezone.utc) + timedelta(
        minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES
    )
    payload = {
        "sub": user_id,
        "email": email,
        "role": role,
        "exp": expire,
    }
    return jwt.encode(payload, settings.JWT_SECRET, algorithm=settings.JWT_ALGORITHM)

def decode_access_token(token: str) -> dict:
    """Decode and validate a JWT. Raises 401 if the token is invalid/expired."""
    try:
        return jwt.decode(
            token, settings.JWT_SECRET, algorithms=[settings.JWT_ALGORITHM]
        )
    except JWTError:
        raise UnauthorizedError("Invalid or expired authentication token.")
