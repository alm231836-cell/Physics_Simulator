package com.physicssim.features.electricity;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CurrentElectricityView extends BorderPane {

    private final OhmsLawView ohmsView = new OhmsLawView();
    private final SeriesCircuitView seriesView = new SeriesCircuitView();
    private final ParallelCircuitView parallelView = new ParallelCircuitView();
    private final KCLView kclView = new KCLView();
    private final PowerView powerView = new PowerView();

    public CurrentElectricityView() {
        setPadding(new Insets(12));
        setBackground(AppTheme.pageBackground());
        setTop(buildHeader());
        setCenter(ohmsView);
    }

    private Node buildHeader() {
        Label title = new Label("Current electricity");
        title.setFont(AppTheme.cardTitleFont());

        Button ohmsBtn = new Button("Ohm's law");
        Button seriesBtn = new Button("Series circuit");
        Button parallelBtn = new Button("Parallel circuit");
        Button kclBtn = new Button("KCL");
        Button powerBtn = new Button("Power & Bulb");

        ohmsBtn.setOnAction(e -> setCenter(ohmsView));
        seriesBtn.setOnAction(e -> setCenter(seriesView));
        parallelBtn.setOnAction(e -> setCenter(parallelView));
        kclBtn.setOnAction(e -> setCenter(kclView));
        powerBtn.setOnAction(e -> setCenter(powerView));

        HBox buttons = new HBox(8, ohmsBtn, seriesBtn, parallelBtn, kclBtn, powerBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(8, title, buttons);
        header.setPadding(new Insets(20));
        header.setBackground(AppTheme.surfaceBackground());
        header.setBorder(AppTheme.cardBorder());
        return header;
    }
}
