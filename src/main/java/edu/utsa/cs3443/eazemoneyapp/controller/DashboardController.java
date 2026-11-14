package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.dashboard.Dashboard;
import edu.utsa.cs3443.eazemoneyapp.model.login.Account;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardController extends BaseController {
    @FXML private Label welcomeLabel;
    @FXML private Label currentBalanceLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private StackedBarChart<String, Number> incomeExpenseChart;
    @FXML private CategoryAxis monthAxis;
    @FXML private NumberAxis amountAxis;

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

            // Top row
            currentBalanceLabel.setText(formatAmount(currentBalance));
            totalIncomeLabel.setText(formatAmount(totalIncome));
            totalExpenseLabel.setText(formatAmount(totalExpense));

            // Middle row
            YearMonth lastMonth = YearMonth.now();
            Map<YearMonth, Dashboard.MonthlyIncomeExpense> monthlyTotals =
                    dashboard.getMonthlyIncomeExpense(lastMonth.minusMonths(11), lastMonth);
            populateIncomeExpenseChart(monthlyTotals);

            // Bottom row

        } catch (IOException e) {
            showError("Could not load dashboard data: " + e.getMessage());
        }
    }

    private void populateIncomeExpenseChart(
            Map<YearMonth, Dashboard.MonthlyIncomeExpense> monthlyTotals) {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yy", Locale.US);

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        double highestMonthlyTotal = 0;
        for (Map.Entry<YearMonth, Dashboard.MonthlyIncomeExpense> entry : monthlyTotals.entrySet()) {
            String monthLabel = entry.getKey().format(monthFormatter);
            BigDecimal income = entry.getValue().income();
            BigDecimal expense = entry.getValue().expense().abs();

            incomeSeries.getData().add(new XYChart.Data<>(monthLabel, income.doubleValue()));
            expenseSeries.getData().add(new XYChart.Data<>(monthLabel, expense.doubleValue()));

            highestMonthlyTotal = Math.max(
                    highestMonthlyTotal, income.add(expense).doubleValue());
        }

        monthAxis.setCategories(FXCollections.observableArrayList(
                monthlyTotals.keySet().stream().map(month -> month.format(monthFormatter)).toList()));

        double padding = highestMonthlyTotal * 0.1;
        if (padding == 0) {
            padding = 1;
        }

        amountAxis.setAutoRanging(false);
        amountAxis.setLowerBound(0);
        amountAxis.setUpperBound(highestMonthlyTotal + padding);
        amountAxis.setTickUnit((amountAxis.getUpperBound() - amountAxis.getLowerBound()) / 6);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        amountAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number value) {
                return currencyFormat.format(value);
            }

            @Override
            public Number fromString(String value) {
                return currencyFormat.parse(value, new java.text.ParsePosition(0)).doubleValue();
            }
        });

        incomeExpenseChart.getData().setAll(List.of(incomeSeries, expenseSeries));
    }

    private String formatAmount(BigDecimal amount) {
        return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
