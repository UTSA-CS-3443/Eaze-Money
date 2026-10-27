package edu.utsa.cs3443.eazemoneyapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WalletsController extends BaseController {
    @FXML private Label pageLabel;

    @FXML
    private void initialize() {
        pageLabel.setText("Wallets Page");
    }

    @Override
    public void onShow() {

    }
}
