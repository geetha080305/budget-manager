// Configuration
// When served via Nginx reverse-proxy on port 80/5500, use relative '/api'
// When opened directly in local browser (e.g. file:// or Live Server), fallback to 'http://localhost:8080/api'
const API_BASE = (window.location.origin && window.location.origin !== 'null' && !window.location.protocol.startsWith('file'))
    ? (window.location.port === '8080' ? 'http://localhost:8080/api' : '/api')
    : 'http://localhost:8080/api';

// Auth utilities
class AuthService {
    static getToken() {
        return localStorage.getItem('token');
    }

    static setToken(token) {
        localStorage.setItem('token', token);
    }

    static clearToken() {
        localStorage.removeItem('token');
    }

    static getUsername() {
        return localStorage.getItem('username');
    }

    static setUsername(username) {
        localStorage.setItem('username', username);
    }

    static isLoggedIn() {
        return !!this.getToken();
    }

    static logout() {
        this.clearToken();
        this.clearUsername();
        window.location.href = 'index.html';
    }
}

// API Client
class ApiClient {
    static async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        const token = AuthService.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(url, {
            ...options,
            headers
        });

        if (response.status === 401) {
            AuthService.logout();
            return;
        }

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.details?.[0] || data.error || 'Request failed');
        }
        return data;
    }

    // Auth endpoints
    static register(username, email, password, fullName) {
        return this.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, email, password, fullName })
        });
    }

    static login(username, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
    }

    // Income endpoints
    static getIncome(start, end) {
        let url = '/income';
        if (start && end) {
            url += `?start=${start}&end=${end}`;
        }
        return this.request(url);
    }

    static getIncomeById(id) {
        return this.request(`/income/${id}`);
    }

    static createIncome(source, amount, date, description) {
        return this.request('/income', {
            method: 'POST',
            body: JSON.stringify({ source, amount: parseFloat(amount), date, description })
        });
    }

    static updateIncome(id, source, amount, date, description) {
        return this.request(`/income/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ source, amount: parseFloat(amount), date, description })
        });
    }

    static deleteIncome(id) {
        return this.request(`/income/${id}`, { method: 'DELETE' });
    }

    // Expense endpoints
    static getExpenses(start, end, category) {
        let url = '/expenses';
        const params = [];
        if (start && end) {
            params.push(`start=${start}`, `end=${end}`);
        }
        if (category) {
            params.push(`category=${category}`);
        }
        if (params.length) url += '?' + params.join('&');
        return this.request(url);
    }

    static getExpenseById(id) {
        return this.request(`/expenses/${id}`);
    }

    static createExpense(category, amount, paymentMethod, date, description) {
        return this.request('/expenses', {
            method: 'POST',
            body: JSON.stringify({
                category,
                amount: parseFloat(amount),
                paymentMethod,
                date,
                description
            })
        });
    }

    static updateExpense(id, category, amount, paymentMethod, date, description) {
        return this.request(`/expenses/${id}`, {
            method: 'PUT',
            body: JSON.stringify({
                category,
                amount: parseFloat(amount),
                paymentMethod,
                date,
                description
            })
        });
    }

    static deleteExpense(id) {
        return this.request(`/expenses/${id}`, { method: 'DELETE' });
    }

    // Budget endpoints
    static getBudgets(year, month) {
        let url = '/budgets';
        const params = [];
        if (year) params.push(`year=${year}`);
        if (month) params.push(`month=${month}`);
        if (params.length) url += '?' + params.join('&');
        return this.request(url);
    }

    static createBudget(category, amount, budgetYear, budgetMonth) {
        return this.request('/budgets', {
            method: 'POST',
            body: JSON.stringify({
                category: category || null,
                amount: parseFloat(amount),
                budgetYear,
                budgetMonth
            })
        });
    }

    static updateBudget(id, category, amount, budgetYear, budgetMonth) {
        return this.request(`/budgets/${id}`, {
            method: 'PUT',
            body: JSON.stringify({
                category: category || null,
                amount: parseFloat(amount),
                budgetYear,
                budgetMonth
            })
        });
    }

    static deleteBudget(id) {
        return this.request(`/budgets/${id}`, { method: 'DELETE' });
    }

    // Savings Goal endpoints
    static getSavingsGoals() {
        return this.request('/savings-goals');
    }

    static createSavingsGoal(name, targetAmount, targetDate) {
        return this.request('/savings-goals', {
            method: 'POST',
            body: JSON.stringify({ name, targetAmount: parseFloat(targetAmount), targetDate })
        });
    }

    static updateSavingsGoal(id, name, targetAmount, targetDate) {
        return this.request(`/savings-goals/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ name, targetAmount: parseFloat(targetAmount), targetDate })
        });
    }

    static contributeSavingsGoal(id, amount) {
        return this.request(`/savings-goals/${id}/contribute`, {
            method: 'POST',
            body: JSON.stringify({ amount: parseFloat(amount) })
        });
    }

    static deleteSavingsGoal(id) {
        return this.request(`/savings-goals/${id}`, { method: 'DELETE' });
    }

    // Payment Reminder endpoints
    static getReminders() {
        return this.request('/payment-reminders');
    }

    static createReminder(billName, amount, dueDate, recurring) {
        return this.request('/payment-reminders', {
            method: 'POST',
            body: JSON.stringify({ billName, amount: parseFloat(amount), dueDate, recurring })
        });
    }

    static updateReminder(id, billName, amount, dueDate, recurring) {
        return this.request(`/payment-reminders/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ billName, amount: parseFloat(amount), dueDate, recurring })
        });
    }

    static markReminderPaid(id) {
        return this.request(`/payment-reminders/${id}/mark-paid`, { method: 'POST' });
    }

    static deleteReminder(id) {
        return this.request(`/payment-reminders/${id}`, { method: 'DELETE' });
    }

    // Dashboard
    static getDashboard(year, month) {
        let url = '/dashboard';
        const params = [];
        if (year) params.push(`year=${year}`);
        if (month) params.push(`month=${month}`);
        if (params.length) url += '?' + params.join('&');
        return this.request(url);
    }

    // Reports
    static getDailyReport(date) {
        return this.request(`/reports/daily?date=${date}`);
    }

    static getWeeklyReport(date) {
        return this.request(`/reports/weekly?date=${date}`);
    }

    static getMonthlyReport(year, month) {
        return this.request(`/reports/monthly?year=${year}&month=${month}`);
    }

    static getCategoryAnalysis(start, end) {
        return this.request(`/reports/category-analysis?start=${start}&end=${end}`);
    }

    static getIncomeVsExpense(start, end) {
        return this.request(`/reports/income-vs-expense?start=${start}&end=${end}`);
    }

    static getMonthlyComparison(months = 6) {
        return this.request(`/reports/monthly-comparison?months=${months}`);
    }
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR'
    }).format(amount);
}

function formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('en-IN');
}

function getCurrentMonth() {
    const now = new Date();
    return {
        year: now.getFullYear(),
        month: now.getMonth() + 1
    };
}

function showAlert(message, type = 'danger') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    const container = document.querySelector('.container-fluid') || document.body;
    container.insertBefore(alertDiv, container.firstChild);
    setTimeout(() => alertDiv.remove(), 5000);
}

function checkAuth() {
    if (!AuthService.isLoggedIn()) {
        window.location.href = 'index.html';
    }
}

function updateNavbar() {
    const navbar = document.querySelector('nav');
    if (AuthService.isLoggedIn()) {
        const username = AuthService.getUsername();
        navbar.innerHTML = `
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
                <div class="container-fluid">
                    <a class="navbar-brand" href="dashboard.html">💰 Expense Manager</a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav ms-auto">
                            <li class="nav-item"><a class="nav-link" href="dashboard.html">Dashboard</a></li>
                            <li class="nav-item"><a class="nav-link" href="income.html">Income</a></li>
                            <li class="nav-item"><a class="nav-link" href="expenses.html">Expenses</a></li>
                            <li class="nav-item"><a class="nav-link" href="budgets.html">Budgets</a></li>
                            <li class="nav-item"><a class="nav-link" href="savings.html">Savings</a></li>
                            <li class="nav-item"><a class="nav-link" href="reminders.html">Reminders</a></li>
                            <li class="nav-item"><a class="nav-link" href="reports.html">Reports</a></li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                    ${username}
                                </a>
                                <ul class="dropdown-menu" aria-labelledby="userDropdown">
                                    <li><a class="dropdown-item" href="#" onclick="AuthService.logout()">Logout</a></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
        `;
    }
}
