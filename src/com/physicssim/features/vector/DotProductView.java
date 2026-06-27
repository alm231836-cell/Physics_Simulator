package com.physicssim.features.vector;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DotProductView extends BorderPane {

    private final Canvas vectorCanvas = new Canvas(560, 360);
    private final Slider firstMagnitude = new Slider(1, 8, 4);
    private final Slider firstAngle = new Slider(0, 360, 35);
    private final Slider secondMagnitude = new Slider(1, 8, 3);
    private final Slider secondAngle = new Slider(0, 360, 120);
    private final Label resultLabel = new Label();
    private final Label angleLabel = new Label();

    public DotProductView(Runnable onBack) {
        getStylesheets().add(getClass().getResource("/css/features/vector/VectorAddition.css").toExternalForm());
        setId("vector-root");
        setPadding(new Insets(12));
        setBackground(AppTheme.pageBackground());

        Button backButton = new Button("Back to overview");
        backButton.getStyleClass().add("vector-button");
        backButton.setOnAction(event -> onBack.run());

        Label title = new Label("Dot Product");
        title.setId("vector-title");

        Label subtitle = new Label("Compute the scalar product of two vectors and explore the angle between them.");
        subtitle.setId("vector-subtitle");
        subtitle.setWrapText(true);

        VBox introCard = new VBox(8, title, subtitle);
        introCard.getStyleClass().add("vector-card");

        VBox controlsCard = new VBox(12);
        controlsCard.getStyleClass().add("vector-card");
        controlsCard.setPadding(new Insets(18));

        Label controlsTitle = new Label("Vector controls");
        controlsTitle.setId("vector-card-title");

        controlsCard.getChildren().addAll(
                controlsTitle,
                buildSliderRow("Vector 1 magnitude", firstMagnitude),
                buildSliderRow("Vector 1 angle", firstAngle),
                buildSliderRow("Vector 2 magnitude", secondMagnitude),
                buildSliderRow("Vector 2 angle", secondAngle),
                resultLabel,
                angleLabel);

        VBox canvasCard = new VBox(12);
        canvasCard.getStyleClass().add("vector-card");
        canvasCard.setPadding(new Insets(18));

        Label canvasTitle = new Label("Dot product diagram");
        canvasTitle.setId("vector-card-title");
        vectorCanvas.getStyleClass().add("vector-canvas");
        canvasCard.getChildren().addAll(canvasTitle, vectorCanvas);

        HBox content = new HBox(18, controlsCard, canvasCard);
        content.setAlignment(Pos.TOP_LEFT);

        VBox root = new VBox(16, backButton, introCard, content);
        root.setPadding(new Insets(8));
        setCenter(root);

        bindListeners();
        updateDiagram();
    }

    private HBox buildSliderRow(String labelText, Slider slider) {
        Label label = new Label(labelText);
        label.setId("vector-control-label");
        Label valueLabel = new Label(String.format("%.0f", slider.getValue()));
        valueLabel.setId("vector-control-value");
        slider.valueProperty().addListener((obs, oldValue, newValue) -> valueLabel.setText(String.format("%.0f", newValue.doubleValue())));

        VBox sliderBox = new VBox(6, label, slider, valueLabel);
        sliderBox.setPadding(new Insets(4, 0, 4, 0));
        return new HBox(sliderBox);
    }

    private void bindListeners() {
        firstMagnitude.valueProperty().addListener((obs, oldValue, newValue) -> updateDiagram());
        firstAngle.valueProperty().addListener((obs, oldValue, newValue) -> updateDiagram());
        secondMagnitude.valueProperty().addListener((obs, oldValue, newValue) -> updateDiagram());
        secondAngle.valueProperty().addListener((obs, oldValue, newValue) -> updateDiagram());
    }

    private void updateDiagram() {
        double x1 = firstMagnitude.getValue() * Math.cos(Math.toRadians(firstAngle.getValue()));
        double y1 = firstMagnitude.getValue() * Math.sin(Math.toRadians(firstAngle.getValue()));
        double x2 = secondMagnitude.getValue() * Math.cos(Math.toRadians(secondAngle.getValue()));
        double y2 = secondMagnitude.getValue() * Math.sin(Math.toRadians(secondAngle.getValue()));

        double mag1 = Math.hypot(x1, y1);
        double mag2 = Math.hypot(x2, y2);
        double rawAngle = Math.abs(firstAngle.getValue() - secondAngle.getValue()) % 360;
        if (rawAngle > 180) {
            rawAngle = 360 - rawAngle;
        }
        double angle = mag1 > 0 && mag2 > 0 ? rawAngle : 0;
        double dot = mag1 > 0 && mag2 > 0 ? mag1 * mag2 * Math.cos(Math.toRadians(angle)) : 0;
        if (Math.abs(dot) < 1e-6) {
            dot = 0;
        }

        resultLabel.setText(String.format("Dot product: %.2f", dot));
        angleLabel.setText(String.format("Angle between: %.2f degrees", angle));
        drawDiagram(x1, y1, x2, y2, dot);
    }

    private void drawDiagram(double x1, double y1, double x2, double y2, double dot) {
        GraphicsContext gc = vectorCanvas.getGraphicsContext2D();
        double width = vectorCanvas.getWidth();
        double height = vectorCanvas.getHeight();

        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        gc.setStroke(Color.web("#d9e2ee"));
        gc.setLineWidth(1);
        gc.strokeLine(30, height / 2, width - 30, height / 2);
        gc.strokeLine(width / 2, 30, width / 2, height - 30);

        double originX = width / 2;
        double originY = height / 2;
        double firstEndX = originX + x1 * 35;
        double firstEndY = originY - y1 * 35;
        double secondEndX = originX + x2 * 35;
        double secondEndY = originY - y2 * 35;

        gc.setStroke(Color.web("#3157d5"));
        gc.setLineWidth(3);
        gc.strokeLine(originX, originY, firstEndX, firstEndY);
        gc.setFill(Color.web("#3157d5"));
        gc.fillOval(firstEndX - 5, firstEndY - 5, 10, 10);

        gc.setStroke(Color.web("#0ea5a4"));
        gc.setLineWidth(3);
        gc.strokeLine(originX, originY, secondEndX, secondEndY);
        gc.setFill(Color.web("#0ea5a4"));
        gc.fillOval(secondEndX - 5, secondEndY - 5, 10, 10);

        gc.setStroke(Color.web("#818cf8"));
        gc.setLineWidth(2);
        gc.strokeLine(firstEndX, firstEndY, secondEndX, secondEndY);

        gc.setFill(Color.web("#111827"));
        gc.fillText("Vector 1", firstEndX + 8, firstEndY - 8);
        gc.fillText("Vector 2", secondEndX + 8, secondEndY - 8);
        gc.fillText("Dot = " + String.format("%.2f", dot), originX + 8, originY - 8);
    }
}
