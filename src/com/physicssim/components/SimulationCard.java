package com.physicssim.components;

import com.physicssim.model.SimulationItem;
import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class SimulationCard extends VBox {

    private static final String CARD_STYLE =
            "-fx-effect: dropshadow(gaussian, rgba(24, 39, 75, 0.12), 18, 0.25, 0, 6);";
    private static final String CARD_HOVER_STYLE =
            "-fx-effect: dropshadow(gaussian, rgba(24, 39, 75, 0.20), 26, 0.30, 0, 12);";

    public SimulationCard(SimulationItem item) {
        Label numberLabel = new Label(item.getNumber());
        numberLabel.setFont(AppTheme.cardNumberFont());
        numberLabel.setTextFill(AppTheme.TEXT_SECONDARY);

        StackPane icon = SimulationIconFactory.create(item.getType());

        Label titleLabel = new Label(item.getTitle());
        titleLabel.setFont(AppTheme.cardTitleFont());
        titleLabel.setTextFill(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setWrapText(true);

        getChildren().addAll(numberLabel, icon, titleLabel);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(16);
        setPadding(new Insets(18, 16, 18, 16));
        setPrefSize(210, 250);
        setMaxSize(210, 250);
        setBackground(new Background(new BackgroundFill(AppTheme.SURFACE, new CornerRadii(18), Insets.EMPTY)));
        setBorder(AppTheme.cardBorder());
        setStyle(CARD_STYLE);
        setCursor(Cursor.HAND);

        setOnMouseEntered(event -> {
            setTranslateY(-5);
            setStyle(CARD_HOVER_STYLE);
        });
        setOnMouseExited(event -> {
            setTranslateY(0);
            setStyle(CARD_STYLE);
        });
    }
}
