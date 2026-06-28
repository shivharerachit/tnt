import bcrypt


salt = bcrypt.gensalt(12)

def get_password_hash(password: str) -> str:
    return bcrypt.hashpw(password.encode(), salt).decode()
