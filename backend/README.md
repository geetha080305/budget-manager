# Personal Expense & Budget Management System — Backend

Spring Boot 3 + MySQL + JWT REST API. Frontend is a separate static HTML/CSS/JS app
consuming this API (not included in this scaffold yet).

## Stack

- Java 21, Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring Security
- MySQL 8
- JWT auth (JJWT library) — stateless, no server-side sessions
- Lombok
- Maven

## What's implemented so far

- `User`, `Income`, `Expense`, `Budget` (handles both overall and per-category), `SavingsGoal`, `PaymentReminder` entities
- Repositories for all of the above, including the aggregate queries the
  dashboard/reports will need (sums by date range, by category, grouped totals)
- Full JWT auth: `POST /api/auth/register`, `POST /api/auth/login`
- Global exception handling (clean JSON errors, no stack traces leaked)
- **Income CRUD**: `POST/GET /api/income`, `GET/PUT/DELETE /api/income/{id}`,
  with `?start=YYYY-MM-DD&end=YYYY-MM-DD` filtering
- **Expense CRUD**: `POST/GET /api/expenses`, `GET/PUT/DELETE /api/expenses/{id}`,
  with `?start=&end=&category=` filtering
- **Budget CRUD**: `POST/GET /api/budgets`, `GET/PUT/DELETE /api/budgets/{id}`,
  `?year=&month=` (defaults to current month); duplicate (user, category, month)
  budgets are rejected with 409, and every budget response includes live spent/remaining
- **Savings goals**: `POST/GET /api/savings-goals`, `GET/PUT/DELETE .../{id}`,
  plus `POST .../{id}/contribute` to add money toward a goal without overwriting progress
- **Payment reminders**: `POST/GET /api/payment-reminders`, `GET/PUT/DELETE .../{id}`,
  plus `POST .../{id}/mark-paid` — for a recurring bill this automatically creates
  next month's reminder (Section 4.8: electricity, rent, subscriptions).
  Status (UPCOMING/DUE/OVERDUE) is computed live from today's date, not stored.
- **Dashboard**: `GET /api/dashboard?year=&month=` — total income/expenses/balance,
  recent transactions, this month's budget status, and category breakdown with
  highest-spending category (Section 4.6, all in one call)
- **Server-side budget alerts**: every expense create/update response includes
  a `budgetAlert` object (category budget takes priority over the overall
  monthly budget) showing budget/spent/remaining/exceeded for that month —
  this is the Section 4.4/5.1 "budget alert" requirement
- Ownership checks on every record: a user can never read/edit/delete another
  user's data (returns 404, not 403, to avoid confirming the ID exists)

- **Reports** (`/api/reports/...`):
  - `GET /daily?date=YYYY-MM-DD` — that day's income/expense totals + transaction list
  - `GET /weekly?date=YYYY-MM-DD` — the Monday-Sunday week containing that date, zero-filled per day
  - `GET /monthly?year=&month=` — full month totals, daily breakdown, and category breakdown
  - `GET /category-analysis?start=&end=` — spend per category with % of total, highest first
  - `GET /income-vs-expense?start=&end=` — totals, balance, and a savings-rate percentage
  - `GET /monthly-comparison?months=6` — last N months side by side (covers both "monthly comparison" and "spending trends" from the doc — a trend is just this series charted)

## Not yet built (next steps)

- The static frontend itself (everything else in the requirements doc now has a working endpoint)

## Running locally

### 1. Start MySQL (Docker — free, no account needed)

```bash
docker run --name expense-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=expense_db \
  -p 3306:3306 -d mysql:8.0
```

No Docker? Install MySQL Community Server directly and create a database
named `expense_db`.

### 2. Set the JWT secret (required)

Never rely on the placeholder in `application.properties` beyond your own
machine. Set a real one as an environment variable:

```bash
export JWT_SECRET="$(openssl rand -base64 48)"
```

(On Windows PowerShell: `$env:JWT_SECRET = [Convert]::ToBase64String((1..48 | %{Get-Random -Max 256}))`)

### 3. Run the app

This scaffold doesn't include the Maven wrapper (`mvnw`) — generate one with
`mvn -N wrapper:wrapper`, or just use your local Maven / your IDE's run button:

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

## Trying the auth endpoints

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"asha","email":"asha@example.com","password":"password123","fullName":"Asha K"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"asha","password":"password123"}'
```

Both return a JSON body like:

```json
{ "token": "eyJhbGciOi...", "tokenType": "Bearer", "username": "asha", "expiresInMs": 3600000 }
```

Use the token on every subsequent request:

```
Authorization: Bearer eyJhbGciOi...
```

## Trying the new endpoints

```bash
TOKEN="<paste the token from /api/auth/login>"

# Add an expense
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"category":"FOOD","amount":450.00,"paymentMethod":"UPI","date":"2026-09-15","description":"Groceries"}'

# List this month's food expenses
curl "http://localhost:8080/api/expenses?category=FOOD&start=2026-09-01&end=2026-09-30" \
  -H "Authorization: Bearer $TOKEN"
```

If a `Budget` row exists for that user/category/month, the expense response's
`budgetAlert` field shows the live spend-vs-budget status.

## Trying the dashboard and reports

```bash
curl "http://localhost:8080/api/dashboard?year=2026&month=9" \
  -H "Authorization: Bearer $TOKEN"

curl "http://localhost:8080/api/reports/monthly-comparison?months=6" \
  -H "Authorization: Bearer $TOKEN"

curl "http://localhost:8080/api/reports/category-analysis?start=2026-09-01&end=2026-09-30" \
  -H "Authorization: Bearer $TOKEN"
```

## Environment variables reference

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `expense_db` | Database name |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | (weak dev default — override this) | HS256 signing key, 32+ chars |
| `JWT_EXPIRATION_MS` | `3600000` (1 hour) | Access token lifetime |
| `SERVER_PORT` | `8080` | API port |

## Notes on the free-tooling constraints

Everything above (JDK, Spring Boot, MySQL, JWT library, Lombok, Maven) is
open-source with no paid tier. When you're ready to move the database or
the app off your machine, see the hosting comparison from earlier in this
conversation — Aiven's free MySQL tier and Render's free web service are
the genuinely no-trial, no-card options.
