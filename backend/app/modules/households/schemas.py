from pydantic import BaseModel, Field
from typing import Optional, List

class HouseholdMemberSummary(BaseModel):
    id: int
    username: str
    fullName: Optional[str] = None

class HouseholdRequest(BaseModel):
    name: str = Field(..., max_length=100)

class JoinHouseholdRequest(BaseModel):
    inviteCode: str

class HouseholdResponse(BaseModel):
    id: int
    name: str
    inviteCode: str
    members: List[HouseholdMemberSummary] = []
