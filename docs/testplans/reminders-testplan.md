# Payment Reminders Test Plan (TP-REM)

## 1. Traceability
- **Requirement**: [REQ-REM](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/reminders-requirements.md)

## 2. Test Cases & Verification Matrix

| TC ID | Test Description | Type | Input / Conditions | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-REM-01** | Create Reminder | Functional | Title: "WiFi Bill", Due: 2026-11-05, Amount: 60 | Reminder stored in `PENDING` state |
| **TC-REM-02** | Mark as Paid | State Flow | Transition status to `PAID` | Status updated; optional prompt to create expense entry |
| **TC-REM-03** | Overdue Detection | Business Logic | Current date is after due date while `PENDING` | Reminder marked as `OVERDUE` |

