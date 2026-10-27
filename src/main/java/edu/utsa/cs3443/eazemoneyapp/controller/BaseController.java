package edu.utsa.cs3443.eazemoneyapp.controller;

import edu.utsa.cs3443.eazemoneyapp.Session;
import edu.utsa.cs3443.eazemoneyapp.model.login.Account;
import edu.utsa.cs3443.eazemoneyapp.util.Dialogs;

public abstract class BaseController {

    /** Called every time this page is shown. Reload data from the Account
     and refresh tables, labels and charts here. */
    public abstract void onShow();

    protected Account getAccount() { return Session.getCurrentAccount(); }
    protected void showError(String message) { Dialogs.showError(message); }
    protected boolean confirm(String message) { return Dialogs.confirm(message); }
}