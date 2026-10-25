import uuid
from datetime import datetime
from sqlalchemy import Column, BigInteger, String, DateTime
from sqlalchemy.orm import relationship
from app.database import Base

def generate_invite_code():
    return uuid.uuid4().hex[:8].upper()

class Household(Base):
    __tablename__ = "households"

    id = Column(BigInteger, primary_key=True, autoincrement=True, index=True)
    name = Column(String(100), nullable=False)
    invite_code = Column(String(12), unique=True, nullable=False, default=generate_invite_code, index=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)

    members = relationship("User", back_populates="household")
