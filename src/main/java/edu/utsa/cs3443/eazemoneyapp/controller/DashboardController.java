package edu.utsa.cs3443.eazemoneyapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController extends BaseController {
    @FXML
    private Label welcomeLabel;

    @Override
    public void onShow() {
        welcomeLabel.setText("Welcome, {NAME HERE}!");
    }
}
