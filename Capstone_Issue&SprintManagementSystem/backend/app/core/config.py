"""
Application configuration.

All settings are read from environment variables (.env)
Using a single Settings object
"""
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # MONGODB
    MONGODB_URI: str
    DATABASE_NAME: str

    # JWT
    JWT_SECRET: str
    JWT_ALGORITHM: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 1440

    # CORS
    CORS_ORIGINS: str

    SEED_DEMO_DATA: bool = True

    DEFAULT_ADMIN_NAME: str = "Administrator"
    DEFAULT_ADMIN_EMAIL: str = "admin@company.com"
    DEFAULT_ADMIN_PASSWORD: str = "password"

    # READ DATA FROM ENVIRONMENT
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    @property
    def cors_origins_list(self) -> list[str]:
        """Turn the comma-separated CORS string into a clean list."""
        return [origin.strip() for origin in self.CORS_ORIGINS.split(",") if origin.strip()]


settings = Settings()