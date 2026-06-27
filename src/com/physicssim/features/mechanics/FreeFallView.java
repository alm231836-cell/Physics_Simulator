package com.physicssim.features.mechanics;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class FreeFallView extends MechanicsToolLayout {

    private final Label heightValue = valueLabel();
    private final Label timeValue = valueLabel();
    private final Label velocityValue = valueLabel();
    private final Pane scenePane = new Pane();
    private final Circle ball = new Circle(18, Color.web("#2563eb"));
    private final Slider heightSlider = new Slider(5, 100, 40);

    public FreeFallView(Runnable onBack) {
        super("Free Fall Simulator", "Simulate a body dropped from a height and observe time and impact velocity.", onBack);
        setToolContent(buildContent());
        updateSimulation();
    }

    private VBox buildContent() {
        Label heightLabel = label("Initial Height");
        heightSlider.valueProperty().addListener((obs, oldValue, newValue) -> updateSimulation());

        scenePane.setPrefSize(420, 280);
        scenePane.setStyle("-fx-background-color: linear-gradient(to bottom, #eef6ff, #fefce8);"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #d9e2ee;"
                + "-fx-border-radius: 16;");
        Line ground = new Line(40, 240, 380, 240);
        ground.setStroke(Color.web("#6b7280"));
        ground.setStrokeWidth(4);
        scenePane.getChildren().addAll(ground, ball);

        VBox controls = new VBox(14,
                heightLabel,
                heightValue,
                heightSlider,
                statBlock("Fall Time", timeValue),
                statBlock("Final Velocity", velocityValue));
        controls.setAlignment(Pos.TOP_LEFT);

        HBox body = new HBox(24, scenePane, controls);
        body.setAlignment(Pos.TOP_LEFT);
        return new VBox(body);
    }

    private void updateSimulation() {
        double height = heightSlider.getValue();
        double gravity = 9.81;
        double time = Math.sqrt((2 * height) / gravity);
        double velocity = gravity * time;

        heightValue.setText(String.format("%.1f m", height));
        timeValue.setText(String.format("%.2f s", time));
        velocityValue.setText(String.format("%.2f m/s", velocity));

        double y = 240 - (height * 1.8);
        ball.setCenterX(210);
        ball.setCenterY(Math.max(40, y));
    }

    private VBox statBlock(String name, Label value) {
        Label label = label(name);
        return new VBox(6, label, value);
    }

    private Label label(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: 700;");
        return label;
    }

    private Label valueLabel() {
        Label label = new Label();
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: 800;");
        return label;
    }
}
