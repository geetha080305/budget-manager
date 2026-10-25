from datetime import date
from decimal import Decimal
from typing import List, Optional
from pydantic import BaseModel
from app.modules.dashboard.schemas import TransactionSummary, CategoryBreakdown

class DailyTotal(BaseModel):
    date: date
    income: Decimal
    expense: Decimal

class CategoryShare(BaseModel):
    category: str
    total: Decimal
    percentage: float

class DailyReportResponse(BaseModel):
    date: date
    totalIncome: Decimal
    totalExpense: Decimal
    transactions: List[TransactionSummary] = []

class WeeklyReportResponse(BaseModel):
    startDate: date
    endDate: date
    totalIncome: Decimal
    totalExpense: Decimal
    dailyTotals: List[DailyTotal] = []

class MonthlyReportResponse(BaseModel):
    year: int
    month: int
    totalIncome: Decimal
    totalExpense: Decimal
    netSavings: Decimal
    dailyTotals: List[DailyTotal] = []
    categoryBreakdown: List[CategoryBreakdown] = []

class CategoryAnalysisResponse(BaseModel):
    startDate: date
    endDate: date
    totalExpense: Decimal
    categories: List[CategoryShare] = []

class IncomeVsExpenseResponse(BaseModel):
    startDate: date
    endDate: date
    totalIncome: Decimal
    totalExpense: Decimal
    netSavings: Decimal
    savingsRate: Optional[float] = None

class MonthlyComparisonEntry(BaseModel):
    year: int
    month: int
    totalIncome: Decimal
    totalExpense: Decimal
    netSavings: Decimal
