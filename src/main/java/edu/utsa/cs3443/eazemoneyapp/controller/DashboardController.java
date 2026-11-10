package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.dashboard.Dashboard;
import edu.utsa.cs3443.eazemoneyapp.model.login.Account;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DashboardController extends BaseController {
    @FXML private Label welcomeLabel;
    @FXML private Label currentBalanceLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;

    @Override
    public void onShow() {
        Account account = getAccount();
        if (account == null) {
            showError("Please sign in to view your dashboard.");
            return;
        }

        welcomeLabel.setText("Welcome, " + account.getDisplayName() + "!");
        currentBalanceLabel.setText("Unavailable");
        totalIncomeLabel.setText("Unavailable");
        totalExpenseLabel.setText("Unavailable");
        Dashboard dashboard = new Dashboard(account.getUserID());

        try {
            BigDecimal currentBalance = dashboard.getCurrentBalance();
            BigDecimal totalIncome = dashboard.getTotalIncome();
            BigDecimal totalExpense = dashboard.getTotalExpense();

            currentBalanceLabel.setText(formatAmount(currentBalance));
            totalIncomeLabel.setText(formatAmount(totalIncome));
            totalExpenseLabel.setText(formatAmount(totalExpense));
        } catch (IOException e) {
            showError("Could not load dashboard totals: " + e.getMessage());
        }
    }

    private String formatAmount(BigDecimal amount) {
        return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
