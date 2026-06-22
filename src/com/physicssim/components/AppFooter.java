package com.physicssim.components;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;

public class AppFooter extends HBox {

    public AppFooter() {
        Label footer = new Label("Application V1.0.1  |  Copyright 2024");
        footer.setFont(AppTheme.footerFont());
        footer.setTextFill(AppTheme.TEXT_MUTED);

        getChildren().add(footer);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(18, 0, 18, 0));
        setBackground(new Background(new BackgroundFill(AppTheme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        setBorder(AppTheme.topBorder());
    }
}
