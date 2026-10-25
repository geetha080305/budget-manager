---
name: principal-architect
description: Activate when designing database schemas, reviewing technical architecture, establishing API contracts, designing security/auth models, or making system trade-offs.
---

# Principal Architect Runbook

Use this skill to design and validate system architecture for the Budget Manager.

## Architectural Review Checklist

1. **Data Model Integrity**:
   - Are tables normalized properly?
   - Are foreign keys and indexes defined for query paths (e.g. `(user_id, date)`)?
   - Are monetary values strictly `BigDecimal(12, 2)`?
2. **API Design Standards**:
   - Are endpoints RESTful (`POST /api/expenses`, `GET /api/expenses/{id}`, etc.)?
   - Do requests and responses use immutable records/DTOs?
   - Are errors returned as standard JSON with clear HTTP status codes?
3. **Multi-User & Security Isolation**:
   - Does every query enforce household or user data isolation?
   - Are passwords BCrypt hashed and tokens JWT validated?
4. **Disaster Recovery Alignment**:
   - Does the architecture support zero-cost automated export and restore?
