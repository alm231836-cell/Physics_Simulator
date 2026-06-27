package com.physicssim.features.kinematics;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

public class KinematicsToolCard extends VBox {

    public KinematicsToolCard(KinematicsToolItem item, Runnable onOpen) {
        Label number = new Label(item.getNumber());
        number.setFont(AppTheme.cardNumberFont());
        number.setTextFill(AppTheme.TEXT_SECONDARY);

        Label title = new Label(item.getTitle());
        title.setTextFill(Color.BLACK);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 800;");

        Label description = new Label(item.getDescription());
        description.setTextFill(Color.BLACK);
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 14px;");

        Label action = new Label("Open tool");
        action.setTextFill(Color.web("#3157d5"));
        action.setStyle("-fx-font-size: 13px; -fx-font-weight: 700;");

        getChildren().addAll(number, title, description, action);
        setSpacing(14);
        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(22));
        setPrefSize(260, 180);
        setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(18), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(
                Color.web("#d9e2ee"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(18),
                new BorderWidths(1))));
        setStyle("-fx-effect: dropshadow(gaussian, rgba(15, 23, 32, 0.08), 18, 0.18, 0, 6);");
        setCursor(Cursor.HAND);
        setOnMouseClicked(event -> onOpen.run());
        setOnMouseEntered(event -> setTranslateY(-4));
        setOnMouseExited(event -> setTranslateY(0));
    }
}
