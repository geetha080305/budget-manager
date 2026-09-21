# Expense & Budget Management System

A full-stack Personal Expense & Budget Management web application built with **Java 21, Spring Boot 3, MySQL 8.0, and an interactive Vanilla HTML/CSS/JS frontend served via Nginx**.

---

## 🚀 Quick Start with Docker (Recommended)

Run everything (MySQL, Spring Boot Backend, Nginx Frontend) with a single command — **no local Java, Maven, or MySQL installation required**:

```bash
docker compose up --build
```

To run in the background (detached mode):
```bash
docker compose up -d --build
```

To stop all services:
```bash
docker compose down
```

---

## 🌐 Application Access & Endpoints

| Component | URL | Description |
| :--- | :--- | :--- |
| **Web UI** | [http://localhost:80](http://localhost:80) or [http://localhost:5500](http://localhost:5500) | Full responsive frontend dashboard |
| **Swagger UI** | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) | Interactive OpenAPI / Swagger REST documentation |
| **API Docs (OpenAPI)** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | Raw OpenAPI v3 JSON spec |
| **Health Check** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Spring Boot actuator health endpoint |
| **MySQL Database** | `localhost:3306` | User: `expense_user` / DB: `expense_db` |

---

## 🛠️ Tech Stack (100% Free & Open Source)

- **Backend**: Java 21, Spring Boot 3.3.4, Spring Data JPA, Spring Security 6, JJWT 0.12.6, Springdoc OpenAPI 2.6.0
- **Database**: MySQL 8.0 Community Edition
- **Frontend**: HTML5, CSS3, JavaScript (ES6+), Bootstrap 5, Chart.js
- **Reverse Proxy & Server**: NGINX 1.27 Alpine
- **Containerization**: Docker & Docker Compose with multi-stage build caching

---

## 📂 Project Structure

```text
budget-manager/
├── backend/                # Java 21 + Spring Boot 3 REST API
│   ├── Dockerfile          # Multi-stage Maven + Temurin JRE build
│   ├── pom.xml
│   └── src/
├── frontend/               # Web Application
│   ├── Dockerfile          # Lightweight Nginx static host
│   ├── nginx.conf          # Reverse proxy routing /api to backend
│   ├── *.html, *.css, *.js
├── docker-compose.yml      # Orchestrates MySQL + Backend + Frontend
├── .dockerignore
└── SETUP.md                # Comprehensive setup and API test guide
```
