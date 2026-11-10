package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.wallet.Wallet;
import edu.utsa.cs3443.eazemoneyapp.model.wallet.WalletList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class WalletsController extends BaseController {
    @FXML private GridPane walletGrid;
    @FXML private Label emptyLabel;

    @Override
    public void onShow() {
        walletGrid.getChildren().clear();
        if (getAccount() == null) {
            showError("Please sign in to view your wallets.");
            return;
        }

        WalletList walletList = getAccount().getWalletList();
        try {
            Map<String, BigDecimal> balances =
                    walletList.loadWalletBalances(getAccount().getUserID());
            int index = 0;
            for (Wallet wallet : walletList.getWallets()) {
                BigDecimal balance = balances.get(wallet.getWalletId());
                walletGrid.add(createWalletCard(wallet, balance), index % 2, index / 2);
                index++;
            }
            emptyLabel.setVisible(index == 0);
            emptyLabel.setManaged(index == 0);
        } catch (IOException e) {
            showError("Could not load wallet balances: " + e.getMessage());
        }
    }

    private VBox createWalletCard(Wallet wallet, BigDecimal balance) {
        Label nameLabel = new Label(wallet.getWalletName());
        nameLabel.getStyleClass().add("wallet-name");

        Label balanceLabel = new Label(formatBalance(balance));
        balanceLabel.getStyleClass().add("wallet-balance");

        VBox card = new VBox(10, nameLabel, balanceLabel);
        card.getStyleClass().add("wallet-card");
        card.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);
        return card;
    }

    private String formatBalance(BigDecimal balance) {
        return "$" + balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
