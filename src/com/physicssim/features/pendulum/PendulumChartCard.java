package com.physicssim.features.pendulum;

import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PendulumChartCard extends VBox {

    private static final double CARD_WIDTH = 255;
    private static final double CARD_HEIGHT = 179;
    private static final double PLOT_LEFT = 44;
    private static final double PLOT_TOP = 14;
    private static final double PLOT_RIGHT = 17;
    private static final double PLOT_BOTTOM = 36;

    private final Canvas canvas = new Canvas(CARD_WIDTH, CARD_HEIGHT);
    private final String yAxisLabel;
    private final String xAxisLabel;

    public PendulumChartCard(String title, String yAxisLabel, String xAxisLabel) {
        this.yAxisLabel = yAxisLabel;
        this.xAxisLabel = xAxisLabel;

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 700;");

        getChildren().addAll(titleLabel, canvas);
        setSpacing(10);
        setPadding(new Insets(15));
        setBackground(new Background(new BackgroundFill(Color.web("#f8fbff"), new CornerRadii(18), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(
                Color.web("#d9e2ee"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(18),
                new BorderWidths(1))));
        setStyle("-fx-effect: dropshadow(gaussian, rgba(15, 23, 32, 0.08), 16, 0.18, 0, 6);"
                + "-fx-background-radius: 18;"
                + "-fx-border-radius: 18;");
    }

    public void plot(List<Double> values, Color lineColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double plotWidth = width - PLOT_LEFT - PLOT_RIGHT;
        double plotHeight = height - PLOT_TOP - PLOT_BOTTOM;

        gc.setFill(Color.web("#f8fbff"));
        gc.fillRoundRect(0, 0, width, height, 14, 14);

        gc.setFill(Color.web("#334155"));
        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(1);
        gc.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 9.5));

        drawAxes(gc, width, height, plotWidth, plotHeight);

        if (values.isEmpty()) {
            gc.setFill(Color.web("#64748b"));
            gc.fillText("Run the simulation to see live data", PLOT_LEFT, height - 16);
            return;
        }

        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(-1);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        if (Math.abs(max - min) < 1e-6) {
            max += 1;
            min -= 1;
        }
        double range = max - min;

        drawGridAndScale(gc, min, max, width, plotHeight, plotWidth, height);

        if (values.size() < 2) {
            double midY = PLOT_TOP + plotHeight / 2.0;
            gc.setStroke(lineColor);
            gc.setLineWidth(3);
            gc.strokeLine(PLOT_LEFT, midY, PLOT_LEFT + plotWidth * 0.12, midY);
            return;
        }

        gc.setStroke(lineColor);
        gc.setLineWidth(2.5);
        for (int index = 1; index < values.size(); index++) {
            double x1 = PLOT_LEFT + ((index - 1) / (double) (values.size() - 1)) * plotWidth;
            double x2 = PLOT_LEFT + (index / (double) (values.size() - 1)) * plotWidth;
            double y1 = PLOT_TOP + plotHeight - ((values.get(index - 1) - min) / range) * plotHeight;
            double y2 = PLOT_TOP + plotHeight - ((values.get(index) - min) / range) * plotHeight;
            gc.strokeLine(x1, y1, x2, y2);
        }

        double lastX = PLOT_LEFT + plotWidth;
        double lastY = PLOT_TOP + plotHeight - ((values.get(values.size() - 1) - min) / range) * plotHeight;
        gc.setFill(lineColor);
        gc.fillOval(lastX - 4, lastY - 4, 8, 8);
    }

    private void drawAxes(GraphicsContext gc, double width, double height, double plotWidth, double plotHeight) {
        gc.setStroke(Color.web("#94a3b8"));
        gc.strokeLine(PLOT_LEFT, PLOT_TOP, PLOT_LEFT, PLOT_TOP + plotHeight);
        gc.strokeLine(PLOT_LEFT, PLOT_TOP + plotHeight, width - PLOT_RIGHT, PLOT_TOP + plotHeight);

        gc.setFill(Color.web("#0f172a"));
        gc.fillText(yAxisLabel, 14, PLOT_TOP + 10);
        gc.fillText(xAxisLabel, PLOT_LEFT + plotWidth / 2.0 - 18, height - 12);
    }

    private void drawGridAndScale(
            GraphicsContext gc,
            double min,
            double max,
            double width,
            double plotHeight,
            double plotWidth,
            double height) {
        gc.setStroke(Color.web("#dce6f2"));
        gc.setLineWidth(1);

        for (int step = 0; step <= 4; step++) {
            double x = PLOT_LEFT + (step / 4.0) * plotWidth;
            gc.strokeLine(x, PLOT_TOP, x, PLOT_TOP + plotHeight);
        }

        for (int step = 0; step <= 4; step++) {
            double y = PLOT_TOP + (step / 4.0) * plotHeight;
            gc.strokeLine(PLOT_LEFT, y, width - PLOT_RIGHT, y);

            double value = max - (step / 4.0) * (max - min);
            gc.setFill(Color.web("#475569"));
            gc.fillText(String.format("%.1f", value), 10, y + 4);
        }

        gc.setFill(Color.web("#475569"));
        gc.fillText("0", PLOT_LEFT - 2, height - 16);
        gc.fillText("recent", width - PLOT_RIGHT - 32, height - 16);
    }
}
