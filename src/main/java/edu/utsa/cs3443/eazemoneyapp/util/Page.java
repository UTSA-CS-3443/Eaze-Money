package edu.utsa.cs3443.eazemoneyapp.util;

/**
 * Every screen the Navigator can show. Each constant knows its FXML file and whether
 * it appears inside the main layout (with the sidebar) or fills the whole window.
 *
 * To add a page: create its FXML in resources/view, add a constant here, and make sure
 * its controller extends BaseController.
 */
public enum Page {

    // Full-window pages (no sidebar)
    STARTING("/edu/utsa/cs3443/eazemoneyapp/view/starting-view.fxml", false),
    LOGIN("/edu/utsa/cs3443/eazemoneyapp/view/login-view.fxml", false),
    REGISTER("/edu/utsa/cs3443/eazemoneyapp/view/register-view.fxml", false),
    ONBOARDING("/edu/utsa/cs3443/eazemoneyapp/view/onboarding-view.fxml", false),

    // Pages shown to the right of the sidebar
    DASHBOARD("/edu/utsa/cs3443/eazemoneyapp/view/dashboard-view.fxml", true),
    TRANSACTIONS("/edu/utsa/cs3443/eazemoneyapp/view/transactions-view.fxml", true),
    BUDGETS("/edu/utsa/cs3443/eazemoneyapp/view/budgets-view.fxml", true),
    WALLETS("/edu/utsa/cs3443/eazemoneyapp/view/wallets-view.fxml", true);

    private final String fxmlPath;
    private final boolean sidebar;

    Page(String fxmlPath, boolean sidebar) {
        this.fxmlPath = fxmlPath;
        this.sidebar = sidebar;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    /** True if this page is displayed inside the main layout next to the sidebar. */
    public boolean hasSidebar() {
        return sidebar;
    }
}