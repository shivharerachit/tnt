"""
FastAPI application entry point.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .core.config import settings
from .core.exceptions import register_exception_handlers
from .db.client import connect_to_mongo, close_mongo_connection, get_db
from .db.seed import seed_demo_data, seed_admin
from .routers import auth, users

async def lifespan(_: FastAPI):
    # Startup: connect to MongoDB
    connect_to_mongo()
    if settings.SEED_DEMO_DATA:
        seed_admin(get_db())
        seed_demo_data(get_db())


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
app.include_router(users.router)

@app.get("/health")
def health_check() -> dict[str, str]:
    return {"status": "ok"}