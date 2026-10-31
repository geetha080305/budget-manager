# Expense & Budget Management System

A modern full-stack Personal & Household Expense Management system powered by **Python 3.15 FastAPI**, **uv package manager**, **MySQL 8.0**, and an interactive **responsive web frontend served via Nginx**.

---

## 🚀 Quick Start with Docker (Recommended)

Run everything (MySQL 8, FastAPI Python 3.15 backend, and Nginx frontend) with a single command — **no local Python or MySQL installation required**:

```bash
docker compose up -d --build
```

To stop all services:
```bash
docker compose down
```

---

## 🌐 Application Access & Endpoints

| Component | URL | Description |
| :--- | :--- | :--- |
| **Web UI** | [http://localhost:5500](http://localhost:5500) (or [http://localhost:80](http://localhost:80)) | Full responsive frontend dashboard |
| **Interactive API Docs** | [http://localhost:8080/docs](http://localhost:8080/docs) | FastAPI OpenAPI 3.1 / Swagger UI |
| **Redoc API Spec** | [http://localhost:8080/redoc](http://localhost:8080/redoc) | Alternate clean API documentation |
| **Health Check** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Health status probe (`{"status":"UP"}`) |
| **MySQL Database** | `localhost:3306` | User: `expense_user` / DB: `expense_db` |

---

## 📚 Requirements & Test Plans Routing

Every functional domain has a dedicated requirement specification and corresponding test plan:

| Functional Domain | Requirements Specification | Quality & Test Plan |
| :--- | :--- | :--- |
| **Master Catalogs** | [Requirements Catalog](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/README.md) | [Test Plans Catalog](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/README.md) |
| **Authentication & Security** | [auth-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/auth-requirements.md) | [auth-testplan.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/auth-testplan.md) |
| **Household Collaboration** | [household-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/household-requirements.md) | [household-testplan.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/household-testplan.md) |
| **Expenses & Income** | [expenses-income-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/expenses-income-requirements.md) | [expenses-income-testplan.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/expenses-income-testplan.md) |
| **Budgets & Alerts** | [budgets-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/budgets-requirements.md) | [budgets-testplan.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/budgets-testplan.md) |
| **Payment Reminders** | [reminders-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/reminders-requirements.md) | [reminders-testplan.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/reminders-testplan.md) |
| **Savings Goals** | [savings-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/savings-requirements.md) | [savings-testplan.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/savings-testplan.md) |
| **Analytics & Reports** | [reports-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/reports-requirements.md) | [reports-testplan.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/reports-testplan.md) |

---

## 🛠️ Tech Stack

- **Backend**: Python 3.15, FastAPI, SQLAlchemy 2.0, Pydantic v2, PyMySQL, Bcrypt, Python-Jose (JWT)
- **Dependency & Runtime Management**: `uv` (Astral toolchain)
- **Database**: MySQL 8.0 Community Edition
- **Frontend**: Responsive HTML5, CSS3, JavaScript (ES6+), Bootstrap 5, Chart.js
- **Reverse Proxy**: NGINX 1.27 Alpine
- **Containerization**: Docker Compose with multi-stage build caching
- **Testing**: `pytest` configured via `pyproject.toml` (`[tool.pytest.ini_options]`)

---

## 🧪 Running Unit Tests

Execute backend unit tests with `uv`:
```bash
cd backend
uv run pytest
```

---

## 📂 Project Structure

```text
budget-manager/
├── backend/                # Python 3.15 + FastAPI REST API
│   ├── app/                # Vertical slice architecture
│   ├── tests/unit/         # Unit test suite
│   ├── pyproject.toml      # UV dependency & pytest configuration
│   └── Dockerfile          # Optimized container build
├── frontend/               # Responsive Web Application
│   ├── Dockerfile          # Lightweight Nginx host
│   ├── nginx.conf          # API reverse proxy
│   └── *.html, *.css, *.js
├── docs/                   # Documentation & Specifications
│   ├── requirements/       # Functional requirements catalog & domain specs
│   └── testplans/          # Test plans, matrices, and QA verification
├── docker-compose.yml      # Orchestrates MySQL + FastAPI + Nginx
└── README.md               # Master project overview
```
