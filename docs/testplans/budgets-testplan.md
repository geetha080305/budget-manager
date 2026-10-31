# Budgets & Alerts Test Plan (TP-BDG)

## 1. Traceability
- **Requirement**: [REQ-BDG](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/budgets-requirements.md)

## 2. Test Cases & Verification Matrix

| TC ID | Test Description | Type | Input / Conditions | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-BDG-01** | Create Monthly Budget | Functional | Category: FOOD, Limit: 500, Month: 11, Year: 2026 | Budget created, initial spent: 0, remaining: 500 |
| **TC-BDG-02** | 80% Threshold Warning | Business Logic | Spend reaches 420 (84%) | `exceeded=false`, `budgetAlert.remaining=80` returned |
| **TC-BDG-03** | 100% Exceeded Alert | Business Logic | Spend reaches 520 (104%) | `exceeded=true`, alert flag raised on response |
| **TC-BDG-04** | Invalid Month Boundary | Validation | Month: 13 | HTTP 422 rejected |

