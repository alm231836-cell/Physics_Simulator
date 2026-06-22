package com.physicssim.components;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class AppHeader extends HBox {

    public AppHeader() {
        Label brand = new Label("PHYSICS SIMULATOR - R13");
        brand.setFont(AppTheme.brandFont());
        brand.setTextFill(Color.web("#1b1f24"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox nav = new HBox(12);
        nav.setAlignment(Pos.CENTER_RIGHT);
        nav.getChildren().addAll(
                createNavButton("Home", true),
                createNavButton("Simulations", false),
                createNavButton("About", false),
                createNavButton("Help", false));

        getChildren().addAll(brand, spacer, nav);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(24);
        setPadding(new Insets(22, 34, 22, 34));
        setBackground(new Background(new BackgroundFill(AppTheme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        setBorder(AppTheme.bottomBorder());
    }

    private Button createNavButton(String text, boolean active) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        button.setCursor(Cursor.HAND);
        button.setBackground(Background.EMPTY);
        button.setFont(AppTheme.navFont(active));
        button.setTextFill(active ? Color.web("#101827") : Color.web("#596579"));
        button.setBorder(Border.EMPTY);
        button.setPadding(new Insets(10, 14, 10, 14));
        return button;
    }
}
