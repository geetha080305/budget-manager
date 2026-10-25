import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_NAME: str = "Expense & Budget Management System"
    SERVER_PORT: int = int(os.getenv("SERVER_PORT", "8080"))
    
    # Database config
    DB_HOST: str = os.getenv("DB_HOST", "mysql")
    DB_PORT: int = int(os.getenv("DB_PORT", "3306"))
    DB_NAME: str = os.getenv("DB_NAME", "expense_db")
    DB_USERNAME: str = os.getenv("DB_USERNAME", "expense_user")
    DB_PASSWORD: str = os.getenv("DB_PASSWORD", "expense_pass")
    
    # JWT security
    JWT_SECRET: str = os.getenv("JWT_SECRET", "c2VjdXJlLWp3dC1zZWNyZXQta2V5LTI1Ni1iaXQtdmFsaWQtZm9yLWV4cGVuc2UtbWFuYWdlcg==")
    JWT_ALGORITHM: str = "HS256"
    JWT_EXPIRATION_MS: int = int(os.getenv("JWT_EXPIRATION_MS", "86400000"))

    @property
    def database_url(self) -> str:
        return f"mysql+pymysql://{self.DB_USERNAME}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}?charset=utf8mb4"

settings = Settings()
