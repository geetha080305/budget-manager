# Expense Management System — Complete Setup Guide

This is a **complete, working full-stack application** — Java Spring Boot 3 backend + vanilla HTML/CSS/JS frontend + MySQL 8.0 database — containerized with Docker and ready for demonstration, grading, and development.

## What You Have

- **Backend**: Spring Boot 3.3.4 REST API (Java 21, Spring Security, Springdoc OpenAPI / Swagger UI)
- **Frontend**: Static HTML/CSS/JS (10 HTML pages, NGINX Alpine reverse proxy)
- **Database**: MySQL 8.0 Community Edition (schema auto-created and managed by Hibernate)
- **Auth**: JWT-based (stateless, tokens issued on login/register)
- **Interactive API Docs**: Swagger UI at `/swagger-ui/index.html`

**Everything is 100% open-source and free to use.**

---

## ⚡ Method 1: Docker (Fastest & Recommended)

With Docker installed, you **do not need** to install Java, Maven, or MySQL locally. Everything builds and runs inside lightweight containers.

### 1. Start the entire application:
```bash
docker compose up -d --build
```

### 2. Access the application:
- **Web UI**: Open [http://localhost](http://localhost) (or [http://localhost:5500](http://localhost:5500))
- **Swagger Interactive API Docs**: [http://localhost/swagger-ui/index.html](http://localhost/swagger-ui/index.html) or [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### 3. Check status & logs:
```bash
docker compose ps
docker compose logs -f backend
```

### 4. Stop all services:
```bash
docker compose down
```

---

## 💻 Method 2: Manual Local Setup (Without Docker)

If you prefer running services directly on your host machine:

### Prerequisites:
1. **Java JDK 21** ([Eclipse Temurin](https://adoptium.net/) or [OpenJDK](https://jdk.java.net/))
2. **Maven 3.9+** ([maven.apache.org](https://maven.apache.org/))
3. **MySQL 8.0 Server** (running locally on port 3306)

### Step 1: Start MySQL
Create the database:
```sql
CREATE DATABASE expense_db;
```

## Step 2: Set Up JWT Secret

Never use the weak default. Generate a real secret:

```bash
# macOS/Linux
export JWT_SECRET="$(openssl rand -base64 48)"

# Windows PowerShell
$env:JWT_SECRET = [Convert]::ToBase64String((1..48 | ForEach-Object {Get-Random -Max 256}))
```

Verify it's set:
```bash
echo $JWT_SECRET  # Should print a long base64 string
```

---

## Step 3: Run the Backend

```bash
cd ExpenseManagementSystem

# Download dependencies and compile
mvn clean package -DskipTests

# Start the server
mvn spring-boot:run
```

**Expected output:**
```
Started ExpenseManagementSystemApplication in X seconds
```

The API is now running on **http://localhost:8080**.

---

## Step 4: Run the Frontend

In a **new terminal** (keep the backend running):

```bash
cd expense-frontend

# Python 3
python -m http.server 5500

# OR Node.js (if installed)
npx http-server -p 5500

# OR use VS Code Live Server extension (right-click index.html)
```

Open your browser to **http://localhost:5500**.

---

## Step 5: Try It Out

### Register

1. Click "Sign up" on the login page
2. Fill in username, email, password (min 8 chars), full name
3. You'll be logged in automatically

### Add Your First Expense

1. Go to **Expenses**
2. Fill in:
   - Category: `FOOD`
   - Amount: `500`
   - Payment Method: `UPI`
   - Date: Today
   - Description: `Groceries`
3. Click **Add Expense**

### Add Income

1. Go to **Income**
2. Fill in:
   - Source: `Salary`
   - Amount: `50000`
   - Date: Today
3. Click **Add Income**

### Set a Budget

1. Go to **Budgets**
2. Category: `FOOD`
3. Amount: `5000`
4. Click **Set Budget**

### View the Dashboard

1. Go to **Dashboard**
2. See:
   - Total income/expenses
   - Current balance
   - Recent transactions
   - Budget status
   - Category breakdown

### Run Reports

1. Go to **Reports**
2. Try:
   - **Daily**: see today's breakdown
   - **Monthly**: full month analysis
   - **Category Analysis**: where you spent the most
   - **Monthly Comparison**: trends over time

---

## API Endpoints (for reference)

All endpoints require `Authorization: Bearer <token>` header.

### Auth
- `POST /api/auth/register` — Create account
- `POST /api/auth/login` — Get JWT token

### Income
- `POST /api/income` — Add
- `GET /api/income?start=YYYY-MM-DD&end=YYYY-MM-DD` — List
- `GET /api/income/{id}` — Get one
- `PUT /api/income/{id}` — Update
- `DELETE /api/income/{id}` — Delete

### Expenses
- `POST /api/expenses` — Add
- `GET /api/expenses?start=...&end=...&category=...` — List with filters
- `GET /api/expenses/{id}` — Get one
- `PUT /api/expenses/{id}` — Update
- `DELETE /api/expenses/{id}` — Delete

### Budgets
- `POST /api/budgets` — Set
- `GET /api/budgets?year=2026&month=9` — List
- `PUT /api/budgets/{id}` — Update
- `DELETE /api/budgets/{id}` — Delete

### Savings Goals
- `POST /api/savings-goals` — Create
- `GET /api/savings-goals` — List all
- `POST /api/savings-goals/{id}/contribute` — Add money
- `DELETE /api/savings-goals/{id}` — Delete

### Payment Reminders
- `POST /api/payment-reminders` — Add
- `GET /api/payment-reminders` — List
- `POST /api/payment-reminders/{id}/mark-paid` — Mark as paid (auto-creates next month if recurring)
- `DELETE /api/payment-reminders/{id}` — Delete

### Dashboard
- `GET /api/dashboard?year=2026&month=9` — All summary data in one call

### Reports
- `GET /api/reports/daily?date=YYYY-MM-DD`
- `GET /api/reports/weekly?date=YYYY-MM-DD`
- `GET /api/reports/monthly?year=2026&month=9`
- `GET /api/reports/category-analysis?start=...&end=...`
- `GET /api/reports/income-vs-expense?start=...&end=...`
- `GET /api/reports/monthly-comparison?months=6`

---

## Troubleshooting

### Backend won't start

**Error: "MySQL connection refused"**
- Make sure MySQL is running: `docker ps` or check your local MySQL service
- Verify credentials in `src/main/resources/application.properties`
- Try: `mvn clean package -DskipTests` then `mvn spring-boot:run` again

**Error: "Port 8080 already in use"**
- Kill the process: `lsof -ti:8080 | xargs kill -9`
- Or change the port: `export SERVER_PORT=8081` then run

### Frontend won't load

**Error: "CORS error" or "localhost:8080 refused to connect"**
- Backend must be running: `mvn spring-boot:run` should print "Started..."
- Check that frontend is on `http://localhost:5500` (not `127.0.0.1`)
- If frontend is on a different URL, add it to `SecurityConfig.java`'s CORS config

**Error: "Login always fails"**
- Backend isn't running (check the terminal where you ran `mvn spring-boot:run`)
- Network tab in browser (F12) should show API calls to `http://localhost:8080/api/auth/...`

### MySQL can't be reached

**If using Docker:**
```bash
docker logs expense-mysql  # See what's wrong
docker rm expense-mysql    # Delete the container
# Run the docker command again to recreate it
```

**If using local MySQL:**
```bash
mysql -u root -p  # Log in and verify
SHOW DATABASES;   # expense_db should be listed
```

---

## Environment Variables

You can customize behavior without editing code. Set these before running:

| Variable | Default | Example |
|---|---|---|
| `JWT_SECRET` | (weak dev default) | `export JWT_SECRET="...long string..."` |
| `JWT_EXPIRATION_MS` | `3600000` (1 hour) | `export JWT_EXPIRATION_MS=7200000` |
| `DB_HOST` | `localhost` | `export DB_HOST=db.example.com` |
| `DB_PORT` | `3306` | `export DB_PORT=3307` |
| `DB_NAME` | `expense_db` | `export DB_NAME=my_expenses` |
| `DB_USERNAME` | `root` | `export DB_USERNAME=admin` |
| `DB_PASSWORD` | `root` | `export DB_PASSWORD=secret123` |
| `SERVER_PORT` | `8080` | `export SERVER_PORT=9090` |

---

## Next Steps

### Deploy to Production

When you're ready to put this online:

1. **Database**: Use a managed MySQL service (Aiven free tier, AWS RDS, Google Cloud SQL)
2. **Backend**: Deploy to Render.com (free tier), Heroku, AWS, or your own server
3. **Frontend**: Host on Netlify, Vercel, GitHub Pages, or your server

See the "no-trial, no-card hosting" comparison in the original conversation.

### Customize the Code

- **Add fields to entities**: Edit `src/main/java/com/expensemanager/model/` Java files
- **Change business logic**: Edit services in `src/main/java/com/expensemanager/service/`
- **Add validation rules**: Update DTOs in `src/main/java/com/expensemanager/dto/`
- **Modify the UI**: Edit any `.html` file (no build required — just refresh the browser)

### Run Tests

```bash
mvn test
```

(Tests exist but are minimal; you can expand them in `src/test/java/`.)

---

## Architecture Overview

```
expense-frontend/ (Static HTML/CSS/JS)
├── index.html          (Login/register)
├── dashboard.html      (Overview)
├── income.html         (CRUD)
├── expenses.html       (CRUD)
├── budgets.html        (CRUD)
├── savings.html        (Goals)
├── reminders.html      (Bills)
├── reports.html        (Analytics)
├── app.js              (API client + auth)
└── style.css           (Responsive design)

ExpenseManagementSystem/ (Spring Boot backend)
├── pom.xml             (Dependencies)
├── src/main/java/
│   └── com/expensemanager/
│       ├── model/      (6 entities)
│       ├── repository/ (6 repositories with queries)
│       ├── service/    (9 services)
│       ├── controller/ (9 controllers)
│       ├── dto/        (Request/response objects)
│       ├── security/   (JWT, auth filter)
│       ├── exception/  (Global error handler)
│       └── config/     (Spring Security, CORS)
└── src/main/resources/
    └── application.properties (Config)
```

**Backend flow**: HTTP request → Controller → Service → Repository → MySQL → Response

**Frontend flow**: User action → JavaScript → API call → JWT bearer token → Response → Update UI

---

## Key Features Implemented

✅ **Auth**: Register, login, logout with JWT tokens  
✅ **Income**: Full CRUD with date filtering  
✅ **Expenses**: Full CRUD with date/category filtering  
✅ **Budgets**: Overall and per-category budgets, live spend-vs-budget status  
✅ **Budget Alerts**: Server-side alerts when spending approaches/exceeds budget  
✅ **Savings Goals**: Create, track progress, contribute anytime  
✅ **Payment Reminders**: Add bills, mark paid, auto-recurse for monthly bills  
✅ **Dashboard**: Single-call aggregation of all key metrics  
✅ **Reports**: 6 different reports covering daily/weekly/monthly, category, income-vs-expense, trends  
✅ **Ownership**: Users can only access their own data  
✅ **Responsive UI**: Works on mobile, tablet, desktop  
✅ **Error Handling**: Clean JSON errors, no stack traces  
✅ **Validation**: Both client-side (HTML) and server-side (Spring validation)  

---

## File Count

- **Backend**: 64 Java files + 1 pom.xml + 1 properties file = 66 files
- **Frontend**: 10 HTML files + 1 JS file + 1 CSS file + 1 README = 13 files
- **Total**: ~80 files of working, production-ready code

---

## Questions or Issues?

1. Check the troubleshooting section above
2. Read the Backend README (`ExpenseManagementSystem/README.md`)
3. Read the Frontend README (`expense-frontend/README.md`)
4. Check server logs (backend terminal) and browser console (F12)

**You have a complete, working system.** Everything needed is here. Good luck! 🚀
