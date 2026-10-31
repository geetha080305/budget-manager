import pytest
from app.core.security import hash_password, verify_password, create_access_token
from jose import jwt
from app.config import settings

def test_password_hashing():
    raw_password = "supersecretpassword123"
    hashed = hash_password(raw_password)
    assert hashed != raw_password
    assert verify_password(raw_password, hashed) is True
    assert verify_password("wrongpassword", hashed) is False

def test_jwt_token_generation_and_decoding():
    subject = "testuser_42"
    token = create_access_token(data={"sub": subject})
    
    payload = jwt.decode(token, settings.JWT_SECRET, algorithms=[settings.JWT_ALGORITHM])
    assert payload.get("sub") == subject
    assert "exp" in payload
