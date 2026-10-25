from datetime import date
from typing import Optional, List
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from app.database import get_db
from app.core.security import get_current_user, get_effective_user_ids
from app.modules.auth.models import User
from app.modules.income.models import Income
from app.modules.income.schemas import IncomeRequest, IncomeResponse

router = APIRouter(prefix="/api/income", tags=["Income"])

def to_income_response(inc: Income) -> IncomeResponse:
    u = inc.user
    username = u.username if u else None
    fullname = u.full_name if u and u.full_name else username
    return IncomeResponse(
        id=inc.id,
        source=inc.source,
        amount=inc.amount,
        date=inc.date,
        description=inc.description,
        addedByUsername=username,
        addedByName=fullname
    )

@router.post("", response_model=IncomeResponse, status_code=status.HTTP_201_CREATED)
def create_income(req: IncomeRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    inc = Income(
        user_id=current_user.id,
        source=req.source,
        amount=req.amount,
        date=req.date,
        description=req.description
    )
    db.add(inc)
    db.commit()
    db.refresh(inc)
    return to_income_response(inc)

@router.get("", response_model=List[IncomeResponse])
def list_income(
    start: Optional[date] = Query(None),
    end: Optional[date] = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    user_ids = get_effective_user_ids(current_user, db)
    q = db.query(Income).filter(Income.user_id.in_(user_ids))
    if start and end:
        q = q.filter(Income.date.between(start, end))
    records = q.order_by(Income.date.desc()).all()
    return [to_income_response(i) for i in records]

@router.get("/{income_id}", response_model=IncomeResponse)
def get_income(income_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    inc = db.query(Income).filter(Income.id == income_id, Income.user_id.in_(user_ids)).first()
    if not inc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Income not found")
    return to_income_response(inc)

@router.put("/{income_id}", response_model=IncomeResponse)
def update_income(income_id: int, req: IncomeRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    inc = db.query(Income).filter(Income.id == income_id, Income.user_id.in_(user_ids)).first()
    if not inc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Income not found")

    inc.source = req.source
    inc.amount = req.amount
    inc.date = req.date
    inc.description = req.description
    db.commit()
    db.refresh(inc)
    return to_income_response(inc)

@router.delete("/{income_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_income(income_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_ids = get_effective_user_ids(current_user, db)
    inc = db.query(Income).filter(Income.id == income_id, Income.user_id.in_(user_ids)).first()
    if not inc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Income not found")
    db.delete(inc)
    db.commit()
    return None
