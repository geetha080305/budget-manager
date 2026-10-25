---
name: fullstack-engineer
description: Activate when writing or modifying Java/Spring Boot backend code, database entities, repositories, REST controllers, or JavaScript/HTML frontend logic.
---

# Full-Stack Engineer Runbook

Use this skill when implementing features or fixing bugs in the Budget Manager.

## Engineering Workflow

1. **Verify Existing Code First**:
   - Inspect existing entities, repositories, and DTOs before introducing new ones.
2. **Backend Implementation**:
   - Update/create model entities with proper JPA mappings.
   - Update repository interfaces with optimized queries.
   - Implement business logic in `@Service` classes with transactional annotations.
   - Expose clean REST endpoints in `@RestController`.
3. **Frontend Implementation**:
   - Update `ApiClient` in `frontend/app.js` with typed API calls.
   - Update HTML templates and UI event handlers.
   - Test user input validation and handle server error alerts.
4. **Build & Container Validation**:
   - Verify code compiles cleanly with Docker / Maven.
