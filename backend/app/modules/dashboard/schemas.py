from datetime import date
from decimal import Decimal
from typing import List, Optional
from pydantic import BaseModel
from app.modules.budgets.schemas import BudgetResponse

class TransactionSummary(BaseModel):
    type: str  # "INCOME" or "EXPENSE"
    id: int
    label: str
    amount: Decimal
    date: date
    addedByUsername: Optional[str] = None
    addedByName: Optional[str] = None

class CategoryBreakdown(BaseModel):
    category: str
    total: Decimal

class DashboardResponse(BaseModel):
    year: int
    month: int
    totalIncome: Decimal
    totalExpenses: Decimal
    balance: Decimal
    totalSavings: Decimal
    savingsGoalsSaved: Decimal
    recentTransactions: List[TransactionSummary] = []
    budgetStatus: List[BudgetResponse] = []
    highestSpendingCategory: Optional[CategoryBreakdown] = None
    expenseByCategory: List[CategoryBreakdown] = []
