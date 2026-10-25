import calendar
from datetime import date
from decimal import Decimal
from typing import Optional, List
from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.database import get_db
from app.core.security import get_current_user, get_effective_user_ids
from app.modules.auth.models import User
from app.modules.dashboard.schemas import DashboardResponse, TransactionSummary, CategoryBreakdown
from app.modules.income.models import Income
from app.modules.expenses.models import Expense
from app.modules.savings.models import SavingsGoal
from app.modules.budgets.models import Budget
from app.modules.budgets.router import to_budget_response

router = APIRouter(prefix="/api/dashboard", tags=["Dashboard"])

@router.get("", response_model=DashboardResponse)
def get_dashboard(
    year: Optional[int] = Query(None),
    month: Optional[int] = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    today = date.today()
    y = year if year else today.year
    m = month if month else today.month
    _, last_day = calendar.monthrange(y, m)
    start_date = date(y, m, 1)
    end_date = date(y, m, last_day)

    user_ids = get_effective_user_ids(current_user, db)

    # 1. Total Income & Expense for period
    total_income = db.query(func.coalesce(func.sum(Income.amount), Decimal(0))).filter(
        Income.user_id.in_(user_ids),
        Income.date.between(start_date, end_date)
    ).scalar() or Decimal(0)

    total_expense = db.query(func.coalesce(func.sum(Expense.amount), Decimal(0))).filter(
        Expense.user_id.in_(user_ids),
        Expense.date.between(start_date, end_date)
    ).scalar() or Decimal(0)

    balance = total_income - total_expense

    # 2. Savings goals total saved
    savings_saved = db.query(func.coalesce(func.sum(SavingsGoal.saved_amount), Decimal(0))).filter(
        SavingsGoal.user_id.in_(user_ids)
    ).scalar() or Decimal(0)

    # 3. Recent 5 Transactions (combined income & expenses)
    recent_incomes = db.query(Income).filter(Income.user_id.in_(user_ids)).order_by(Income.date.desc()).limit(5).all()
    recent_expenses = db.query(Expense).filter(Expense.user_id.in_(user_ids)).order_by(Expense.date.desc()).limit(5).all()

    tx_list: List[TransactionSummary] = []
    for inc in recent_incomes:
        u = inc.user
        tx_list.append(TransactionSummary(
            type="INCOME",
            id=inc.id,
            label=inc.source,
            amount=inc.amount,
            date=inc.date,
            addedByUsername=u.username if u else None,
            addedByName=u.full_name if u and u.full_name else (u.username if u else None)
        ))
    for exp in recent_expenses:
        u = exp.user
        tx_list.append(TransactionSummary(
            type="EXPENSE",
            id=exp.id,
            label=exp.category.value,
            amount=exp.amount,
            date=exp.date,
            addedByUsername=u.username if u else None,
            addedByName=u.full_name if u and u.full_name else (u.username if u else None)
        ))

    tx_list.sort(key=lambda t: t.date, reverse=True)
    recent_transactions = tx_list[:5]

    # 4. Budget Status
    budgets = db.query(Budget).filter(
        Budget.user_id.in_(user_ids),
        Budget.budget_year == y,
        Budget.budget_month == m
    ).all()
    budget_status = [to_budget_response(b, user_ids, db) for b in budgets]

    # 5. Category breakdown
    cat_query = db.query(
        Expense.category,
        func.coalesce(func.sum(Expense.amount), Decimal(0)).label("cat_total")
    ).filter(
        Expense.user_id.in_(user_ids),
        Expense.date.between(start_date, end_date)
    ).group_by(Expense.category).order_by(func.sum(Expense.amount).desc()).all()

    expense_by_category = [CategoryBreakdown(category=cq[0].value, total=cq[1]) for cq in cat_query]
    highest = expense_by_category[0] if expense_by_category else None

    return DashboardResponse(
        year=y,
        month=m,
        totalIncome=total_income,
        totalExpenses=total_expense,
        balance=balance,
        totalSavings=balance,
        savingsGoalsSaved=savings_saved,
        recentTransactions=recent_transactions,
        budgetStatus=budget_status,
        highestSpendingCategory=highest,
        expenseByCategory=expense_by_category
    )
