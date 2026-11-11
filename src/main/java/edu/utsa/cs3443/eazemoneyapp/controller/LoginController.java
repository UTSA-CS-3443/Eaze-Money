package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;


    @FXML private void onDashboard() { Navigator.showPage(Page.DASHBOARD); }

    @FXML private void onBack() { Navigator.showPage(Page.STARTING);}

    @FXML private void onSignIn() { Navigator.showPage(Page.ONBOARDING);}

    @FXML private void onForgotPassword() { Navigator.showPage(Page.FORGOT_PASSWORD);}

    @FXML private void onCreateAccount() { Navigator.showPage(Page.REGISTER);}

    @Override
    public void onShow() {

    }
}
