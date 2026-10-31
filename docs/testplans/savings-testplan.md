# Savings Goals Test Plan (TP-SVG)

## 1. Traceability
- **Requirement**: [REQ-SVG](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/savings-requirements.md)

## 2. Test Cases & Verification Matrix

| TC ID | Test Description | Type | Input / Conditions | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-SVG-01** | Create Savings Goal | Functional | Target: 2000.00, Goal: "Emergency Fund" | Goal initialized with 0 current amount |
| **TC-SVG-02** | Add Contribution | Math / State | Add 500.00 contribution | Current amount = 500.00, progress = 25% |
| **TC-SVG-03** | 100% Goal Attainment | Boundary | Total contributed reaches 2000.00 | Progress = 100%, marked completed/achieved |
