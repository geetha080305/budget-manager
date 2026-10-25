from datetime import date
from decimal import Decimal
from typing import Optional
from pydantic import BaseModel, Field

class IncomeRequest(BaseModel):
    source: str = Field(..., max_length=80)
    amount: Decimal = Field(..., gt=0)
    date: date
    description: Optional[str] = None

class IncomeResponse(BaseModel):
    id: int
    source: str
    amount: Decimal
    date: date
    description: Optional[str] = None
    addedByUsername: Optional[str] = None
    addedByName: Optional[str] = None

    class Config:
        from_attributes = True
