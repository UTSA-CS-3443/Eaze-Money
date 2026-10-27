package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;

/**
 * Controller for the sidebar in sidebar-view.fxml.
 * Each button simply asks the Navigator to show a page.
 */
public class SideBarController {

    @FXML private Label userLabel;

    /** Runs once, right after the FXML is loaded (after login, so the account exists). */
    @FXML
    private void initialize() {
        if (Session.isLoggedIn()) {
            userLabel.setText(Session.getCurrentAccount().getDisplayName());
        }
    }

    @FXML private void onDashboard()    { Navigator.showPage(Page.DASHBOARD); }
    @FXML private void onTransactions() { Navigator.showPage(Page.TRANSACTIONS); }
    @FXML private void onBudgets()      { Navigator.showPage(Page.BUDGETS); }
    @FXML private void onWallets()      { Navigator.showPage(Page.WALLETS); }
    @FXML private void onSignOut()      { Navigator.signOut(); }
}