from decimal import Decimal
from sqlalchemy import Column, BigInteger, Numeric, Integer, Enum, ForeignKey, UniqueConstraint
from sqlalchemy.orm import relationship
from app.database import Base
from app.modules.expenses.models import ExpenseCategory

class Budget(Base):
    __tablename__ = "budgets"

    id = Column(BigInteger, primary_key=True, autoincrement=True, index=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False, index=True)
    category = Column(Enum(ExpenseCategory), nullable=True)
    amount = Column(Numeric(12, 2), nullable=False)
    budget_year = Column(Integer, nullable=False)
    budget_month = Column(Integer, nullable=False)

    user = relationship("app.modules.auth.models.User")

    __table_args__ = (
        UniqueConstraint("user_id", "category", "budget_year", "budget_month", name="uk_budget_user_category_period"),
    )
