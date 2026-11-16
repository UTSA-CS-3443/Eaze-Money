package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.model.login.AccountsManager;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 *
 */
public class ForgotPasswordController extends BaseController {
    /**
     * Called every time this page is shown. Reload data from the Account
     * and refresh tables, labels and charts here.
     */

    @FXML private Label errorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private void showFormError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML private void hideFormError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML private void onSave() {
        hideFormError();

        try {
            AccountsManager manager = new AccountsManager();
            String error = manager.resetPassword(usernameField.getText(), passwordField.getText(), confirmPasswordField.getText());

            if (error != null) {
                showFormError(error);
                passwordField.clear();
                confirmPasswordField.clear();
                return;
            }
            Dialogs.showInfo("Password has updated. Please sign in with your new password.");
            Navigator.showPage(Page.LOGIN);

        } catch (IOException e) {
            showError("Could not save password. Please try again.");
        }
    }

    @Override
    public void onShow() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        hideFormError();
    }
}
