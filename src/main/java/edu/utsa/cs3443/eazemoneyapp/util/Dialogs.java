package edu.utsa.cs3443.eazemoneyapp.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Pop-up messages used everywhere in the app, so every error, notice and confirmation
 * looks and behaves the same. These methods wait until the user closes the pop-up.
 * Must be called from the JavaFX application thread (i.e. from a button handler or onShow()).
 */
public final class Dialogs {

    private Dialogs() { }

    /** Shows a red error pop-up, e.g. "Amount must be a number." */
    public static void showError(String message) {
        build(AlertType.ERROR, "Error", message).showAndWait();
    }

    /** Shows a plain information pop-up. */
    public static void showInfo(String message) {
        build(AlertType.INFORMATION, "Information", message).showAndWait();
    }

    /** Shows a warning pop-up, e.g. for a budget that has been exceeded. */
    public static void showWarning(String message) {
        build(AlertType.WARNING, "Warning", message).showAndWait();
    }

    /** Asks a Yes/No question. @return true only if the user pressed Yes */
    public static boolean confirm(String message) {
        Alert alert = build(AlertType.CONFIRMATION, "Please confirm", message);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().filter(button -> button == ButtonType.YES).isPresent();
    }

    private static Alert build(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);        // no big header line, just the message
        alert.setContentText(message);
        alert.initOwner(Navigator.getStage());   // pop-up stays on top of the main window
        return alert;
    }
}