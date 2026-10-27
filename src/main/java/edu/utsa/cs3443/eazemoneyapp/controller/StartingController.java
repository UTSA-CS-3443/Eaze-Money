package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StartingController extends BaseController {
    @FXML private Label pageLabel;

    @FXML
    private void initialize() {
        pageLabel.setText("Starting Page");
    }

    @FXML private void onLogin() { Navigator.showPage(Page.LOGIN); }
    @FXML private void onRegister() { Navigator.showPage(Page.REGISTER); }

    @Override
    public void onShow() {

    }
}
