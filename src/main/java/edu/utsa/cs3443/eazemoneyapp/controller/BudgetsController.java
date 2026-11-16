package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.budgets.Budget;
import edu.utsa.cs3443.eazemoneyapp.model.budgets.BudgetList;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Budgets page (shown next to the sidebar).
 * Top: a table of the logged-in user's current budgets with how much has been spent.
 * Bottom: a form to add a new budget.
 * Validation and saving to data/&lt;userId&gt;/budgets.csv happen in BudgetList;
 * this controller only reads the form and displays the data.
 */
public class BudgetsController extends BaseController {

    @FXML private TableView<Budget> budgetsTable;
    @FXML private TableColumn<Budget, String> nameColumn;
    @FXML private TableColumn<Budget, String> categoryColumn;
    @FXML private TableColumn<Budget, String> periodColumn;
    @FXML private TableColumn<Budget, String> amountColumn;
    @FXML private TableColumn<Budget, String> spentColumn;
    @FXML private TableColumn<Budget, String> remainingColumn;
    @FXML private TableColumn<Budget, String> statusColumn;

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> periodBox;

    private BudgetList budgetList;
    /** Spent per budget id, calculated once each time the table is refreshed. */
    private final Map<String, BigDecimal> spentById = new HashMap<>();

    @FXML
    private void initialize() {
        periodBox.getItems().setAll(Budget.VALID_PERIODS);
        periodBox.getSelectionModel().select("MONTHLY");

        nameColumn.setCellValueFactory(c -> text(c.getValue().getBudgetName()));
        categoryColumn.setCellValueFactory(c -> text(c.getValue().getCategory()));
        periodColumn.setCellValueFactory(c -> text(c.getValue().getBudgetPeriod()));
        amountColumn.setCellValueFactory(c -> text(money(c.getValue().getBudgetAmount())));
        spentColumn.setCellValueFactory(c -> text(money(spent(c.getValue()))));
        remainingColumn.setCellValueFactory(c ->
                text(money(c.getValue().getBudgetAmount().subtract(spent(c.getValue())))));
        statusColumn.setCellValueFactory(c ->
                text(spent(c.getValue()).compareTo(c.getValue().getBudgetAmount()) > 0
                        ? "Over budget" : "On track"));
    }

    @Override
    public void onShow() {
        if (getAccount() == null) {
            Navigator.showPage(Page.STARTING);
            return;
        }
        budgetList = new BudgetList(getAccount().getUserID());
        try {
            budgetList.load();
        } catch (IOException | NumberFormatException e) {
            showError("Could not load your budgets: " + e.getMessage());
        }
        refreshTable();
        clearForm();
    }

    @FXML
    private void onSave() {
        try {
            Budget budget = budgetList.addBudget(
                    nameField.getText(),
                    categoryField.getText(),
                    amountField.getText(),
                    periodBox.getValue());
            refreshTable();
            clearForm();
            Dialogs.showInfo("Budget \"" + budget.getBudgetName() + "\" saved for "
                    + budget.getCategory() + ".");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            showError("Could not save your budget: " + e.getMessage());
        }
    }

    /** Recalculates spending and reloads the table from the BudgetList. */
    private void refreshTable() {
        spentById.clear();
        try {
            for (Budget b : budgetList.getBudgets()) {
                spentById.put(b.getBudgetId(), budgetList.getSpent(b));
            }
        } catch (IOException | NumberFormatException e) {
            showError("Could not read your transactions: " + e.getMessage());
        }
        budgetsTable.getItems().setAll(budgetList.getBudgets());
    }

    private BigDecimal spent(Budget b) {
        return spentById.getOrDefault(b.getBudgetId(), BigDecimal.ZERO);
    }

    private static SimpleStringProperty text(String value) {
        return new SimpleStringProperty(value);
    }

    private static String money(BigDecimal amount) {
        return (amount.signum() < 0 ? "-$" : "$") + amount.abs().setScale(2).toPlainString();
    }

    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        amountField.clear();
        periodBox.getSelectionModel().select("MONTHLY");
    }
}
