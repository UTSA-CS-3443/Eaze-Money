package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.budgets.Budget;
import edu.utsa.cs3443.eazemoneyapp.model.budgets.BudgetList;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Budgets page (full window): a form for adding a budget for the logged-in user.
 * Validation and saving to data/&lt;userId&gt;/budgets.csv happen in BudgetList.addBudget();
 * this controller only reads the form and shows the result.
 */
public class BudgetsController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> periodBox;

    private BudgetList budgetList;

    @FXML
    private void initialize() {
        periodBox.getItems().setAll(Budget.VALID_PERIODS);
        periodBox.getSelectionModel().select("MONTHLY");
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
            Dialogs.showInfo("Budget \"" + budget.getBudgetName() + "\" saved for "
                    + budget.getCategory() + ".");
            clearForm();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            showError("Could not save your budget: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Navigator.showPage(Page.DASHBOARD);
    }

    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        amountField.clear();
        periodBox.getSelectionModel().select("MONTHLY");
    }
}
