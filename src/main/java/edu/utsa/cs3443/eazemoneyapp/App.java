package edu.utsa.cs3443.eazemoneyapp;

import javafx.application.Application;
import javafx.stage.Stage;
import edu.utsa.cs3443.eazemoneyapp.util.Navigator;
import edu.utsa.cs3443.eazemoneyapp.util.Page;

/** JavaFX entry point: creates the window and shows the starting page. */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Eaze Money");
        Navigator.init(stage);
        Navigator.showPage(Page.STARTING);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
