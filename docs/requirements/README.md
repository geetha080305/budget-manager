# Requirements Catalog & Traceability Index

Welcome to the **Budget & Expense Management System Requirements Index**.  
This document acts as the master registry routing to all domain-specific specifications and business requirements.

---

## 🧭 Functional Domains & Specifications

| Module Code | Functional Domain | Status | Specification File | Key Features & Epics |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-AUTH** | **Authentication & Security** | `Approved` | [auth-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/auth-requirements.md) | User registration, login, JWT token issuance, Bcrypt password hashing. |
| **REQ-HOU** | **Household Collaboration** | `Approved` | [household-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/household-requirements.md) | Multi-user households, unique invite code generation, member joining. |
| **REQ-EXP** | **Expenses & Income** | `Approved` | [expenses-income-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/expenses-income-requirements.md) | Inflow/outflow tracking, category tags, payment methods, timestamps. |
| **REQ-BDG** | **Budgets & Threshold Alerts** | `Approved` | [budgets-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/budgets-requirements.md) | Monthly limits per category, 80% warning triggers, 100% exceeded flags. |
| **REQ-REM** | **Payment Reminders** | `Approved` | [reminders-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/reminders-requirements.md) | Recurring bill scheduling, status states (`PENDING`, `PAID`, `OVERDUE`). |
| **REQ-SVG** | **Savings Goals** | `Approved` | [savings-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/savings-requirements.md) | Target funding, deadlines, incremental contributions, milestone tracking. |
| **REQ-REP** | **Analytics & Reporting** | `Approved` | [reports-requirements.md](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/reports-requirements.md) | Daily/Weekly/Monthly breakdowns, category charts, cash flow balance. |

---

## 📋 Comprehensive Baseline
- For the full consolidated architectural and technical baseline, see the [Master Functional Requirements](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/functional-requirements.md).
- To view test cases mapped against these requirements, consult the [Test Plans Master Catalog](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/testplans/README.md).

