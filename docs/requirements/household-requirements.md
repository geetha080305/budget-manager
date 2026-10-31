# Household Collaboration Requirements (REQ-HOU)

## 1. Overview
Enables multi-user family/household shared budget tracking and pooled financial visibility.

## 2. Requirements & User Stories
- **REQ-HOU-01 (Create Household)**: Any authenticated user can create a household, automatically becoming its primary owner/member.
- **REQ-HOU-02 (Unique Invite Code)**: A cryptographically unique invite code is auto-generated upon household creation.
- **REQ-HOU-03 (Join via Code)**: Other users can join the household by providing the valid invite code (`POST /api/households/join`).
- **REQ-HOU-04 (Shared View)**: Once joined, members gain combined visibility of household expenses, shared budgets, and goals.
