from decimal import Decimal
from typing import Optional
from pydantic import BaseModel, Field
from app.modules.expenses.models import ExpenseCategory

class BudgetRequest(BaseModel):
    category: Optional[ExpenseCategory] = None
    amount: Decimal = Field(..., gt=0)
    budgetYear: int = Field(..., ge=2020, le=2050)
    budgetMonth: int = Field(..., ge=1, le=12)

class BudgetResponse(BaseModel):
    id: int
    category: Optional[ExpenseCategory] = None
    amount: Decimal
    budgetYear: int
    budgetMonth: int
    spent: Decimal = Decimal(0)
    remaining: Decimal = Decimal(0)
    exceeded: bool = False

    class Config:
        from_attributes = True
