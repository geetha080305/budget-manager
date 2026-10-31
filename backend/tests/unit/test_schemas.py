import pytest
from decimal import Decimal
from datetime import date
from pydantic import ValidationError
from app.modules.expenses.schemas import ExpenseRequest
from app.modules.expenses.models import ExpenseCategory, PaymentMethod
from app.modules.budgets.schemas import BudgetRequest

def test_expense_request_valid():
    expense = ExpenseRequest(
        amount=Decimal("45.50"),
        category=ExpenseCategory.FOOD,
        description="Grocery run",
        date=date(2026, 10, 31),
        paymentMethod=PaymentMethod.UPI
    )
    assert expense.amount == Decimal("45.50")
    assert expense.category == ExpenseCategory.FOOD
    assert expense.paymentMethod == PaymentMethod.UPI

def test_expense_request_negative_amount_fails():
    with pytest.raises(ValidationError):
        ExpenseRequest(
            amount=Decimal("-10.00"),
            category=ExpenseCategory.FOOD,
            date=date(2026, 10, 31),
            paymentMethod=PaymentMethod.CASH
        )

def test_budget_request_valid():
    budget = BudgetRequest(
        category=ExpenseCategory.ENTERTAINMENT,
        amount=Decimal("300.00"),
        budgetMonth=11,
        budgetYear=2026
    )
    assert budget.amount == Decimal("300.00")
    assert budget.budgetMonth == 11
    assert budget.budgetYear == 2026

def test_budget_invalid_month_fails():
    with pytest.raises(ValidationError):
        BudgetRequest(
            category=ExpenseCategory.ENTERTAINMENT,
            amount=Decimal("300.00"),
            budgetMonth=13,
            budgetYear=2026
        )
