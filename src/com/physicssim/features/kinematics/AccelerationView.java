package com.physicssim.features.kinematics;

import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AccelerationView extends BorderPane {

    private static final double PIXELS_PER_METER = 4.0;
    private static final double TRACK_Y = 200;
    private static final double START_X = 30;
    private static final double END_X = 490;
    private static final double TRACK_LENGTH_METERS = (END_X - START_X) / PIXELS_PER_METER;
    private static final double BALL_RADIUS = 10;

    private enum RunState {
        IDLE, RUNNING, PAUSED, STOPPED
    }

    private final Canvas canvas = new Canvas(520, 260);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final TextField initialVelocityField = new TextField("5");
    private final TextField accelerationField = new TextField("2");
    private final Slider velocitySlider = new Slider(0, 20, 5);
    private final Slider accelerationSlider = new Slider(-10, 10, 2);

    private final Label uDisplayLabel = new Label("5.00 m/s");
    private final Label aDisplayLabel = new Label("2.00 m/s²");
    private final Label timeLabel = new Label("0.00 s");
    private final Label velocityLabel = new Label("5.00 m/s");
    private final Label distanceLabel = new Label("0.00 m");

    private final Button startBtn = new Button("Start");
    private final Button pauseBtn = new Button("Pause");
    private final Button resumeBtn = new Button("Resume");
    private final Button resetBtn = new Button("Reset");

    private RunState runState = RunState.IDLE;
    private double simulationU = 5;
    private double simulationA = 2;
    private double elapsedTime = 0;
    private double positionMeters = 0;
    private double currentVelocity = 0;
    private boolean motionStopped = false;

    private AnimationTimer animationTimer;

    public AccelerationView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        Pane canvasContainer = new Pane(canvas);
        canvasContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d9e2ee; -fx-border-radius: 8; -fx-background-radius: 8;");

        startBtn.setText("Start");
        pauseBtn.setText("Pause");
        resumeBtn.setText("Resume");
        resetBtn.setText("Reset");

        styleButton(startBtn, "#22c55e");
        styleButton(pauseBtn, "#f59e0b");
        styleButton(resumeBtn, "#3157d5");
        styleButton(resetBtn, "#ef4444");

        startBtn.setOnAction(event -> startSimulation());
        pauseBtn.setOnAction(event -> pauseSimulation());
        resumeBtn.setOnAction(event -> resumeSimulation());
        resetBtn.setOnAction(event -> resetSimulation());

        HBox buttons = new HBox(8, startBtn, pauseBtn, resumeBtn, resetBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label formula = new Label("v = u + at");
        formula.setFont(AppTheme.cardTitleFont());
        formula.setTextFill(AppTheme.TEXT_PRIMARY);

        Label note = new Label("Note: When a = 0, the ball moves with constant velocity");
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280; -fx-font-style: italic;");
        note.setWrapText(true);

        VBox controls = new VBox(14,
                formula,
                velocityInputRow("Initial Velocity (u)", initialVelocityField, velocitySlider, "m/s"),
                accelerationInputRow("Acceleration (a)", accelerationField, accelerationSlider, "m/s²"),
                note,
                buttons,
                valueRow("u", uDisplayLabel),
                valueRow("a", aDisplayLabel),
                valueRow("t", timeLabel),
                valueRow("v", velocityLabel),
                valueRow("Distance", distanceLabel)
        );
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPadding(new Insets(8));
        controls.setPrefWidth(220);

        HBox body = new HBox(24, canvasContainer, controls);
        body.setAlignment(Pos.TOP_LEFT);

        setCenter(body);
        setupAnimation();
        resetSimulation();
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

                // Cap large frame gaps so tabbing away does not jump the simulation.
                deltaTime = Math.min(deltaTime, 0.05);

                AccelerationModel.SimulationStep step = AccelerationModel.advance(
                        simulationU,
                        simulationA,
                        elapsedTime,
                        positionMeters,
                        motionStopped,
                        deltaTime,
                        TRACK_LENGTH_METERS);

                elapsedTime = step.elapsedTime();
                positionMeters = step.positionMeters();
                currentVelocity = step.velocity();
                motionStopped = step.stopped();

                if (motionStopped) {
                    runState = RunState.STOPPED;
                    updateButtonStates();
                }

                updateDisplays();
                drawCanvas();
            }
        };
        animationTimer.start();
    }

    private void startSimulation() {
        if (!readInputs()) {
            return;
        }

        elapsedTime = 0;
        positionMeters = 0;
        currentVelocity = simulationU;
        motionStopped = false;
        runState = RunState.RUNNING;

        lockInputs(true);
        updateButtonStates();
        updateDisplays();
        drawCanvas();
    }

    private void pauseSimulation() {
        if (runState == RunState.RUNNING) {
            runState = RunState.PAUSED;
            updateButtonStates();
        }
    }

    private void resumeSimulation() {
        if (runState == RunState.PAUSED) {
            runState = RunState.RUNNING;
            updateButtonStates();
        }
    }

    private void resetSimulation() {
        runState = RunState.IDLE;
        elapsedTime = 0;
        positionMeters = 0;
        motionStopped = false;

        readInputs();
        currentVelocity = simulationU;

        lockInputs(false);
        updateButtonStates();
        updateDisplays();
        drawCanvas();
    }

    private boolean readInputs() {
        try {
            simulationU = Math.max(0, Double.parseDouble(initialVelocityField.getText().trim()));
            simulationA = Double.parseDouble(accelerationField.getText().trim());
            initialVelocityField.setText(String.format("%.2f", simulationU));
            accelerationField.setText(String.format("%.2f", simulationA));
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void lockInputs(boolean locked) {
        initialVelocityField.setDisable(locked);
        accelerationField.setDisable(locked);
        velocitySlider.setDisable(locked);
        accelerationSlider.setDisable(locked);
    }

    private void updateButtonStates() {
        startBtn.setDisable(runState != RunState.IDLE);
        pauseBtn.setDisable(runState != RunState.RUNNING);
        resumeBtn.setDisable(runState != RunState.PAUSED);
        resetBtn.setDisable(false);
    }

    private void updateDisplays() {
        uDisplayLabel.setText(String.format("%.2f m/s", simulationU));
        aDisplayLabel.setText(String.format("%.2f m/s²", simulationA));
        timeLabel.setText(String.format("%.2f s", elapsedTime));
        velocityLabel.setText(String.format("%.2f m/s", Math.max(0, currentVelocity)));
        distanceLabel.setText(String.format("%.2f m", positionMeters));
    }

    private double ballX() {
        return START_X + positionMeters * PIXELS_PER_METER;
    }

    private void drawCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw track line at the bottom of the ball
        gc.setStroke(Color.web("#374151"));
        gc.setLineWidth(3);
        gc.strokeLine(START_X, TRACK_Y, END_X, TRACK_Y);

        double bx = ballX();
        gc.setFill(Color.web("#2563eb"));
        gc.fillOval(bx - BALL_RADIUS, TRACK_Y - 2 * BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
        
        // Show velocity and time while ball is moving
        if (runState == RunState.RUNNING || runState == RunState.PAUSED) {
            gc.setFill(Color.web("#dc2626"));
            gc.setFont(javafx.scene.text.Font.font(14));
            gc.fillText(String.format("v = %.2f m/s", currentVelocity), bx - 30, TRACK_Y - 40);
            gc.fillText(String.format("t = %.2f s", elapsedTime), bx - 30, TRACK_Y - 25);
        }
    }

    private HBox velocityInputRow(String name, TextField field, Slider slider, String unit) {
        Label label = new Label(name);
        label.setPrefWidth(150);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        label.setTextFill(AppTheme.TEXT_PRIMARY);
        field.setPrefWidth(70);
        field.setDisable(false);
        
        slider.setPrefWidth(120);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        
        Label valueLabel = new Label();
        valueLabel.setPrefWidth(50);
        valueLabel.setTextFill(AppTheme.TEXT_SECONDARY);
        
        // Update text field when slider changes
        slider.valueProperty().addListener((obs, old, val) -> {
            if (runState == RunState.IDLE) {
                field.setText(String.format("%.2f", val.doubleValue()));
                valueLabel.setText(String.format("%.1f", val.doubleValue()));
                readInputs();
                currentVelocity = simulationU;
                updateDisplays();
                drawCanvas();
            }
        });
        
        // Update slider when text field changes
        field.textProperty().addListener((obs, old, val) -> {
            if (runState == RunState.IDLE && readInputs()) {
                slider.setValue(simulationU);
                valueLabel.setText(String.format("%.1f", simulationU));
                currentVelocity = simulationU;
                updateDisplays();
                drawCanvas();
            }
        });
        
        // Initialize value label
        valueLabel.setText(String.format("%.1f", slider.getValue()));
        
        Label unitLabel = new Label(unit);
        unitLabel.setTextFill(AppTheme.TEXT_SECONDARY);
        
        HBox row = new HBox(8, label, field, slider, valueLabel, unitLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox accelerationInputRow(String name, TextField field, Slider slider, String unit) {
        Label label = new Label(name);
        label.setPrefWidth(150);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        label.setTextFill(AppTheme.TEXT_PRIMARY);
        field.setPrefWidth(70);
        field.setDisable(false);
        
        slider.setPrefWidth(120);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        
        Label valueLabel = new Label();
        valueLabel.setPrefWidth(50);
        valueLabel.setTextFill(AppTheme.TEXT_SECONDARY);
        
        // Update text field when slider changes
        slider.valueProperty().addListener((obs, old, val) -> {
            if (runState == RunState.IDLE) {
                field.setText(String.format("%.2f", val.doubleValue()));
                valueLabel.setText(String.format("%.1f", val.doubleValue()));
                readInputs();
                updateDisplays();
                drawCanvas();
            }
        });
        
        // Update slider when text field changes
        field.textProperty().addListener((obs, old, val) -> {
            if (runState == RunState.IDLE && readInputs()) {
                slider.setValue(simulationA);
                valueLabel.setText(String.format("%.1f", simulationA));
                updateDisplays();
                drawCanvas();
            }
        });
        
        // Initialize value label
        valueLabel.setText(String.format("%.1f", slider.getValue()));
        
        Label unitLabel = new Label(unit);
        unitLabel.setTextFill(AppTheme.TEXT_SECONDARY);
        
        HBox row = new HBox(8, label, field, slider, valueLabel, unitLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox valueRow(String symbol, Label valueLabel) {
        Label label = new Label(symbol + ":");
        label.setPrefWidth(70);
        label.setFont(AppTheme.cardTitleFont());
        label.setTextFill(AppTheme.TEXT_PRIMARY);
        valueLabel.setTextFill(AppTheme.TEXT_PRIMARY);
        HBox row = new HBox(8, label, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16; -fx-min-width: 80px;");
    }
}
