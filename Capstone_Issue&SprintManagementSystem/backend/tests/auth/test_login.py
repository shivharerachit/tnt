import uuid


def register_user(client, email, password):
    """Registers a user for login testing."""

    client.post(
        "/auth/register",
        json={
            "name": "Test User",
            "email": email,
            "password": password,
            "role": "MEMBER",
        },
    )


def test_login_success(client):
    """Test successful login."""

    email = f"{uuid.uuid4()}@test.com"
    password = "Password@123"

    register_user(client, email, password)

    response = client.post("/auth/login", json={"email": email, "password": password})

    assert response.status_code == 200
    body = response.json()
    assert "access_token" in body
    assert body["token_type"] == "bearer"


def test_users_accepts_bearer_and_raw_token(client):
    """Test /users with both Bearer and raw token headers."""

    email = f"{uuid.uuid4()}@test.com"
    password = "Password@123"

    register_user(client, email, password)

    login_response = client.post(
        "/auth/login", json={"email": email, "password": password}
    )
    token = login_response.json()["access_token"]

    bearer_response = client.get(
        "/users", headers={"Authorization": f"Bearer {token}"}
    )
    raw_response = client.get("/users", headers={"Authorization": token})

    assert bearer_response.status_code == 200
    assert raw_response.status_code == 200


def test_login_invalid_email(client):
    """Test login with invalid email."""

    response = client.post(
        "/auth/login", json={"email": "wrong@test.com", "password": "Password@123"}
    )

    assert response.status_code == 401
    assert response.json()["detail"] == "Invalid credentials"


def test_login_invalid_password(client):
    """Test login with invalid password."""

    email = f"{uuid.uuid4()}@test.com"

    register_user(client, email, "Password@123")

    response = client.post(
        "/auth/login", json={"email": email, "password": "WrongPassword"}
    )

    assert response.status_code == 401
    assert response.json()["detail"] == "Invalid credentials"


def test_login_missing_email(client):
    """Test login with missing email."""
    
    response = client.post("/auth/login", json={"password": "Password@123"})

    assert response.status_code == 400


def test_login_missing_password(client):
    """Test login with missing password."""

    response = client.post("/auth/login", json={"email": "test@test.com"})

    assert response.status_code == 400