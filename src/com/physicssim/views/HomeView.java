package com.physicssim.views;

import com.physicssim.components.AppFooter;
import com.physicssim.components.AppHeader;
import com.physicssim.components.SimulationCard;
import com.physicssim.model.SimulationCatalog;
import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class HomeView extends BorderPane {

    public HomeView() {
        setBackground(AppTheme.pageBackground());

        VBox page = new VBox();
        VBox contentSection = createContentSection();
        VBox.setVgrow(contentSection, Priority.ALWAYS);

        page.getChildren().addAll(new AppHeader(), contentSection, new AppFooter());
        setCenter(page);
    }

    private VBox createContentSection() {
        Label title = new Label("Explore the World of Motion");
        title.setFont(AppTheme.heroFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label subtitle = new Label("Choose your simulation to get started");
        subtitle.setFont(AppTheme.subtitleFont());
        subtitle.setTextFill(AppTheme.TEXT_SECONDARY);

        HBox cardsRow = new HBox(24);
        cardsRow.setAlignment(Pos.CENTER);
        SimulationCatalog.homeItems()
                .stream()
                .map(SimulationCard::new)
                .forEach(cardsRow.getChildren()::add);

        VBox content = new VBox(36, title, subtitle, cardsRow);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(64, 40, 64, 40));
        content.setBackground(AppTheme.surfaceBackground());

        BorderPane wrapper = new BorderPane(content);
        wrapper.setPadding(new Insets(28, 26, 28, 26));

        VBox outer = new VBox(wrapper);
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        return outer;
    }
}
