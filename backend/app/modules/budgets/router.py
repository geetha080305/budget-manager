import calendar
from datetime import date
from decimal import Decimal
from typing import Optional, List
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.database import get_db
from app.core.security import get_current_user, get_effective_user_ids
from app.modules.auth.models import User
from app.modules.budgets.models import Budget
from app.modules.budgets.schemas import BudgetRequest, BudgetResponse
from app.modules.expenses.models import Expense

router = APIRouter(prefix="/api/budgets", tags=["Budgets"])

def to_budget_response(budget: Budget, user_ids: List[int], db: Session) -> BudgetResponse:
    year = budget.budget_year
    month = budget.budget_month
    _, last_day = calendar.monthrange(year, month)
    start_date = date(year, month, 1)
    end_date = date(year, month, last_day)

    q = db.query(func.coalesce(func.sum(Expense.amount), Decimal(0))).filter(
        Expense.user_id.in_(user_ids),
        Expense.date.between(start_date, end_date)
    )
    if budget.category:
        q = q.filter(Expense.category == budget.category)

    spent = q.scalar() or Decimal(0)
    rem = budget.amount - spent
    exceeded = rem < 0

    return BudgetResponse(
        id=budget.id,
        category=budget.category,
        amount=budget.amount,
        budgetYear=budget.budget_year,
        budgetMonth=budget.budget_month,
        spent=spent,
        remaining=Decimal(0) if exceeded else rem,
        exceeded=exceeded
    )

@router.post("", response_model=BudgetResponse, status_code=status.HTTP_201_CREATED)
def create_budget(req: BudgetRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    existing = db.query(Budget).filter(
        Budget.user_id.in_(user_ids),
        Budget.category == req.category,
        Budget.budget_year == req.budgetYear,
        Budget.budget_month == req.budgetMonth
    ).first()

    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="A budget already exists for that category and month - update it instead"
        )

    budget = Budget(
        user_id=current_user.id,
        category=req.category,
        amount=req.amount,
        budget_year=req.budgetYear,
        budget_month=req.budgetMonth
    )
    db.add(budget)
    db.commit()
    db.refresh(budget)
    return to_budget_response(budget, user_ids, db)

@router.get("", response_model=List[BudgetResponse])
def list_budgets(
    year: Optional[int] = Query(None),
    month: Optional[int] = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    y = year if year else date.today().year
    m = month if month else date.today().month
    user_ids = get_effective_user_ids(current_user, db)

    budgets = db.query(Budget).filter(
        Budget.user_id.in_(user_ids),
        Budget.budget_year == y,
        Budget.budget_month == m
    ).all()

    return [to_budget_response(b, user_ids, db) for b in budgets]

@router.get("/{budget_id}", response_model=BudgetResponse)
def get_budget(budget_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    b = db.query(Budget).filter(Budget.id == budget_id, Budget.user_id.in_(user_ids)).first()
    if not b:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Budget not found")
    return to_budget_response(b, user_ids, db)

@router.put("/{budget_id}", response_model=BudgetResponse)
def update_budget(budget_id: int, req: BudgetRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    b = db.query(Budget).filter(Budget.id == budget_id, Budget.user_id.in_(user_ids)).first()
    if not b:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Budget not found")

    b.category = req.category
    b.amount = req.amount
    b.budget_year = req.budgetYear
    b.budget_month = req.budgetMonth
    db.commit()
    db.refresh(b)
    return to_budget_response(b, user_ids, db)

@router.delete("/{budget_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_budget(budget_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    b = db.query(Budget).filter(Budget.id == budget_id, Budget.user_id.in_(user_ids)).first()
    if not b:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Budget not found")
    db.delete(b)
    db.commit()
    return None
