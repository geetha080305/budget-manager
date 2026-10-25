import enum
from datetime import date
from decimal import Decimal
from sqlalchemy import Column, BigInteger, String, Date, Numeric, Boolean, Enum, ForeignKey, Index
from sqlalchemy.orm import relationship
from app.database import Base

class ReminderStatus(str, enum.Enum):
    UPCOMING = "UPCOMING"
    DUE = "DUE"
    OVERDUE = "OVERDUE"
    PAID = "PAID"

class PaymentReminder(Base):
    __tablename__ = "payment_reminders"

    id = Column(BigInteger, primary_key=True, autoincrement=True, index=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False, index=True)
    bill_name = Column(String(100), nullable=False)
    amount = Column(Numeric(12, 2), nullable=False)
    due_date = Column(Date, nullable=False, index=True)
    status = Column(Enum(ReminderStatus), nullable=False, default=ReminderStatus.UPCOMING)
    is_recurring = Column(Boolean, nullable=False, default=False)

    user = relationship("app.modules.auth.models.User")

    __table_args__ = (
        Index("idx_reminder_user_duedate", "user_id", "due_date"),
    )
