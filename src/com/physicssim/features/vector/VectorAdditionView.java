package com.physicssim.features.vector;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class VectorAdditionView extends BorderPane {

    private final BorderPane contentHost = new BorderPane();
    private final Canvas vectorCanvas = new Canvas(560, 360);
    private final Slider firstMagnitude = new Slider(1, 8, 4);
    private final Slider firstAngle = new Slider(0, 360, 35);
    private final Slider secondMagnitude = new Slider(1, 8, 3);
    private final Slider secondAngle = new Slider(0, 360, 120);
    private final Label resultLabel = new Label();
    private final Label directionLabel = new Label();

    public VectorAdditionView() {
        getStylesheets().add(getClass().getResource("/css/features/vector/VectorAddition.css").toExternalForm());
        setId("vector-root");
        setPadding(new Insets(12));
        setBackground(AppTheme.pageBackground());
        setCenter(contentHost);
        showHub();
        bindListeners();
        updateDiagram();
    }

    private void showHub() {
        VBox hub = new VBox(18);
        hub.setPadding(new Insets(8));

        Label title = new Label("Vector Addition");
        title.setId("vector-title");

        Label subtitle = new Label("Explore how two vectors combine to make a resultant vector.");
        subtitle.setId("vector-subtitle");
        subtitle.setWrapText(true);

        VBox introCard = new VBox(8, title, subtitle);
        introCard.getStyleClass().add("vector-card");

        VBox toolCard = new VBox(10);
        toolCard.getStyleClass().add("vector-card");
        toolCard.setPrefSize(320, 180);
        toolCard.setCursor(Cursor.HAND);
        toolCard.setOnMouseClicked(event -> showInteractiveView());

        Label toolTitle = new Label("Interactive addition");
        toolTitle.setId("vector-card-title");

        Label toolDescription = new Label("Adjust the two vectors and watch the resultant appear on the diagram.");
        toolDescription.setId("vector-card-description");
        toolDescription.setWrapText(true);

        Button openButton = new Button("Open tool");
        openButton.getStyleClass().add("vector-button");
        openButton.setOnAction(event -> showInteractiveView());

        toolCard.getChildren().addAll(toolTitle, toolDescription, openButton);

        HBox cards = new HBox(18, introCard, toolCard);
        cards.setAlignment(Pos.TOP_LEFT);
        hub.getChildren().add(cards);
        contentHost.setCenter(hub);
    }

    private void showInteractiveView() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(8));

        Button backButton = new Button("Back to overview");
        backButton.getStyleClass().add("vector-button");
        backButton.setOnAction(event -> showHub());

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
                directionLabel);

        VBox canvasCard = new VBox(12);
        canvasCard.getStyleClass().add("vector-card");
        canvasCard.setPadding(new Insets(18));

        Label canvasTitle = new Label("Resultant vector");
        canvasTitle.setId("vector-card-title");
        vectorCanvas.getStyleClass().add("vector-canvas");
        canvasCard.getChildren().addAll(canvasTitle, vectorCanvas);

        HBox content = new HBox(18, controlsCard, canvasCard);
        content.setAlignment(Pos.TOP_LEFT);

        page.getChildren().addAll(backButton, content);
        contentHost.setCenter(page);
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

        double rx = x1 + x2;
        double ry = y1 + y2;
        double magnitude = Math.hypot(rx, ry);
        double directionDegrees = Math.toDegrees(Math.atan2(ry, rx));

        resultLabel.setText(String.format("Magnitude: %.2f   Resultant: (%.2f, %.2f)", magnitude, rx, ry));
        directionLabel.setText(String.format("Triangle-law direction: %.2f° from +x", directionDegrees));
        drawDiagram(x1, y1, x2, y2, rx, ry, directionDegrees);
    }

    private void drawDiagram(double x1, double y1, double x2, double y2, double rx, double ry, double directionDegrees) {
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
        double secondEndX = firstEndX + x2 * 35;
        double secondEndY = firstEndY - y2 * 35;
        double resultantEndX = originX + rx * 35;
        double resultantEndY = originY - ry * 35;

        gc.setStroke(Color.web("#3157d5"));
        gc.setLineWidth(3);
        gc.strokeLine(originX, originY, firstEndX, firstEndY);
        gc.setFill(Color.web("#3157d5"));
        gc.fillOval(firstEndX - 5, firstEndY - 5, 10, 10);

        gc.setStroke(Color.web("#0ea5a4"));
        gc.setLineWidth(3);
        gc.strokeLine(firstEndX, firstEndY, secondEndX, secondEndY);
        gc.setFill(Color.web("#0ea5a4"));
        gc.fillOval(secondEndX - 5, secondEndY - 5, 10, 10);

        gc.setStroke(Color.web("#ef4444"));
        gc.setLineWidth(3);
        gc.strokeLine(originX, originY, resultantEndX, resultantEndY);
        gc.setFill(Color.web("#ef4444"));
        gc.fillOval(resultantEndX - 5, resultantEndY - 5, 10, 10);

        gc.setFill(Color.web("#111827"));
        gc.fillText("Vector 1", firstEndX + 8, firstEndY - 8);
        gc.fillText("Vector 2", secondEndX + 8, secondEndY - 8);
        gc.fillText(String.format("R = %.2f°", directionDegrees), resultantEndX + 8, resultantEndY - 8);
    }

    private void drawVector(GraphicsContext gc, double startX, double startY, double dx, double dy, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeLine(startX, startY, startX + dx, startY + dy);
        gc.setFill(color);
        gc.fillOval(startX + dx - 5, startY + dy - 5, 10, 10);
    }
}
