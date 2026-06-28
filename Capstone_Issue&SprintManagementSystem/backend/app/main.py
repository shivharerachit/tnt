from fastapi import FastAPI

app = FastAPI(title="Issue & Sprint Management System")

@app.get("/health")
def health_check() -> dict[str, str]:
    return {"status": "ok"}