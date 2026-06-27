package com.physicssim.features.kinematics;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class DistanceDisplacementView extends BorderPane {

    private final Canvas canvas = new Canvas(520, 300);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final List<Double> pathX = new ArrayList<>();
    private final List<Double> pathY = new ArrayList<>();

    private double startX = -1;
    private double startY = -1;
    private double endX = -1;
    private double endY = -1;

    private final Label distanceLabel = new Label("0.00 m");
    private final Label displacementLabel = new Label("0.00 m");

    private boolean isDrawingPath = false;
    private boolean isDraggingStart = false;
    private boolean isDraggingEnd = false;

    private double distanceMeters = 0;
    private double displacementMeters = 0;

    private static final double POINT_RADIUS = 14;
    private static final double MIN_DRAG_DISTANCE = 6;
    private static final double END_SYNC_EPSILON = 1.0;

    public DistanceDisplacementView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        Pane canvasContainer = new Pane(canvas);
        canvasContainer.setStyle("-fx-background-color: #f0fdf4; -fx-background-radius: 16; -fx-border-color: #d9e2ee; -fx-border-radius: 16;");

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-background-color: #dbeafe; -fx-text-fill: #000000; -fx-background-radius: 10; -fx-padding: 10 20;");
        clearBtn.setOnAction(event -> clearAll());

        VBox controls = new VBox(16,
                new HBox(10, clearBtn),
                statBlock("Total Distance", distanceLabel, "Orange: Length of the path you draw"),
                statBlock("Displacement", displacementLabel, "Blue: Straight line from start to end")
        );
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPadding(new Insets(8));

        HBox body = new HBox(24, canvasContainer, controls);
        body.setAlignment(Pos.TOP_LEFT);

        setCenter(body);
        setupCanvas();
        drawCanvas();
    }

    private void setupCanvas() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isOnStart(event)) {
                isDraggingStart = true;
                canvas.setCursor(Cursor.CLOSED_HAND);
            } else if (isOnEnd(event)) {
                isDraggingEnd = true;
                canvas.setCursor(Cursor.CLOSED_HAND);
            } else {
                beginNewPath(event.getX(), event.getY());
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isDraggingStart) {
                startX = event.getX();
                startY = event.getY();
                if (!pathX.isEmpty()) {
                    pathX.set(0, startX);
                    pathY.set(0, startY);
                }
                updateCalculations();
                drawCanvas();
            } else if (isDraggingEnd) {
                endX = event.getX();
                endY = event.getY();
                syncEndIntoPath();
                updateCalculations();
                drawCanvas();
            } else if (isDrawingPath) {
                double lastX = pathX.get(pathX.size() - 1);
                double lastY = pathY.get(pathY.size() - 1);
                double dx = event.getX() - lastX;
                double dy = event.getY() - lastY;
                if (Math.sqrt(dx * dx + dy * dy) >= MIN_DRAG_DISTANCE) {
                    pathX.add(event.getX());
                    pathY.add(event.getY());
                }
                endX = event.getX();
                endY = event.getY();
                updateCalculations();
                drawCanvas();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isDrawingPath) {
                endX = event.getX();
                endY = event.getY();
                appendEndToPath();
                updateCalculations();
            }
            isDrawingPath = false;
            isDraggingStart = false;
            isDraggingEnd = false;
            canvas.setCursor(Cursor.DEFAULT);
            drawCanvas();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (isOnStart(event) || isOnEnd(event)) {
                canvas.setCursor(Cursor.HAND);
            } else if (!isDrawingPath && !isDraggingStart && !isDraggingEnd) {
                canvas.setCursor(Cursor.CROSSHAIR);
            }
        });
    }

    private void beginNewPath(double x, double y) {
        pathX.clear();
        pathY.clear();
        startX = x;
        startY = y;
        endX = x;
        endY = y;
        pathX.add(startX);
        pathY.add(startY);
        isDrawingPath = true;
        isDraggingStart = false;
        isDraggingEnd = false;
        updateCalculations();
        drawCanvas();
    }

    private boolean isOnStart(MouseEvent e) {
        if (startX == -1) {
            return false;
        }
        double dx = e.getX() - startX;
        double dy = e.getY() - startY;
        return Math.sqrt(dx * dx + dy * dy) <= POINT_RADIUS + 5;
    }

    private boolean isOnEnd(MouseEvent e) {
        if (endX == -1 || isDrawingPath) {
            return false;
        }
        double dx = e.getX() - endX;
        double dy = e.getY() - endY;
        return Math.sqrt(dx * dx + dy * dy) <= POINT_RADIUS + 5;
    }

    private void clearAll() {
        startX = -1;
        startY = -1;
        endX = -1;
        endY = -1;
        pathX.clear();
        pathY.clear();
        isDrawingPath = false;
        isDraggingStart = false;
        isDraggingEnd = false;
        distanceMeters = 0;
        displacementMeters = 0;
        distanceLabel.setText("0.00 m");
        displacementLabel.setText("0.00 m");
        canvas.setCursor(Cursor.CROSSHAIR);
        drawCanvas();
    }

    private void appendEndToPath() {
        if (pathX.isEmpty()) {
            return;
        }

        double lastX = pathX.get(pathX.size() - 1);
        double lastY = pathY.get(pathY.size() - 1);
        if (DistanceDisplacementModel.segmentLengthPixels(lastX, lastY, endX, endY) > END_SYNC_EPSILON) {
            pathX.add(endX);
            pathY.add(endY);
        } else if (pathX.size() == 1) {
            pathX.add(endX);
            pathY.add(endY);
        }
    }

    private void syncEndIntoPath() {
        if (pathX.isEmpty()) {
            return;
        }

        if (pathX.size() == 1) {
            pathX.add(endX);
            pathY.add(endY);
        } else {
            int lastIndex = pathX.size() - 1;
            pathX.set(lastIndex, endX);
            pathY.set(lastIndex, endY);
        }
    }

    private void updateCalculations() {
        boolean includeLiveEnd = isDrawingPath;

        distanceMeters = DistanceDisplacementModel.calculateDistanceMeters(
                pathX, pathY, endX, endY, includeLiveEnd);
        displacementMeters = DistanceDisplacementModel.calculateDisplacementMeters(
                startX, startY, endX, endY);

        distanceLabel.setText(String.format("%.2f m", distanceMeters));
        displacementLabel.setText(String.format("%.2f m", displacementMeters));
    }

    private void drawCanvas() {
        gc.setFill(Color.web("#f0fdf4"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.web("#e2e8f0"));
        gc.setLineWidth(1);
        for (int i = 0; i < canvas.getWidth(); i += 40) {
            gc.strokeLine(i, 0, i, canvas.getHeight());
        }
        for (int i = 0; i < canvas.getHeight(); i += 40) {
            gc.strokeLine(0, i, canvas.getWidth(), i);
        }

        gc.setFill(Color.web("#475569"));
        gc.setFont(javafx.scene.text.Font.font(14));
        if (isDrawingPath) {
            gc.fillText("Drag to draw your path — release at the end point", 20, 30);
        } else if (startX == -1) {
            gc.fillText("Click and drag from start to end to measure distance & displacement", 20, 30);
        } else {
            gc.fillText("Drag START/END to adjust, or click elsewhere to draw a new path", 20, 30);
        }
        gc.fillText("Scale: 40 pixels = 10 meters", 20, 50);

        if (startX != -1 && endX != -1) {
            gc.setStroke(Color.web("#3b82f6"));
            gc.setLineWidth(3);
            gc.setLineDashes(8, 4);
            gc.strokeLine(startX, startY, endX, endY);
            gc.setLineDashes(0);
        }

        if (!pathX.isEmpty()) {
            gc.setStroke(Color.web("#f59e0b"));
            gc.setLineWidth(4);
            for (int i = 1; i < pathX.size(); i++) {
                gc.strokeLine(pathX.get(i - 1), pathY.get(i - 1), pathX.get(i), pathY.get(i));
            }
            if (isDrawingPath && endX != -1) {
                gc.strokeLine(pathX.get(pathX.size() - 1), pathY.get(pathY.size() - 1), endX, endY);
            }
        }

        if (startX != -1 && endX != -1 && (distanceMeters > 0 || displacementMeters > 0)) {
            double labelX = (startX + endX) / 2 + 12;
            double labelY = (startY + endY) / 2 - 10;
            gc.setFill(Color.web("#1e293b"));
            gc.setFont(javafx.scene.text.Font.font(13));
            gc.fillText(String.format("d = %.2f m", distanceMeters), labelX, labelY);
            gc.fillText(String.format("s = %.2f m", displacementMeters), labelX, labelY + 18);
        }

        if (startX != -1) {
            gc.setFill(Color.web("#ef4444"));
            gc.fillOval(startX - POINT_RADIUS, startY - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(12));
            gc.fillText("START", startX - 22, startY - 18);
        }

        if (endX != -1 && !isDrawingPath) {
            gc.setFill(Color.web("#22c55e"));
            gc.fillOval(endX - POINT_RADIUS, endY - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);
            gc.setFill(Color.WHITE);
            gc.fillText("END", endX - 17, endY - 18);
        } else if (isDrawingPath) {
            gc.setFill(Color.web("#22c55e"));
            gc.fillOval(endX - POINT_RADIUS, endY - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);
        }
    }

    private VBox statBlock(String name, Label value, String desc) {
        Label label = new Label(name);
        label.setFont(AppTheme.cardTitleFont());
        label.setTextFill(AppTheme.TEXT_PRIMARY);

        value.setFont(javafx.scene.text.Font.font(24));
        value.setStyle("-fx-font-weight: 800;");

        Label description = new Label(desc);
        description.setTextFill(AppTheme.TEXT_SECONDARY);
        description.setStyle("-fx-font-size: 12px;");
        description.setWrapText(true);

        return new VBox(6, label, value, description);
    }
}
