# Expenses & Income Test Plan (TP-EXP)

## 1. Traceability
- **Requirement**: [REQ-EXP](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/expenses-income-requirements.md)

## 2. Test Cases & Verification Matrix

| TC ID | Test Description | Type | Input / Conditions | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-EXP-01** | Add Valid Expense | CRUD | Amount: 50.00, Category: FOOD, Method: UPI | HTTP 200, expense saved with user attribution |
| **TC-EXP-02** | Prevent Negative Amount | Boundary | Amount: -10.00 | HTTP 422 Unprocessable Entity (Pydantic validation) |
| **TC-EXP-03** | Record Income | CRUD | Source: "Salary", Amount: 3000.00 | HTTP 200, balance incremented |
| **TC-EXP-04** | Filter by Date Range | Functional | Query between 2026-10-01 and 2026-10-31 | Returns only records within bounds |

