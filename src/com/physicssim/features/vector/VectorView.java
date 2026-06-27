package com.physicssim.features.vector;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class VectorView extends BorderPane {

    private final Canvas vectorCanvas = new Canvas(520, 320);
    private final Slider magnitudeSlider = new Slider(1, 10, 5);
    private final Slider angleSlider = new Slider(0, 360, 45);
    private final Label magnitudeValue = new Label();
    private final Label componentValue = new Label();

    public VectorView() {
        setPadding(new Insets(12));
        setBackground(AppTheme.pageBackground());
        setTop(buildHeader());
        setCenter(buildBody());
        updateVisualization();

        magnitudeSlider.valueProperty().addListener((obs, oldValue, newValue) -> updateVisualization());
        angleSlider.valueProperty().addListener((obs, oldValue, newValue) -> updateVisualization());
    }

    private Node buildHeader() {
        Label title = new Label("Vector fundamentals");
        title.setFont(AppTheme.cardTitleFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label subtitle = new Label("Explore vector magnitude, direction, and components with an interactive diagram.");
        subtitle.setFont(AppTheme.subtitleFont());
        subtitle.setTextFill(AppTheme.TEXT_SECONDARY);
        subtitle.setWrapText(true);

        VBox header = new VBox(8, title, subtitle);
        header.setPadding(new Insets(20));
        header.setBackground(AppTheme.surfaceBackground());
        header.setBorder(AppTheme.cardBorder());
        return header;
    }

    private Node buildBody() {
        Label controlsTitle = new Label("Controls");
        controlsTitle.setFont(AppTheme.cardTitleFont());
        controlsTitle.setTextFill(AppTheme.TEXT_PRIMARY);

        Label magnitudeLabel = new Label("Magnitude");
        magnitudeLabel.setTextFill(AppTheme.TEXT_PRIMARY);
        magnitudeSlider.setShowTickLabels(true);
        magnitudeSlider.setShowTickMarks(true);
        magnitudeSlider.setMajorTickUnit(1);
        magnitudeSlider.setMinorTickCount(0);

        Label angleLabel = new Label("Angle (degrees)");
        angleLabel.setTextFill(AppTheme.TEXT_PRIMARY);
        angleSlider.setShowTickLabels(true);
        angleSlider.setShowTickMarks(true);
        angleSlider.setMajorTickUnit(60);
        angleSlider.setMinorTickCount(0);

        magnitudeValue.setTextFill(AppTheme.TEXT_SECONDARY);
        componentValue.setTextFill(AppTheme.TEXT_SECONDARY);

        VBox controls = new VBox(12,
                controlsTitle,
                magnitudeLabel,
                magnitudeSlider,
                angleLabel,
                angleSlider,
                magnitudeValue,
                componentValue);
        controls.setPadding(new Insets(16));
        controls.setBackground(AppTheme.surfaceBackground());
        controls.setBorder(AppTheme.cardBorder());

        Label intro = new Label("A vector can be described by its length and direction. The horizontal and vertical components are found by projecting the vector onto the axes.");
        intro.setWrapText(true);
        intro.setTextFill(AppTheme.TEXT_SECONDARY);

        HBox content = new HBox(20, controls, buildCanvasPanel());
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(12));

        VBox page = new VBox(16, intro, content);
        return page;
    }

    private Node buildCanvasPanel() {
        Label canvasTitle = new Label("Vector diagram");
        canvasTitle.setFont(AppTheme.cardTitleFont());
        canvasTitle.setTextFill(AppTheme.TEXT_PRIMARY);

        VBox panel = new VBox(12, canvasTitle, vectorCanvas);
        panel.setPadding(new Insets(16));
        panel.setBackground(AppTheme.surfaceBackground());
        panel.setBorder(AppTheme.cardBorder());
        return panel;
    }

    private void updateVisualization() {
        double magnitude = magnitudeSlider.getValue();
        double angleDegrees = angleSlider.getValue();
        double angleRadians = Math.toRadians(angleDegrees);

        double vx = magnitude * Math.cos(angleRadians);
        double vy = magnitude * Math.sin(angleRadians);

        magnitudeValue.setText(String.format("Magnitude: %.2f", magnitude));
        componentValue.setText(String.format("Components: (%.2f, %.2f)", vx, vy));
        drawVector(vx, vy);
    }

    private void drawVector(double vx, double vy) {
        GraphicsContext gc = vectorCanvas.getGraphicsContext2D();
        double width = vectorCanvas.getWidth();
        double height = vectorCanvas.getHeight();

        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        gc.setStroke(Color.web("#d0d7de"));
        gc.setLineWidth(1);
        gc.strokeLine(20, height / 2, width - 20, height / 2);
        gc.strokeLine(width / 2, 20, width / 2, height - 20);

        gc.setStroke(Color.web("#3157d5"));
        gc.setLineWidth(3);
        gc.strokeLine(width / 2, height / 2, width / 2 + vx * 30, height / 2 - vy * 30);

        gc.setFill(Color.web("#3157d5"));
        gc.fillOval(width / 2 + vx * 30 - 5, height / 2 - vy * 30 - 5, 10, 10);

        gc.setFill(Color.web("#1f2937"));
        gc.fillText("x", width - 30, height / 2 - 8);
        gc.fillText("y", width / 2 + 8, 20);
        gc.fillText("vector", width / 2 + vx * 30 + 8, height / 2 - vy * 30 - 8);
    }
}
