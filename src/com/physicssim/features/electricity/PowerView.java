package com.physicssim.features.electricity;

import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Power and Light Bulb Simulation View
 * Shows a circuit with a light bulb whose brightness depends on power (P = V * I)
 * When current flows, the bulb lights up, with intensity based on power dissipation
 */
public class PowerView extends BorderPane {

    private double voltage = 12.0;
    private double resistance = 10.0;
    private boolean circuitClosed = false;
    
    private final Canvas canvas = new Canvas(520, 280);
    private double electronOffset = 0;
    
    private final Slider voltageSlider = new Slider(0, 24, voltage);
    private final Slider resistanceSlider = new Slider(1, 50, resistance);
    private final ToggleButton switchButton = new ToggleButton("Switch: Open");
    private final TextField voltageField = new TextField(String.format("%.2f", voltage));
    private final TextField resistanceField = new TextField(String.format("%.2f", resistance));

    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (circuitClosed) {
                electronOffset = (electronOffset + 2.5) % 300;
            }
            redraw();
        }
    };

    public PowerView() {
        setPadding(new Insets(18));
        setBackground(AppTheme.pageBackground());

        VBox controls = buildControls();
        setTop(controls);

        // Wrap canvas in card-style container
        VBox canvasContainer = new VBox(canvas);
        canvasContainer.setPadding(new Insets(10));
        canvasContainer.setBackground(AppTheme.surfaceBackground());
        canvasContainer.setBorder(AppTheme.cardBorder());
        VBox.setMargin(canvasContainer, new Insets(10, 0, 0, 0));

        setCenter(canvasContainer);

        animationTimer.start();
        redraw();
    }

    private VBox buildControls() {
        Label title = new Label("Power and Light Bulb");
        title.setFont(AppTheme.cardTitleFont());

        Label description = new Label(
                "Power is the rate at which energy is used in an electrical circuit. P = V × I (Watts). " +
                "The light bulb's brightness depends on the power dissipated through it. " +
                "More power = brighter light. Adjust voltage and resistance to see the light bulb glow!");
        description.setFont(AppTheme.cardNumberFont());
        description.setWrapText(true);
        description.setMaxWidth(520);

        Label theory = new Label("P = V × I = V² / R");
        theory.setFont(AppTheme.subtitleFont());

        // Power display
        Label powerDisplay = new Label();
        powerDisplay.setFont(AppTheme.cardNumberFont());
        powerDisplay.setStyle("-fx-text-fill: #FF8C00; -fx-font-weight: bold;");

        voltageSlider.valueProperty().addListener((s, o, n) -> {
            voltage = n.doubleValue();
            voltageField.setText(String.format("%.2f", voltage));
            redraw();
        });

        resistanceSlider.valueProperty().addListener((s, o, n) -> {
            resistance = n.doubleValue();
            resistanceField.setText(String.format("%.2f", resistance));
            redraw();
        });

        voltageField.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                try {
                    double val = Double.parseDouble(voltageField.getText());
                    if (val >= 0 && val <= 24) {
                        voltageSlider.setValue(val);
                    }
                } catch (NumberFormatException ex) {
                    voltageField.setText(String.format("%.2f", voltage));
                }
            }
        });

        resistanceField.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                try {
                    double val = Double.parseDouble(resistanceField.getText());
                    if (val >= 1 && val <= 50) {
                        resistanceSlider.setValue(val);
                    }
                } catch (NumberFormatException ex) {
                    resistanceField.setText(String.format("%.2f", resistance));
                }
            }
        });

        switchButton.setStyle("-fx-font-size: 12px; -fx-padding: 8px 16px;");
        switchButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            circuitClosed = newVal;
            switchButton.setText(newVal ? "Switch: Closed" : "Switch: Open");
            redraw();
        });

        powerDisplay.textProperty().bind(Bindings.createStringBinding(() -> {
            double current = voltage / resistance;
            double power = voltage * current;
            return String.format("Current: %.2f A  |  Power: %.2f W", current, power);
        }, voltageSlider.valueProperty(), resistanceSlider.valueProperty()));

        // Voltage control row
        Label vLabel = new Label("Voltage (V):");
        vLabel.setFont(AppTheme.cardNumberFont());
        HBox voltageRow = new HBox(8, vLabel, voltageSlider, voltageField);
        voltageRow.setAlignment(Pos.CENTER_LEFT);

        // Resistance control row
        Label rLabel = new Label("Resistance (Ω):");
        rLabel.setFont(AppTheme.cardNumberFont());
        HBox resistanceRow = new HBox(8, rLabel, resistanceSlider, resistanceField);
        resistanceRow.setAlignment(Pos.CENTER_LEFT);

        // Switch row
        HBox switchRow = new HBox(8, new Label("Circuit:"), switchButton);
        switchRow.setAlignment(Pos.CENTER_LEFT);

        VBox controls = new VBox(10, title, description, theory, powerDisplay, voltageRow, resistanceRow, switchRow);
        controls.setPadding(new Insets(20));
        controls.setBackground(AppTheme.surfaceBackground());
        controls.setBorder(AppTheme.cardBorder());
        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Calculate circuit parameters
        double current = circuitClosed ? voltage / resistance : 0;
        double power = voltage * current;

        // Draw the circuit schematic
        drawCircuitSchematic(gc, current, power);
    }

    private void drawCircuitSchematic(GraphicsContext gc, double current, double power) {
        double leftX = 80;
        double rightX = canvas.getWidth() - 80;
        double topY = 40;
        double bottomY = canvas.getHeight() - 50;

        // Background for circuit
        gc.setFill(AppTheme.SURFACE);
        gc.fillRoundRect(10, 10, canvas.getWidth() - 20, canvas.getHeight() - 20, 15, 15);

        // Wires color
        gc.setStroke(Color.web("#654321"));
        gc.setLineWidth(4);

        // ===== BATTERY (left side) - REALISTIC BATTERY =====
        double batteryX = leftX - 40;
        double batteryTopY = topY + 30;
        double batteryBottomY = bottomY - 30;
        
        // Battery body
        gc.setFill(Color.web("#4a4a4a"));
        gc.fillRoundRect(batteryX, batteryTopY, 35, batteryBottomY - batteryTopY, 8, 8);
        
        // Battery positive terminal
        gc.setFill(Color.web("#c0c0c0"));
        gc.fillOval(batteryX + 10, batteryTopY - 8, 15, 12);
        
        // Battery negative terminal
        gc.setFill(Color.web("#808080"));
        gc.fillRect(batteryX + 5, batteryBottomY - 5, 25, 8);
        
        // Labels
        gc.setFill(Color.web("#fff"));
        gc.setFont(Font.font("Arial", 14));
        gc.fillText("+", batteryX + 13, batteryTopY + 2);
        gc.fillText("-", batteryX + 13, batteryBottomY - 2);
        
        gc.setFill(Color.web("#000"));
        gc.setFont(Font.font("Arial", 12));
        gc.fillText(String.format("%.2f V", voltage), batteryX - 5, batteryBottomY + 25);
        gc.fillText("Battery", batteryX, batteryTopY - 10);

        // Connect battery to circuit
        gc.strokeLine(leftX, topY, batteryX + 17, batteryTopY - 8);
        gc.strokeLine(leftX, bottomY, batteryX + 17, batteryBottomY);
        gc.strokeLine(leftX, topY, leftX, bottomY);

        // ===== TOP WIRE (left to switch) =====
        double switchX = leftX + 90;
        gc.strokeLine(leftX, topY, switchX - 30, topY);

        // ===== SWITCH/KEY - REALISTIC SWITCH =====
        drawRealisticSwitch(gc, switchX, topY, circuitClosed);
        gc.strokeLine(switchX + 30, topY, switchX + 80, topY);

        // ===== TOP WIRE (switch to bulb) =====
        double bulbX = leftX + 260;
        double bulbY = topY;
        gc.strokeLine(switchX + 80, topY, bulbX - 30, topY);

        // ===== LIGHT BULB - REALISTIC BULB =====
        drawRealisticLightBulb(gc, bulbX, bulbY, current, power);
        gc.strokeLine(bulbX + 30, topY, rightX, topY);

        // ===== RIGHT WIRE (top to bottom) =====
        gc.strokeLine(rightX, topY, rightX, bottomY);

        // ===== BOTTOM WIRE (right to left) =====
        gc.strokeLine(rightX, bottomY, leftX, bottomY);

        // ===== RESISTOR (bottom middle) - REALISTIC RESISTOR =====
        double resistorStartX = leftX + 120;
        double resistorEndX = resistorStartX + 130;
        gc.strokeLine(leftX, bottomY, resistorStartX, bottomY);
        drawRealisticResistorHorizontal(gc, resistorStartX, bottomY, resistorEndX - resistorStartX);
        gc.strokeLine(resistorEndX, bottomY, rightX, bottomY);

        // ===== Connection nodes at junctions =====
        gc.setFill(Color.web("#333"));
        double[] nodeXs = {leftX, rightX, rightX, leftX};
        double[] nodeYs = {topY, topY, bottomY, bottomY};
        for (int i = 0; i < nodeXs.length; i++) {
            gc.fillOval(nodeXs[i] - 5, nodeYs[i] - 5, 10, 10);
        }

        // ===== RESISTOR LABEL =====
        gc.setFill(Color.web("#000"));
        gc.setFont(Font.font("Arial", 13));
        gc.fillText(String.format("%.1f Ω", resistance), resistorStartX + 40, bottomY + 28);

        // ===== ANIMATED CURRENT FLOW =====
        if (circuitClosed && current > 0) {
            drawCurrentMarkers(gc, leftX, rightX, topY, bottomY, bulbX);
        }

        // ===== CIRCUIT VALUES =====
        drawCircuitValuesSchematic(gc, current, power);
    }

    private void drawRealisticSwitch(GraphicsContext gc, double kx, double ky, boolean closed) {
        // Switch base
        gc.setFill(Color.web("#555555"));
        gc.fillRoundRect(kx - 15, ky - 12, 30, 24, 5, 5);
        
        // Contacts
        gc.setStroke(Color.web("#c0c0c0"));
        gc.setLineWidth(4);
        gc.strokeLine(kx - 25, ky, kx - 15, ky);
        gc.strokeLine(kx + 15, ky, kx + 25, ky);
        
        // Switch lever
        gc.setStroke(Color.web("#ffd700"));
        gc.setLineWidth(3);
        if (closed) {
            gc.strokeLine(kx - 15, ky, kx + 15, ky);
            gc.setFill(Color.web("#4CAF50"));
            gc.fillOval(kx + 5, ky - 7, 14, 14);
        } else {
            gc.strokeLine(kx - 15, ky, kx + 10, ky - 18);
            gc.setFill(Color.web("#f44336"));
            gc.fillOval(kx + 5, ky - 22, 14, 14);
        }
        
        // Label
        gc.setFill(Color.web("#fff"));
        gc.setFont(Font.font("Arial", 11));
        gc.fillText("Switch", kx - 15, ky - 20);
    }

    private void drawRealisticLightBulb(GraphicsContext gc, double x, double y, double current, double power) {
        double maxPower = (24.0 * 24.0) / 1.0;
        double brightness = Math.min(1.0, power / (maxPower * 0.3));

        // Connecting wires
        gc.setStroke(Color.web("#654321"));
        gc.setLineWidth(4);
        gc.strokeLine(x - 30, y, x - 18, y);
        gc.strokeLine(x + 18, y, x + 30, y);

        // Glow effect
        if (brightness > 0.05) {
            for (int i = 0; i < 3; i++) {
                Color glowColor = Color.color(1.0, 1.0, 0.0, 0.4 - i * 0.12);
                gc.setStroke(glowColor);
                gc.setLineWidth(6 - i * 1.5);
                int radius = 30 + (int)(brightness * 15) + i * 6;
                gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
            }
        }

        // Bulb glass
        gc.setStroke(Color.web("#d0d0d0"));
        gc.setLineWidth(3);
        gc.setFill(Color.color(0.95, 0.95, 1.0, 0.6));
        gc.fillOval(x - 18, y - 22, 36, 40);
        gc.strokeOval(x - 18, y - 22, 36, 40);

        // Filament
        gc.setStroke(Color.color(brightness, brightness * 0.8, 0.0));
        gc.setLineWidth(2);
        gc.beginPath();
        gc.moveTo(x - 8, y - 8);
        gc.lineTo(x - 4, y + 8);
        gc.lineTo(x + 2, y - 6);
        gc.lineTo(x + 6, y + 6);
        gc.lineTo(x + 10, y - 4);
        gc.stroke();

        // Bulb base
        gc.setFill(Color.web("#808080"));
        gc.fillRoundRect(x - 10, y + 18, 20, 16, 4, 4);

        // Power label
        gc.setFill(Color.web("#ff8c00"));
        gc.setFont(Font.font("Arial", 13));
        gc.fillText(String.format("%.1f W", power), x - 18, y + 52);
    }

    private void drawRealisticResistorHorizontal(GraphicsContext gc, double x, double y, double width) {
        // Resistor body
        gc.setFill(Color.web("#d35400"));
        gc.fillRoundRect(x, y - 10, width, 20, 8, 8);
        
        // Resistor bands (color bands)
        gc.setStroke(Color.web("#000"));
        gc.setLineWidth(4);
        gc.strokeLine(x + 15, y - 10, x + 15, y + 10);
        gc.strokeLine(x + 25, y - 10, x + 25, y + 10);
        gc.strokeLine(x + width - 20, y - 10, x + width - 20, y + 10);
        
        // Resistor wires
        gc.setStroke(Color.web("#c0c0c0"));
        gc.setLineWidth(4);
        gc.strokeLine(x - 15, y, x, y);
        gc.strokeLine(x + width, y, x + width + 15, y);
    }

    private void drawCurrentMarkers(GraphicsContext gc, double leftX, double rightX, 
                                     double topY, double bottomY, double bulbX) {
        gc.setFill(Color.web("#2b8aef"));

        double topLen1 = bulbX - leftX;
        double topLen2 = rightX - bulbX;
        double rightLen = bottomY - topY;
        double bottomLen = rightX - leftX;
        double leftLen = bottomY - topY;
        double totalLen = topLen1 + topLen2 + rightLen + bottomLen + leftLen;

        double chargeSpacing = 35;
        for (int i = 0; i < 10; i++) {
            double distance = (electronOffset + i * chargeSpacing) % totalLen;
            double x, y;

            if (distance < topLen1) {
                x = leftX + distance;
                y = topY - 9;
            } else if (distance < topLen1 + topLen2) {
                x = bulbX + (distance - topLen1);
                y = topY - 9;
            } else if (distance < topLen1 + topLen2 + rightLen) {
                x = rightX + 9;
                y = topY + (distance - topLen1 - topLen2);
            } else if (distance < topLen1 + topLen2 + rightLen + bottomLen) {
                x = rightX - (distance - topLen1 - topLen2 - rightLen);
                y = bottomY + 9;
            } else {
                x = leftX - 9;
                y = bottomY - (distance - topLen1 - topLen2 - rightLen - bottomLen);
            }

            gc.fillOval(x - 5, y - 5, 10, 10);
        }
    }

    private void drawCircuitValuesSchematic(GraphicsContext gc, double current, double power) {
        gc.setFill(Color.web("#000"));
        gc.setFont(Font.font("Arial", 12));

        double x = 50;
        double y = canvas.getHeight() - 20;

        String currentText = String.format("I = %.3f A", current);
        String powerText = String.format("P = %.2f W", power);
        String statusText = circuitClosed ? "Circuit: CLOSED" : "Circuit: OPEN";

        gc.fillText(currentText, x, y);
        gc.fillText(powerText, x + 180, y);
        gc.setFill(circuitClosed ? Color.web("#00AA00") : Color.web("#CC0000"));
        gc.fillText(statusText, x + 350, y);
    }
}
