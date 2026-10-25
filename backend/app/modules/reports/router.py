import calendar
from datetime import date, timedelta
from decimal import Decimal
from typing import Optional, List
from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.database import get_db
from app.core.security import get_current_user, get_effective_user_ids
from app.modules.auth.models import User
from app.modules.income.models import Income
from app.modules.expenses.models import Expense
from app.modules.dashboard.schemas import TransactionSummary, CategoryBreakdown
from app.modules.reports.schemas import (
    DailyReportResponse, WeeklyReportResponse, MonthlyReportResponse,
    CategoryAnalysisResponse, CategoryShare, IncomeVsExpenseResponse,
    MonthlyComparisonEntry, DailyTotal
)

router = APIRouter(prefix="/api/reports", tags=["Reports"])

def build_daily_totals(start_date: date, end_date: date, user_ids: List[int], db: Session) -> List[DailyTotal]:
    # Group incomes by date
    incomes = db.query(Income.date, func.sum(Income.amount)).filter(
        Income.user_id.in_(user_ids),
        Income.date.between(start_date, end_date)
    ).group_by(Income.date).all()
    inc_map = {row[0]: row[1] for row in incomes}

    # Group expenses by date
    expenses = db.query(Expense.date, func.sum(Expense.amount)).filter(
        Expense.user_id.in_(user_ids),
        Expense.date.between(start_date, end_date)
    ).group_by(Expense.date).all()
    exp_map = {row[0]: row[1] for row in expenses}

    totals = []
    curr = start_date
    while curr <= end_date:
        totals.append(DailyTotal(
            date=curr,
            income=inc_map.get(curr, Decimal(0)),
            expense=exp_map.get(curr, Decimal(0))
        ))
        curr += timedelta(days=1)
    return totals

@router.get("/daily", response_model=DailyReportResponse)
def get_daily_report(
    report_date: Optional[date] = Query(None, alias="date"),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    target = report_date if report_date else date.today()
    user_ids = get_effective_user_ids(current_user, db)

    incomes = db.query(Income).filter(Income.user_id.in_(user_ids), Income.date == target).all()
    expenses = db.query(Expense).filter(Expense.user_id.in_(user_ids), Expense.date == target).all()

    total_inc = sum((i.amount for i in incomes), Decimal(0))
    total_exp = sum((e.amount for e in expenses), Decimal(0))

    tx: List[TransactionSummary] = []
    for inc in incomes:
        u = inc.user
        tx.append(TransactionSummary(
            type="INCOME", id=inc.id, label=inc.source, amount=inc.amount, date=inc.date,
            addedByUsername=u.username if u else None,
            addedByName=u.full_name if u and u.full_name else (u.username if u else None)
        ))
    for exp in expenses:
        u = exp.user
        tx.append(TransactionSummary(
            type="EXPENSE", id=exp.id, label=exp.category.value, amount=exp.amount, date=exp.date,
            addedByUsername=u.username if u else None,
            addedByName=u.full_name if u and u.full_name else (u.username if u else None)
        ))
    tx.sort(key=lambda t: t.date, reverse=True)

    return DailyReportResponse(date=target, totalIncome=total_inc, totalExpense=total_exp, transactions=tx)

@router.get("/weekly", response_model=WeeklyReportResponse)
def get_weekly_report(
    target_date: Optional[date] = Query(None, alias="date"),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    ref = target_date if target_date else date.today()
    start = ref - timedelta(days=ref.weekday())  # Monday
    end = start + timedelta(days=6)              # Sunday
    user_ids = get_effective_user_ids(current_user, db)

    daily_totals = build_daily_totals(start, end, user_ids, db)
    total_inc = sum((d.income for d in daily_totals), Decimal(0))
    total_exp = sum((d.expense for d in daily_totals), Decimal(0))

    return WeeklyReportResponse(startDate=start, endDate=end, totalIncome=total_inc, totalExpense=total_exp, dailyTotals=daily_totals)

@router.get("/monthly", response_model=MonthlyReportResponse)
def get_monthly_report(
    year: int = Query(...),
    month: int = Query(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    _, last_day = calendar.monthrange(year, month)
    start = date(year, month, 1)
    end = date(year, month, last_day)
    user_ids = get_effective_user_ids(current_user, db)

    daily_totals = build_daily_totals(start, end, user_ids, db)
    total_inc = sum((d.income for d in daily_totals), Decimal(0))
    total_exp = sum((d.expense for d in daily_totals), Decimal(0))

    cat_query = db.query(Expense.category, func.sum(Expense.amount)).filter(
        Expense.user_id.in_(user_ids),
        Expense.date.between(start, end)
    ).group_by(Expense.category).order_by(func.sum(Expense.amount).desc()).all()

    breakdown = [CategoryBreakdown(category=cq[0].value, total=cq[1]) for cq in cat_query]

    return MonthlyReportResponse(
        year=year, month=month,
        totalIncome=total_inc, totalExpense=total_exp,
        netSavings=total_inc - total_exp,
        dailyTotals=daily_totals, categoryBreakdown=breakdown
    )

@router.get("/category-analysis", response_model=CategoryAnalysisResponse)
def get_category_analysis(
    start: date = Query(...),
    end: date = Query(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    user_ids = get_effective_user_ids(current_user, db)
    cat_query = db.query(Expense.category, func.sum(Expense.amount)).filter(
        Expense.user_id.in_(user_ids),
        Expense.date.between(start, end)
    ).group_by(Expense.category).order_by(func.sum(Expense.amount).desc()).all()

    total = sum((cq[1] for cq in cat_query), Decimal(0))
    shares = []
    for cq in cat_query:
        pct = float((cq[1] / total) * 100) if total > 0 else 0.0
        shares.append(CategoryShare(category=cq[0].value, total=cq[1], percentage=round(pct, 1)))

    return CategoryAnalysisResponse(startDate=start, endDate=end, totalExpense=total, categories=shares)

@router.get("/income-vs-expense", response_model=IncomeVsExpenseResponse)
def get_income_vs_expense(
    start: date = Query(...),
    end: date = Query(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    user_ids = get_effective_user_ids(current_user, db)
    total_inc = db.query(func.coalesce(func.sum(Income.amount), Decimal(0))).filter(
        Income.user_id.in_(user_ids), Income.date.between(start, end)
    ).scalar() or Decimal(0)

    total_exp = db.query(func.coalesce(func.sum(Expense.amount), Decimal(0))).filter(
        Expense.user_id.in_(user_ids), Expense.date.between(start, end)
    ).scalar() or Decimal(0)

    balance = total_inc - total_exp
    rate = float((balance / total_inc) * 100) if total_inc > 0 else None

    return IncomeVsExpenseResponse(
        startDate=start, endDate=end,
        totalIncome=total_inc, totalExpense=total_exp,
        netSavings=balance, savingsRate=round(rate, 1) if rate is not None else None
    )

@router.get("/monthly-comparison", response_model=List[MonthlyComparisonEntry])
def get_monthly_comparison(
    months: int = Query(6, ge=1, le=24),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    user_ids = get_effective_user_ids(current_user, db)
    today = date.today()
    entries = []

    for i in range(months - 1, -1, -1):
        # Calculate year and month for past i months
        m = today.month - i
        y = today.year
        while m <= 0:
            m += 12
            y -= 1

        _, last_day = calendar.monthrange(y, m)
        start = date(y, m, 1)
        end = date(y, m, last_day)

        inc = db.query(func.coalesce(func.sum(Income.amount), Decimal(0))).filter(
            Income.user_id.in_(user_ids), Income.date.between(start, end)
        ).scalar() or Decimal(0)

        exp = db.query(func.coalesce(func.sum(Expense.amount), Decimal(0))).filter(
            Expense.user_id.in_(user_ids), Expense.date.between(start, end)
        ).scalar() or Decimal(0)

        entries.append(MonthlyComparisonEntry(
            year=y, month=m,
            totalIncome=inc, totalExpense=exp,
            netSavings=inc - exp
        ))

    return entries
