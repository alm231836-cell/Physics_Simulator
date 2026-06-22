package com.physicssim.app;

import com.physicssim.views.HomeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PhysicsSimulatorApp extends Application {

    private static final double WINDOW_WIDTH = 1180;
    private static final double WINDOW_HEIGHT = 720;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new HomeView(), WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setTitle("Physics Simulator - R13");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
