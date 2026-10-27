package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RegisterController extends BaseController {
    @FXML private Label pageLabel;

    @FXML
    private void initialize() {
        pageLabel.setText("Register Page");
    }

    @FXML private void onOnboarding() { Navigator.showPage(Page.ONBOARDING); }

    @Override
    public void onShow() {

    }
}
