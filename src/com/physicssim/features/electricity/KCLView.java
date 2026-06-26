package com.physicssim.features.electricity;

import com.physicssim.theme.AppTheme;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class KCLView extends BorderPane {

    private final Canvas canvas = new Canvas(520, 260);
    private final Slider i1Slider = new Slider(0, 6, 2.0);
    private final Slider i2Slider = new Slider(0, 6, 1.5);
    private final Slider i3Slider = new Slider(0, 6, 2.2);

    public KCLView() {
        setPadding(new Insets(12));
        setStyle("-fx-background-color: transparent;");

        VBox controls = buildControls();
        setTop(controls);
        setCenter(canvas);

        i1Slider.valueProperty().addListener((s, o, n) -> redraw());
        i2Slider.valueProperty().addListener((s, o, n) -> redraw());
        i3Slider.valueProperty().addListener((s, o, n) -> redraw());

        redraw();
    }

    private VBox buildControls() {
        Label title = new Label("Kirchhoff's Current Law");
        title.setFont(AppTheme.cardTitleFont());

        Label description = new Label(
                "KCL states that the total current entering a junction equals the total current leaving it. " +
                "In this example, I1, I2 and I3 flow into the node and I_total flows out.");
        description.setFont(AppTheme.cardNumberFont());
        description.setWrapText(true);
        description.setMaxWidth(520);

        Label theory = new Label("I1 + I2 + I3 = I_total");
        theory.setFont(AppTheme.subtitleFont());

        Label currentSummary = new Label();
        currentSummary.setFont(AppTheme.cardNumberFont());
        Label balanceLabel = new Label();
        balanceLabel.setFont(AppTheme.cardNumberFont());
        DoubleProperty[] deps = new DoubleProperty[]{i1Slider.valueProperty(), i2Slider.valueProperty(), i3Slider.valueProperty()};
        currentSummary.textProperty().bind(Bindings.createStringBinding(() -> {
            double i1 = i1Slider.getValue();
            double i2 = i2Slider.getValue();
            double i3 = i3Slider.getValue();
            double total = i1 + i2 + i3;
            return String.format("Incoming currents: I1=%.2f A, I2=%.2f A, I3=%.2f A   →   Outgoing I_total=%.2f A", i1, i2, i3, total);
        }, deps));
        balanceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double i1 = i1Slider.getValue();
            double i2 = i2Slider.getValue();
            double i3 = i3Slider.getValue();
            double total = i1 + i2 + i3;
            return String.format("%.2f + %.2f + %.2f = %.2f A", i1, i2, i3, total);
        }, deps));

        HBox i1Row = buildSliderRow("I1", i1Slider);
        HBox i2Row = buildSliderRow("I2", i2Slider);
        HBox i3Row = buildSliderRow("I3", i3Slider);

        VBox controls = new VBox(10, title, description, theory, currentSummary, balanceLabel, i1Row, i2Row, i3Row);
        controls.setPadding(new Insets(20));
        controls.setBackground(AppTheme.surfaceBackground());
        controls.setBorder(AppTheme.cardBorder());
        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    private HBox buildSliderRow(String labelText, Slider slider) {
        Label label = new Label(labelText + ":");
        label.setFont(AppTheme.cardNumberFont());
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(2);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(0.1);
        slider.setPrefWidth(260);

        Button minus = new Button("-");
        minus.setFocusTraversable(false);
        minus.setOnAction(ev -> slider.setValue(Math.max(slider.getMin(), Math.round((slider.getValue() - 0.1) * 100.0) / 100.0)));
        Button plus = new Button("+");
        plus.setFocusTraversable(false);
        plus.setOnAction(ev -> slider.setValue(Math.min(slider.getMax(), Math.round((slider.getValue() + 0.1) * 100.0) / 100.0)));
        Label adjustLabel = new Label("Adjust:");
        adjustLabel.setFont(AppTheme.cardNumberFont());
        minus.setTooltip(new Tooltip("Decrease " + labelText));
        minus.setAccessibleText("Decrease " + labelText);
        plus.setTooltip(new Tooltip("Increase " + labelText));
        plus.setAccessibleText("Increase " + labelText);

        Label valueLabel = new Label();
        valueLabel.setFont(AppTheme.cardNumberFont());
        valueLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%.2f A", slider.getValue()),
                slider.valueProperty()));

        HBox row = new HBox(8, label, adjustLabel, minus, slider, plus, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void redraw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Background
        g.setFill(AppTheme.SURFACE);
        g.fillRoundRect(10, 10, canvas.getWidth() - 20, canvas.getHeight() - 20, 15, 15);

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2 - 10;
        double nodeRadius = 20;

        // Compact legend placed at top-center to avoid overlapping branch labels
        double legendW = 440;
        double legendH = 30;
        double legendX = centerX - legendW / 2;
        double legendY = 12;
        g.setFill(Color.web("#ffffff"));
        g.fillRoundRect(legendX, legendY, legendW, legendH, 8, 8);
        g.setStroke(Color.web("#cbd5e1"));
        g.setLineWidth(2);
        g.strokeRoundRect(legendX, legendY, legendW, legendH, 8, 8);

        g.setFill(Color.web("#000"));
        g.setFont(Font.font("Arial", 13));
        double lineX = legendX + 12;
        double lineY = legendY + 20;
        String legendText = String.format("I1\u2190=%.2f A   I2\u2191=%.2f A   I3\u2193=%.2f A   I_total\u2192=%.2f A",
            i1Slider.getValue(), i2Slider.getValue(), i3Slider.getValue(), i1Slider.getValue() + i2Slider.getValue() + i3Slider.getValue());
        g.fillText(legendText, lineX, lineY);

        // Left branch
        double leftX = 80;
        drawBranch(g, leftX, centerY, centerX - nodeRadius, centerY, i1Slider.getValue(), "I1 in");
        
        // Top branch
        double topY = 40;
        drawBranch(g, centerX, topY, centerX, centerY - nodeRadius, i2Slider.getValue(), "I2 in");
        
        // Bottom branch
        double bottomY = canvas.getHeight() - 40;
        drawBranch(g, centerX, bottomY, centerX, centerY + nodeRadius, i3Slider.getValue(), "I3 in");
        
        // Right outgoing total
        double rightX = canvas.getWidth() - 80;
        double totalCurrent = i1Slider.getValue() + i2Slider.getValue() + i3Slider.getValue();
        drawBranch(g, centerX + nodeRadius, centerY, rightX, centerY, totalCurrent, "I_total out");

        // Realistic node
        g.setFill(Color.web("#333333"));
        g.fillOval(centerX - nodeRadius, centerY - nodeRadius, nodeRadius * 2, nodeRadius * 2);
        g.setFill(Color.web("#555555"));
        g.fillOval(centerX - nodeRadius + 4, centerY - nodeRadius + 4, nodeRadius * 2 - 8, nodeRadius * 2 - 8);
        g.setFill(Color.web("#ffffff"));
        g.setFont(Font.font("Arial", 14));
        g.fillText("Node", centerX - 18, centerY + 5);

        g.setFill(Color.web("#000000"));
        g.setFont(Font.font("Arial", 12));
        g.fillText("Kirchhoff's Current Law visualization", 18, canvas.getHeight() - 12);
    }
    
    private void drawBranch(GraphicsContext g, double x1, double y1, double x2, double y2, double current, String label) {
        // Branch color based on current magnitude (redder for higher current)
        double intensity = Math.min(current / 6.0, 1.0);
        Color wireColor = Color.color(0.4 + intensity * 0.4, 0.27 - intensity * 0.1, 0.13);
        
        g.setStroke(wireColor);
        g.setLineWidth(4 + current * 0.8);
        g.strokeLine(x1, y1, x2, y2);
        
        // Draw arrow
        drawArrow(g, x1, y1, x2, y2);
        
        // Draw label
        g.setFill(Color.web("#000000"));
        g.setFont(Font.font("Arial", 12));
        double labelX, labelY;
        if (x1 == x2) { // Vertical branch
            labelX = x1 + 12;
            labelY = (y1 + y2) / 2;
            if (y1 < y2) { // Downward
                labelY += 20;
            } else { // Upward
                labelY -= 20;
            }
        } else { // Horizontal branch
            labelX = (x1 + x2) / 2;
            labelY = y1 - 20;
        }
        g.fillText(label, labelX - 25, labelY);
        g.fillText(String.format("%.2f A", current), labelX - 25, labelY + 16);
    }

    private void drawArrow(GraphicsContext g, double x1, double y1, double x2, double y2) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        double arrowLength = 10;
        double arrowAngle = Math.PI / 6;
        double x3 = x2 - arrowLength * Math.cos(theta - arrowAngle);
        double y3 = y2 - arrowLength * Math.sin(theta - arrowAngle);
        double x4 = x2 - arrowLength * Math.cos(theta + arrowAngle);
        double y4 = y2 - arrowLength * Math.sin(theta + arrowAngle);
        g.strokeLine(x1, y1, x2, y2);
        g.strokeLine(x2, y2, x3, y3);
        g.strokeLine(x2, y2, x4, y4);
    }
}
