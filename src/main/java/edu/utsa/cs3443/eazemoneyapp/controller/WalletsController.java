package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.wallet.Wallet;
import edu.utsa.cs3443.eazemoneyapp.model.wallet.WalletList;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

public class WalletsController extends BaseController {
    private static final double WALLET_ROW_HEIGHT = 40;
    private static final double TABLE_HEADER_HEIGHT = 55;
    private static final double EMPTY_TABLE_HEIGHT = 100;

    @FXML private TableView<Wallet> walletsTable;
    @FXML private TableColumn<Wallet, String> idColumn;
    @FXML private TableColumn<Wallet, String> nameColumn;
    @FXML private TableColumn<Wallet, String> startingBalanceColumn;
    @FXML private TableColumn<Wallet, String> currentBalanceColumn;

    private Map<String, BigDecimal> balances = Map.of();

    @FXML
    private void initialize() {
        walletsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        walletsTable.setFixedCellSize(WALLET_ROW_HEIGHT);
        walletsTable.prefHeightProperty().bind(Bindings.when(Bindings.isEmpty(walletsTable.getItems()))
                .then(EMPTY_TABLE_HEIGHT)
                .otherwise(Bindings.size(walletsTable.getItems())
                        .multiply(WALLET_ROW_HEIGHT).add(TABLE_HEADER_HEIGHT)));
        walletsTable.maxHeightProperty().bind(walletsTable.prefHeightProperty());
        idColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getWalletId()));
        nameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getWalletName()));
        startingBalanceColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(formatBalance(
                        BigDecimal.valueOf(cell.getValue().getStartingBalance()))));
        currentBalanceColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(formatBalance(
                        balances.getOrDefault(cell.getValue().getWalletId(),
                                BigDecimal.valueOf(cell.getValue().getStartingBalance())))));
    }

    @Override
    public void onShow() {
        if (getAccount() == null) {
            showError("Please sign in to view your wallets.");
            walletsTable.getItems().clear();
            return;
        }

        try {
            WalletList walletList = getAccount().getWalletList();
            balances = walletList.loadWalletBalances(getAccount().getUserID());
            walletsTable.getItems().setAll(walletList.getWallets());
            walletsTable.refresh();
        } catch (IOException e) {
            walletsTable.getItems().clear();
            showError("Could not load wallets: " + e.getMessage());
        }
    }

    @FXML
    private void onAddWallet() {
        showWalletEditor(null, null).ifPresent(details -> {
            WalletList walletList = getAccount().getWalletList();
            try {
                walletList.addWallet(new Wallet(walletList.nextWalletId(), details.name(),
                        details.startingBalance()));
                refreshWallets();
                Dialogs.showInfo("Wallet added successfully.");
            } catch (IOException | IllegalArgumentException e) {
                showError("Could not add wallet: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onEditWallet() {
        Wallet selected = walletsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a wallet to edit.");
            return;
        }

        showWalletEditor(selected.getWalletName(),
                Double.toString(selected.getStartingBalance())).ifPresent(details -> {
            WalletList walletList = getAccount().getWalletList();
            Wallet updated = new Wallet(selected.getWalletId(), details.name(), details.startingBalance());
            try {
                if (walletList.updateWallet(updated)) {
                    refreshWallets();
                    Dialogs.showInfo("Wallet updated successfully.");
                } else {
                    showError("Could not find the selected wallet.");
                }
            } catch (IOException | IllegalArgumentException e) {
                showError("Could not update wallet: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onDeleteWallet() {
        Wallet selected = walletsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a wallet to delete.");
            return;
        }
        if (!Dialogs.confirm("Are you sure you want to delete wallet \""
                + selected.getWalletName() + "\"?")) {
            return;
        }

        WalletList walletList = getAccount().getWalletList();
        try {
            if (walletList.hasTransactionsForWallet(selected.getWalletId())) {
                showError("Delete this wallet's transactions before deleting the wallet.");
                return;
            }
            if (walletList.deleteWallet(selected.getWalletId())) {
                refreshWallets();
                Dialogs.showInfo("Wallet deleted successfully.");
            } else {
                showError("Could not find the selected wallet.");
            }
        } catch (IOException e) {
            showError("Could not delete wallet: " + e.getMessage());
        }
    }

    private void refreshWallets() {
        onShow();
    }

    private static String formatBalance(BigDecimal balance) {
        return "$" + balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /** Opens the wallet add/edit form. */
    public static Optional<WalletDetails> showWalletEditor(String walletName, String startingBalance) {
        Dialog<WalletDetails> dialog = new Dialog<>();
        dialog.setTitle(walletName == null ? "Add Wallet" : "Edit Wallet");
        dialog.setHeaderText(walletName == null ? "Enter wallet details" : "Update wallet details");
        dialog.initOwner(Navigator.getStage());

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(saveButton, cancelButton);

        TextField nameField = new TextField(walletName == null ? "" : walletName);
        nameField.setPromptText("Wallet name");
        TextField balanceField = new TextField(startingBalance == null ? "" : startingBalance);
        balanceField.setPromptText("Starting balance");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(10, 0, 0, 0));
        form.addRow(0, new Label("Wallet name:"), nameField);
        form.addRow(1, new Label("Starting balance:"), balanceField);
        dialog.getDialogPane().setContent(form);

        dialog.getDialogPane().lookupButton(saveButton).addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) {
                        Dialogs.showError("Please enter a wallet name.");
                        event.consume();
                        return;
                    }
                    try {
                        double balance = new BigDecimal(balanceField.getText().trim().replace(",", "")).doubleValue();
                        if (!Double.isFinite(balance)) {
                            throw new NumberFormatException("Balance is out of range.");
                        }
                    } catch (NumberFormatException e) {
                        Dialogs.showError("Please enter a valid starting balance.");
                        event.consume();
                    }
                });

        dialog.setResultConverter(button -> {
            if (button != saveButton) {
                return null;
            }
            double balance = new BigDecimal(balanceField.getText().trim().replace(",", "")).doubleValue();
            return new WalletDetails(nameField.getText().trim(), balance);
        });
        return dialog.showAndWait();
    }

    public record WalletDetails(String name, double startingBalance) { }
}
