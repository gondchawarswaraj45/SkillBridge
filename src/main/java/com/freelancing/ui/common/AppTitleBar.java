package com.freelancing.ui.common;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

/**
 * Custom application window title bar with vibrant blue styling,
 * clear white title typography, and window controls (minimize, maximize, close).
 * Replaces the native OS title bar for a seamless, branded desktop experience.
 * 100% annotation-free.
 */
public class AppTitleBar extends HBox {

    private final Stage stage;
    private final Label titleLabel;
    private final Button btnMin;
    private final Button btnMax;
    private final Button btnClose;

    private boolean isMaximized = false;
    private double prevX, prevY, prevW, prevH;
    private double xOffset = 0;
    private double yOffset = 0;

    public AppTitleBar(Stage stage, String title) {
        this.stage = stage;

        setPrefHeight(36);
        setMinHeight(36);
        setMaxHeight(36);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(0, 0, 0, 14));
        setStyle("-fx-background-color: linear-gradient(to right, #1E3A8A 0%, #1D4ED8 45%, #2563EB 100%); "
                + "-fx-border-color: #1D4ED8; -fx-border-width: 0 0 1 0;");

        // App Logo Icon
        Label icon = new Label("⚡");
        icon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        icon.setTextFill(Color.web("#93C5FD"));

        // Title text in bright, clear white
        titleLabel = new Label(title != null ? title : "SkillBridge - Freelancing & Skill Exchange Platform");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12.5));
        titleLabel.setTextFill(Color.web("#FFFFFF"));
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.45), 3, 0, 0, 1);");

        HBox left = new HBox(10, icon, titleLabel);
        left.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Window Controls
        btnMin = createControlButton("—", () -> {
            if (stage != null) stage.setIconified(true);
        }, false);

        btnMax = createControlButton("□", this::toggleMaximize, false);

        btnClose = createControlButton("✕", () -> {
            Platform.exit();
            System.exit(0);
        }, true);

        HBox windowControls = new HBox(0, btnMin, btnMax, btnClose);
        windowControls.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(left, spacer, windowControls);

        // Window drag handling
        setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            if (stage == null) return;
            if (isMaximized) {
                double mouseRatio = Math.max(0.1, Math.min(0.9, e.getX() / Math.max(1, getWidth())));
                toggleMaximize();
                xOffset = prevW * mouseRatio;
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
                return;
            }
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                toggleMaximize();
            }
        });
    }

    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title != null ? title : "SkillBridge");
        }
    }

    public boolean isMaximized() {
        return isMaximized;
    }

    public void toggleMaximize() {
        if (stage == null) return;
        List<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Screen screen = (screens != null && !screens.isEmpty()) ? screens.get(0) : Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        if (!isMaximized) {
            prevX = stage.getX();
            prevY = stage.getY();
            prevW = stage.getWidth();
            prevH = stage.getHeight();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            btnMax.setText("❐");
            isMaximized = true;
        } else {
            stage.setX(prevX);
            stage.setY(prevY);
            stage.setWidth(prevW);
            stage.setHeight(prevH);

            btnMax.setText("□");
            isMaximized = false;
        }
    }

    private Button createControlButton(String symbol, Runnable action, boolean isClose) {
        Button btn = new Button(symbol);
        btn.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        btn.setPrefHeight(36);
        btn.setMinHeight(36);
        btn.setMaxHeight(36);
        btn.setPrefWidth(isClose ? 48 : 44);

        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-background-radius: 0; -fx-cursor: hand; -fx-padding: 0;";
        String hoverStyle = isClose
                ? "-fx-background-color: #E11D48; -fx-text-fill: #FFFFFF; -fx-background-radius: 0; -fx-cursor: hand; -fx-padding: 0;"
                : "-fx-background-color: rgba(255, 255, 255, 0.18); -fx-text-fill: #FFFFFF; -fx-background-radius: 0; -fx-cursor: hand; -fx-padding: 0;";

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        btn.setOnAction(e -> action.run());

        return btn;
    }
}
