from fastapi import APIRouter, Depends, HTTPException, status, Response
from sqlalchemy.orm import Session
from app.database import get_db
from app.core.security import get_current_user
from app.modules.auth.models import User
from app.modules.households.models import Household
from app.modules.households.schemas import HouseholdRequest, JoinHouseholdRequest, HouseholdResponse, HouseholdMemberSummary

router = APIRouter(prefix="/api/household", tags=["Household"])

def to_household_response(household: Household, db: Session) -> HouseholdResponse:
    members = db.query(User).filter(User.household_id == household.id).all()
    member_summaries = [
        HouseholdMemberSummary(id=m.id, username=m.username, fullName=m.full_name)
        for m in members
    ]
    return HouseholdResponse(
        id=household.id,
        name=household.name,
        inviteCode=household.invite_code,
        members=member_summaries
    )

@router.get("", response_model=HouseholdResponse)
def get_my_household(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if not current_user.household_id:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    household = db.query(Household).filter(Household.id == current_user.household_id).first()
    if not household:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    return to_household_response(household, db)

@router.post("", response_model=HouseholdResponse, status_code=status.HTTP_201_CREATED)
def create_household(req: HouseholdRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if current_user.household_id:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="You are already in a household")
    
    household = Household(name=req.name.strip())
    db.add(household)
    db.commit()
    db.refresh(household)

    current_user.household_id = household.id
    db.commit()

    return to_household_response(household, db)

@router.post("/join", response_model=HouseholdResponse)
def join_household(req: JoinHouseholdRequest, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if current_user.household_id:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="You are already in a household")

    household = db.query(Household).filter(Household.invite_code == req.inviteCode.strip().upper()).first()
    if not household:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Invalid household invite code")

    current_user.household_id = household.id
    db.commit()

    return to_household_response(household, db)

@router.post("/leave", status_code=status.HTTP_204_NO_CONTENT)
def leave_household(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if not current_user.household_id:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="You are not a member of any household")

    household_id = current_user.household_id
    current_user.household_id = None
    db.commit()

    # Clean up empty household
    remaining = db.query(User).filter(User.household_id == household_id).count()
    if remaining == 0:
        db.query(Household).filter(Household.id == household_id).delete()
        db.commit()
    return None
