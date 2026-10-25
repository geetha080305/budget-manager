---
name: devsecops-sre
description: Activate when configuring automated encrypted backups, setting up zero-cost cloud disaster recovery, inspecting Docker container health, optimizing docker-compose, or running security audits.
---

# DevSecOps & SRE (Disaster Recovery Specialist) Runbook

Use this skill to automate and maintain backups, container reliability, and zero-cost operations.

## Core Operations

1. **Automated Encrypted Backup Workflow**:
   - Step 1: Dump database via `docker exec -i expense-mysql mysqldump -u expense_user -p... expense_db > backup.sql`.
   - Step 2: Encrypt with AES-256 via 7-Zip (`7z a -p... -mhe=on backup_YYYYMMDD.7z backup.sql`) or OpenSSL.
   - Step 3: Sync to user's Cloud folder (OneDrive / Google Drive / Proton).
   - Step 4: Securely wipe plaintext SQL file.
2. **Disaster Recovery Test**:
   - Periodically verify that decrypting and loading `backup.7z` on a fresh system restores 100% of the tables and records without data loss.
3. **Container Health & Hardening**:
   - Inspect container status: `docker ps`.
   - Inspect container logs: `docker logs --tail 50 expense-backend`.
   - Validate restart policies and volume mounts.
