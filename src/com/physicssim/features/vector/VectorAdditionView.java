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
import javafx.scene.layout.FlowPane;
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
    private boolean subtractionMode = false;

    public VectorAdditionView() {
        getStylesheets().add(getClass().getResource("/css/features/vector/VectorAddition.css").toExternalForm());
        setId("vector-root");
        setPadding(new Insets(12));
        setBackground(AppTheme.pageBackground());
        setCenter(contentHost);

        bindListeners();
        showHub();
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

        VBox toolCard = createToolCard("Interactive addition",
                "Adjust the two vectors and explore the resultant using the parallelogram law.",
                event -> showVectorTool(false));

        VBox subtractCard = createToolCard("Vector subtraction",
                "Subtract the second vector from the first using the parallelogram law.",
                event -> showVectorTool(true));

        VBox dotCard = createToolCard("Dot product",
                "Multiply two vectors to compute their scalar dot product.",
                event -> showDotProductView());

        VBox crossCard = createToolCard("Cross product",
                "Multiply two vectors and display direction using the right-hand rule.",
                event -> showCrossProductView());

        FlowPane cards = new FlowPane(18, 18, introCard, toolCard, subtractCard, dotCard, crossCard);
        cards.setPrefWrapLength(1080);
        cards.setAlignment(Pos.TOP_LEFT);

        hub.getChildren().add(cards);
        contentHost.setCenter(hub);
    }

    private VBox createToolCard(String cardTitleText, String cardDescriptionText, javafx.event.EventHandler<javafx.scene.input.MouseEvent> clickAction) {
        VBox card = new VBox(10);
        card.getStyleClass().add("vector-card");
        card.setPrefSize(320, 180);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(clickAction);

        Label cardTitle = new Label(cardTitleText);
        cardTitle.setId("vector-card-title");

        Label cardDescription = new Label(cardDescriptionText);
        cardDescription.setId("vector-card-description");
        cardDescription.setWrapText(true);

        Button openButton = new Button("Open tool");
        openButton.getStyleClass().add("vector-button");
        openButton.setOnAction(event -> clickAction.handle(null));

        card.getChildren().addAll(cardTitle, cardDescription, openButton);
        return card;
    }

    private void showInteractiveView() {
        showVectorTool(false);
    }

    private void showSubtractionView() {
        showVectorTool(true);
    }

    private void showDotProductView() {
        contentHost.setCenter(new DotProductView(this::showHub));
    }

    private void showCrossProductView() {
        contentHost.setCenter(new CrossProductView(this::showHub));
    }

    private void showVectorTool(boolean subtraction) {
        subtractionMode = subtraction;

        Button backButton = new Button("Back to overview");
        backButton.getStyleClass().add("vector-button");
        backButton.setOnAction(event -> showHub());

        Label pageTitle = new Label(subtraction ? "Vector subtraction" : "Vector addition");
        pageTitle.setId("vector-card-title");

        Label pageDescription = new Label(subtraction
                ? "Use the parallelogram law to subtract Vector 2 from Vector 1 and view the resulting vector."
                : "Adjust the two vectors and watch the resultant appear using the parallelogram law.");
        pageDescription.setId("vector-card-description");
        pageDescription.setWrapText(true);

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

        VBox page = new VBox(16, backButton, pageTitle, pageDescription, content);
        page.setPadding(new Insets(8));
        contentHost.setCenter(page);
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

        double arrowX2 = subtractionMode ? -x2 : x2;
        double arrowY2 = subtractionMode ? -y2 : y2;
        double rx = x1 + arrowX2;
        double ry = y1 + arrowY2;
        double magnitude = Math.hypot(rx, ry);
        double directionDegrees = Math.toDegrees(Math.atan2(ry, rx));

        double mag1 = Math.hypot(x1, y1);
        double mag2 = Math.hypot(arrowX2, arrowY2);
        double betweenAngle = 0;
        if (mag1 > 0 && mag2 > 0) {
            double dot = x1 * arrowX2 + y1 * arrowY2;
            betweenAngle = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, dot / (mag1 * mag2)))));
        }

        String operation = subtractionMode ? "Subtraction result" : "Addition result";
        String betweenLabel = subtractionMode ? "Angle V1 to -V2" : "Angle V1 to V2";

        resultLabel.setText(String.format("%s: %.2f   Resultant: (%.2f, %.2f)", operation, magnitude, rx, ry));
        directionLabel.setText(String.format("Direction: %.2f degrees from +x axis   %s: %.2f degrees", directionDegrees, betweenLabel, betweenAngle));
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
        double secondEndX = originX + x2 * 35;
        double secondEndY = originY - y2 * 35;
        double arrowX2 = subtractionMode ? -x2 : x2;
        double arrowY2 = subtractionMode ? -y2 : y2;
        double secondArrowEndX = originX + arrowX2 * 35;
        double secondArrowEndY = originY - arrowY2 * 35;
        double resultantEndX = originX + rx * 35;
        double resultantEndY = originY - ry * 35;

        gc.setStroke(Color.web("#3157d5"));
        gc.setLineWidth(3);
        gc.strokeLine(originX, originY, firstEndX, firstEndY);
        gc.setFill(Color.web("#3157d5"));
        gc.fillOval(firstEndX - 5, firstEndY - 5, 10, 10);

        gc.setStroke(Color.web("#0ea5a4"));
        gc.setLineWidth(3);
        gc.strokeLine(originX, originY, secondArrowEndX, secondArrowEndY);
        gc.setFill(Color.web("#0ea5a4"));
        gc.fillOval(secondArrowEndX - 5, secondArrowEndY - 5, 10, 10);

        gc.setStroke(Color.web("#f97316"));
        gc.setLineWidth(3);
        gc.strokeLine(originX, originY, resultantEndX, resultantEndY);
        gc.setFill(Color.web("#f97316"));
        gc.fillOval(resultantEndX - 5, resultantEndY - 5, 10, 10);

        gc.setStroke(Color.web("#9ca3af"));
        gc.setLineWidth(1.5);
        gc.strokeLine(firstEndX, firstEndY, firstEndX + arrowX2 * 35, firstEndY - arrowY2 * 35);
        gc.strokeLine(secondArrowEndX, secondArrowEndY, secondArrowEndX + x1 * 35, secondArrowEndY - y1 * 35);

        gc.setFill(Color.web("#111827"));
        gc.fillText("Vector 1", firstEndX + 8, firstEndY - 8);
        gc.fillText(subtractionMode ? "-Vector 2" : "Vector 2", secondArrowEndX + 8, secondArrowEndY - 8);
        gc.fillText("Resultant", resultantEndX + 8, resultantEndY - 8);
        gc.fillText(String.format("Direction: %.2f degrees", directionDegrees), originX + 8, originY - 8);
    }
}
