"""
FastAPI application entry point.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .core.config import settings
from .core.exceptions import register_exception_handlers
from .db.client import connect_to_mongo, close_mongo_connection
from .routers import auth

async def lifespan(_: FastAPI):
    # Startup: connect to MongoDB
    connect_to_mongo()

    yield

    # Shutdown: close the connection
    close_mongo_connection()

app = FastAPI(
    title="Issue & Sprint Management System API",
    description="RESTful backend APIs",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Consistent JSON error responses
register_exception_handlers(app)

# Register all routers
app.include_router(auth.router)

@app.get("/health")
def health_check() -> dict[str, str]:
    return {"status": "ok"}