from datetime import date
from decimal import Decimal
from pydantic import BaseModel, Field
from app.modules.reminders.models import ReminderStatus

class PaymentReminderRequest(BaseModel):
    billName: str = Field(..., max_length=100)
    amount: Decimal = Field(..., gt=0)
    dueDate: date
    recurring: bool = False

class PaymentReminderResponse(BaseModel):
    id: int
    billName: str
    amount: Decimal
    dueDate: date
    status: ReminderStatus
    recurring: bool

    class Config:
        from_attributes = True
