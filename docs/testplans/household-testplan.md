# Household Collaboration Test Plan (TP-HOU)

## 1. Traceability
- **Requirement**: [REQ-HOU](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/household-requirements.md)

## 2. Test Cases & Verification Matrix

| TC ID | Test Description | Type | Input / Conditions | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-HOU-01** | Create Household | Functional | Authenticated user creates "Sharma Family" | Household created, invite code generated, user is creator |
| **TC-HOU-02** | Join With Valid Code | Functional | Second user inputs valid invite code | Successfully added to household members list |
| **TC-HOU-03** | Join With Invalid Code | Validation | Input random/non-existent invite code | HTTP 404 / 400 with descriptive error message |
| **TC-HOU-04** | Shared Visibility | E2E | User A adds expense in shared household | User B can see transaction in shared reports |
