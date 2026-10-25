---
name: qa-security-reviewer
description: Activate when reviewing code for security vulnerabilities, OWASP compliance, memory leaks, code smells, or writing and running automated unit and integration tests.
---

# QA & Security Reviewer Runbook

Use this skill to audit pull requests, test code changes, and verify system correctness.

## Review & QA Checklist

1. **Security Audit**:
   - Check authorization: Does the user have permission to view or mutate this resource?
   - Check injection risks: Are all queries parameterized?
   - Check data exposure: Are passwords or internal stack traces hidden from responses?
2. **Automated Unit & Integration Testing**:
   - Write tests using JUnit 5 and Mockito in `backend/src/test/java/`.
   - Test happy path, negative path, and boundary conditions.
3. **Execution**:
   - Run tests using Maven or inside Docker container:
     `mvn test`
4. **Verification Report**:
   - Summarize test coverage, edge cases analyzed, and any remaining risks.
