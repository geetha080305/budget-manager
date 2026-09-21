package com.expensemanager.model;

/**
 * Fixed category list per the requirements doc (Section 4.3).
 * Kept as an enum (not a DB-editable table) since the doc treats
 * categories as a closed set. If per-user custom categories are
 * ever needed, promote this to its own entity.
 */
public enum ExpenseCategory {
    FOOD,
    TRANSPORT,
    EDUCATION,
    SHOPPING,
    RENT,
    BILLS,
    ENTERTAINMENT,
    HEALTHCARE,
    OTHER
}
