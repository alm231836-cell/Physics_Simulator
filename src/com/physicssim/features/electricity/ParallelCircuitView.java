package com.physicssim.features.electricity;
import com.physicssim.model.electricity.ParallelCircuitSimulation;
import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

public class ParallelCircuitView extends BorderPane {

    private final ParallelCircuitSimulation model = new ParallelCircuitSimulation();
    private final Canvas canvas = new Canvas(520, 220);
    private double electronOffset = 0;
    private final BooleanProperty keyClosedProperty = new SimpleBooleanProperty(true);
    private double keyX, keyY, keyW, keyH;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            electronOffset = (electronOffset + 1.8) % 1000;
            redraw();
        }
    };

    public ParallelCircuitView() {
        setPadding(new Insets(12));
        setStyle("-fx-background-color: transparent;");

        VBox controls = buildControls();
        setTop(controls);
        setCenter(canvas);

        timer.start();
        redraw();

        canvas.setOnMouseClicked(e -> {
            double mx = e.getX();
            double my = e.getY();
            if (mx >= keyX && mx <= keyX + keyW && my >= keyY && my <= keyY + keyH) {
                keyClosedProperty.set(!keyClosedProperty.get());
                redraw();
            }
        });
    }

    private VBox buildControls() {
        Label title = new Label("Parallel Circuit");
        title.setFont(AppTheme.cardTitleFont());

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

        Label resistorsLabel = new Label("Resistors (3 controls):");
        resistorsLabel.setFont(AppTheme.cardNumberFont());
        VBox resistorsBox = new VBox(8, resistorsLabel);
        DoubleProperty[] resistorValues = new DoubleProperty[model.getResistorCount()];

        for (int i = 0; i < model.getResistorCount(); i++) {
            final int idx = i;
            Slider rSlider = new Slider(0, 100, model.getResistance(i));
            rSlider.setShowTickMarks(false);
            rSlider.setShowTickLabels(false);
            resistorValues[i] = rSlider.valueProperty();
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
                double drop = model.getVoltageAcrossResistor(idx);
                return String.format("%.2f V", drop);
            }, rSlider.valueProperty(), vSlider.valueProperty()));

            Label currentLabel = new Label();
            currentLabel.setFont(AppTheme.cardNumberFont());
            currentLabel.textProperty().bind(Bindings.createStringBinding(() -> {
                if (!keyClosedProperty.get()) return "0.00 A";
                double current = model.getCurrentThrough(idx);
                if (Double.isInfinite(current)) return "∞ A";
                return String.format("%.2f A", current);
            }, rSlider.valueProperty(), vSlider.valueProperty(), keyClosedProperty));

            Label resistorTitle = new Label("R" + (i + 1) + ":");
            resistorTitle.setFont(AppTheme.cardNumberFont());
            HBox row = new HBox(8, resistorTitle, rMinus, rSlider, rPlus, rField, new Label("Ω"), dropLabel, new Label("I:"), currentLabel);
            row.setAlignment(Pos.CENTER_LEFT);
            resistorsBox.getChildren().add(row);
        }

        Label eqLabel = new Label();
        eqLabel.setFont(AppTheme.cardNumberFont());
        eqLabel.setWrapText(true);
        Observable[] eqDependencies = new Observable[resistorValues.length + 1];
        eqDependencies[0] = vSlider.valueProperty();
        System.arraycopy(resistorValues, 0, eqDependencies, 1, resistorValues.length);
        eqLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double r1 = model.getResistance(0);
            double r2 = model.getResistance(1);
            double r3 = model.getResistance(2);
            double inv1 = r1 <= 0 ? 0.0 : 1.0 / r1;
            double inv2 = r2 <= 0 ? 0.0 : 1.0 / r2;
            double inv3 = r3 <= 0 ? 0.0 : 1.0 / r3;
            String formula = String.format("1/R_eq = 1/R1 + 1/R2 + 1/R3 = %.4f + %.4f + %.4f", inv1, inv2, inv3);
            return String.format("%s = 1/%.2f = %.2f Ω", formula, model.getEquivalentResistance(), model.getEquivalentResistance());
        }, (Observable[]) eqDependencies));

        Observable[] totalDependencies = new Observable[resistorValues.length + 1];
        totalDependencies[0] = vSlider.valueProperty();
        System.arraycopy(resistorValues, 0, totalDependencies, 1, resistorValues.length);
        Label keyStateLabel = new Label();
        keyStateLabel.setFont(AppTheme.cardNumberFont());
        keyStateLabel.textProperty().bind(Bindings.createStringBinding(() -> keyClosedProperty.get() ? "Switch: Closed" : "Switch: Open", keyClosedProperty));

        Label totalLabel = new Label();
        totalLabel.setFont(AppTheme.cardNumberFont());
        Observable[] totalDepsWithKey = new Observable[totalDependencies.length + 1];
        System.arraycopy(totalDependencies, 0, totalDepsWithKey, 0, totalDependencies.length);
        totalDepsWithKey[totalDependencies.length] = keyClosedProperty;
        totalLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double req = model.getEquivalentResistance();
            String currentText = keyClosedProperty.get()
                ? (Double.isInfinite(model.getTotalCurrent()) ? "∞" : String.format("%.4f", model.getTotalCurrent()))
                : "0.00";
            return String.format("R_eq: %.2f Ω  |  I_total: %s A", req, currentText);
        }, totalDepsWithKey));

        VBox controls = new VBox(10, title, voltageBox, resistorsBox, keyStateLabel, eqLabel, totalLabel);
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

        double leftX = 120;
        double rightX = canvas.getWidth() - 80;
        double topY = 40;
        double bottomY = canvas.getHeight() - 50;

        g.setFill(Color.web("#f8fbff"));
        g.fillRoundRect(10, 10, canvas.getWidth() - 20, canvas.getHeight() - 20, 15, 15);

        // Draw main rails and battery on left
        g.setStroke(Color.web("#654321"));
        g.setLineWidth(4);
        g.strokeLine(leftX, topY, leftX, bottomY);
        g.setLineWidth(4);

        double keyWidth = 36;
        double keyHeight = 18;
        double keyCenterY = topY;
        keyX = leftX + 48;
        keyY = keyCenterY - keyHeight / 2;
        keyW = keyWidth;
        keyH = keyHeight;

        g.strokeLine(leftX, topY, leftX + 40, topY);
        drawKey(g, leftX + 40, keyCenterY, keyWidth, keyHeight, keyClosedProperty.get());
        double keyOutputX = leftX + 40 + keyWidth;
        g.strokeLine(keyOutputX, topY, rightX, topY);
        g.strokeLine(leftX, bottomY, rightX, bottomY);
        g.strokeLine(rightX, topY, rightX, bottomY);

        // Realistic battery
        double batteryX = leftX - 50;
        double batteryTopY = topY + 25;
        double batteryBottomY = bottomY - 25;
        drawRealisticBattery(g, batteryX, batteryTopY, batteryBottomY, model.getVoltage());
        g.strokeLine(leftX, topY, batteryX + 17, batteryTopY - 8);
        g.strokeLine(leftX, bottomY, batteryX + 17, batteryBottomY);

        double branchTrackTop = topY + 24;
        double branchTrackBottom = bottomY - 24;
        double branchGap = (rightX - leftX - 160) / Math.max(1, model.getResistorCount() - 1);

        for (int i = 0; i < model.getResistorCount(); i++) {
            double branchX = leftX + 90 + i * branchGap;
            double branchTop = branchTrackTop;
            double branchBottom = branchTrackBottom;
            double branchHeight = branchBottom - branchTop;

            g.setLineWidth(3);
            g.strokeLine(branchX, topY, branchX, branchTop);
            drawZigzagResistorVertical(g, branchX, branchTop, branchHeight);
            g.strokeLine(branchX, branchBottom, branchX, bottomY);

            // branch labels
            g.setFill(Color.web("#000"));
            g.setFont(Font.font("Arial", 12));
            g.fillText(String.format("R%d = %.1fΩ", i + 1, model.getResistance(i)), branchX + 12, branchTop + 20);
            String branchCurrentText = keyClosedProperty.get()
                ? String.format("I%d = %.2f A", i + 1, model.getCurrentThrough(i))
                : String.format("I%d = 0.00 A", i + 1);
            g.fillText(branchCurrentText, branchX + 12, branchBottom - 10);

            if (keyClosedProperty.get()) {
                drawBranchCurrentMarkers(g, branchX, branchTop, branchBottom, i);
            }

            // direction arrow for current down branch
            g.setFill(Color.web("#333"));
            double arrowY = branchTop + branchHeight / 2;
            g.fillPolygon(new double[]{branchX - 6, branchX + 6, branchX}, new double[]{arrowY - 8, arrowY - 8, arrowY + 4}, 3);
        }

        // node dots at junctions
        g.setFill(Color.web("#333"));
        g.fillOval(leftX - 4, topY - 4, 8, 8);
        g.fillOval(leftX - 4, bottomY - 4, 8, 8);
        g.fillOval(rightX - 4, topY - 4, 8, 8);
        g.fillOval(rightX - 4, bottomY - 4, 8, 8);

        g.setFont(Font.font("Arial", 12));
        g.setFill(Color.web("#333"));
    }

    private void drawZigzagResistorVertical(GraphicsContext g, double x, double y, double height) {
        double segment = height / 8.0;
        double amplitude = 12;
        double currentY = y;
        for (int i = 0; i < 8; i++) {
            double nextY = currentY + segment;
            double nextX = x + (i % 2 == 0 ? amplitude : -amplitude);
            g.strokeLine(x, currentY, nextX, nextY);
            currentY = nextY;
            g.strokeLine(nextX, nextY, x, nextY);
        }
    }

    private void drawBranchCurrentMarkers(GraphicsContext g, double x, double top, double bottom, int branchIndex) {
        g.setFill(Color.web("#2b8aef"));
        double branchHeight = bottom - top;
        int count = 4;
        for (int i = 0; i < count; i++) {
            double offset = ((electronOffset + branchIndex * 18 + i * 24) % branchHeight);
            double y = top + offset;
            g.fillOval(x - 5, y - 4, 8, 8);
        }
    }

    private void drawKey(GraphicsContext g, double keyX, double keyY, double keyW, double keyH, boolean closed) {
        g.setStroke(Color.web("#333"));
        g.setLineWidth(3);
        g.strokeLine(keyX - 32, keyY, keyX - 16, keyY);
        g.strokeLine(keyX + keyW + 16, keyY, keyX + keyW + 32, keyY);

        if (closed) {
            g.setStroke(Color.web("#333"));
            g.strokeLine(keyX - 16, keyY, keyX + keyW + 16, keyY);
            g.setFill(Color.web("#2b8aef"));
            g.fillOval(keyX + keyW - 12, keyY - 6, 12, 12);
        } else {
            g.setStroke(Color.web("#333"));
            g.strokeLine(keyX - 16, keyY, keyX + 4, keyY);
            g.strokeLine(keyX + keyW + 12, keyY, keyX + keyW + 32, keyY);
            double[] xPoints = {keyX + 4, keyX + keyW + 12, keyX + keyW + 4};
            double[] yPoints = {keyY, keyY - 12, keyY - 12};
            g.strokePolygon(xPoints, yPoints, xPoints.length);
            g.setFill(Color.web("#c33"));
            g.fillOval(keyX + keyW - 12, keyY - 10, 12, 12);
        }

        g.setFill(Color.web("#000"));
        g.setFont(Font.font("Arial", 12));
        g.fillText("Key", keyX + keyW / 2 - 10, keyY - 10);
    }
}
