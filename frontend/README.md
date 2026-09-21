# Expense Manager — Frontend

Static HTML/CSS/JavaScript frontend consuming the Spring Boot REST API.

## Files

- `index.html` — Login & registration page
- `dashboard.html` — Overview: income, expenses, balance, budgets, recent transactions
- `income.html` — Income CRUD with date filtering
- `expenses.html` — Expense CRUD with date/category filtering
- `budgets.html` — Set and track monthly/category budgets
- `savings.html` — Create savings goals and track progress
- `reminders.html` — Add and track recurring bills
- `reports.html` — Daily/weekly/monthly reports, category analysis, income vs expense, trends
- `app.js` — Shared utilities: API client, auth, formatting helpers
- `style.css` — Styling (responsive, dark-aware)

## Running Locally

### Option 1: Simple HTTP Server (Python)

```bash
cd expense-frontend
python -m http.server 5500
```

Then open **http://localhost:5500** in your browser.

### Option 2: Node.js (http-server)

```bash
npm install -g http-server
http-server .
```

### Option 3: VS Code Live Server Extension

Right-click `index.html` → "Open with Live Server"

## Configuration

The API base URL is hardcoded in `app.js`:

```javascript
const API_BASE = 'http://localhost:8080/api';
```

If your backend runs on a different port/host, update this line.

## CORS

The backend's Spring Security config allows these origins:
- `http://localhost:5500`
- `http://127.0.0.1:5500`
- `http://localhost:3000`

If you run the frontend elsewhere, update the CORS config in `SecurityConfig.java`.

## Features

- **Authentication**: JWT tokens stored in `localStorage`
- **Responsive Design**: Works on mobile, tablet, desktop
- **All CRUD Operations**: Create, read, update, delete for all resource types
- **Filtering**: By date range, category, payment method
- **Budget Alerts**: Shows when spending exceeds budget
- **Recurring Bills**: Auto-creates next month's reminder when marked paid
- **Reports**: 6 different report types covering all analytics from the requirements
- **Dark Mode**: CSS uses neutral colors that adapt to system preference

## Authentication Flow

1. Register or login on `index.html`
2. Server returns a JWT token + username
3. Token stored in `localStorage`
4. Every API request includes `Authorization: Bearer <token>` header
5. If token expires, user is redirected to login

## API Calls

All API interactions go through the `ApiClient` class in `app.js`:

```javascript
// Example: get this month's expenses by category
const expenses = await ApiClient.getExpenses('2026-09-01', '2026-09-30', 'FOOD');

// Example: add income
const response = await ApiClient.createIncome('Salary', 50000, '2026-09-20', 'Monthly salary');

// Example: get dashboard
const dashboard = await ApiClient.getDashboard(2026, 9);
```

Error handling: all methods throw on non-2xx responses; catch them to show alerts.

## No Build Step Required

This is vanilla HTML/CSS/JavaScript—no build tools, transpilers, or bundlers needed. Just open `index.html` in a browser (or serve via HTTP).
