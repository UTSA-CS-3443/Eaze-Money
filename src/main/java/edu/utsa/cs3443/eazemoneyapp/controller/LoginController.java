package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.Session;
import edu.utsa.cs3443.eazemoneyapp.model.login.Account;
import edu.utsa.cs3443.eazemoneyapp.model.login.AccountsManager;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController extends BaseController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;


    @FXML private void showFormError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML private void hideFormError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML private void onBack() { Navigator.showPage(Page.STARTING);}

    @FXML private void onSignIn() {
        hideFormError();

        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            AccountsManager manager = new AccountsManager();
            Account account = manager.authenticate(username, password);

            if (account == null) {
                showFormError("Incorrect username or password");
                passwordField.clear();
                return;
            }

                Session.login(account);
                if (account.isOnboardingComplete()) {
                    Navigator.showPage(Page.DASHBOARD);
                } else {
                    Navigator.showPage(Page.ONBOARDING);
                }

        } catch (IOException e) {
            showError("Could not read information about username or password. Please try again.");
        }
    }

    @FXML private void onForgotPassword() { Navigator.showPage(Page.FORGOT_PASSWORD);}

    @FXML private void onCreateAccount() { Navigator.showPage(Page.REGISTER);}

    @Override
    public void onShow() {
        usernameField.clear();
        passwordField.clear();
        hideFormError();
    }
}
