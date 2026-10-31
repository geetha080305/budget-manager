# Budgets & Alerts Requirements (REQ-BDG)

## 1. Overview
Proactive financial limit tracking and early warning alerts against overspending.

## 2. Requirements & User Stories
- **REQ-BDG-01 (Set Monthly Limits)**: Users can define budget spending caps per category for a specific month and year.
- **REQ-BDG-02 (Threshold Warnings)**:
  - If spending is between $\ge 80\%$ and $< 100\%$ of the budget limit, return warning state `exceeded=false` with remaining balance.
  - If spending reaches $\ge 100\%$, set `exceeded=true`.
- **REQ-BDG-03 (Real-time Calculation)**: Any newly logged expense recalculates category budget consumption on-the-fly and embeds budget alert status in the response.

