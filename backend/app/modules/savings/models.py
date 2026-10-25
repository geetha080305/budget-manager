from decimal import Decimal
from sqlalchemy import Column, BigInteger, String, Date, Numeric, ForeignKey
from sqlalchemy.orm import relationship
from app.database import Base

class SavingsGoal(Base):
    __tablename__ = "savings_goals"

    id = Column(BigInteger, primary_key=True, autoincrement=True, index=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False, index=True)
    name = Column(String(100), nullable=False)
    target_amount = Column(Numeric(12, 2), nullable=False)
    saved_amount = Column(Numeric(12, 2), nullable=False, default=Decimal(0))
    target_date = Column(Date, nullable=True)

    user = relationship("app.modules.auth.models.User")
