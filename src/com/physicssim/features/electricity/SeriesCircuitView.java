package com.physicssim.features.electricity;

import com.physicssim.model.electricity.SeriesCircuitSimulation;
import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Series circuit simulation view (MVP): 3 resistors in series, adjustable values,
 * computed totals and simple animated charge markers.
 */
public class SeriesCircuitView extends BorderPane {

    private final SeriesCircuitSimulation model = new SeriesCircuitSimulation();
    private final Canvas canvas = new Canvas(520, 220);
    private double electronOffset = 0;
    // key hit area (computed in redraw)
    private double keyX, keyY, keyW, keyH;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            double speed = 0.0;
            if (model.isClosed()) {
                speed = Math.max(0.5, Math.min(6.0, model.getCurrent() * 8.0));
            }
            electronOffset = (electronOffset + speed) % 1000;
            redraw();
        }
    };

    public SeriesCircuitView() {
        setPadding(new Insets(12));
        setStyle("-fx-background-color: transparent;");

        VBox controls = buildControls();
        setTop(controls);
        setCenter(canvas);

        timer.start();
        redraw();
        // toggle key by clicking on the canvas key area
        canvas.setOnMouseClicked(e -> {
            double mx = e.getX();
            double my = e.getY();
            if (mx >= keyX && mx <= keyX + keyW && my >= keyY && my <= keyY + keyH) {
                model.setClosed(!model.isClosed());
                redraw();
            }
        });
    }

    private VBox buildControls() {
        Label title = new Label("Series Circuit");
        title.setFont(AppTheme.cardTitleFont());

        // Voltage control
        Slider vSlider = new Slider(0, 24, model.getVoltage());
        vSlider.setShowTickMarks(true);
        vSlider.setShowTickLabels(true);
        TextField vField = new TextField(String.format("%.2f", model.getVoltage()));
        vField.setMaxWidth(80);
        Button vMinus = new Button("Voltage -");
        vMinus.setTooltip(new Tooltip("Decrease voltage"));
        vMinus.setAccessibleText("Decrease voltage");
        Button vPlus = new Button("Voltage +");
        vPlus.setTooltip(new Tooltip("Increase voltage"));
        vPlus.setAccessibleText("Increase voltage");
        vMinus.setOnAction(e -> vSlider.setValue(Math.max(vSlider.getMin(), Math.round((vSlider.getValue() - 0.1) * 100.0) / 100.0)));
        vPlus.setOnAction(e -> vSlider.setValue(Math.min(vSlider.getMax(), Math.round((vSlider.getValue() + 0.1) * 100.0) / 100.0)));

        vSlider.valueProperty().addListener((s, o, n) -> {
            model.setVoltage(n.doubleValue());
            vField.setText(String.format("%.2f", model.getVoltage()));
            redraw();
        });
        vField.setOnAction(e -> {
            try { vSlider.setValue(Double.parseDouble(vField.getText())); } catch (NumberFormatException ex) { vField.setText(String.format("%.2f", model.getVoltage())); }
        });

        Label voltageLabel = new Label("Voltage source (1 control):");
        voltageLabel.setFont(AppTheme.cardNumberFont());
        HBox voltageBox = new HBox(8, voltageLabel, vMinus, vSlider, vPlus, vField, new Label("V"));
        voltageBox.setAlignment(Pos.CENTER_LEFT);

        // Resistor controls (3 resistors)
        Label resistorsLabel = new Label("Resistors (3 controls):");
        resistorsLabel.setFont(AppTheme.cardNumberFont());
        VBox resistorsBox = new VBox(8, resistorsLabel);
        DoubleProperty[] resistorProperties = new DoubleProperty[model.getResistorCount()];
        for (int i = 0; i < model.getResistorCount(); i++) {
            final int idx = i;
            Slider rSlider = new Slider(0, 100, model.getResistance(i));
            rSlider.setShowTickMarks(false);
            rSlider.setShowTickLabels(false);
            resistorProperties[i] = rSlider.valueProperty();
            TextField rField = new TextField(String.format("%.2f", model.getResistance(i)));
            rField.setMaxWidth(80);
            Button rMinus = new Button("R" + (i + 1) + " -");
            rMinus.setTooltip(new Tooltip(String.format("Decrease resistance for R%d", i + 1)));
            rMinus.setAccessibleText(String.format("Decrease resistance for R%d", i + 1));
            Button rPlus = new Button("R" + (i + 1) + " +");
            rPlus.setTooltip(new Tooltip(String.format("Increase resistance for R%d", i + 1)));
            rPlus.setAccessibleText(String.format("Increase resistance for R%d", i + 1));
            rMinus.setOnAction(e -> rSlider.setValue(Math.max(rSlider.getMin(), Math.round((rSlider.getValue() - 1.0) * 100.0) / 100.0)));
            rPlus.setOnAction(e -> rSlider.setValue(Math.min(rSlider.getMax(), Math.round((rSlider.getValue() + 1.0) * 100.0) / 100.0)));

            rSlider.valueProperty().addListener((s, o, n) -> {
                model.setResistance(idx, n.doubleValue());
                rField.setText(String.format("%.2f", model.getResistance(idx)));
                redraw();
            });
            rField.setOnAction(e -> {
                try { rSlider.setValue(Double.parseDouble(rField.getText())); } catch (NumberFormatException ex) { rField.setText(String.format("%.2f", model.getResistance(idx))); }
            });

            Label dropLabel = new Label();
            dropLabel.setFont(AppTheme.cardNumberFont());
            dropLabel.textProperty().bind(Bindings.createStringBinding(() -> {
                double drop = model.getVoltageDrop(idx);
                if (Double.isInfinite(drop)) return "∞ V";
                return String.format("%.2f V", drop);
            }, rSlider.valueProperty(), vSlider.valueProperty()));

            Label resistorTitle = new Label("R" + (i+1) + ":");
            resistorTitle.setFont(AppTheme.cardNumberFont());
            HBox row = new HBox(8, resistorTitle, rMinus, rSlider, rPlus, rField, new Label("Ω"), dropLabel);
            row.setAlignment(Pos.CENTER_LEFT);
            resistorsBox.getChildren().add(row);
        }

        Label eqLabel = new Label("R_eq = R1 + R2 + R3 = 10.00 + 10.00 + 10.00 = 30.00 Ω");
        eqLabel.setFont(AppTheme.cardNumberFont());
        eqLabel.setWrapText(true);
        eqLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            String formula = String.format("R_eq = R1 + R2 + R3 = %.2f + %.2f + %.2f", model.getResistance(0), model.getResistance(1), model.getResistance(2));
            return String.format("%s = %.2f Ω", formula, model.getTotalResistance());
        }, resistorProperties));

        Label totalLabel = new Label();
        totalLabel.setFont(AppTheme.cardNumberFont());
        DoubleProperty[] totalDependencies = new DoubleProperty[resistorProperties.length + 1];
        System.arraycopy(resistorProperties, 0, totalDependencies, 0, resistorProperties.length);
        totalDependencies[resistorProperties.length] = vSlider.valueProperty();
        totalLabel.textProperty().bind(Bindings.createStringBinding(() ->
            String.format("R_total: %.2f Ω  |  I: %s A",
                model.getTotalResistance(), Double.isInfinite(model.getCurrent()) ? "∞" : String.format("%.4f", model.getCurrent())),
            totalDependencies));

        VBox controls = new VBox(10, title, voltageBox, resistorsBox, eqLabel, totalLabel);
        controls.setPadding(new Insets(8));
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
        
        double leftX = 120;
        double rightX = canvas.getWidth() - 80;
        double topY = 40;
        double bottomY = canvas.getHeight() - 48;

        g.setStroke(Color.web("#654321"));
        g.setLineWidth(4);

        // Realistic battery
        double batteryX = leftX - 50;
        double batteryTopY = topY + 25;
        double batteryBottomY = bottomY - 25;
        drawRealisticBattery(g, batteryX, batteryTopY, batteryBottomY, model.getVoltage());
        g.strokeLine(leftX, topY, batteryX + 17, batteryTopY - 8);
        g.strokeLine(leftX, bottomY, batteryX + 17, batteryBottomY);
        g.strokeLine(leftX, bottomY, leftX, topY);

        // resistors along top wire
        int n = model.getResistorCount();
        double keyWidth = 32;
        double keyHeight = 18;
        double keyGap = 10;
        double spacing = (rightX - leftX - keyWidth - 48) / n;
        double currentX = leftX;
        g.setLineWidth(3);
        for (int i = 0; i < n; i++) {
            double startX = currentX + 12;
            double endX = startX + spacing - 6;
            g.strokeLine(currentX, topY, startX, topY);
            drawZigzagResistor(g, startX, topY, endX - startX);
            currentX = endX;
            g.strokeLine(currentX, topY, currentX + 6, topY);
            g.setFill(Color.web("#000"));
            g.fillText(String.format("R%d: %.1fΩ", i + 1, model.getResistance(i)), startX + 4, topY - 12);
            g.fillText(String.format("V: %.2fV", model.getVoltageDrop(i)), startX + 4, topY + 30);
        }

        // top wire from last resistor to key
        double kx = currentX + 16;
        double ky = topY - keyHeight / 2;
        keyX = kx;
        keyY = ky;
        keyW = keyWidth;
        keyH = keyHeight;
        g.strokeLine(currentX + 6, topY, kx - 18, topY);
        if (model.isClosed()) {
            drawClosedKey(g, kx, topY);
            g.strokeLine(kx + 18, topY, rightX, topY);
        } else {
            drawOpenKey(g, kx, topY);
            g.strokeLine(kx + 22, topY, rightX, topY);
        }
        g.setFill(Color.web("#000"));
        g.setFont(AppTheme.cardNumberFont());
        g.fillText("Key", kx + 2, ky - 4);

        // right and bottom and left wires
        g.strokeLine(rightX, topY, rightX, bottomY);
        g.strokeLine(rightX, bottomY, leftX, bottomY);

        // connection nodes
        g.setFill(Color.web("#333"));
        double[] nodeXs = {leftX, rightX, rightX, leftX};
        double[] nodeYs = {topY, topY, bottomY, bottomY};
        for (int j = 0; j < nodeXs.length; j++) {
            g.fillOval(nodeXs[j] - 4, nodeYs[j] - 4, 8, 8);
        }

        if (model.isClosed()) {
            // moving charges around full loop
            g.setFill(Color.web("#2b8aef"));
            double topLen = rightX - leftX;
            double rightLen = bottomY - topY;
            double bottomLen = topLen;
            double leftLen = rightLen;
            double loopLength = topLen + rightLen + bottomLen + leftLen;
            double chargeSpacing = 36;
            for (int i = 0; i < 10; i++) {
                double distance = (electronOffset + i * chargeSpacing) % loopLength;
                double x, y;
                if (distance < topLen) {
                    x = leftX + distance;
                    y = topY - 8;
                } else if (distance < topLen + rightLen) {
                    x = rightX + 8;
                    y = topY + (distance - topLen);
                } else if (distance < topLen + rightLen + bottomLen) {
                    x = rightX - (distance - topLen - rightLen);
                    y = bottomY + 8;
                } else {
                    x = leftX - 8;
                    y = bottomY - (distance - topLen - rightLen - bottomLen);
                }
                g.fillOval(x - 4, y - 4, 8, 8);
            }
        }
    }

    private void drawZigzagResistor(GraphicsContext g, double x, double y, double width) {
        double segment = width / 8.0;
        double amplitude = 12;
        double currentX = x;
        for (int i = 0; i < 8; i++) {
            double nextX = currentX + segment;
            double nextY = y + (i % 2 == 0 ? -amplitude : amplitude);
            g.strokeLine(currentX, y, nextX, nextY);
            currentX = nextX;
            y = nextY;
        }
        g.strokeLine(currentX, y, x + width, y);
    }

    private void drawClosedKey(GraphicsContext g, double keyX, double keyY) {
        g.setStroke(Color.web("#333"));
        g.setLineWidth(3);
        g.strokeLine(keyX - 16, keyY, keyX - 10, keyY);
        g.strokeLine(keyX + 10, keyY, keyX + 16, keyY);
        double[] xPoints = {keyX - 10, keyX + 2, keyX + 8, keyX + 8, keyX + 2};
        double[] yPoints = {keyY, keyY - 6, keyY - 4, keyY + 4, keyY + 6};
        g.setFill(Color.web("#ddd"));
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        g.strokePolygon(xPoints, yPoints, xPoints.length);
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
        g.setFill(Color.web("#333"));
        g.fillOval(keyX - 14, keyY - 4, 8, 8);
        g.setFill(Color.web("#c33"));
        g.fillOval(keyX + 6, keyY - 14, 8, 8);
    }
}

