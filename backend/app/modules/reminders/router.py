from datetime import date, timedelta
from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.core.security import get_current_user, get_effective_user_ids
from app.modules.auth.models import User
from app.modules.reminders.models import PaymentReminder, ReminderStatus
from app.modules.reminders.schemas import PaymentReminderRequest, PaymentReminderResponse
from app.modules.expenses.models import Expense, ExpenseCategory, PaymentMethod

router = APIRouter(prefix="/api/reminders", tags=["Payment Reminders"])

def compute_effective_status(rem: PaymentReminder) -> ReminderStatus:
    if rem.status == ReminderStatus.PAID:
        return ReminderStatus.PAID
    today = date.today()
    if rem.due_date < today:
        return ReminderStatus.OVERDUE
    if rem.due_date <= today + timedelta(days=3):
        return ReminderStatus.DUE
    return ReminderStatus.UPCOMING

def to_reminder_response(rem: PaymentReminder) -> PaymentReminderResponse:
    eff_status = compute_effective_status(rem)
    return PaymentReminderResponse(
        id=rem.id,
        billName=rem.bill_name,
        amount=rem.amount,
        dueDate=rem.due_date,
        status=eff_status,
        recurring=rem.is_recurring
    )

@router.post("", response_model=PaymentReminderResponse, status_code=status.HTTP_201_CREATED)
def create_reminder(req: PaymentReminderRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    rem = PaymentReminder(
        user_id=current_user.id,
        bill_name=req.billName,
        amount=req.amount,
        due_date=req.dueDate,
        is_recurring=req.recurring,
        status=ReminderStatus.UPCOMING
    )
    db.add(rem)
    db.commit()
    db.refresh(rem)
    return to_reminder_response(rem)

@router.get("", response_model=List[PaymentReminderResponse])
def list_reminders(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    rems = db.query(PaymentReminder).filter(PaymentReminder.user_id.in_(user_ids)).order_by(PaymentReminder.due_date.asc()).all()
    return [to_reminder_response(r) for r in rems]

@router.get("/{reminder_id}", response_model=PaymentReminderResponse)
def get_reminder(reminder_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    rem = db.query(PaymentReminder).filter(PaymentReminder.id == reminder_id, PaymentReminder.user_id.in_(user_ids)).first()
    if not rem:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Payment reminder not found")
    return to_reminder_response(rem)

@router.put("/{reminder_id}", response_model=PaymentReminderResponse)
def update_reminder(reminder_id: int, req: PaymentReminderRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    rem = db.query(PaymentReminder).filter(PaymentReminder.id == reminder_id, PaymentReminder.user_id.in_(user_ids)).first()
    if not rem:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Payment reminder not found")

    rem.bill_name = req.billName
    rem.amount = req.amount
    rem.due_date = req.dueDate
    rem.is_recurring = req.recurring
    db.commit()
    db.refresh(rem)
    return to_reminder_response(rem)

@router.post("/{reminder_id}/pay", response_model=PaymentReminderResponse)
def mark_paid(reminder_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    rem = db.query(PaymentReminder).filter(PaymentReminder.id == reminder_id, PaymentReminder.user_id.in_(user_ids)).first()
    if not rem:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Payment reminder not found")

    rem.status = ReminderStatus.PAID

    # Auto log bill expense
    exp = Expense(
        user_id=current_user.id,
        category=ExpenseCategory.BILLS,
        amount=rem.amount,
        payment_method=PaymentMethod.UPI,
        date=date.today(),
        description=f"Bill Paid: {rem.bill_name}"
    )
    db.add(exp)

    # If recurring, schedule for next month
    if rem.is_recurring:
        next_month = rem.due_date.month + 1 if rem.due_date.month < 12 else 1
        next_year = rem.due_date.year if rem.due_date.month < 12 else rem.due_date.year + 1
        next_due = date(next_year, next_month, min(rem.due_date.day, 28))
        next_rem = PaymentReminder(
            user_id=rem.user_id,
            bill_name=rem.bill_name,
            amount=rem.amount,
            due_date=next_due,
            is_recurring=True,
            status=ReminderStatus.UPCOMING
        )
        db.add(next_rem)

    db.commit()
    db.refresh(rem)
    return to_reminder_response(rem)

@router.delete("/{reminder_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_reminder(reminder_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    rem = db.query(PaymentReminder).filter(PaymentReminder.id == reminder_id, PaymentReminder.user_id.in_(user_ids)).first()
    if not rem:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Payment reminder not found")
    db.delete(rem)
    db.commit()
    return None
