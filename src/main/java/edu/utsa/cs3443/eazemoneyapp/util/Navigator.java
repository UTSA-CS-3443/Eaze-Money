package edu.utsa.cs3443.eazemoneyapp.util;

import edu.utsa.cs3443.eazemoneyapp.Session;
import edu.utsa.cs3443.eazemoneyapp.controller.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Switches between screens. The app has one window (Stage) and one Scene; "changing page" means
 * loading another FXML file and putting it on the screen.
 *
 * There are two kinds of page (see Page.hasSidebar()):
 *   - full-window pages (login, register, onboarding) replace the whole scene,
 *   - sidebar pages (dashboard, transactions, ...) are placed in the CENTER of sidebar-view.fxml,
 *     so the sidebar stays on screen while only the content changes.
 *
 * After every switch it calls onShow() on the new page's controller.
 * sidebar-view.fxml must have a BorderPane as its root element.
 */
public final class Navigator {

    private static final String SIDEBAR_VIEW_FXML = "/edu/utsa/cs3443/eazemoneyapp/view/sidebar-view.fxml";
    private static final String STYLESHEET = "/edu/utsa/cs3443/eazemoneyapp/css/styles.css";

    private static Stage stage;
    private static BorderPane mainLayout;   // null whenever a full-window page is showing

    private Navigator() { }

    /** Call once from App.start(). Creates the scene and applies the shared stylesheet. */
    public static void init(Stage primaryStage) {
        stage = primaryStage;
        Scene scene = new Scene(new StackPane(), 1100, 720);
        URL css = Navigator.class.getResource(STYLESHEET);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
    }

    /** The main window. Dialogs use it as their owner. */
    public static Stage getStage() {
        return stage;
    }

    /** Shows the given page and then calls its controller's onShow(). */
    public static void showPage(Page page) {
        FXMLLoader loader = newLoader(page.getFxmlPath());
        Parent content = load(loader, page.getFxmlPath());

        Scene scene = stage.getScene();
        // only load the sidebar for pages that requires it
        if (page.hasSidebar()) {
            if (mainLayout == null) {
                mainLayout = loadMainLayout();
            }
            mainLayout.setCenter(content);
            if (scene.getRoot() != mainLayout) {
                scene.setRoot(mainLayout);
            }
        } else {
            mainLayout = null;
            scene.setRoot(content);
        }

        Object controller = loader.getController();
        if (controller instanceof BaseController) {
            ((BaseController) controller).onShow();
        } else if (controller != null) {
            throw new IllegalStateException(page + " controller must extend BaseController.");
        }
    }

    /** Ends the session and returns to the login page. Used by the sidebar's Sign Out button. */
    public static void signOut() {
        Session.logout();
        showPage(Page.STARTING);
    }

    private static BorderPane loadMainLayout() {
        FXMLLoader loader = newLoader(SIDEBAR_VIEW_FXML);
        return (BorderPane) load(loader, SIDEBAR_VIEW_FXML);
    }

    private static FXMLLoader newLoader(String fxmlPath) {
        URL url = Navigator.class.getResource(fxmlPath);
        if (url == null) {
            throw new IllegalStateException("Cannot find FXML file: " + fxmlPath
                    + " (it must be in src/main/resources/edu/utsa/cs3443/eazemoneyapp/view)");
        }
        return new FXMLLoader(url);
    }

    private static Parent load(FXMLLoader loader, String fxmlPath) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load " + fxmlPath + ": " + e.getMessage(), e);
        }
    }
}