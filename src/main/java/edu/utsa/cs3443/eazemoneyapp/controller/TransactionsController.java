
package edu.utsa.cs3443.eazemoneyapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import edu.utsa.cs3443.eazemoneyapp.Session;
import edu.utsa.cs3443.eazemoneyapp.model.transactions.Transaction;
import edu.utsa.cs3443.eazemoneyapp.model.transactions.TransactionList;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionsController extends BaseController {

    @FXML
    private Button addTransactionButton;

    @FXML
    private TableView<Object> transactionsTable;

    @FXML
    private TableColumn<Object, String> dateColumn;

    @FXML
    private TableColumn<Object, String> transactionColumn;

    @FXML
    private TableColumn<Object, String> categoryColumn;

    @FXML
    private TableColumn<Object, String> walletColumn;

    @FXML
    private TableColumn<Object, String> typeColumn;

    @FXML
    private TableColumn<Object, String> amountColumn;

    private TransactionList transactionList;

    // Table data and button functionality will be added next.
    @FXML
    private void initialize() {
        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        ((Transaction) data.getValue()).getDate().toString()
                )
        );

        transactionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        ((Transaction) data.getValue()).getNote()
                )
        );

        categoryColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        ((Transaction) data.getValue()).getCategory()
                )
        );

        walletColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        ((Transaction) data.getValue()).getWalletId()
                )
        );

        amountColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        ((Transaction) data.getValue()).getAmount().toString()
                )
        );
        addTransactionButton.setOnAction(event -> showAddTransactionDialog());
    }
        // Refresh transactions when this page is displayed
@Override
public void onShow() {
    String userId = Session.getUserID();

    if (userId == null) {
        Dialogs.showError("No user is logged in.");
        return;
    }

    transactionList = new TransactionList(userId);

    try {
        transactionList.loadTransactions();
    } catch (IOException e) {
        Dialogs.showError("Failed to load transactions: " + e.getMessage());
    }
    transactionsTable.getItems().setAll(transactionList.getTransactions());
}
    private void showAddTransactionDialog() {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Add Transaction");
        dialog.setHeaderText("Enter transaction details");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField noteField = new TextField();
        TextField categoryField = new TextField();
        TextField walletField = new TextField();
        TextField amountField = new TextField();

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Date:"), datePicker);
        grid.addRow(1, new Label("Description:"), noteField);
        grid.addRow(2, new Label("Category:"), categoryField);
        grid.addRow(3, new Label("Wallet ID:"), walletField);
        grid.addRow(4, new Label("Amount:"), amountField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButton) {
                try {
                    return new Transaction(
                            UUID.randomUUID().toString(),
                            datePicker.getValue(),
                            noteField.getText(),
                            categoryField.getText(),
                            walletField.getText(),
                            new BigDecimal(amountField.getText())
                    );
                } catch (Exception e) {
                    Dialogs.showError("Invalid transaction details: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(transaction -> {
            if (transactionList != null && transactionList.addTransaction(transaction)) {
                try {
                    transactionList.saveTransactions();
                    transactionsTable.getItems().setAll(transactionList.getTransactions());
                    Dialogs.showInfo("Transaction saved successfully!");
                } catch (IOException e) {
                    Dialogs.showError("Failed to save transaction: " + e.getMessage());
                }
            }
        });
    }
}
