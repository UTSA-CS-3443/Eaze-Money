package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.wallet.Wallet;
import edu.utsa.cs3443.eazemoneyapp.model.wallet.WalletList;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class WalletsController extends BaseController {
    private static final int MAX_WALLETS_PER_ROW = 4;
    private static final double MIN_WALLET_CARD_WIDTH = 230;
    private static final double WALLET_CARD_GAP = 18;

    @FXML private GridPane walletsGrid;
    @FXML private Label walletCountLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label emptyWalletsLabel;

    private Map<String, BigDecimal> balances = Map.of();
    private List<Wallet> wallets = List.of();
    private int walletGridColumnCount;

    @FXML
    private void initialize() {
        walletsGrid.setMaxWidth(Double.MAX_VALUE);
        walletsGrid.widthProperty().addListener((observable, oldWidth, newWidth) -> updateWalletGrid());
        updateWalletGrid();
    }

    @Override
    public void onShow() {
        if (getAccount() == null) {
            showError("Please sign in to view your wallets.");
            balances = Map.of();
            wallets = List.of();
            updateSummary();
            renderWalletCards();
            return;
        }

        try {
            WalletList walletList = getAccount().getWalletList();
            balances = walletList.loadWalletBalances(getAccount().getUserID());
            wallets = new ArrayList<>(walletList.getWallets());
            updateSummary();
            updateWalletGrid();
            renderWalletCards();
        } catch (IOException e) {
            balances = Map.of();
            wallets = List.of();
            updateSummary();
            updateWalletGrid();
            renderWalletCards();
            showError("Could not load wallets: " + e.getMessage());
        }
    }

    @FXML
    private void onAddWallet() {
        if (getAccount() == null) {
            showError("Please sign in to add a wallet.");
            return;
        }
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

    private void onEditWallet(Wallet selected) {
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

    private void onDeleteWallet(Wallet selected) {
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

    private void updateSummary() {
        int walletCount = wallets.size();
        walletCountLabel.setText(walletCount + (walletCount == 1 ? " wallet" : " wallets"));

        BigDecimal total = BigDecimal.ZERO;
        for (Wallet wallet : wallets) {
            total = total.add(balances.getOrDefault(wallet.getWalletId(),
                    BigDecimal.valueOf(wallet.getStartingBalance())));
        }
        totalBalanceLabel.setText(formatBalance(total));
    }

    private void updateWalletGrid() {
        double width = walletsGrid.getWidth();
        int columns = width <= 0 ? 1 : (int) ((width + WALLET_CARD_GAP)
                / (MIN_WALLET_CARD_WIDTH + WALLET_CARD_GAP));
        columns = Math.max(1, Math.min(MAX_WALLETS_PER_ROW, columns));
        if (wallets.isEmpty()) {
            columns = 1;
        } else {
            columns = Math.min(columns, wallets.size());
        }
        if (columns == walletGridColumnCount) {
            return;
        }

        walletGridColumnCount = columns;
        walletsGrid.getColumnConstraints().clear();
        for (int i = 0; i < columns; i++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(100.0 / columns);
            constraints.setHgrow(Priority.ALWAYS);
            walletsGrid.getColumnConstraints().add(constraints);
        }
        renderWalletCards();
    }

    private void renderWalletCards() {
        walletsGrid.getChildren().clear();
        emptyWalletsLabel.setVisible(wallets.isEmpty());
        emptyWalletsLabel.setManaged(wallets.isEmpty());
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet = wallets.get(i);
            walletsGrid.add(createWalletCard(wallet), i % walletGridColumnCount,
                    i / walletGridColumnCount);
        }
    }

    private VBox createWalletCard(Wallet wallet) {
        BigDecimal startingBalance = BigDecimal.valueOf(wallet.getStartingBalance());
        BigDecimal currentBalance = balances.getOrDefault(wallet.getWalletId(), startingBalance);
        BigDecimal change = currentBalance.subtract(startingBalance);

        Label name = new Label(wallet.getWalletName());
        name.getStyleClass().add("wallet-card-name");
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(event -> onEditWallet(wallet));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> onDeleteWallet(wallet));

        MenuButton menu = new MenuButton("⋮", null, editItem, deleteItem);
        menu.getStyleClass().add("wallet-card-menu");

        HBox heading = new HBox(name, menu);
        heading.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label currentTitle = new Label("Current Balance");
        currentTitle.getStyleClass().add("wallet-card-section-label");
        Label currentAmount = new Label(formatBalance(currentBalance));
        currentAmount.getStyleClass().add("wallet-card-current-balance");

        Region divider = new Region();
        divider.setMinHeight(1);
        divider.setPrefHeight(1);
        divider.setMaxHeight(1);
        divider.getStyleClass().add("wallet-card-divider");

        Label startingTitle = new Label("Starting Balance");
        startingTitle.getStyleClass().add("wallet-card-section-label");

        Label startingAmount = new Label(formatBalance(startingBalance));
        startingAmount.getStyleClass().add("wallet-card-starting-balance");

        Label changeAmount = new Label(formatChange(change));
        changeAmount.getStyleClass().add(change.signum() >= 0
                ? "wallet-card-gain" : "wallet-card-loss");

        VBox startingDetails = new VBox(4, startingTitle, startingAmount);
        HBox footer = new HBox(startingDetails, changeAmount);
        footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        footer.setSpacing(8);
        HBox.setHgrow(startingDetails, Priority.ALWAYS);

        VBox card = new VBox(14, heading, currentTitle, currentAmount, divider, footer);
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("wallet-card");
        return card;
    }

    private static String formatChange(BigDecimal change) {
        return (change.signum() >= 0 ? "+" : "-") + formatBalance(change.abs());
    }

    private static String formatBalance(BigDecimal balance) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return formatter.format(balance);
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
