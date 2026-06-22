package com.physicssim.theme;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class AppTheme {

    public static final Color PAGE_BACKGROUND = Color.web("#f5f7fb");
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = Color.web("#d9e1ea");
    public static final Color CARD_BORDER = Color.web("#dbe5ec");
    public static final Color TEXT_PRIMARY = Color.web("#0f1720");
    public static final Color TEXT_SECONDARY = Color.web("#556270");
    public static final Color TEXT_MUTED = Color.web("#6b7280");
    public static final Color ICON_DARK = Color.web("#2c3541");
    public static final Color ICON_MID = Color.web("#37424d");
    public static final Color ICON_LIGHT = Color.web("#c7d0d8");

    private AppTheme() {
    }

    public static Background surfaceBackground() {
        return new Background(new BackgroundFill(SURFACE, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public static Background pageBackground() {
        return new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public static Border bottomBorder() {
        return new Border(new BorderStroke(
                BORDER,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(0, 0, 1, 0)));
    }

    public static Border topBorder() {
        return new Border(new BorderStroke(
                BORDER,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(1, 0, 0, 0)));
    }

    public static Border cardBorder() {
        return new Border(new BorderStroke(
                CARD_BORDER,
                BorderStrokeStyle.SOLID,
                new CornerRadii(18),
                new BorderWidths(1.2)));
    }

    public static Font brandFont() {
        return Font.font("Arial", FontWeight.EXTRA_BOLD, 22);
    }

    public static Font navFont(boolean active) {
        return Font.font("Arial", active ? FontWeight.BOLD : FontWeight.MEDIUM, 18);
    }

    public static Font heroFont() {
        return Font.font("Arial", FontWeight.EXTRA_BOLD, 48);
    }

    public static Font subtitleFont() {
        return Font.font("Arial", FontWeight.MEDIUM, 24);
    }

    public static Font cardNumberFont() {
        return Font.font("Arial", FontWeight.MEDIUM, 18);
    }

    public static Font cardTitleFont() {
        return Font.font("Arial", FontWeight.BOLD, 19);
    }

    public static Font footerFont() {
        return Font.font("Arial", FontWeight.NORMAL, 16);
    }
}
