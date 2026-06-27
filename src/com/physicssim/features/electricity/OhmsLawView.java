package com.physicssim.features.electricity;

import com.physicssim.model.electricity.OhmsLawSimulation;
import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * A simple Ohm's law view that lets the user adjust voltage and resistance and
 * visualizes a minimal circuit (voltage source + resistor) and computed current.
 */

public class OhmsLawView extends BorderPane {

    private final OhmsLawSimulation model = new OhmsLawSimulation(5.0, 10.0);
    private final Canvas canvas = new Canvas(520, 220);
    private final ToggleButton keyToggle = new ToggleButton("Key: Closed");
    private final Label keyStatus = new Label("Key: Closed");
    private double electronOffset = 0;
    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (model.isClosed()) {
                electronOffset = (electronOffset + 3.2) % 200;
                redraw();
            }
        }
    };

    public OhmsLawView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        VBox controls = buildControls();
        VBox.setMargin(controls, new Insets(8));

        setTop(new Label("Current electricity — Voltage, Resistance and Current"));
        setCenter(canvas);
        setRight(controls);

        loadStyles();
        setupCanvasInteraction();
        animationTimer.start();
        redraw();
    }

    private void loadStyles() {
        String cssPath = "/resources/style/css/electricity/Electricity.css";
        java.net.URL res = getClass().getResource(cssPath);
        if (res != null) {
            String url = res.toExternalForm();
            if (!getStylesheets().contains(url)) getStylesheets().add(url);
        }
    }

    private VBox buildControls() {
        Label vLabel = new Label("Voltage");
        vLabel.setFont(AppTheme.cardTitleFont());

        Slider vSlider = new Slider(0, 24, model.getVoltage());
        vSlider.setShowTickMarks(true);
        vSlider.setShowTickLabels(true);

        TextField vField = new TextField(String.format("%.2f", model.getVoltage()));
        vField.setPromptText("Voltage");
        vField.setMaxWidth(80);

        vSlider.valueProperty().addListener((s, o, n) -> {
            model.setVoltage(n.doubleValue());
            vField.setText(String.format("%.2f", model.getVoltage()));
            redraw();
        });

        vField.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                try {
                    double val = Double.parseDouble(vField.getText());
                    vSlider.setValue(val);
                } catch (NumberFormatException ex) {
                    vField.setText(String.format("%.2f", model.getVoltage()));
                }
            }
        });

        Label rLabel = new Label("Resistance");
        rLabel.setFont(AppTheme.cardTitleFont());

        Slider rSlider = new Slider(0, 100, model.getResistance());
        rSlider.setShowTickMarks(true);
        rSlider.setShowTickLabels(true);

        TextField rField = new TextField(String.format("%.2f", model.getResistance()));
        rField.setPromptText("Resistance");
        rField.setMaxWidth(80);

        rSlider.valueProperty().addListener((s, o, n) -> {
            model.setResistance(n.doubleValue());
            rField.setText(String.format("%.2f", model.getResistance()));
            redraw();
        });

        rField.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                try {
                    double val = Double.parseDouble(rField.getText());
                    rSlider.setValue(val);
                } catch (NumberFormatException ex) {
                    rField.setText(String.format("%.2f", model.getResistance()));
                }
            }
        });

        keyToggle.setSelected(model.isClosed());
        keyToggle.setFont(AppTheme.cardNumberFont());
        keyToggle.setOnAction(ev -> {
            model.setClosed(keyToggle.isSelected());
            keyStatus.setText(keyToggle.isSelected() ? "Key: Closed" : "Key: Open");
            redraw();
        });
        keyStatus.setFont(AppTheme.cardNumberFont());
        keyStatus.setText(model.isClosed() ? "Key: Closed" : "Key: Open");

        Label currentLabel = new Label();
        currentLabel.setFont(AppTheme.subtitleFont());
        currentLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    if (!model.isClosed()) return "Current: 0.0000 A (open)";
                    double i = model.getCurrent();
                    if (Double.isInfinite(i)) return "Current: ∞ A (R=0)";
                    return String.format("Current: %.4f A", i);
                },
                vSlider.valueProperty(), rSlider.valueProperty(), keyToggle.selectedProperty()));

        Label vUnitLabel = new Label("V");
        vUnitLabel.setFont(AppTheme.cardNumberFont());

        Label vValueLabel = new Label();
        vValueLabel.setFont(AppTheme.cardNumberFont());
        vValueLabel.textProperty().bind(Bindings.createStringBinding(() ->
            String.format("%.2f V", model.getVoltage()), vSlider.valueProperty()));

        javafx.scene.control.Button vMinus = new javafx.scene.control.Button("Voltage -");
        javafx.scene.control.Button vPlus = new javafx.scene.control.Button("Voltage +");
        vMinus.setFocusTraversable(false);
        vPlus.setFocusTraversable(false);
        vMinus.setOnAction(ev -> {
            double step = 0.1;
            vSlider.setValue(Math.max(vSlider.getMin(), Math.round((vSlider.getValue() - step) * 100.0) / 100.0));
        });
        vPlus.setOnAction(ev -> {
            double step = 0.1;
            vSlider.setValue(Math.min(vSlider.getMax(), Math.round((vSlider.getValue() + step) * 100.0) / 100.0));
        });

        HBox vBox = new HBox(6, vMinus, vSlider, vPlus, vField, vUnitLabel, vValueLabel);
        vBox.setAlignment(Pos.CENTER_LEFT);

        Label rUnitLabel = new Label("Ω");
        rUnitLabel.setFont(AppTheme.cardNumberFont());

        Label rValueLabel = new Label();
        rValueLabel.setFont(AppTheme.cardNumberFont());
        rValueLabel.textProperty().bind(Bindings.createStringBinding(() ->
            String.format("%.2f Ω", model.getResistance()), rSlider.valueProperty()));

        javafx.scene.control.Button rMinus = new javafx.scene.control.Button("Resistor -");
        javafx.scene.control.Button rPlus = new javafx.scene.control.Button("Resistor +");
        rMinus.setFocusTraversable(false);
        rPlus.setFocusTraversable(false);
        rMinus.setOnAction(ev -> {
            double step = 1.0;
            rSlider.setValue(Math.max(rSlider.getMin(), Math.round((rSlider.getValue() - step) * 100.0) / 100.0));
        });
        rPlus.setOnAction(ev -> {
            double step = 1.0;
            rSlider.setValue(Math.min(rSlider.getMax(), Math.round((rSlider.getValue() + step) * 100.0) / 100.0));
        });

        HBox rBox = new HBox(6, rMinus, rSlider, rPlus, rField, rUnitLabel, rValueLabel);
        rBox.setAlignment(Pos.CENTER_LEFT);

        VBox controls = new VBox(12, vLabel, vBox, rLabel, rBox, keyToggle, keyStatus, currentLabel);
        controls.setPadding(new Insets(8));
        controls.setPrefWidth(320);
        return controls;
    }

    private void drawRealisticBattery(GraphicsContext g, double batteryX, double batteryTopY, double batteryBottomY, double voltage) {
        // Battery body
        g.setFill(Color.web("#4a4a4a"));
        g.fillRoundRect(batteryX, batteryTopY, 35, batteryBottomY - batteryTopY, 8, 8);
        
        // Battery positive terminal
        g.setFill(Color.web("#c0c0c0"));
        g.fillOval(batteryX + 10, batteryTopY - 8, 15, 12);
        
        // Battery negative terminal
        g.setFill(Color.web("#808080"));
        g.fillRect(batteryX + 5, batteryBottomY - 5, 25, 8);
        
        // Labels
        g.setFill(Color.web("#fff"));
        g.setFont(Font.font("Arial", 14));
        g.fillText("+", batteryX + 13, batteryTopY + 2);
        g.fillText("-", batteryX + 13, batteryBottomY - 2);
        
        g.setFill(Color.web("#000"));
        g.setFont(Font.font("Arial", 12));
        g.fillText(String.format("%.2f V", voltage), batteryX - 5, batteryBottomY + 25);
        g.fillText("Battery", batteryX, batteryTopY - 10);
    }
    
    private void redraw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Background
        g.setFill(Color.web("#f8fbff"));
        g.fillRoundRect(10, 10, canvas.getWidth() - 20, canvas.getHeight() - 20, 15, 15);

        // Draw simple circuit: battery on left, resistor on right, connecting wires
        double centerY = canvas.getHeight() / 2;

        // rectangle loop coordinates
        double leftX = 120;
        double rightX = 440;
        double topY = 40;
        double bottomY = 150;

        g.setStroke(Color.web("#654321"));
        g.setLineWidth(4);

        double keyX = leftX + 180;
        if (model.isClosed()) {
            g.strokeLine(leftX, topY, rightX, topY);
            drawArrow(g, leftX + 40, topY, 1);
            drawClosedKey(g, keyX, topY);
        } else {
            g.strokeLine(leftX, topY, keyX - 14, topY);
            g.strokeLine(keyX + 14, topY, rightX, topY);
            drawOpenKey(g, keyX, topY);
        }

        // right vertical (resistor placed here)
        g.strokeLine(rightX, topY, rightX, bottomY);

        // bottom wire (right to left)
        g.strokeLine(rightX, bottomY, leftX, bottomY);

        // left vertical (battery placed here)
        g.strokeLine(leftX, bottomY, leftX, topY);

        // Realistic battery
        double batteryX = leftX - 50;
        double batteryTopY = topY + 25;
        double batteryBottomY = bottomY - 25;
        drawRealisticBattery(g, batteryX, batteryTopY, batteryBottomY, model.getVoltage());
        g.strokeLine(leftX, topY, batteryX + 17, batteryTopY - 8);
        g.strokeLine(leftX, bottomY, batteryX + 17, batteryBottomY);

        // resistor on right vertical: draw a polished zig-zag symbol
        double ry1 = topY + 20;
        double ry2 = bottomY - 20;
        double size = 10;
        double[] xPoints = {
                rightX, rightX + size, rightX - size, rightX + size,
                rightX - size, rightX + size, rightX - size, rightX
        };
        double[] yPoints = {
                ry1, ry1 + 10, ry1 + 20, ry1 + 30,
                ry1 + 40, ry1 + 50, ry1 + 60, ry2
        };
        g.setLineWidth(3);
        g.strokePolyline(xPoints, yPoints, xPoints.length);
        g.setLineWidth(1.5);
        g.setFill(Color.web("#000"));
        g.fillText(String.format("%.2f Ω", model.getResistance()), rightX + 18, (ry1 + ry2) / 2 + 4);

        if (model.isClosed()) {
            drawCharges(g, leftX, topY, rightX, bottomY);
        }

        // current text - larger, more visible
        double current = model.getCurrent();
        String currentText = Double.isInfinite(current) ? "I = ∞ A (R = 0)" : String.format("I = %.4f A", current);
        g.setFont(Font.font("Arial", 20));
        g.setFill(Color.web("#000"));
        g.fillText(currentText, (leftX + rightX) / 2 - 80, bottomY + 45);
        g.setFont(Font.font("Arial", 14));
        g.fillText("I = V / R", (leftX + rightX) / 2 - 60, bottomY + 70);
    }

    private void drawClosedKey(GraphicsContext g, double keyX, double keyY) {
        g.setStroke(Color.web("#333"));
        g.setLineWidth(3);
        // contact pads
        g.strokeLine(keyX - 16, keyY, keyX - 10, keyY);
        g.strokeLine(keyX + 10, keyY, keyX + 16, keyY);

        // key body and blade
        double[] xPoints = {keyX - 10, keyX + 2, keyX + 8, keyX + 8, keyX + 2};
        double[] yPoints = {keyY, keyY - 6, keyY - 4, keyY + 4, keyY + 6};
        g.setFill(Color.web("#ddd"));
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        g.strokePolygon(xPoints, yPoints, xPoints.length);

        // key handle
        g.setFill(Color.web("#2b8aef"));
        g.fillOval(keyX + 8, keyY - 5, 10, 10);
        g.strokeOval(keyX + 8, keyY - 5, 10, 10);
    }

    private void drawOpenKey(GraphicsContext g, double keyX, double keyY) {
        g.setStroke(Color.web("#333"));
        g.setLineWidth(3);
        g.strokeLine(keyX - 16, keyY, keyX - 10, keyY);
        g.strokeLine(keyX + 10, keyY - 2, keyX + 16, keyY - 2);

        double[] xPoints = {keyX - 10, keyX + 2, keyX + 8, keyX + 8, keyX + 2};
        double[] yPoints = {keyY, keyY - 12, keyY - 10, keyY - 8, keyY - 6};
        g.setFill(Color.web("#ddd"));
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        g.strokePolygon(xPoints, yPoints, xPoints.length);

        // hinge pin and open handle
        g.setFill(Color.web("#333"));
        g.fillOval(keyX - 14, keyY - 4, 8, 8);
        g.setFill(Color.web("#c33"));
        g.fillOval(keyX + 6, keyY - 14, 8, 8);
    }

    private void drawCharges(GraphicsContext g, double leftX, double topY, double rightX, double bottomY) {
        g.setFill(Color.web("#2b8aef"));
        double loopLength = 2 * (rightX - leftX) + 2 * (bottomY - topY);
        double spacing = 28;
        for (int i = 0; i < 6; i++) {
            double distance = (electronOffset + i * spacing) % loopLength;
            double x;
            double y;
            double topLength = rightX - leftX;
            double rightLength = bottomY - topY;
            double bottomLength = topLength;
            if (distance < topLength) {
                x = leftX + distance;
                y = topY;
            } else if (distance < topLength + rightLength) {
                x = rightX;
                y = topY + (distance - topLength);
            } else if (distance < topLength + rightLength + bottomLength) {
                x = rightX - (distance - topLength - rightLength);
                y = bottomY;
            } else {
                x = leftX;
                y = bottomY - (distance - topLength - rightLength - bottomLength);
            }
            g.fillOval(x - 4, y - 4, 8, 8);
        }
    }

    private void setupCanvasInteraction() {
        canvas.setOnMouseClicked(event -> {
            double leftX = 80;
            double keyX = leftX + 180;
            double topY = 36;
            double clickX = event.getX();
            double clickY = event.getY();
            if (Math.abs(clickY - topY) < 14 && Math.abs(clickX - keyX) < 24) {
                boolean closed = !model.isClosed();
                model.setClosed(closed);
                keyToggle.setSelected(closed);
                keyStatus.setText(closed ? "Key: Closed" : "Key: Open");
                redraw();
            }
        });
    }

    private void drawArrow(GraphicsContext g, double x, double y, int direction) {
        // direction: 1 = right, -1 = left
        double len = 12;
        g.setFill(Color.web("#2b8aef"));
        g.setStroke(Color.web("#2b8aef"));
        g.setLineWidth(2);
        if (direction == 1) {
            g.strokeLine(x - len, y, x + len, y);
            g.strokeLine(x + len, y, x + len - 6, y - 6);
            g.strokeLine(x + len, y, x + len - 6, y + 6);
        } else {
            g.strokeLine(x + len, y, x - len, y);
            g.strokeLine(x - len, y, x - len + 6, y - 6);
            g.strokeLine(x - len, y, x - len + 6, y + 6);
        }
    }
}
