package com.physicssim.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class PhysicsButton extends Button {

    public enum Style {
        TEXT_ONLY,
        RIGHT_ICON,
        LEFT_AND_RIGHT_ICON
    }

    public PhysicsButton(String text, Style style) {
        this(text, null, null, style);
    }

    public PhysicsButton(String text, String rightIcon, Style style) {
        this(text, null, rightIcon, style);
    }

    public PhysicsButton(String text,
                        String leftIcon,
                        String rightIcon,
                        Style style) {

        getStyleClass().add("physics-button");

        switch (style) {

            case TEXT_ONLY -> setText(text);

            case RIGHT_ICON -> {
                HBox box = new HBox(10);
                box.setAlignment(Pos.CENTER);

                Label textLabel = new Label(text);
                Label right = new Label(
                        rightIcon == null ? "➜" : rightIcon
                );

                textLabel.getStyleClass().add("button-text");
                right.getStyleClass().add("button-icon");

                box.getChildren().addAll(textLabel, right);
                setGraphic(box);
            }

            case LEFT_AND_RIGHT_ICON -> {
                HBox box = new HBox(10);
                box.setAlignment(Pos.CENTER);

                Label left = new Label(
                        leftIcon == null ? "⚛" : leftIcon
                );

                Label textLabel = new Label(text);

                Label right = new Label(
                        rightIcon == null ? "➜" : rightIcon
                );

                left.getStyleClass().add("button-icon");
                textLabel.getStyleClass().add("button-text");
                right.getStyleClass().add("button-icon");

                box.getChildren().addAll(left, textLabel, right);
                setGraphic(box);
            }
        }
    }

    /**
     * Creates a styled PhysicsButton with background, border, and padding.
     * @param text Button text
     * @param backgroundColor Background color
     * @param borderColor Border color
     * @return Styled PhysicsButton
     */
    public static PhysicsButton createStyled(String text, Color backgroundColor, Color borderColor) {
        PhysicsButton button = new PhysicsButton(text, Style.TEXT_ONLY);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setBackground(new Background(new BackgroundFill(
                backgroundColor,
                new CornerRadii(12),
                Insets.EMPTY
        )));
        button.setBorder(new Border(new BorderStroke(
                borderColor,
                BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                new BorderWidths(1)
        )));
        button.setPadding(new Insets(12, 18, 12, 18));
        button.setTextFill(Color.WHITE);
        return button;
    }
}