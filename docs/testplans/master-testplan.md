# Quality Assurance & Test Master Plan

## 1. Objectives
Ensure high reliability, security compliance, and correct financial computation across the Budget & Expense Manager application.

## 2. Test Strategy & Levels
### 2.1 Level 1: Unit Testing (Backend)
- **Scope**: Modules under `backend/app/modules/`, security utilities (`security.py`), and configuration parsing (`config.py`).
- **Runner**: `pytest` executed inside Python 3.15 virtualenv via `uv run pytest`.
- **Target Coverage**: $\ge 80\%$ on critical financial calculation logic and validation rules.

### 2.2 Level 2: API & Integration Testing
- **Scope**: REST endpoints executed with an in-memory SQLite / test MySQL container via `httpx.AsyncClient`.
- **Key Test Suites**:
  - `auth_test`: Registration, authentication failure cases, invalid password, expired JWT validation.
  - `household_test`: Creation, duplicate code prevention, member joining.
  - `expense_budget_test`: Expense logging, category constraints, budget threshold alert triggering.
  - `reports_test`: Aggregations and date range filters.

### 2.3 Level 3: End-to-End & Manual Smoke Testing
- **Scope**: Full stack execution via Docker Compose (`docker compose up -d`).
- **Smoke Checklist**:
  1. Open frontend at `http://localhost:5500` or `http://localhost`.
  2. Register new user and sign in.
  3. Verify OpenAPI Swagger UI at `http://localhost:8080/docs`.
  4. Create a Category Budget of \$500.
  5. Add an expense of \$450 and observe the >80% warning banner.
  6. Confirm health check status at `http://localhost:8080/actuator/health`.

## 3. Automation Cadence
- Local pre-commit / testing via `uv run pytest`.
- CI pipeline running containerized tests on pull requests to `main`.

