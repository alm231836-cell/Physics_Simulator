package com.physicssim.features.pendulum;

import java.util.ArrayDeque;
import java.util.Deque;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

public class PendulumCanvas extends Pane {

    private static final int TRAIL_LIMIT = 80;

    private final PendulumModel model;
    private final Rectangle supportBeam = new Rectangle();
    private final Line rod = new Line();
    private final Circle pivot = new Circle(7, Color.web("#1d4ed8"));
    private final Circle bobGlow = new Circle();
    private final Circle bob = new Circle();
    private final Polyline trail = new Polyline();
    private final Group grid = new Group();
    private final Line horizontalAxis = new Line();
    private final Line verticalAxis = new Line();
    private final Label pivotLabel = createOverlayLabel("Pivot");
    private final Label bobLabel = createOverlayLabel("Bob position");
    private final Label sceneLabel = createOverlayLabel("Live swing");
    private final Deque<Double> trailPoints = new ArrayDeque<>();

    public PendulumCanvas(PendulumModel model) {
        this.model = model;

        setPrefSize(595, 357);
        setMinSize(357, 238);
        setStyle("-fx-background-color: linear-gradient(to bottom, #f8fbff, #eef6ff);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: #d9e4f2;"
                + "-fx-border-radius: 18;");

        supportBeam.setArcWidth(14);
        supportBeam.setArcHeight(14);
        supportBeam.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#64748b")),
                new Stop(1, Color.web("#94a3b8"))));

        rod.setStroke(Color.web("#3b82f6"));
        rod.setStrokeWidth(4);

        bobGlow.setFill(Color.color(0.37, 0.51, 0.96, 0.16));
        bobGlow.setEffect(new DropShadow(12, Color.color(0.11, 0.16, 0.24, 0.25)));

        bob.setFill(Color.web("#60a5fa"));
        bob.setStroke(Color.web("#1d4ed8"));
        bob.setStrokeWidth(3);
        bob.setEffect(new DropShadow(8, Color.color(0.05, 0.11, 0.2, 0.25)));

        trail.setStroke(Color.web("#22c55e"));
        trail.setStrokeWidth(3);
        trail.setOpacity(0.95);

        horizontalAxis.setStroke(Color.web("#9ca3af"));
        verticalAxis.setStroke(Color.web("#9ca3af"));

        getChildren().addAll(grid, horizontalAxis, verticalAxis, supportBeam, trail, rod, pivot, bobGlow, bob, sceneLabel, pivotLabel, bobLabel);
        widthProperty().addListener((obs, oldVal, newVal) -> render());
        heightProperty().addListener((obs, oldVal, newVal) -> render());
        render();
    }

    public void clearTrail() {
        trailPoints.clear();
        trail.getPoints().clear();
    }

    public void render() {
        double width = getWidth() <= 0 ? getPrefWidth() : getWidth();
        double height = getHeight() <= 0 ? getPrefHeight() : getHeight();
        double pivotX = width / 2.0;
        double pivotY = 61;
        double displayLength = model.getDisplayLength();
        double bobX = pivotX + displayLength * Math.sin(model.getAngle());
        double bobY = pivotY + displayLength * Math.cos(model.getAngle());

        drawGrid(width, height);

        supportBeam.setX(pivotX - 94);
        supportBeam.setY(31);
        supportBeam.setWidth(187);
        supportBeam.setHeight(12);

        horizontalAxis.setStartX(31);
        horizontalAxis.setStartY(height / 2.0);
        horizontalAxis.setEndX(width - 31);
        horizontalAxis.setEndY(height / 2.0);

        verticalAxis.setStartX(width / 2.0);
        verticalAxis.setStartY(15);
        verticalAxis.setEndX(width / 2.0);
        verticalAxis.setEndY(height - 15);

        rod.setStartX(pivotX);
        rod.setStartY(pivotY);
        rod.setEndX(bobX);
        rod.setEndY(bobY);

        pivot.setCenterX(pivotX);
        pivot.setCenterY(pivotY);

        bobGlow.setRadius(model.getBobRadius() + 9);
        bobGlow.setCenterX(bobX);
        bobGlow.setCenterY(bobY);

        bob.setRadius(model.getBobRadius());
        bob.setCenterX(bobX);
        bob.setCenterY(bobY);

        trailPoints.addLast(bobX);
        trailPoints.addLast(bobY);
        while (trailPoints.size() > TRAIL_LIMIT * 2) {
            trailPoints.removeFirst();
            trailPoints.removeFirst();
        }
        trail.getPoints().setAll(trailPoints);

        sceneLabel.relocate(24, 18);
        pivotLabel.relocate(pivotX + 10, pivotY - 20);
        bobLabel.relocate(bobX + 14, bobY - 7);
    }

    private void drawGrid(double width, double height) {
        grid.getChildren().clear();
        for (int x = 26; x < width; x += 26) {
            Line line = new Line(x, 17, x, height - 17);
            line.setStroke(Color.web("#e5edf7"));
            grid.getChildren().add(line);
        }
        for (int y = 17; y < height; y += 26) {
            Line line = new Line(26, y, width - 26, y);
            line.setStroke(Color.web("#e5edf7"));
            grid.getChildren().add(line);
        }
    }

    private Label createOverlayLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-font-weight: 700; "
                + "-fx-text-fill: #0f1720; "
                + "-fx-background-color: rgba(248, 251, 255, 0.92); "
                + "-fx-background-radius: 8; "
                + "-fx-padding: 3 6;");
        return label;
    }
}
