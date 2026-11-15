package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.Session;
import edu.utsa.cs3443.eazemoneyapp.model.login.Account;
import edu.utsa.cs3443.eazemoneyapp.model.login.AccountsManager;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController extends BaseController {
    @FXML private TextField displayNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox agreeBox;
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

    @FXML private void onSignIn() {
        Navigator.showPage(Page.LOGIN);
    }

    @FXML private void onRegister() {
        hideFormError();

        if(!agreeBox.isSelected()) {
            showFormError("Please agree to the Terms of Service and Privacy Policy");
            return;
        }

        try {
            AccountsManager manager = new AccountsManager();
            Account account = manager.registerAccount(usernameField.getText(),  passwordField.getText(), confirmPasswordField.getText(), displayNameField.getText());
            Session.login(account);

            Navigator.showPage(Page.ONBOARDING);
        } catch (IllegalArgumentException e) {
            showFormError(e.getMessage());
            passwordField.clear();
            confirmPasswordField.clear();
        } catch (IOException e) {
            showError("Could not save your account. Please try again");
        }
    }
    @Override
    public void onShow() {
        displayNameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        agreeBox.setSelected(false);
        hideFormError();
    }
}
