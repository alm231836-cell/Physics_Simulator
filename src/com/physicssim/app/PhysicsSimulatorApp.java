package com.physicssim.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PhysicsSimulatorApp extends Application {

    private static final double WINDOW_WIDTH = 1180;
    private static final double WINDOW_HEIGHT = 720;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new AppShell(), WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.getStylesheets().add(
            getClass()
                .getResource("/css/app.css")
                .toExternalForm()
        );

        stage.setTitle("Physica");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
