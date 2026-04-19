# API Endpoints

Base URL: `http://localhost:8080`

## 1) Search Users
- Get all users:
    - `GET /users/search`
- Search with filters:
    - `GET /users/search?name=Priya&age=22&role=USER`

## 2) Delete User
- Delete user with ID `1` (confirmation required):
    - `DELETE /users/1?confirm=true`

## 3) Submit Post
- Create a new submission:
    - `POST /submit`

Request Body:
```json
{
    "title": "Health is wealth",
    "description": "Health is another wealth",
    "submittedBy": "Aman"
}
```