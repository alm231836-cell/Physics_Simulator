package com.physicssim.features.kinematics;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class KinematicsToolLayout extends VBox {

    private final BorderPane card = new BorderPane();

    public KinematicsToolLayout(String titleText, String subtitleText, Runnable onBack) {
        Button backButton = new Button("Back to kinematics");
        backButton.setTextFill(Color.BLACK);
        backButton.setStyle("-fx-font-size: 14px; -fx-font-weight: 700;");
        backButton.setBackground(new Background(new BackgroundFill(Color.web("#dbeafe"), new CornerRadii(10), Insets.EMPTY)));
        backButton.setPadding(new Insets(10, 14, 10, 14));
        backButton.setOnAction(event -> onBack.run());

        HBox topBar = new HBox(backButton);

        Label title = new Label(titleText);
        title.setTextFill(Color.BLACK);
        title.setStyle("-fx-font-size: 34px; -fx-font-weight: 800;");

        Label subtitle = new Label(subtitleText);
        subtitle.setTextFill(Color.BLACK);
        subtitle.setWrapText(true);
        subtitle.setStyle("-fx-font-size: 17px;");

        VBox textBlock = new VBox(10, title, subtitle);

        card.setPadding(new Insets(24));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(20), Insets.EMPTY)));
        card.setStyle("-fx-border-color: #d9e2ee;"
                + "-fx-border-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(15, 23, 32, 0.08), 18, 0.18, 0, 6);");

        getChildren().addAll(topBar, textBlock, card);
        setSpacing(18);
        setPadding(new Insets(10, 12, 14, 12));
    }

    protected void setToolContent(VBox content) {
        card.setCenter(content);
    }
}
