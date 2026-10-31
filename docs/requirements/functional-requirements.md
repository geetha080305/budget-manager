# Functional & Technical Requirements

## 1. Overview
The **Budget & Expense Management System** is a lightweight, responsive financial tracking platform designed for individual users and shared household members. It facilitates expense tracking, budget limit monitoring with alerts, recurring bill reminders, savings goal planning, and analytical reporting.

## 2. Architecture & Tech Stack
- **Backend Language**: Python 3.15 (managed seamlessly with `uv`)
- **Web Framework**: FastAPI (Asynchronous ASGI, OpenAPI 3.1 / Swagger UI at `/docs`)
- **Database**: MySQL 8.0 via SQLAlchemy 2.0 (Async/Declarative) with PyMySQL
- **Authentication**: Stateless JWT (HS256) with Bcrypt password hashing
- **Frontend**: Responsive Single-Page Application (HTML5, Tailwind/Modern CSS, JavaScript) served via Nginx reverse proxy
- **Containerization**: Docker Compose with multi-stage build caching

## 3. Core Functional Domains
### 3.1 Authentication & Profile
- User registration (`POST /api/auth/register`) with unique username and email validation.
- User login (`POST /api/auth/login`) returning a Bearer JWT token.
- Secure password hashing with salt using Bcrypt.

### 3.2 Household & Multi-User Collaboration
- Create households (`POST /api/households`).
- Join households using an invitation code (`POST /api/households/join`).
- Multi-member aggregated view of expenses, budgets, and savings.

### 3.3 Expense & Income Management
- Record income streams with source categorization and transaction dates.
- Record expenses categorized by essential living, entertainment, utilities, etc.
- Support payment methods: `CASH`, `CREDIT_CARD`, `DEBIT_CARD`, `UPI`, `BANK_TRANSFER`.

### 3.4 Budgets & Proactive Alerts
- Set monthly budget limits per category (`FOOD`, `TRANSPORT`, `HOUSING`, `ENTERTAINMENT`, etc.).
- Automated budget threshold warnings when spending exceeds 80% and 100% of the allocated quota.

### 3.5 Payment Reminders
- Schedule recurring bill payments (subscriptions, utilities, rent).
- Track reminder status (`PENDING`, `PAID`, `OVERDUE`).

### 3.6 Savings Goals
- Define target amounts and deadlines for specific savings goals.
- Log incremental contributions and compute percentage progress.

### 3.7 Analytics & Reporting
- Daily, weekly, monthly, and category breakdown reports.
- Income vs. Expense net cash flow comparison.

