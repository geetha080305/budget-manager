from datetime import date
from decimal import Decimal
from typing import Optional
from pydantic import BaseModel, Field

class SavingsGoalRequest(BaseModel):
    name: str = Field(..., max_length=100)
    targetAmount: Decimal = Field(..., gt=0)
    targetDate: Optional[date] = None

class ContributionRequest(BaseModel):
    amount: Decimal = Field(..., gt=0)

class SavingsGoalResponse(BaseModel):
    id: int
    name: str
    targetAmount: Decimal
    savedAmount: Decimal
    remainingAmount: Decimal
    targetDate: Optional[date] = None

    class Config:
        from_attributes = True
