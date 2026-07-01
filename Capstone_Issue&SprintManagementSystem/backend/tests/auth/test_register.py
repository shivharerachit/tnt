def register(client, payload):
    return client.post("/auth/register", json=payload)


def test_register_invalid_email(client):
    response = register(
        client,
        {
            "name": "John",
            "email": "invalid-email",
            "password": "Password@123",
            "role": "EMPLOYEE",
        },
    )

    assert response.status_code == 422


def test_register_missing_name(client):
    response = register(
        client,
        {
            "email": "john@example.com",
            "password": "Password@123",
            "role": "EMPLOYEE",
        },
    )

    assert response.status_code == 422


def test_register_missing_password(client):
    response = register(
        client,
        {"name": "John", "email": "john@example.com", "role": "EMPLOYEE"},
    )

    assert response.status_code == 422


def test_register_missing_role(client):
    response = register(
        client,
        {"name": "John", "email": "john@example.com", "password": "Password@123"},
    )

    assert response.status_code == 400


def test_register_invalid_role(client):
    response = register(
        client,
        {
            "name": "John",
            "email": "john@example.com",
            "password": "Password@123",
            "role": "ADMINISTRATOR",
        },
    )

    assert response.status_code == 422