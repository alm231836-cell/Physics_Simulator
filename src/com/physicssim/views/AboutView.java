package com.physicssim.views;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AboutView extends BorderPane {

    public AboutView() {
        setBackground(AppTheme.pageBackground());

        Label title = new Label("About This Project");
        title.setFont(AppTheme.heroFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label body = new Label(
                "Physica is being built as a modular JavaFX application.\n"
                        + "Each simulation will live in its own feature package so the team can extend it safely.");
        body.setFont(AppTheme.subtitleFont());
        body.setTextFill(AppTheme.TEXT_SECONDARY);
        body.setWrapText(true);
        body.setMaxWidth(780);

        VBox content = new VBox(24, title, body);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(70, 40, 70, 40));
        content.setBackground(AppTheme.surfaceBackground());

        BorderPane wrapper = new BorderPane(content);
        wrapper.setPadding(new Insets(28, 26, 28, 26));
        setCenter(wrapper);
    }
}
