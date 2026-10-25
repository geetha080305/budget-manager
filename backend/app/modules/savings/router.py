from datetime import date
from decimal import Decimal
from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.core.security import get_current_user, get_effective_user_ids
from app.modules.auth.models import User
from app.modules.savings.models import SavingsGoal
from app.modules.savings.schemas import SavingsGoalRequest, ContributionRequest, SavingsGoalResponse
from app.modules.expenses.models import Expense, ExpenseCategory, PaymentMethod

router = APIRouter(prefix="/api/savings-goals", tags=["Savings Goals"])

def to_savings_response(goal: SavingsGoal) -> SavingsGoalResponse:
    rem = goal.target_amount - goal.saved_amount
    return SavingsGoalResponse(
        id=goal.id,
        name=goal.name,
        targetAmount=goal.target_amount,
        savedAmount=goal.saved_amount,
        remainingAmount=Decimal(0) if rem < 0 else rem,
        targetDate=goal.target_date
    )

@router.post("", response_model=SavingsGoalResponse, status_code=status.HTTP_201_CREATED)
def create_goal(req: SavingsGoalRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    goal = SavingsGoal(
        user_id=current_user.id,
        name=req.name,
        target_amount=req.targetAmount,
        target_date=req.targetDate,
        saved_amount=Decimal(0)
    )
    db.add(goal)
    db.commit()
    db.refresh(goal)
    return to_savings_response(goal)

@router.get("", response_model=List[SavingsGoalResponse])
def list_goals(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    goals = db.query(SavingsGoal).filter(SavingsGoal.user_id.in_(user_ids)).order_by(SavingsGoal.id.desc()).all()
    return [to_savings_response(g) for g in goals]

@router.get("/{goal_id}", response_model=SavingsGoalResponse)
def get_goal(goal_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    goal = db.query(SavingsGoal).filter(SavingsGoal.id == goal_id, SavingsGoal.user_id.in_(user_ids)).first()
    if not goal:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Savings goal not found")
    return to_savings_response(goal)

@router.put("/{goal_id}", response_model=SavingsGoalResponse)
def update_goal(goal_id: int, req: SavingsGoalRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    goal = db.query(SavingsGoal).filter(SavingsGoal.id == goal_id, SavingsGoal.user_id.in_(user_ids)).first()
    if not goal:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Savings goal not found")

    goal.name = req.name
    goal.target_amount = req.targetAmount
    goal.target_date = req.targetDate
    db.commit()
    db.refresh(goal)
    return to_savings_response(goal)

@router.post("/{goal_id}/contribute", response_model=SavingsGoalResponse)
def contribute_goal(goal_id: int, req: ContributionRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    goal = db.query(SavingsGoal).filter(SavingsGoal.id == goal_id, SavingsGoal.user_id.in_(user_ids)).first()
    if not goal:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Savings goal not found")

    # Increment saved amount
    goal.saved_amount += req.amount

    # Auto-log expense entry for contribution tracking
    exp = Expense(
        user_id=current_user.id,
        category=ExpenseCategory.OTHER,
        amount=req.amount,
        payment_method=PaymentMethod.UPI,
        date=date.today(),
        description=f"Savings Goal: {goal.name}"
    )
    db.add(exp)
    db.commit()
    db.refresh(goal)
    return to_savings_response(goal)

@router.delete("/{goal_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_goal(goal_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    goal = db.query(SavingsGoal).filter(SavingsGoal.id == goal_id, SavingsGoal.user_id.in_(user_ids)).first()
    if not goal:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Savings goal not found")
    db.delete(goal)
    db.commit()
    return None
