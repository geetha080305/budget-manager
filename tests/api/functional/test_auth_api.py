import time
import pytest
import httpx

@pytest.mark.asyncio
async def test_auth_registration_and_login_flow(api_client: httpx.AsyncClient):
    """
    Industry Standard Functional API Test:
    1. Register a new unique user.
    2. Log in with the registered credentials to receive a valid JWT token.
    3. Verify token format and authentication response structure.
    """
    unique_suffix = int(time.time() * 1000)
    username = f"qa_user_{unique_suffix}"
    email = f"qa_{unique_suffix}@example.com"
    password = "StrongPassword@123"
    full_name = "QA Automation User"

    # Step 1: User Registration
    register_payload = {
        "username": username,
        "email": email,
        "password": password,
        "fullName": full_name
    }
    reg_response = await api_client.post("/api/auth/register", json=register_payload)
    assert reg_response.status_code in (200, 201), f"Registration failed: {reg_response.text}"
    
    reg_data = reg_response.json()
    assert reg_data.get("username") == username
    assert "token" in reg_data
    assert reg_data.get("type") == "Bearer"

    # Step 2: Login Flow
    login_payload = {
        "username": username,
        "password": password
    }
    login_response = await api_client.post("/api/auth/login", json=login_payload)
    assert login_response.status_code == 200, f"Login failed: {login_response.text}"
    
    login_data = login_response.json()
    assert "token" in login_data or "access_token" in login_data
    token = login_data.get("token") or login_data.get("access_token")
    assert isinstance(token, str) and len(token) > 20
    assert login_data.get("username") == username

@pytest.mark.asyncio
async def test_auth_login_invalid_credentials(api_client: httpx.AsyncClient):
    """
    Verify security boundary: Invalid credentials return 401 Unauthorized.
    """
    login_payload = {
        "username": "non_existent_qa_user_999",
        "password": "WrongPassword!"
    }
    response = await api_client.post("/api/auth/login", json=login_payload)
    assert response.status_code == 401

