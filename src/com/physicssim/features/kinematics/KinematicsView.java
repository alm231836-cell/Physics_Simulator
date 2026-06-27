package com.physicssim.features.kinematics;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.EnumMap;
import java.util.Map;

public class KinematicsView extends BorderPane {

    private final DistanceDisplacementView distanceDisplacementView = new DistanceDisplacementView();
    private final SpeedVelocityView speedVelocityView = new SpeedVelocityView();
    private final AccelerationView accelerationView = new AccelerationView();
    private final FreeFallView freeFallView = new FreeFallView();
    private final ProjectileMotionView projectileMotionView = new ProjectileMotionView();

    private final Map<KinematicsToolType, Button> navButtons = new EnumMap<>(KinematicsToolType.class);
    private Button activeButton;

    public KinematicsView() {
        setPadding(new Insets(12));
        setBackground(AppTheme.pageBackground());
        setTop(buildHeader());
        showSubsection(KinematicsToolType.DISTANCE_DISPLACEMENT);
    }

    private Node buildHeader() {
        Label title = new Label("Kinematics (Motion)");
        title.setFont(AppTheme.heroFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label subtitle = new Label("One kinematics module with five subsections. Pick a topic below.");
        subtitle.setFont(AppTheme.subtitleFont());
        subtitle.setTextFill(AppTheme.TEXT_SECONDARY);
        subtitle.setWrapText(true);

        FlowPane nav = new FlowPane(8, 8);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPrefWrapLength(960);

        for (KinematicsToolItem item : KinematicsCatalog.tools()) {
            Button button = createNavButton(item);
            navButtons.put(item.getType(), button);
            nav.getChildren().add(button);
        }

        VBox header = new VBox(12, title, subtitle, nav);
        header.setPadding(new Insets(20));
        header.setBackground(AppTheme.surfaceBackground());
        header.setBorder(AppTheme.cardBorder());
        return header;
    }

    private Button createNavButton(KinematicsToolItem item) {
        Button button = new Button(item.getNumber() + " " + item.getTitle());
        button.setWrapText(true);
        button.setStyle(inactiveButtonStyle());
        button.setOnAction(event -> showSubsection(item.getType()));
        return button;
    }

    private void showSubsection(KinematicsToolType type) {
        Node view = switch (type) {
            case DISTANCE_DISPLACEMENT -> distanceDisplacementView;
            case SPEED_VELOCITY -> speedVelocityView;
            case ACCELERATION -> accelerationView;
            case FREE_FALL -> freeFallView;
            case PROJECTILE_MOTION -> projectileMotionView;
        };

        setCenter(view);

        if (activeButton != null) {
            activeButton.setStyle(inactiveButtonStyle());
        }

        activeButton = navButtons.get(type);
        if (activeButton != null) {
            activeButton.setStyle(activeButtonStyle());
        }
    }

    private String inactiveButtonStyle() {
        return "-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-color: #eef2ff;"
                + "-fx-text-fill: #1e293b; -fx-background-radius: 10; -fx-padding: 10 14;";
    }

    private String activeButtonStyle() {
        return "-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-color: #3157d5;"
                + "-fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 14;";
    }
}
