package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.Session;
import edu.utsa.cs3443.eazemoneyapp.model.login.AccountsManager;
import edu.utsa.cs3443.eazemoneyapp.model.wallet.Wallet;
import edu.utsa.cs3443.eazemoneyapp.model.wallet.WalletList;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class OnboardingController extends BaseController {
    @FXML private Label welcomeLabel;
    @FXML private Label walletCountLabel;
    @FXML private VBox walletRows;
    @FXML private Label errorLabel;
    @FXML private TextField walletNameField;
    @FXML private TextField balanceField;

    private final WalletList wallets = new WalletList();

    @FXML private void showFormError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML private void hideFormError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML private void onAddWallet() {
        hideFormError();

        String walletName = walletNameField.getText().trim();
        if (walletName.isEmpty()) {
            showFormError("Please enter a wallet name");
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceField.getText().trim().replace(",",""));
        } catch (NumberFormatException e) {
            showFormError("Enter a number, e.g 2,000");
            return;
        }

        wallets.addWallet(new Wallet(wallets.nextWalletId(), walletName, balance));

        refreshList();
        walletNameField.clear();
        balanceField.clear();
        walletNameField.requestFocus();
    }

    @FXML private void onContinue() {
        try {
            new AccountsManager().completeOnboarding(Session.getUserID());
            Navigator.showPage(Page.DASHBOARD);
        } catch (IOException e) {
            showError("Could not save. Please try again");
        }
    }

    private void refreshList(){
        wallets.loadWallets(Session.getUserID());
        walletRows.getChildren().clear();
        walletCountLabel.setText("Wallets added (" + wallets.getWallets().size() + ")");

        if (wallets.getWallets().isEmpty()) {
            Label emptyLabel = new Label("No wallets added.");
            emptyLabel.getStyleClass().add("wallet-empty");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            walletRows.getChildren().add(emptyLabel);
        }

        for (Wallet w : wallets.getWallets()) {
            Label name = new Label (w.getWalletName());
            name.getStyleClass().add("wallet-name");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label amount = new Label(String.format("$%,.2f", w.getStartingBalance()));
            amount.getStyleClass().add("wallet-amount");

            HBox row = new HBox(name, spacer, amount);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("wallet-row");
            walletRows.getChildren().add(row);
        }
    }

    @Override
    public void onShow() {
        welcomeLabel.setText("Welcome, " + getAccount().getDisplayName());
        refreshList();
    }


}