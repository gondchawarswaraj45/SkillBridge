package com.freelancing.ui.common;

import com.freelancing.config.AppTheme;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Animated splash screen for SkillBridge platform.
 * Shows branded logo animation, database check, and transitions to HomePage.
 */
public class SplashScreen extends StackPane {

    private Runnable onComplete;
    private ProgressBar progressBar;
    private Label statusLabel;

    public SplashScreen(Runnable onComplete) {
        this.onComplete = onComplete;
        buildUI();
        startAnimation();
    }

    private void buildUI() {
        boolean dark = AppTheme.isDarkMode();
        String bgColor = dark ? "#0a0a1a" : "#e8e0f0";
        setStyle("-fx-background-color: " + bgColor + ";");
        setAlignment(Pos.CENTER);

        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(500);

        // Logo circles
        HBox logoCircles = new HBox(8);
        logoCircles.setAlignment(Pos.CENTER);
        String[] colors = {"#6C63FF", "#FF6584", "#43E97B", "#FFD700"};
        for (String color : colors) {
            Circle circle = new Circle(12);
            circle.setFill(Color.web(color));
            circle.setEffect(new Glow(0.8));
            logoCircles.getChildren().add(circle);

            ScaleTransition pulse = new ScaleTransition(Duration.millis(600), circle);
            pulse.setFromX(0.8);
            pulse.setFromY(0.8);
            pulse.setToX(1.15);
            pulse.setToY(1.15);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(3);
            pulse.play();
        }

        // Brand name
        Label brandLabel = new Label("SkillBridge");
        brandLabel.setFont(Font.font("Segoe UI", FontWeight.BLACK, 48));
        brandLabel.setTextFill(Color.web(dark ? "#6C63FF" : "#5A52D5"));
        DropShadow brandGlow = new DropShadow();
        brandGlow.setColor(Color.web("#6C63FF"));
        brandGlow.setRadius(30);
        brandGlow.setSpread(0.3);
        brandLabel.setEffect(brandGlow);

        // Tagline
        Label tagline = new Label("Where Talent Meets Opportunity");
        tagline.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        tagline.setTextFill(Color.web(dark ? "#8888aa" : "#6666aa"));

        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(6);
        String pbBg = dark ? "#1a1a2e" : "#d0c8e0";
        String pbFill = "#6C63FF";
        progressBar.setStyle("-fx-accent: " + pbFill + "; -fx-control-inner-background: " + pbBg + "; "
                + "-fx-background-radius: 10; -fx-background-color: " + pbBg + ";");

        // Status label
        statusLabel = new Label("Initializing...");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        statusLabel.setTextFill(Color.web(dark ? "#666688" : "#8888aa"));

        // Version
        Label versionLabel = new Label("v1.0.0 — Desktop Freelancing Platform");
        versionLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        versionLabel.setTextFill(Color.web(dark ? "#444466" : "#999999"));

        container.getChildren().addAll(logoCircles, brandLabel, tagline, progressBar, statusLabel, versionLabel);

        // Fade in the entire container
        container.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        getChildren().add(container);
    }

    private void startAnimation() {
        String[] statuses = {
            "Loading design system...",
            "Connecting to local SQLite database...",
            "Preparing marketplace and profiles...",
            "Initializing smart escrow services...",
            "Almost ready..."
        };

        Timeline timeline = new Timeline();
        double totalDuration = 900; // Snappy 0.9s duration
        double step = totalDuration / statuses.length;

        for (int i = 0; i < statuses.length; i++) {
            final int idx = i;
            double progress = (double)(i + 1) / statuses.length;
            timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(step * (i + 1)), e -> {
                    progressBar.setProgress(progress);
                    statusLabel.setText(statuses[idx]);
                })
            );
        }

        timeline.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), this);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> {
                if (onComplete != null) onComplete.run();
            });
            fadeOut.play();
        });

        timeline.play();
    }
}
