# Authentication & Security Test Plan (TP-AUTH)

## 1. Traceability
- **Requirement**: [REQ-AUTH](file:///Users/pasupathithumbur/pasu-t/budget-manager/docs/requirements/auth-requirements.md)

## 2. Test Cases & Verification Matrix

| TC ID | Test Description | Type | Input / Conditions | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-AUTH-01** | Successful Registration | Integration / API | Valid username, email, full name, password | HTTP 201 / 200, user created, password salted & hashed |
| **TC-AUTH-02** | Duplicate Username Rejection | Validation | Register with already existing username | HTTP 400 Bad Request ("Username already taken") |
| **TC-AUTH-03** | Successful Login | Security | Correct username and password | HTTP 200 with signed JWT access token |
| **TC-AUTH-04** | Invalid Password Login | Security | Correct username, incorrect password | HTTP 401 Unauthorized |
| **TC-AUTH-05** | Expired / Tampered JWT | Security | Request with corrupted or expired signature | HTTP 401 Unauthorized |
