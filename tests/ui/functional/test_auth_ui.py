import time
import pytest
from playwright.sync_api import Page, expect

UI_BASE_URL = "http://localhost:5500"

def test_ui_login_page_renders(page: Page):
    """
    Test 1: Verify UI loads properly, displaying login form and branding.
    """
    page.goto(f"{UI_BASE_URL}/index.html")
    expect(page).to_have_title("Login - Expense Manager")
    expect(page.locator("#formTitle")).to_have_text("Login")
    expect(page.locator("#loginUsername")).to_be_visible()
    expect(page.locator("#loginPassword")).to_be_visible()
    expect(page.locator("button:has-text('Login')")).to_be_visible()

def test_ui_registration_e2e_flow(page: Page):
    """
    Test 2: Complete User Journey: Registration redirects directly to dashboard.html.
    """
    unique_suffix = int(time.time() * 1000)
    username = f"ui_reg_{unique_suffix}"
    email = f"ui_{unique_suffix}@example.com"
    password = "StrongPassword@123"
    full_name = "UI Registration Tester"

    # Open Login Page
    page.goto(f"{UI_BASE_URL}/index.html")

    # Toggle to Registration Form
    page.click("text=Sign up")
    expect(page.locator("#registerForm")).to_be_visible()

    # Fill and submit registration
    page.fill("#regUsername", username)
    page.fill("#regEmail", email)
    page.fill("#regFullName", full_name)
    page.fill("#regPassword", password)
    page.click("button:has-text('Sign Up')")

    # Automatic redirect to dashboard upon successful registration
    page.wait_for_url("**/dashboard.html", timeout=10000)
    expect(page).to_have_url(f"{UI_BASE_URL}/dashboard.html")

def test_ui_login_existing_user_e2e_flow(page: Page):
    """
    Test 3: Existing user login flow redirects to dashboard.html.
    """
    unique_suffix = int(time.time() * 1000)
    username = f"ui_log_{unique_suffix}"
    email = f"uilog_{unique_suffix}@example.com"
    password = "StrongPassword@123"
    full_name = "UI Login Tester"

    # First register user
    page.goto(f"{UI_BASE_URL}/index.html")
    page.click("text=Sign up")
    page.fill("#regUsername", username)
    page.fill("#regEmail", email)
    page.fill("#regFullName", full_name)
    page.fill("#regPassword", password)
    page.click("button:has-text('Sign Up')")
    page.wait_for_url("**/dashboard.html", timeout=10000)

    # Clear auth storage and return to login page
    page.evaluate("() => localStorage.clear()")
    page.goto(f"{UI_BASE_URL}/index.html")

    # Perform Login
    expect(page.locator("#loginForm")).to_be_visible()
    page.fill("#loginUsername", username)
    page.fill("#loginPassword", password)
    page.click("button:has-text('Login')")

    # Verify redirect
    page.wait_for_url("**/dashboard.html", timeout=10000)
    expect(page).to_have_url(f"{UI_BASE_URL}/dashboard.html")

