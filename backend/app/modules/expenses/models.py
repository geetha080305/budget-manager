import enum
from datetime import date
from decimal import Decimal
from sqlalchemy import Column, BigInteger, String, Date, Numeric, Enum, ForeignKey, Index
from sqlalchemy.orm import relationship
from app.database import Base

class ExpenseCategory(str, enum.Enum):
    FOOD = "FOOD"
    TRANSPORT = "TRANSPORT"
    EDUCATION = "EDUCATION"
    SHOPPING = "SHOPPING"
    RENT = "RENT"
    BILLS = "BILLS"
    ENTERTAINMENT = "ENTERTAINMENT"
    HEALTHCARE = "HEALTHCARE"
    OTHER = "OTHER"

class PaymentMethod(str, enum.Enum):
    CASH = "CASH"
    CARD = "CARD"
    UPI = "UPI"
    BANK_TRANSFER = "BANK_TRANSFER"
    OTHER = "OTHER"

class Expense(Base):
    __tablename__ = "expenses"

    id = Column(BigInteger, primary_key=True, autoincrement=True, index=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False, index=True)
    category = Column(Enum(ExpenseCategory), nullable=False)
    amount = Column(Numeric(12, 2), nullable=False)
    payment_method = Column(Enum(PaymentMethod), nullable=False)
    date = Column(Date, nullable=False, index=True)
    description = Column(String(255), nullable=True)

    user = relationship("app.modules.auth.models.User")

    __table_args__ = (
        Index("idx_expense_user_date", "user_id", "date"),
        Index("idx_expense_user_category", "user_id", "category"),
    )
