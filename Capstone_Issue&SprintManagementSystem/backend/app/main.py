from fastapi import FastAPI

from backend.app.routers.auth import router as auth_router

app = FastAPI(title="Issue & Sprint Management System")
app.include_router(auth_router)

@app.get("/health")
def health_check() -> dict[str, str]:
    return {"status": "ok"}