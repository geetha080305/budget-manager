# Expenses & Income Requirements (REQ-EXP)

## 1. Overview
Core transactional tracking for all money flows (inflow income and outflow expenses).

## 2. Requirements & User Stories
- **REQ-EXP-01 (Add Expense)**: Users can record an expense specifying amount (>0), category (`FOOD`, `TRANSPORT`, `HOUSING`, `ENTERTAINMENT`, `UTILITIES`, etc.), date, payment method (`CASH`, `CREDIT_CARD`, `DEBIT_CARD`, `UPI`, `BANK_TRANSFER`), and optional note.
- **REQ-EXP-02 (Add Income)**: Users can record an income source, amount (>0), date, and description.
- **REQ-EXP-03 (List & Filter)**: Transactions must support listing and filtering by date range, payment method, or category.
- **REQ-EXP-04 (Attribution)**: In household accounts, each transaction records the creator while remaining visible to the shared pool.

