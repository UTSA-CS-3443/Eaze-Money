package edu.utsa.cs3443.eazemoneyapp.model.budgets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * A spending limit for exactly one transaction category over a period.
 */
public class Budget {

    /** Periods a budget can cover. Stored in budgets.csv as these exact strings. */
    public static final String[] VALID_PERIODS = {"WEEKLY", "MONTHLY", "YEARLY"};

    private String budgetId;
    private String budgetName;
    private BigDecimal budgetAmount;
    private String budgetPeriod;
    private String category;

    public Budget(String budgetId, String budgetName, BigDecimal budgetAmount,
                  String budgetPeriod, String category) {
        this.budgetId = budgetId;
        this.budgetName = budgetName;
        this.budgetAmount = budgetAmount;
        this.budgetPeriod = normalize(budgetPeriod);
        this.category = normalize(category);
    }

    /**
     * Categories and periods are stored upper-case so that "food", "Food" and "FOOD"
     * are treated as the same category everywhere.
     */
    public static String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isValidPeriod(String period) {
        String p = normalize(period);
        for (String valid : VALID_PERIODS) {
            if (valid.equals(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the text a user typed for a budget name.
     * @return the trimmed name
     * @throws IllegalArgumentException with a message that can be shown to the user
     */
    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Budget name is required.");
        }
        String trimmed = name.trim();
        if (trimmed.length() > 50) {
            throw new IllegalArgumentException("Budget name must be 50 characters or fewer.");
        }
        return trimmed;
    }

    /**
     * Checks the text a user typed for a category.
     * @return the category in upper case (see normalize)
     * @throws IllegalArgumentException with a message that can be shown to the user
     */
    public static String validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (category.contains(",") || category.contains("\"")) {
            throw new IllegalArgumentException("Category cannot contain commas or quotes.");
        }
        return normalize(category);
    }

    /**
     * Turns the text a user typed (e.g. "250", "$1,200.5") into an amount with 2 decimals.
     * @throws IllegalArgumentException if it is not a number greater than 0
     */
    public static BigDecimal parseAmount(String amountText) {
        if (amountText == null || amountText.isBlank()) {
            throw new IllegalArgumentException("Amount is required.");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim().replace("$", "").replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a number, e.g. 250.00");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Checks a period chosen by the user.
     * @return the period in upper case
     * @throws IllegalArgumentException if it is not WEEKLY, MONTHLY or YEARLY
     */
    public static String validatePeriod(String period) {
        if (!isValidPeriod(period)) {
            throw new IllegalArgumentException("Please choose a period (Weekly, Monthly or Yearly).");
        }
        return normalize(period);
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setBudgetPeriod(String budgetPeriod) {
        this.budgetPeriod = normalize(budgetPeriod);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = normalize(category);
    }

    @Override
    public String toString() {
        return budgetId + " | " + budgetName + " | " + category + " | "
                + budgetPeriod + " | " + budgetAmount.toPlainString();
    }
}
