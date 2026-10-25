from datetime import date
from decimal import Decimal
from typing import Optional
from pydantic import BaseModel, Field
from app.modules.expenses.models import ExpenseCategory, PaymentMethod

class BudgetAlert(BaseModel):
    scope: str
    budget: Decimal
    spent: Decimal
    remaining: Decimal
    exceeded: bool

class ExpenseRequest(BaseModel):
    category: ExpenseCategory
    amount: Decimal = Field(..., gt=0)
    paymentMethod: PaymentMethod
    date: date
    description: Optional[str] = None

class ExpenseResponse(BaseModel):
    id: int
    category: ExpenseCategory
    amount: Decimal
    paymentMethod: PaymentMethod
    date: date
    description: Optional[str] = None
    budgetAlert: Optional[BudgetAlert] = None
    paidByUsername: Optional[str] = None
    paidByName: Optional[str] = None

    class Config:
        from_attributes = True
