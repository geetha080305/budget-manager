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

    static clearUsername() {
        localStorage.removeItem('username');
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

        if (response.status === 401 && !endpoint.startsWith('/auth/')) {
            AuthService.logout();
            return;
        }

        if (response.status === 204 || response.status === 205) {
            return null;
        }

        const text = await response.text();
        let data = null;
        if (text) {
            try {
                data = JSON.parse(text);
            } catch (e) {
                // If the response is HTML (e.g. Cloudflare error, 502 Bad Gateway), extract title or provide clean message
                if (text.includes('<html') || text.includes('Error 1033') || text.includes('Cloudflare')) {
                    data = { error: `Server unreachable (HTTP ${response.status}). Please check if the backend is running.` };
                } else {
                    data = { error: text };
                }
            }
        }

        if (!response.ok) {
            throw new Error(data?.details?.[0] || data?.error || `Request failed with status ${response.status}`);
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

    // Household endpoints
    static getHousehold() {
        return this.request('/household');
    }

    static createHousehold(name) {
        return this.request('/household', {
            method: 'POST',
            body: JSON.stringify({ name })
        });
    }

    static joinHousehold(inviteCode) {
        return this.request('/household/join', {
            method: 'POST',
            body: JSON.stringify({ inviteCode })
        });
    }

    static leaveHousehold() {
        return this.request('/household/leave', {
            method: 'POST'
        });
    }
}

// Utility functions
function formatCurrency(amount) {
    const val = Number(amount);
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR'
    }).format(isNaN(val) ? 0 : val);
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

async function updateNavbar() {
    const navbar = document.querySelector('nav');
    if (AuthService.isLoggedIn()) {
        const username = AuthService.getUsername();
        let householdBadge = '';
        try {
            const hh = await ApiClient.getHousehold();
            if (hh && hh.name) {
                householdBadge = `<span class="badge bg-success ms-2" title="Household: ${hh.name} (Code: ${hh.inviteCode})">🏡 ${hh.name}</span>`;
            }
        } catch (e) {
            // Ignore if household fetch fails
        }

        navbar.innerHTML = `
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
                <div class="container-fluid">
                    <a class="navbar-brand d-flex align-items-center" href="dashboard.html">
                        <span>💰 Expense Manager</span>
                        ${householdBadge}
                    </a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav ms-auto align-items-center">
                            <li class="nav-item"><a class="nav-link" href="dashboard.html">Dashboard</a></li>
                            <li class="nav-item"><a class="nav-link" href="income.html">Income</a></li>
                            <li class="nav-item"><a class="nav-link" href="expenses.html">Expenses</a></li>
                            <li class="nav-item"><a class="nav-link" href="budgets.html">Budgets</a></li>
                            <li class="nav-item"><a class="nav-link" href="savings.html">Savings</a></li>
                            <li class="nav-item"><a class="nav-link" href="reminders.html">Reminders</a></li>
                            <li class="nav-item"><a class="nav-link" href="reports.html">Reports</a></li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                    👤 ${username}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                    <li><a class="dropdown-item" href="#" onclick="openHouseholdModal()">🏡 Family / Household Settings</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item text-danger" href="#" onclick="AuthService.logout()">Logout</a></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <!-- Household Modal -->
            <div id="householdModal" class="modal fade" tabindex="-1" style="display: none;">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">🏡 Family / Household Account</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" onclick="closeHouseholdModal()"></button>
                        </div>
                        <div class="modal-body" id="householdModalBody">
                            <p class="text-muted">Loading household details...</p>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
}

async function openHouseholdModal() {
    let modalEl = document.getElementById('householdModal');
    if (!modalEl) return;
    const body = document.getElementById('householdModalBody');
    body.innerHTML = `<div class="text-center py-3"><div class="spinner-border text-primary" role="status"></div></div>`;
    modalEl.style.display = 'block';
    modalEl.classList.add('show');
    document.body.classList.add('modal-open');

    try {
        const hh = await ApiClient.getHousehold();
        if (hh && hh.name) {
            const memberList = hh.members.map(m => `
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${m.fullName || m.username}</strong>
                        <div class="small text-muted">@${m.username}</div>
                    </div>
                    <span class="badge bg-primary rounded-pill">Member</span>
                </li>
            `).join('');

            body.innerHTML = `
                <div class="mb-3">
                    <label class="form-label text-muted small">HOUSEHOLD NAME</label>
                    <h4 class="text-primary">${hh.name}</h4>
                </div>
                <div class="alert alert-info">
                    <strong>Invite Code:</strong> <code style="font-size: 1.2rem; font-weight: bold; letter-spacing: 2px;">${hh.inviteCode}</code>
                    <button class="btn btn-sm btn-outline-secondary ms-2" onclick="navigator.clipboard.writeText('${hh.inviteCode}'); alert('Invite code copied!');">Copy</button>
                    <div class="small mt-1 text-muted">Share this code with your spouse or family members so they can join this household!</div>
                </div>
                <h6>Members (${hh.members.length})</h6>
                <ul class="list-group mb-3">
                    ${memberList}
                </ul>
                <button class="btn btn-outline-danger btn-sm w-100" onclick="handleLeaveHousehold()">Leave Household</button>
            `;
        } else {
            body.innerHTML = `
                <p class="text-muted">You are currently using an individual account. You can create a Family Household or join an existing one using an invite code.</p>
                
                <div class="card mb-3">
                    <div class="card-body">
                        <h6>Create Family / Household</h6>
                        <div class="input-group mb-2">
                            <input type="text" id="createHhName" class="form-control" placeholder="e.g. My Family">
                            <button class="btn btn-success" onclick="handleCreateHousehold()">Create</button>
                        </div>
                        <small class="text-muted">Creates a shared workspace for your family.</small>
                    </div>
                </div>

                <div class="card">
                    <div class="card-body">
                        <h6>Join Existing Household</h6>
                        <div class="input-group mb-2">
                            <input type="text" id="joinHhCode" class="form-control" placeholder="Enter 8-digit invite code">
                            <button class="btn btn-primary" onclick="handleJoinHousehold()">Join</button>
                        </div>
                        <small class="text-muted">Ask your family member for their household invite code.</small>
                    </div>
                </div>
            `;
        }
    } catch (err) {
        body.innerHTML = `<div class="alert alert-danger">Error loading household: ${err.message}</div>`;
    }
}

function closeHouseholdModal() {
    const modalEl = document.getElementById('householdModal');
    if (modalEl) {
        modalEl.style.display = 'none';
        modalEl.classList.remove('show');
        document.body.classList.remove('modal-open');
    }
}

async function handleCreateHousehold() {
    const name = document.getElementById('createHhName')?.value?.trim();
    if (!name) {
        alert('Please enter a household name');
        return;
    }
    try {
        await ApiClient.createHousehold(name);
        alert('Household created successfully!');
        window.location.reload();
    } catch (err) {
        alert('Failed to create household: ' + err.message);
    }
}

async function handleJoinHousehold() {
    const code = document.getElementById('joinHhCode')?.value?.trim();
    if (!code) {
        alert('Please enter an invite code');
        return;
    }
    try {
        await ApiClient.joinHousehold(code);
        alert('Joined household successfully!');
        window.location.reload();
    } catch (err) {
        alert('Failed to join household: ' + err.message);
    }
}

async function handleLeaveHousehold() {
    if (!confirm('Are you sure you want to leave this household? You will return to an individual dashboard.')) {
        return;
    }
    try {
        await ApiClient.leaveHousehold();
        alert('Left household.');
        window.location.reload();
    } catch (err) {
        alert('Failed to leave household: ' + err.message);
    }
}
