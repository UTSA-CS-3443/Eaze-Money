package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.budgets.Budget;
import edu.utsa.cs3443.eazemoneyapp.model.budgets.BudgetList;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Budgets page (shown next to the sidebar). Demonstrates all four CRUD operations on the
 * logged-in user's budgets:
 *   Create - "+ Add Budget" opens a pop-up form
 *   Read   - the table lists every budget with what has been spent this period
 *   Update - "Edit" (or double-clicking a row) opens the same form filled in
 *   Delete - "Delete" asks for confirmation, then removes the budget
 * Validation and saving to data/&lt;userId&gt;/budgets.csv happen in BudgetList;
 * this controller only shows the data and the pop-ups.
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

    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private BudgetList budgetList;
    /** Spent per budget id, calculated once each time the table is refreshed. */
    private final Map<String, BigDecimal> spentById = new HashMap<>();

    @FXML
    private void initialize() {
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

        // Edit and Delete only make sense once a budget is selected in the table.
        editButton.disableProperty().bind(budgetsTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(budgetsTable.getSelectionModel().selectedItemProperty().isNull());

        // Double-clicking a row is a shortcut for Edit.
        budgetsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && budgetsTable.getSelectionModel().getSelectedItem() != null) {
                onEdit();
            }
        });
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
        refreshTable(null);
    }

    // ------------------------------------------------------------------ CRUD buttons

    /** Create: opens an empty budget form. */
    @FXML
    private void onAdd() {
        Budget added = showBudgetDialog(null);
        if (added != null) {
            refreshTable(added.getBudgetId());
        }
    }

    /** Update: opens the form filled in with the selected budget. */
    @FXML
    private void onEdit() {
        Budget selected = budgetsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a budget in the table first.");
            return;
        }
        Budget updated = showBudgetDialog(selected);
        if (updated != null) {
            refreshTable(updated.getBudgetId());
        }
    }

    /** Delete: asks for confirmation, then removes the selected budget. */
    @FXML
    private void onDelete() {
        Budget selected = budgetsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a budget in the table first.");
            return;
        }
        boolean yes = confirm("Delete this budget?\n\n"
                + selected.getBudgetName() + "\n"
                + selected.getCategory() + " - " + money(selected.getBudgetAmount())
                + " " + selected.getBudgetPeriod().toLowerCase() + "\n\n"
                + "This cannot be undone.");
        if (!yes) {
            return;
        }
        try {
            budgetList.deleteBudget(selected.getBudgetId());
            refreshTable(null);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            refreshTable(null);
        } catch (IOException e) {
            showError("Could not delete the budget: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ pop-up form

    /**
     * Shows the budget form as a pop-up. With {@code existing == null} it adds a new budget,
     * otherwise it edits that budget. The pop-up stays open until the input is valid and
     * saved, or the user presses Cancel.
     *
     * @return the saved budget, or null if the user cancelled
     */
    private Budget showBudgetDialog(Budget existing) {
        boolean editing = existing != null;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(Navigator.getStage());
        dialog.setTitle(editing ? "Edit Budget" : "Add Budget");
        dialog.setHeaderText(editing ? "Change the details of " + existing.getBudgetName()
                                     : "Enter the details of your new budget");
        // Use the app's stylesheet so the pop-up matches the rest of the app.
        dialog.getDialogPane().getStylesheets().addAll(Navigator.getStage().getScene().getStylesheets());

        TextField nameField = new TextField(editing ? existing.getBudgetName() : "");
        nameField.setPromptText("e.g. Groceries Budget");
        TextField categoryField = new TextField(editing ? existing.getCategory() : "");
        categoryField.setPromptText("e.g. GROCERIES");
        TextField amountField = new TextField(editing ? existing.getBudgetAmount().toPlainString() : "");
        amountField.setPromptText("e.g. 250.00");
        ComboBox<String> periodBox = new ComboBox<>();
        periodBox.getItems().setAll(Budget.VALID_PERIODS);
        periodBox.getSelectionModel().select(editing ? existing.getBudgetPeriod() : "MONTHLY");
        periodBox.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Budget name:"), nameField);
        grid.addRow(1, new Label("Category:"), categoryField);
        grid.addRow(2, new Label("Amount:"), amountField);
        grid.addRow(3, new Label("Period:"), periodBox);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveType = new ButtonType(editing ? "Save Changes" : "Add Budget",
                ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        // Save when the button is pressed; if something is wrong, show the error and keep
        // the pop-up open so the user can fix it without retyping everything.
        Budget[] saved = new Budget[1];
        Node saveButton = dialog.getDialogPane().lookupButton(saveType);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                if (editing) {
                    saved[0] = budgetList.updateBudget(existing.getBudgetId(), nameField.getText(),
                            categoryField.getText(), amountField.getText(), periodBox.getValue());
                } else {
                    saved[0] = budgetList.addBudget(nameField.getText(), categoryField.getText(),
                            amountField.getText(), periodBox.getValue());
                }
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
                event.consume();
            } catch (IOException e) {
                showError("Could not save the budget: " + e.getMessage());
                event.consume();
            }
        });

        nameField.requestFocus();
        dialog.showAndWait();
        return saved[0];
    }

    // ------------------------------------------------------------------ table helpers

    /**
     * Recalculates spending, reloads the table and selects the budget with
     * {@code selectId} (or nothing if it is null).
     */
    private void refreshTable(String selectId) {
        spentById.clear();
        try {
            for (Budget b : budgetList.getBudgets()) {
                spentById.put(b.getBudgetId(), budgetList.getSpent(b));
            }
        } catch (IOException | NumberFormatException e) {
            showError("Could not read your transactions: " + e.getMessage());
        }
        budgetsTable.getItems().setAll(budgetList.getBudgets());

        budgetsTable.getSelectionModel().clearSelection();
        if (selectId != null) {
            for (Budget b : budgetsTable.getItems()) {
                if (b.getBudgetId().equals(selectId)) {
                    budgetsTable.getSelectionModel().select(b);
                    budgetsTable.scrollTo(b);
                    break;
                }
            }
        }
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
}
