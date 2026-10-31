# Authentication & Security Requirements (REQ-AUTH)

## 1. Overview
Secure authentication and user lifecycle management ensuring protected access to financial data.

## 2. Requirements & User Stories
- **REQ-AUTH-01 (Registration)**: Users must be able to register with username, full name, email, and password. Duplicate emails or usernames must return HTTP 400.
- **REQ-AUTH-02 (Password Security)**: Passwords must be hashed using `Bcrypt` with cryptographic salt before persisting.
- **REQ-AUTH-03 (Authentication)**: Valid login credentials (`POST /api/auth/login`) issue a signed JWT access token (HS256).
- **REQ-AUTH-04 (Token Expiry)**: Access tokens expire after 24 hours. Expired or forged tokens must be rejected with HTTP 401 Unauthorized.
