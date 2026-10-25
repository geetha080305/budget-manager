from datetime import date, datetime
from decimal import Decimal
import calendar
from typing import Optional, List
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.database import get_db
from app.core.security import get_current_user, get_effective_user_ids
from app.modules.auth.models import User
from app.modules.expenses.models import Expense, ExpenseCategory, PaymentMethod
from app.modules.expenses.schemas import ExpenseRequest, ExpenseResponse, BudgetAlert

router = APIRouter(prefix="/api/expenses", tags=["Expenses"])

def compute_budget_alert(user_ids: List[int], category: ExpenseCategory, expense_date: date, db: Session) -> Optional[BudgetAlert]:
    from app.modules.budgets.models import Budget
    year = expense_date.year
    month = expense_date.month
    _, last_day = calendar.monthrange(year, month)
    start_date = date(year, month, 1)
    end_date = date(year, month, last_day)

    # 1. Check category-specific budget
    cat_budget = db.query(Budget).filter(
        Budget.user_id.in_(user_ids),
        Budget.category == category,
        Budget.budget_year == year,
        Budget.budget_month == month
    ).first()

    if cat_budget:
        spent = db.query(func.coalesce(func.sum(Expense.amount), Decimal(0))).filter(
            Expense.user_id.in_(user_ids),
            Expense.category == category,
            Expense.date.between(start_date, end_date)
        ).scalar()
        rem = cat_budget.amount - spent
        exceeded = rem < 0
        return BudgetAlert(
            scope="CATEGORY",
            budget=cat_budget.amount,
            spent=spent,
            remaining=Decimal(0) if exceeded else rem,
            exceeded=exceeded
        )

    # 2. Check overall monthly budget (category is None)
    overall_budget = db.query(Budget).filter(
        Budget.user_id.in_(user_ids),
        Budget.category == None,
        Budget.budget_year == year,
        Budget.budget_month == month
    ).first()

    if overall_budget:
        spent = db.query(func.coalesce(func.sum(Expense.amount), Decimal(0))).filter(
            Expense.user_id.in_(user_ids),
            Expense.date.between(start_date, end_date)
        ).scalar()
        rem = overall_budget.amount - spent
        exceeded = rem < 0
        return BudgetAlert(
            scope="OVERALL",
            budget=overall_budget.amount,
            spent=spent,
            remaining=Decimal(0) if exceeded else rem,
            exceeded=exceeded
        )

    return None

def to_expense_response(exp: Expense, alert: Optional[BudgetAlert] = None) -> ExpenseResponse:
    u = exp.user
    username = u.username if u else None
    fullname = u.full_name if u and u.full_name else username
    return ExpenseResponse(
        id=exp.id,
        category=exp.category,
        amount=exp.amount,
        paymentMethod=exp.payment_method,
        date=exp.date,
        description=exp.description,
        budgetAlert=alert,
        paidByUsername=username,
        paidByName=fullname
    )

@router.post("", response_model=ExpenseResponse, status_code=status.HTTP_201_CREATED)
def create_expense(req: ExpenseRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    exp = Expense(
        user_id=current_user.id,
        category=req.category,
        amount=req.amount,
        payment_method=req.paymentMethod,
        date=req.date,
        description=req.description
    )
    db.add(exp)
    db.commit()
    db.refresh(exp)

    user_ids = get_effective_user_ids(current_user, db)
    alert = compute_budget_alert(user_ids, req.category, req.date, db)
    return to_expense_response(exp, alert)

@router.get("", response_model=List[ExpenseResponse])
def list_expenses(
    start: Optional[date] = Query(None),
    end: Optional[date] = Query(None),
    category: Optional[ExpenseCategory] = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    user_ids = get_effective_user_ids(current_user, db)
    q = db.query(Expense).filter(Expense.user_id.in_(user_ids))

    if category:
        q = q.filter(Expense.category == category)
    if start and end:
        q = q.filter(Expense.date.between(start, end))

    records = q.order_by(Expense.date.desc()).all()
    return [to_expense_response(e) for e in records]

@router.get("/{expense_id}", response_model=ExpenseResponse)
def get_expense(expense_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    exp = db.query(Expense).filter(Expense.id == expense_id, Expense.user_id.in_(user_ids)).first()
    if not exp:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Expense not found")
    return to_expense_response(exp)

@router.put("/{expense_id}", response_model=ExpenseResponse)
def update_expense(expense_id: int, req: ExpenseRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    exp = db.query(Expense).filter(Expense.id == expense_id, Expense.user_id.in_(user_ids)).first()
    if not exp:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Expense not found")

    exp.category = req.category
    exp.amount = req.amount
    exp.payment_method = req.paymentMethod
    exp.date = req.date
    exp.description = req.description
    db.commit()
    db.refresh(exp)

    alert = compute_budget_alert(user_ids, req.category, req.date, db)
    return to_expense_response(exp, alert)

@router.delete("/{expense_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_expense(expense_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    exp = db.query(Expense).filter(Expense.id == expense_id, Expense.user_id.in_(user_ids)).first()
    if not exp:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Expense not found")
    db.delete(exp)
    db.commit()
    return None
