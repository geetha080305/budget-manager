from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import settings
from app.database import engine, Base

# Import all models to ensure metadata registration
import app.modules.auth.models
import app.modules.households.models
import app.modules.expenses.models
import app.modules.income.models
import app.modules.budgets.models
import app.modules.savings.models
import app.modules.reminders.models

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Auto-create any missing tables at startup if connection available
    try:
        Base.metadata.create_all(bind=engine)
    except Exception:
        # Avoid crashing when running standalone unit tests without live DB
        pass
    yield

# Import module routers
from app.modules.auth.router import router as auth_router
from app.modules.households.router import router as household_router
from app.modules.expenses.router import router as expenses_router
from app.modules.income.router import router as income_router
from app.modules.budgets.router import router as budgets_router
from app.modules.savings.router import router as savings_router
from app.modules.reminders.router import router as reminders_router
from app.modules.dashboard.router import router as dashboard_router
from app.modules.reports.router import router as reports_router

app = FastAPI(
    title=settings.PROJECT_NAME,
    version="2.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    lifespan=lifespan
)

# CORS setup
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Health endpoint for Docker & monitoring
@app.get("/actuator/health", tags=["Health"])
def health_check():
    return {"status": "UP"}

# Include vertical slice routers
app.include_router(auth_router)
app.include_router(household_router)
app.include_router(expenses_router)
app.include_router(income_router)
app.include_router(budgets_router)
app.include_router(savings_router)
app.include_router(reminders_router)
app.include_router(dashboard_router)
app.include_router(reports_router)
