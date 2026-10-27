package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginController extends BaseController {
    @FXML private Label pageLabel;

    @FXML
    private void initialize() {
        pageLabel.setText("Login Page");
    }

    @FXML private void onDashboard() { Navigator.showPage(Page.DASHBOARD); }

    @Override
    public void onShow() {

    }
}
