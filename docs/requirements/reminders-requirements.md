# Payment Reminders Requirements (REQ-REM)

## 1. Overview
Management of recurring bills, utility obligations, and subscription payments.

## 2. Requirements & User Stories
- **REQ-REM-01 (Create Reminder)**: Record bill title, due date, amount, recurrence interval, and remarks.
- **REQ-REM-02 (Status Lifecycle)**: State changes between `PENDING` $\rightarrow$ `PAID` or automatically flagged as `OVERDUE` when current date exceeds due date.
- **REQ-REM-03 (Quick Settle)**: Marking a reminder `PAID` allows one-click auto-creation of a corresponding expense entry.

