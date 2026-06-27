package com.physicssim.features.kinematics;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class ProjectileMotionView extends BorderPane {

    private final Canvas canvas = new Canvas(600, 350);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final TextField initialVelocityField = new TextField("20");
    private final TextField launchAngleField = new TextField("45");
    private final TextField initialHeightField = new TextField("10");
    private final TextField gravityField = new TextField("9.81");
    private final Slider speedSlider = new Slider(5, 50, 20);
    private final Slider angleSlider = new Slider(10, 80, 45);
    private final Slider heightSlider = new Slider(0, 100, 10);
    private final Slider gravitySlider = new Slider(1, 25, 9.81);
    private final Label speedLabel = new Label("20.0 m/s");
    private final Label angleLabel = new Label("45.0°");
    private final Label heightSliderLabel = new Label("10.0 m");
    private final Label gravityLabel = new Label("9.81 m/s²");
    private final Label timeLabel = new Label("0.00 s");
    private final Label vxLabel = new Label("0.0 m/s");
    private final Label vyLabel = new Label("0.0 m/s");
    private final Label distanceLabel = new Label("0.0 m");
    private final Label currentHeightDisplayLabel = new Label("0.0 m");
    private final Label speedDisplayLabel = new Label("0.0 m/s");
    private final Label maxHeightLabel = new Label("0.0 m");
    private final Label rangeLabel = new Label("0.0 m");
    private final Button startBtn = new Button("Start");
    private final Button pauseBtn = new Button("Pause");
    private final Button resetBtn = new Button("Reset");
    private final Button replayBtn = new Button("Replay");

    private enum RunState {
        IDLE, RUNNING, PAUSED, STOPPED
    }

    private RunState runState = RunState.IDLE;
    private double initialVelocity = 20;
    private double launchAngle = 45;
    private double initialHeight = 10;
    private double gravity = 9.81;
    private double currentX = 0;
    private double currentY = 0;
    private double elapsedTime = 0;
    private double maxHeightReached = 0;
    private double pixelsPerMeter = 3;
    private AnimationTimer animationTimer;
    private final List<Double> trailX = new ArrayList<>();
    private final List<Double> trailY = new ArrayList<>();

    public ProjectileMotionView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        Pane canvasContainer = new Pane(canvas);
        canvasContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #dbeafe, #f0fdf4); -fx-background-radius: 16; -fx-border-color: #d9e2ee; -fx-border-radius: 16;");

        speedSlider.valueProperty().addListener((obs, old, newVal) -> {
            speedLabel.setText(String.format("%.1f m/s", newVal.doubleValue()));
            if (runState == RunState.IDLE) {
                initialVelocityField.setText(String.format("%.2f", newVal.doubleValue()));
                updateStaticLabels();
            }
        });

        angleSlider.valueProperty().addListener((obs, old, newVal) -> {
            angleLabel.setText(String.format("%.1f°", newVal.doubleValue()));
            if (runState == RunState.IDLE) {
                launchAngleField.setText(String.format("%.2f", newVal.doubleValue()));
                updateStaticLabels();
            }
        });

        heightSlider.valueProperty().addListener((obs, old, newVal) -> {
            heightSliderLabel.setText(String.format("%.1f m", newVal.doubleValue()));
            if (runState == RunState.IDLE) {
                initialHeightField.setText(String.format("%.2f", newVal.doubleValue()));
                initialHeight = newVal.doubleValue();
                updateStaticLabels();
                drawCanvas();
            }
        });

        gravitySlider.valueProperty().addListener((obs, old, newVal) -> {
            gravityLabel.setText(String.format("%.2f m/s²", newVal.doubleValue()));
            if (runState == RunState.IDLE) {
                gravityField.setText(String.format("%.2f", newVal.doubleValue()));
                gravity = newVal.doubleValue();
                updateStaticLabels();
                drawCanvas();
            }
        });

        styleButton(startBtn, "#22c55e");
        styleButton(pauseBtn, "#f59e0b");
        styleButton(resetBtn, "#ef4444");
        styleButton(replayBtn, "#3157d5");

        startBtn.setOnAction(event -> startSimulation());
        pauseBtn.setOnAction(event -> pauseSimulation());
        resetBtn.setOnAction(event -> resetSimulation());
        replayBtn.setOnAction(event -> replaySimulation());

        HBox buttons = new HBox(8, startBtn, pauseBtn, resetBtn, replayBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox controls = new VBox(6,
                buttons,
                statBlock("Initial Velocity (v₀)", speedLabel),
                speedSlider,
                inputRow("Custom Velocity (m/s)", initialVelocityField),
                statBlock("Launch Angle (θ)", angleLabel),
                angleSlider,
                inputRow("Custom Angle (deg)", launchAngleField),
                statBlock("Initial Height (m)", heightSliderLabel),
                heightSlider,
                inputRow("Custom Height (m)", initialHeightField),
                statBlock("Gravity (m/s²)", gravityLabel),
                gravitySlider,
                inputRow("Custom Gravity", gravityField),
                statBlock("Time (t)", timeLabel),
                statBlock("Horizontal Velocity (Vx)", vxLabel),
                statBlock("Vertical Velocity (Vy)", vyLabel),
                statBlock("Horizontal Distance (x)", distanceLabel),
                statBlock("Current Height (y)", currentHeightDisplayLabel),
                statBlock("Total Speed", speedDisplayLabel),
                statBlock("Maximum Height", maxHeightLabel),
                statBlock("Total Range", rangeLabel));
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPadding(new Insets(8));
        controls.setPrefWidth(320);

        ScrollPane scrollPane = new ScrollPane(controls);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(340);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox body = new HBox(24, canvasContainer, scrollPane);
        body.setAlignment(Pos.TOP_LEFT);

        setCenter(body);
        setupAnimation();
        heightSlider.setValue(initialHeight);
        gravitySlider.setValue(gravity);
        readInputs();
        updateStaticLabels();
        drawCanvas();
    }

    private void setupAnimation() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (runState != RunState.RUNNING) {
                    lastUpdate = 0;
                    return;
                }

                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1e9;
                lastUpdate = now;

                // Cap large frame gaps
                deltaTime = Math.min(deltaTime, 0.05);

                ProjectileModel step = ProjectileModel.advance(
                        initialVelocity,
                        launchAngle,
                        initialHeight,
                        gravity,
                        elapsedTime,
                        200, // max horizontal range in meters
                        runState == RunState.STOPPED,
                        deltaTime);

                elapsedTime = step.time();
                currentX = step.x();
                currentY = step.y();
                maxHeightReached = step.maxHeight();

                // Add to trail
                trailX.add(currentX * pixelsPerMeter);
                trailY.add(currentY * pixelsPerMeter);

                if (step.stopped()) {
                    runState = RunState.STOPPED;
                    updateButtonStates();
                }

                updateDisplays(step);
                drawCanvas();
            }
        };
        animationTimer.start();
    }

    private void startSimulation() {
        if (runState == RunState.RUNNING) {
            return;
        }
        if (runState == RunState.IDLE) {
            if (!readInputs()) {
                return;
            }
            elapsedTime = 0;
            currentX = 0;
            currentY = initialHeight;
            maxHeightReached = initialHeight;
            trailX.clear();
            trailY.clear();
        }
        runState = RunState.RUNNING;

        lockInputs(true);
        updateButtonStates();
        updateDisplays(null);
        drawCanvas();
    }

    private void pauseSimulation() {
        if (runState == RunState.RUNNING) {
            runState = RunState.PAUSED;
            updateButtonStates();
        }
    }

    private void resetSimulation() {
        runState = RunState.IDLE;
        elapsedTime = 0;
        currentX = 0;
        currentY = 0;
        maxHeightReached = 0;
        trailX.clear();
        trailY.clear();

        readInputs();
        lockInputs(false);
        updateButtonStates();
        updateStaticLabels();
        drawCanvas();
    }

    private void replaySimulation() {
        resetSimulation();
        startSimulation();
    }

    private boolean readInputs() {
        try {
            initialVelocity = Double.parseDouble(initialVelocityField.getText().trim());
            launchAngle = Double.parseDouble(launchAngleField.getText().trim());
            initialHeight = Double.parseDouble(initialHeightField.getText().trim());
            gravity = Double.parseDouble(gravityField.getText().trim());

            // Validate inputs
            if (initialVelocity < 0 || launchAngle < 0 || launchAngle > 90 || initialHeight < 0 || gravity <= 0) {
                return false;
            }

            // Update text fields with formatted values
            initialVelocityField.setText(String.format("%.2f", initialVelocity));
            launchAngleField.setText(String.format("%.2f", launchAngle));
            initialHeightField.setText(String.format("%.2f", initialHeight));
            gravityField.setText(String.format("%.2f", gravity));

            // Update sliders
            speedSlider.setValue(initialVelocity);
            angleSlider.setValue(launchAngle);
            heightSlider.setValue(initialHeight);
            gravitySlider.setValue(gravity);

            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void lockInputs(boolean locked) {
        initialVelocityField.setDisable(locked);
        launchAngleField.setDisable(locked);
        initialHeightField.setDisable(locked);
        gravityField.setDisable(locked);
        speedSlider.setDisable(locked);
        angleSlider.setDisable(locked);
        heightSlider.setDisable(locked);
        gravitySlider.setDisable(locked);
    }

    private void updateButtonStates() {
        // Don't disable buttons to maintain their colors
        // State logic is handled in action handlers
    }

    private void updateDisplays(ProjectileModel step) {
        if (step == null) {
            // Static display mode
            double angleRad = Math.toRadians(launchAngle);
            double vx = initialVelocity * Math.cos(angleRad);
            double vy0 = initialVelocity * Math.sin(angleRad);
            double tFlight = (2 * vy0) / gravity;
            double range = vx * tFlight;
            double maxH = initialHeight + (vy0 * vy0) / (2 * gravity);

            timeLabel.setText(String.format("%.2f s", 0.0));
            vxLabel.setText(String.format("%.2f m/s", vx));
            vyLabel.setText(String.format("%.2f m/s", vy0));
            distanceLabel.setText(String.format("%.2f m", 0.0));
            currentHeightDisplayLabel.setText(String.format("%.2f m", initialHeight));
            speedDisplayLabel.setText(String.format("%.2f m/s", initialVelocity));
            maxHeightLabel.setText(String.format("%.2f m", maxH));
            rangeLabel.setText(String.format("%.2f m", range));
        } else {
            // Real-time display
            timeLabel.setText(String.format("%.2f s", step.time()));
            vxLabel.setText(String.format("%.2f m/s", step.vx()));
            vyLabel.setText(String.format("%.2f m/s", step.vy()));
            distanceLabel.setText(String.format("%.2f m", step.x()));
            currentHeightDisplayLabel.setText(String.format("%.2f m", step.y()));
            speedDisplayLabel.setText(String.format("%.2f m/s", step.speed()));
            maxHeightLabel.setText(String.format("%.2f m", step.maxHeight()));
            rangeLabel.setText(String.format("%.2f m", step.totalRange()));
        }
    }

    private void updateStaticLabels() {
        updateDisplays(null);
    }

    private void drawCanvas() {
        gc.setFill(Color.web("#dbeafe"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw ground line
        gc.setStroke(Color.web("#6b7280"));
        gc.setLineWidth(4);
        gc.strokeLine(30, 310, 570, 310);

        // Draw trajectory trail
        if (trailX.size() > 1) {
            gc.setStroke(Color.web("#16a34a"));
            gc.setLineWidth(2);
            gc.beginPath();
            gc.moveTo(50 + trailX.get(0), 310 - trailY.get(0));
            for (int i = 1; i < trailX.size(); i++) {
                gc.lineTo(50 + trailX.get(i), 310 - trailY.get(i));
            }
            gc.stroke();
        }

        // Draw projectile
        gc.setFill(Color.web("#22c55e"));
        double ballX = 50 + currentX * pixelsPerMeter;
        double ballY = 310 - currentY * pixelsPerMeter;
        gc.fillOval(ballX - 8, ballY - 8, 16, 16);

        // Draw velocity vectors
        if (runState == RunState.RUNNING || runState == RunState.PAUSED) {
            double angleRad = Math.toRadians(launchAngle);
            double vx = initialVelocity * Math.cos(angleRad);
            double vy = initialVelocity * Math.sin(angleRad) - gravity * elapsedTime;

            // Scale vectors for visibility
            double vxScale = 3;
            double vyScale = 3;

            // Draw Vx vector (horizontal, green)
            gc.setStroke(Color.web("#16a34a"));
            gc.setLineWidth(2);
            gc.strokeLine(ballX, ballY, ballX + vx * vxScale, ballY);

            // Draw Vy vector (vertical, red)
            gc.setStroke(Color.web("#dc2626"));
            gc.strokeLine(ballX, ballY, ballX, ballY - vy * vyScale);

            // Draw labels
            gc.setFill(Color.web("#16a34a"));
            gc.setFont(javafx.scene.text.Font.font(12));
            gc.fillText(String.format("Vx=%.1f", vx), ballX + vx * vxScale + 5, ballY + 4);

            gc.setFill(Color.web("#dc2626"));
            double vyLabelY = ballY - vy * vyScale - 5;
            // Keep Vy label within canvas bounds
            if (vyLabelY < 15) vyLabelY = 15;
            if (vyLabelY > canvas.getHeight() - 10) vyLabelY = canvas.getHeight() - 10;
            gc.fillText(String.format("Vy=%.1f", vy), ballX + 5, vyLabelY);

            // Draw real-time height and distance labels
            gc.setFill(Color.web("#1e293b"));
            gc.setFont(javafx.scene.text.Font.font(14));
            gc.fillText(String.format("Height: %.2f m", currentY), 20, 30);
            gc.fillText(String.format("Distance: %.2f m", currentX), 20, 50);
        }
    }

    private VBox statBlock(String name, Label value) {
        Label label = new Label(name);
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 700;");
        return new VBox(4, label, value);
    }

    private HBox inputRow(String name, TextField field) {
        Label label = new Label(name);
        label.setPrefWidth(140);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        field.setPrefWidth(70);
        field.setDisable(false);

        field.textProperty().addListener((obs, old, val) -> {
            if (runState == RunState.IDLE && readInputs()) {
                speedSlider.setValue(initialVelocity);
                angleSlider.setValue(launchAngle);
                heightSlider.setValue(initialHeight);
                gravitySlider.setValue(gravity);
                speedLabel.setText(String.format("%.1f m/s", initialVelocity));
                angleLabel.setText(String.format("%.1f°", launchAngle));
                heightSliderLabel.setText(String.format("%.1f m", initialHeight));
                gravityLabel.setText(String.format("%.2f m/s²", gravity));
                updateStaticLabels();
                drawCanvas();
            }
        });

        HBox row = new HBox(8, label, field);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-font-size: 13px; -fx-font-weight: 700;"
                + "-fx-background-color: " + color + ";"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 10 16;"
                + "-fx-min-width: 80px;"
                + "-fx-text-fill: white;");
    }
}
