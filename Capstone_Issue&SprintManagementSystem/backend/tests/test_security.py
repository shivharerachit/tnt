from backend.app.core.security import hash_password, verify_password


def test_long_passwords_are_supported_by_bcrypt():
    password = "a" * 100

    hashed = hash_password(password)

    assert verify_password(password, hashed) is True
