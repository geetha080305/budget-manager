# Analytics & Reports Test Plan (TP-REP)

## 1. Traceability
- **Requirement**: [REQ-REP](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/reports-requirements.md)

## 2. Test Cases & Verification Matrix

| TC ID | Test Description | Type | Input / Conditions | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-REP-01** | Category Share Distribution | Calculations | Multiple expenses across categories | Percentages sum to 100%, amounts correctly grouped |
| **TC-REP-02** | Cash Flow Aggregation | Calculations | Total income 5000, Total expenses 3200 | Net balance returns 1800 |
| **TC-REP-03** | Daily & Monthly Totals | Reporting | Query transactions across current calendar month | Accurate daily totals aggregated without timezone offset |

