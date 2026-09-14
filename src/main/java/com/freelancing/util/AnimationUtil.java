package com.freelancing.util;

import com.freelancing.config.AppTheme;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Enterprise Animation & Concurrency Utility for SkillBridge.
 * Provides rich, modern page transition animations (Fade, Slide, Scale, Pop)
 * and safe, non-blocking background thread execution to prevent UI freezes.
 */
public class AnimationUtil {

    // Dedicated daemon thread pool for non-blocking asynchronous data fetching
    private static final ExecutorService BG_EXECUTOR = Executors.newFixedThreadPool(
            Math.max(4, Runtime.getRuntime().availableProcessors()),
            r -> {
                Thread t = new Thread(r, "SkillBridge-WorkerThread");
                t.setDaemon(true);
                return t;
            }
    );

    /**
     * Executes a data task asynchronously in the background and calls the consumer
     * on the JavaFX UI Application Thread upon completion.
     */
    public static <T> void runAsync(Supplier<T> backgroundSupplier, Consumer<T> uiConsumer) {
        System.out.println("[SkillBridge-ThreadPool] Dispatching async computation task to daemon pool...");
        CompletableFuture.supplyAsync(backgroundSupplier, BG_EXECUTOR)
                .thenAccept(result -> Platform.runLater(() -> {
                    System.out.println("[SkillBridge-ThreadPool] Async task completed successfully. Rendering on JavaFX thread.");
                    uiConsumer.accept(result);
                }))
                .exceptionally(ex -> {
                    System.err.println("[SkillBridge-ThreadPool] Error during async task execution: " + ex.getMessage());
                    LoggingUtil.error("AnimationUtil", "Async task failed", ex);
                    return null;
                });
    }

    /**
     * Executes a runnable task in the background, then executes a callback on the JavaFX UI thread.
     */
    public static void runAsync(Runnable backgroundTask, Runnable uiCallback) {
        System.out.println("[SkillBridge-ThreadPool] Dispatching background runnable...");
        CompletableFuture.runAsync(backgroundTask, BG_EXECUTOR)
                .thenRun(() -> Platform.runLater(() -> {
                    System.out.println("[SkillBridge-ThreadPool] Background runnable finished. Executing UI callback.");
                    uiCallback.run();
                }))
                .exceptionally(ex -> {
                    System.err.println("[SkillBridge-ThreadPool] Error during async runnable execution: " + ex.getMessage());
                    LoggingUtil.error("AnimationUtil", "Async task failed", ex);
                    return null;
                });
    }

    /**
     * Creates an elegant loading spinner with a message while async queries execute.
     */
    public static StackPane createLoadingOverlay(String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: transparent;");

        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(42, 42);
        spinner.setStyle("-fx-progress-color: #6366f1;");

        Label label = new Label(message != null ? message : "Loading data...");
        label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        label.setTextFill(Color.web(AppTheme.getTextMuted()));

        box.getChildren().addAll(spinner, label);
        overlay.getChildren().add(box);

        applyFadeZoom(overlay, 180);
        return overlay;
    }

    // =========================================================================
    // VARIED TRANSITIONS
    // =========================================================================

    public enum TransitionStyle {
        FADE_ZOOM,
        SLIDE_IN_RIGHT,
        SLIDE_IN_LEFT,
        SLIDE_UP,
        POP_SCALE
    }

    /**
     * Applies a varied transition to a node based on an index, alternating across
     * styles so each page or tab switch has a distinct, fluid feel.
     */
    public static void applyVariedTransition(Node node, int index) {
        TransitionStyle[] styles = TransitionStyle.values();
        TransitionStyle chosen = styles[Math.abs(index) % styles.length];
        applyTransition(node, chosen, 320);
    }

    public static void applyTransition(Node node, TransitionStyle style, int durationMs) {
        if (node == null) return;
        System.out.println("[SkillBridge-Animation] Playing transition animation: " + style + " (" + durationMs + "ms) on " + node.getClass().getSimpleName());
        switch (style) {
            case SLIDE_IN_RIGHT:
                applySlideInRight(node, durationMs);
                break;
            case SLIDE_IN_LEFT:
                applySlideInLeft(node, durationMs);
                break;
            case SLIDE_UP:
                applySlideUp(node, durationMs);
                break;
            case POP_SCALE:
                applyPopScale(node, durationMs);
                break;
            case FADE_ZOOM:
            default:
                applyFadeZoom(node, durationMs);
                break;
        }
    }

    /**
     * Subtle Fade & Zoom (scale from 0.97 -> 1.0 with opacity 0.0 -> 1.0)
     */
    public static void applyFadeZoom(Node node, int durationMs) {
        node.setOpacity(0.0);
        node.setScaleX(0.97);
        node.setScaleY(0.97);

        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition st = new ScaleTransition(Duration.millis(durationMs), node);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition pt = new ParallelTransition(ft, st);
        pt.play();
    }

    /**
     * Slide in from right (+50px -> 0px) with fade in
     */
    public static void applySlideInRight(Node node, int durationMs) {
        node.setOpacity(0.0);
        node.setTranslateX(50.0);

        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setToX(0.0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    /**
     * Slide in from left (-50px -> 0px) with fade in
     */
    public static void applySlideInLeft(Node node, int durationMs) {
        node.setOpacity(0.0);
        node.setTranslateX(-50.0);

        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setToX(0.0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    /**
     * Slide up from bottom (+40px -> 0px) with fade in
     */
    public static void applySlideUp(Node node, int durationMs) {
        node.setOpacity(0.0);
        node.setTranslateY(40.0);

        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setToY(0.0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    /**
     * Pop scale animation (0.93 -> 1.0) with ease-out
     */
    public static void applyPopScale(Node node, int durationMs) {
        node.setOpacity(0.0);
        node.setScaleX(0.93);
        node.setScaleY(0.93);

        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition st = new ScaleTransition(Duration.millis(durationMs), node);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0));

        ParallelTransition pt = new ParallelTransition(ft, st);
        pt.play();
    }

    /**
     * Subtle horizontal shake animation for validation errors
     */
    public static void shakeNode(Node node) {
        if (node == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(8);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }
}
