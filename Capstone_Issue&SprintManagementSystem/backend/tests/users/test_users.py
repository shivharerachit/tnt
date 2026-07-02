import uuid


def test_users_route_accepts_raw_and_bearer_token(client):
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
