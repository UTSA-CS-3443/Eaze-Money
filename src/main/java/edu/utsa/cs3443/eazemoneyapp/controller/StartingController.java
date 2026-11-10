package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;
import javafx.fxml.FXML;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;

/**
 *
 */
public class StartingController extends BaseController {


    @FXML private void onLogin() { Navigator.showPage(Page.LOGIN); }
    @FXML private void onRegister() { Navigator.showPage(Page.REGISTER); }

    @FXML
    private void onTerms() {
        Dialogs.showInfo("Eaze Money is a class project for CS3443. Use it to track your own spending, it gives no financial advice.");
    }

    @FXML
    private void onPrivacy() {
        Dialogs.showInfo("All your data is saved as files on this computer. Nothing is sent to the internet.");
    }
    @Override
    public void onShow() {

    }
}
