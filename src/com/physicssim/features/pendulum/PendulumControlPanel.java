package com.physicssim.features.pendulum;

import com.physicssim.components.PhysicsButton;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PendulumControlPanel extends VBox {

    private final Label gravityValueLabel = new Label();
    private final Label lengthValueLabel = new Label();
    private final Label massValueLabel = new Label();
    private final Label angleValueLabel = new Label();

    private final Slider gravitySlider = new Slider(1.0, 20.0, 9.81);
    private final Slider lengthSlider = new Slider(0.8, 3.0, 2.0);
    private final Slider massSlider = new Slider(0.5, 5.0, 1.5);
    private final Slider angleSlider = new Slider(5, 75, 30);

    public PendulumControlPanel(
            Runnable onPlayPause,
            Runnable onReset,
            Consumer<Double> onAngleChanged,
            Consumer<Double> onGravityChanged,
            Consumer<Double> onLengthChanged,
            Consumer<Double> onMassChanged) {
        Label title = sectionTitle("Pendulum Controls");
        Label subtitle = new Label("Fine-tune motion and watch the scene respond.");
        subtitle.setTextFill(Color.web("#64748b"));
        subtitle.setStyle("-fx-font-size: 11px; -fx-font-weight: 600;");

        VBox gravityBlock = buildSliderBlock("Gravity", gravityValueLabel, gravitySlider, onGravityChanged, "%.2f m/s^2");
        VBox lengthBlock = buildSliderBlock("Rod Length", lengthValueLabel, lengthSlider, onLengthChanged, "%.2f m");
        VBox massBlock = buildSliderBlock("Bob Mass", massValueLabel, massSlider, onMassChanged, "%.2f kg");
        VBox angleBlock = buildSliderBlock("Initial Angle", angleValueLabel, angleSlider, onAngleChanged, "%.1f deg");

        Button playPauseButton = PhysicsButton.createStyled("START / PAUSE", Color.web("#2563eb"), Color.web("#1d4ed8"));
        Button resetButton = PhysicsButton.createStyled("RESET", Color.web("#f59e0b"), Color.web("#d97706"));
        playPauseButton.setOnAction(event -> onPlayPause.run());
        resetButton.setOnAction(event -> onReset.run());
        playPauseButton.setPrefSize(150, 38);
        resetButton.setPrefSize(110, 38);
        playPauseButton.setTextFill(Color.WHITE);
        resetButton.setTextFill(Color.WHITE);
        playPauseButton.setStyle(playPauseButton.getStyle() + " -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-background-radius: 14;");
        resetButton.setStyle(resetButton.getStyle() + " -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 700; -fx-background-radius: 14;");

        HBox buttonRow = new HBox(10, playPauseButton, resetButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(new VBox(3, title, subtitle), gravityBlock, lengthBlock, massBlock, angleBlock, buttonRow);
        setSpacing(13);
        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(16));
        setPrefWidth(250);
        setMinWidth(250);
        setBackground(new Background(new BackgroundFill(Color.web("#f8fbff"), new CornerRadii(20), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(
                Color.web("#dce7f3"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(20),
                new BorderWidths(1))));
        setStyle("-fx-effect: dropshadow(gaussian, rgba(15, 23, 32, 0.08), 18, 0.18, 0, 6);"
                + "-fx-background-radius: 20;"
                + "-fx-border-radius: 20;");
    }

    public double getSelectedAngle() {
        return angleSlider.getValue();
    }

    private VBox buildSliderBlock(
            String name,
            Label valueLabel,
            Slider slider,
            Consumer<Double> onChanged,
            String format) {
        Label nameLabel = sectionLabel(name);
        nameLabel.setMinHeight(18);

        valueLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1d4ed8;");
        valueLabel.setMinWidth(92);
        valueLabel.setMinHeight(18);
        valueLabel.setAlignment(Pos.CENTER_LEFT);
        valueLabel.setVisible(true);
        valueLabel.setManaged(true);

        slider.setMajorTickUnit((slider.getMax() - slider.getMin()) / 4.0);
        slider.setMinorTickCount(2);
        slider.setShowTickMarks(false);
        slider.setShowTickLabels(false);
        slider.setBlockIncrement((slider.getMax() - slider.getMin()) / 20.0);
        slider.setStyle("-fx-control-inner-background: white; -fx-accent: #2563eb;");
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            valueLabel.setText(String.format(format, newValue.doubleValue()));
            onChanged.accept(newValue.doubleValue());
        });
        valueLabel.setText(String.format(format, slider.getValue()));

        Label helperLabel = new Label("Adjust to update the simulation instantly");
        helperLabel.setTextFill(Color.web("#556270"));
        helperLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: 600;");
        helperLabel.setMinHeight(14);

        VBox block = new VBox(6, nameLabel, valueLabel, slider, helperLabel);
        block.setAlignment(Pos.TOP_LEFT);
        return block;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: 800;");
        label.setTextFill(Color.BLACK);
        return label;
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: black;");
        label.setTextFill(Color.BLACK);
        return label;
    }

}
