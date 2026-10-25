from datetime import date
from decimal import Decimal
from sqlalchemy import Column, BigInteger, String, Date, Numeric, ForeignKey, Index
from sqlalchemy.orm import relationship
from app.database import Base

class Income(Base):
    __tablename__ = "income"

    id = Column(BigInteger, primary_key=True, autoincrement=True, index=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False, index=True)
    source = Column(String(80), nullable=False)
    amount = Column(Numeric(12, 2), nullable=False)
    date = Column(Date, nullable=False, index=True)
    description = Column(String(255), nullable=True)

    user = relationship("app.modules.auth.models.User")

    __table_args__ = (
        Index("idx_income_user_date", "user_id", "date"),
    )
