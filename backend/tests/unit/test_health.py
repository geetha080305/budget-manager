from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health_check_endpoint():
    response = client.get("/actuator/health")
    assert response.status_code == 200
    data = response.json()
    assert data.get("status") == "UP"

def test_openapi_docs_endpoint():
    response = client.get("/docs")
    assert response.status_code == 200
