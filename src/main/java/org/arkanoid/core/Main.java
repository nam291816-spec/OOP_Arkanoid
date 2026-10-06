package org.arkanoid.core;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        App app = new App();
        app.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}