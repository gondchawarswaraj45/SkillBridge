package com.freelancing.ui.common;

import com.freelancing.app.NavigationManager;
import com.freelancing.config.AppTheme;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UIComponents {

    public static String COLOR_BG_DARK = AppTheme.getBgDark();
    public static String COLOR_BG_SIDEBAR = AppTheme.getBgPanel();
    public static String COLOR_BG_CARD = AppTheme.getBgCard();
    public static String COLOR_BG_INPUT = AppTheme.getBgInput();
    public static String COLOR_BORDER = AppTheme.getBorderColor();

    public static final String COLOR_PRIMARY = AppTheme.COLOR_PRIMARY;
    public static final String COLOR_PRIMARY_HOVER = AppTheme.COLOR_PRIMARY_HOVER;
    public static final String COLOR_SUCCESS = AppTheme.COLOR_SUCCESS;
    public static final String COLOR_AMBER = AppTheme.COLOR_AMBER;
    public static final String COLOR_DANGER = AppTheme.COLOR_DANGER;
    public static final String COLOR_PURPLE = AppTheme.COLOR_ACCENT;

    public static String COLOR_TEXT_PRIMARY = AppTheme.getTextPrimary();
    public static String COLOR_TEXT_MUTED = AppTheme.getTextMuted();

    static {
        AppTheme.addThemeListener(mode -> updateThemeColors());
    }

    public static void updateThemeColors() {
        COLOR_BG_DARK = AppTheme.getBgDark();
        COLOR_BG_SIDEBAR = AppTheme.getBgPanel();
        COLOR_BG_CARD = AppTheme.getBgCard();
        COLOR_BG_INPUT = AppTheme.getBgInput();
        COLOR_BORDER = AppTheme.getBorderColor();
        COLOR_TEXT_PRIMARY = AppTheme.getTextPrimary();
        COLOR_TEXT_MUTED = AppTheme.getTextMuted();
    }

    // ═══════════════════════════════════════════════════════════════
    // ANIMATION UTILITIES
    // ═══════════════════════════════════════════════════════════════

    /** Fade in a node from 0 to 1 opacity */
    public static void fadeIn(Node node, double durationMs) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_OUT);
        ft.play();
    }

    /** Fade in with default duration */
    public static void fadeIn(Node node) {
        fadeIn(node, AppTheme.ANIM_ENTRANCE);
    }

    /** Slide in from the left */
    public static void slideInFromLeft(Node node, double durationMs) {
        node.setTranslateX(-40);
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setFromX(-40);
        tt.setToX(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(tt, ft).play();
    }

    /** Slide in from the bottom */
    public static void slideInFromBottom(Node node, double durationMs) {
        node.setTranslateY(30);
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setFromY(30);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(tt, ft).play();
    }

    /** Scale in from small to full size */
    public static void scaleIn(Node node, double durationMs) {
        node.setScaleX(0.7);
        node.setScaleY(0.7);
        node.setOpacity(0);
        ScaleTransition st = new ScaleTransition(Duration.millis(durationMs), node);
        st.setFromX(0.7);
        st.setFromY(0.7);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs * 0.6), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(st, ft).play();
    }

    /** Staggered entrance animation for a list of nodes */
    public static void staggeredEntrance(List<Node> nodes, double staggerDelayMs) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            node.setOpacity(0);
            node.setTranslateY(20);
            final int index = i;
            Timeline tl = new Timeline(
                    new KeyFrame(Duration.millis(staggerDelayMs * index), e -> {
                        TranslateTransition tt = new TranslateTransition(Duration.millis(AppTheme.ANIM_ENTRANCE), node);
                        tt.setFromY(20);
                        tt.setToY(0);
                        tt.setInterpolator(Interpolator.EASE_OUT);
                        FadeTransition ft = new FadeTransition(Duration.millis(AppTheme.ANIM_ENTRANCE), node);
                        ft.setFromValue(0);
                        ft.setToValue(1);
                        ft.setInterpolator(Interpolator.EASE_OUT);
                        new ParallelTransition(tt, ft).play();
                    }));
            tl.play();
        }
    }

    /** Pulse animation (subtle breathing effect) */
    public static void pulse(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(800), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.05);
        st.setToY(1.05);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.setInterpolator(Interpolator.EASE_BOTH);
        st.play();
    }

    /** Apply hover scale animation to a button */
    public static void setupButtonAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
    }

    /** Apply hover scale + glow animation to any node */
    public static void setupHoverGlow(Node node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), node);
            st.setToX(1.02);
            st.setToY(1.02);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), node);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
    }

    /** Apply entrance animation to a card (fade + slide up) */
    public static void applyEntranceAnimation(Node card, int index) {
        card.setOpacity(0);
        card.setTranslateY(25);
        Timeline delay = new Timeline(new KeyFrame(
                Duration.millis(AppTheme.ANIM_STAGGER_DELAY * index),
                e -> slideInFromBottom(card, AppTheme.ANIM_ENTRANCE)));
        delay.play();
    }

    // ═══════════════════════════════════════════════════════════════
    // TEXT BUILDERS
    // ═══════════════════════════════════════════════════════════════

    public static Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        label.setTextFill(Color.web(AppTheme.getTextPrimary()));
        return label;
    }

    public static Label createHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        label.setTextFill(Color.web(AppTheme.getTextPrimary()));
        return label;
    }

    public static Label createSubHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web(AppTheme.getTextMuted()));
        return label;
    }

    // ═══════════════════════════════════════════════════════════════
    // BUTTON BUILDERS (with built-in hover scale animations)
    // ═══════════════════════════════════════════════════════════════

    public static Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle(AppTheme.getBtnPrimaryStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnPrimaryHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.06);
            st.setToY(1.06);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnPrimaryStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createSuccessButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle(AppTheme.getBtnSuccessStyle());
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.06);
            st.setToY(1.06);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createDangerButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle(AppTheme.getBtnDangerStyle());
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.06);
            st.setToY(1.06);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        btn.setStyle(AppTheme.getBtnSecondaryStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnSecondaryHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnSecondaryStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createClientDemoButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle(AppTheme.getBtnClientDemoStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnClientDemoHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.06);
            st.setToY(1.06);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnClientDemoStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createFreelancerDemoButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle(AppTheme.getBtnFreelancerDemoStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnFreelancerDemoHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.06);
            st.setToY(1.06);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnFreelancerDemoStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createExportButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btn.setStyle(AppTheme.getBtnExportStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnExportHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnExportStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createQuickViewButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btn.setStyle(AppTheme.getBtnQuickViewStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnQuickViewHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnQuickViewStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createPortfolioButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btn.setStyle(AppTheme.getBtnPortfolioStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnPortfolioHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnPortfolioStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static Button createAccentButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle(AppTheme.getBtnAccentStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnAccentHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.06);
            st.setToY(1.06);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnAccentStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return btn;
    }

    public static void styleButton(Button btn, String colorHex) {
        String baseStyle = "-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(baseStyle + "-fx-opacity: 0.88; -fx-effect: dropshadow(gaussian, " + colorHex
                    + "88, 10, 0, 0, 2);");
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
    }

    // ═══════════════════════════════════════════════════════════════
    // INPUT BUILDERS (with focus glow animations)
    // ═══════════════════════════════════════════════════════════════

    public static TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font("Segoe UI", 13));
        tf.setStyle(AppTheme.getInputStyle());
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                tf.setStyle(AppTheme.getInputStyle() + "-fx-border-color: " + AppTheme.COLOR_PRIMARY
                        + "; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.3), 8, 0, 0, 0);");
            } else {
                tf.setStyle(AppTheme.getInputStyle());
            }
        });
        return tf;
    }

    public static PasswordField createPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setFont(Font.font("Segoe UI", 13));
        pf.setStyle(AppTheme.getInputStyle());
        pf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                pf.setStyle(AppTheme.getInputStyle() + "-fx-border-color: " + AppTheme.COLOR_PRIMARY
                        + "; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.3), 8, 0, 0, 0);");
            } else {
                pf.setStyle(AppTheme.getInputStyle());
            }
        });
        return pf;
    }

    public static TextArea createTextArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setFont(Font.font("Segoe UI", 13));
        ta.setWrapText(true);
        ta.setStyle("-fx-control-inner-background: " + AppTheme.getBgInput() + ";" +
                "-fx-text-fill: " + AppTheme.getTextPrimary() + ";" +
                "-fx-prompt-text-fill: " + AppTheme.getTextMuted() + ";" +
                "-fx-border-color: " + AppTheme.getBorderColor() + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;");
        return ta;
    }

    // ═══════════════════════════════════════════════════════════════
    // STYLED COMBOBOX (CRAZY NEON GLOW SELECTS)
    // ═══════════════════════════════════════════════════════════════

    /** Creates a premium styled ComboBox with neon glow hover effects */
    @SafeVarargs
    public static <T> ComboBox<T> createStyledComboBox(T... items) {
        ComboBox<T> cb = new ComboBox<>();
        if (items != null && items.length > 0) {
            cb.getItems().addAll(items);
            cb.setValue(items[0]);
        }
        cb.setStyle(AppTheme.getComboBoxStyle());
        cb.setOnMouseEntered(e -> {
            cb.setStyle(AppTheme.getComboBoxHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), cb);
            st.setToX(1.03);
            st.setToY(1.03);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        cb.setOnMouseExited(e -> {
            cb.setStyle(AppTheme.getComboBoxStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), cb);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        return cb;
    }

    /** Apply neon glow styling to an existing ComboBox */
    public static void styleComboBox(ComboBox<?> cb) {
        cb.setStyle(AppTheme.getComboBoxStyle());
        cb.setOnMouseEntered(e -> {
            cb.setStyle(AppTheme.getComboBoxHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), cb);
            st.setToX(1.03);
            st.setToY(1.03);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        cb.setOnMouseExited(e -> {
            cb.setStyle(AppTheme.getComboBoxStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), cb);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
    }

    // ═══════════════════════════════════════════════════════════════
    // CARD BUILDERS (with hover glow)
    // ═══════════════════════════════════════════════════════════════

    public static VBox createCard() {
        VBox card = new VBox(12);
        card.setStyle(AppTheme.getCardSolidStyle());
        setupHoverGlow(card);
        return card;
    }

    public static VBox createGlassCard() {
        VBox card = new VBox(12);
        card.setStyle(AppTheme.getCardGlassStyle());
        setupHoverGlow(card);
        return card;
    }

    public static VBox createStatCard(int index, String icon, String title, String value, String accentColor) {
        VBox card = new VBox(6);
        card.setStyle(AppTheme.getStatBlockStyle(index));
        card.setMinHeight(96);
        card.setPrefHeight(96);
        card.setMaxHeight(96);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font("Segoe UI", 18));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        titleLbl.setTextFill(Color.web(AppTheme.getTextMuted()));

        HBox topRow = new HBox(8, iconLbl, titleLbl);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(topRow, spacer, valLbl);
        setupHoverGlow(card);
        return card;
    }

    public static VBox createStatCard(String icon, String title, String value, String accentColor) {
        return createStatCard(0, icon, title, value, accentColor);
    }

    public static VBox createMetricCard(String title, String value, String accentColor) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; " +
                "-fx-border-color: " + accentColor + "44; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 14px 18px;");
        card.setMinHeight(96);
        card.setPrefHeight(96);
        card.setMaxHeight(96);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        titleLbl.setTextFill(Color.web(AppTheme.getTextMuted()));

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valLbl.setTextFill(Color.web(accentColor));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(titleLbl, spacer, valLbl);
        setupHoverGlow(card);
        return card;
    }

    /**
     * Creates a modern, non-blocking inline notification banner to replace disruptive modal dialogs.
     */
    public static HBox createInlineAlert(String title, String message, boolean isError, Runnable onClose) {
        HBox banner = new HBox(12);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(10, 16, 10, 16));

        String bg = isError ? "rgba(239, 68, 68, 0.12)" : "rgba(245, 158, 11, 0.12)";
        String border = isError ? "#EF4444" : "#F59E0B";
        String iconText = isError ? "⚠️" : "ℹ️";

        banner.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + border
                + "; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label iconLbl = new Label(iconText);
        iconLbl.setFont(Font.font("Segoe UI", 16));

        VBox textBox = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        titleLbl.setTextFill(Color.web(border));

        Label msgLbl = new Label(message);
        msgLbl.setFont(Font.font("Segoe UI", 12));
        msgLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));
        msgLbl.setWrapText(true);

        textBox.getChildren().addAll(titleLbl, msgLbl);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + border
                + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 2 6;");
        closeBtn.setOnAction(e -> {
            if (onClose != null) onClose.run();
        });

        banner.getChildren().addAll(iconLbl, textBox, closeBtn);
        com.freelancing.util.AnimationUtil.applySlideUp(banner, 240);
        return banner;
    }

    public static Label createBadge(String text, String bgColor, String fgColor) {
        Label badge = new Label(text);
        badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        badge.setStyle("-fx-background-color: " + bgColor + ";" +
                 "-fx-text-fill: " + fgColor + ";" +
                 "-fx-background-radius: 12;" +
                 "-fx-padding: 3 10 3 10;");
        return badge;
    }

    public static Label createSkillTag(String skill) {
        Label tag = new Label(skill);
        tag.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
        tag.setStyle("-fx-background-color: rgba(99, 102, 241, 0.15);" +
                     "-fx-text-fill: " + (AppTheme.isDarkMode() ? "#A5B4FC" : "#4F46E5") + ";" +
                     "-fx-border-color: rgba(99, 102, 241, 0.35);" +
                     "-fx-border-radius: 12;" +
                     "-fx-background-radius: 12;" +
                     "-fx-padding: 3 10 3 10;");
        return tag;
    }

    public static HBox createLiveStatusPill(String text) {
        HBox pill = new HBox(6);
        pill.setAlignment(Pos.CENTER);
        pill.setPadding(new Insets(4, 12, 4, 12));
        pill.setStyle("-fx-background-color: rgba(16, 185, 129, 0.15); " +
                "-fx-border-color: rgba(16, 185, 129, 0.4); " +
                "-fx-border-radius: 20; " +
                "-fx-background-radius: 20;");

        Label dot = new Label("●");
        dot.setTextFill(Color.web(COLOR_SUCCESS));
        dot.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        // Breathing animation for live dot
        ScaleTransition breathing = new ScaleTransition(Duration.millis(1000), dot);
        breathing.setFromX(1.0); breathing.setFromY(1.0);
        breathing.setToX(1.4); breathing.setToY(1.4);
        breathing.setCycleCount(Animation.INDEFINITE);
        breathing.setAutoReverse(true);
        breathing.setInterpolator(Interpolator.EASE_BOTH);
        breathing.play();

        Label label = new Label(text);
        label.setTextFill(Color.web("#10B981"));
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

        pill.getChildren().addAll(dot, label);
        return pill;
    }

    // Theme Switcher Button
    public static Button createThemeToggle(Runnable onToggle) {
        boolean dark = AppTheme.isDarkMode();
        Button btn = new Button(dark ? "🌙 Dark" : "☀️ Light");
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btn.setStyle(AppTheme.getBtnThemeToggleStyle());
        btn.setOnMouseEntered(e -> {
            btn.setStyle(AppTheme.getBtnThemeToggleHoverStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.08); st.setToY(1.08); st.setInterpolator(Interpolator.EASE_OUT); st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(AppTheme.getBtnThemeToggleStyle());
            ScaleTransition st = new ScaleTransition(Duration.millis(AppTheme.ANIM_FAST), btn);
            st.setToX(1.0); st.setToY(1.0); st.setInterpolator(Interpolator.EASE_OUT); st.play();
        });
        btn.setOnAction(e -> {
            AppTheme.toggleTheme();
            btn.setText(AppTheme.isDarkMode() ? "🌙 Dark" : "☀️ Light");
            btn.setStyle(AppTheme.getBtnThemeToggleStyle());
            if (onToggle != null) {
                onToggle.run();
            }
        });
        return btn;
    }

    // Currency Selector ComboBox with Neon Glow
    public static ComboBox<String> createCurrencySelector(Consumer<String> onCurrencyChange) {
        ComboBox<String> cb = createStyledComboBox("USD ($)", "EUR (€)", "INR (₹)", "GBP (£)");
        cb.setValue("USD ($)");
        cb.setOnAction(e -> {
            String val = cb.getValue();
            String code = val != null && val.contains("(") ? val.substring(0, 3) : "USD";
            AppTheme.setCurrentCurrency(code);
            if (onCurrencyChange != null) {
                onCurrencyChange.accept(code);
            }
        });
        return cb;
    }

    // Notification Bell Component with Shake + Pulse Badge
    public static StackPane createNotificationBell(int unreadCount, Runnable onBellClick) {
        StackPane stack = new StackPane();
        Button bellBtn = new Button("🔔");
        bellBtn.setFont(Font.font("Segoe UI", 15));
        bellBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4 8;");
        bellBtn.setOnAction(e -> {
            if (onBellClick != null) onBellClick.run();
        });

        bellBtn.setOnMouseEntered(e -> {
            RotateTransition rt = new RotateTransition(Duration.millis(100), bellBtn);
            rt.setFromAngle(0);
            rt.setByAngle(15);
            rt.setCycleCount(6);
            rt.setAutoReverse(true);
            rt.setInterpolator(Interpolator.EASE_BOTH);
            rt.play();
        });

        stack.getChildren().add(bellBtn);

        if (unreadCount > 0) {
            Label badge = new Label(String.valueOf(unreadCount));
            badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
            badge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 1 5;");
            StackPane.setAlignment(badge, Pos.TOP_RIGHT);
            ScaleTransition pulse = new ScaleTransition(Duration.millis(600), badge);
            pulse.setFromX(1.0); pulse.setFromY(1.0);
            pulse.setToX(1.2); pulse.setToY(1.2);
            pulse.setCycleCount(Animation.INDEFINITE);
            pulse.setAutoReverse(true);
            pulse.setInterpolator(Interpolator.EASE_BOTH);
            pulse.play();
            stack.getChildren().add(badge);
        }

        return stack;
    }

    // Rating Stars Component
    public static HBox createRatingStars(double rating) {
        HBox stars = new HBox(3);
        stars.setAlignment(Pos.CENTER_LEFT);

        int fullStars = (int) Math.floor(rating);
        boolean hasHalf = (rating - fullStars) >= 0.4;

        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < fullStars ? "★" : (i == fullStars && hasHalf ? "★" : "☆"));
            star.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            star.setTextFill(i < fullStars || (i == fullStars && hasHalf) ? Color.web(COLOR_AMBER) : Color.web("#94A3B8"));
            stars.getChildren().add(star);
        }

        Label score = new Label(String.format(" %.1f", rating));
        score.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        score.setTextFill(Color.web(AppTheme.getTextPrimary()));
        stars.getChildren().add(score);

        return stars;
    }

    // Milestone Stepper Component
    public static HBox createMilestoneStepper(int currentStep, String[] stepTitles) {
        HBox stepper = new HBox(8);
        stepper.setAlignment(Pos.CENTER_LEFT);
        stepper.setPadding(new Insets(8, 0, 8, 0));

        for (int i = 0; i < stepTitles.length; i++) {
            boolean isCompleted = i < currentStep;
            boolean isCurrent = i == currentStep;

            HBox stepNode = new HBox(6);
            stepNode.setAlignment(Pos.CENTER);

            Label circle = new Label(isCompleted ? "✓" : String.valueOf(i + 1));
            circle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
            String inactiveBg = AppTheme.isDarkMode() ? "#334155" : "#BACADB";
            String inactiveFg = AppTheme.isDarkMode() ? "white" : "#1E293B";
            circle.setStyle("-fx-background-color: " + (isCompleted ? COLOR_SUCCESS : (isCurrent ? COLOR_PRIMARY : inactiveBg)) + ";" +
                            "-fx-text-fill: " + (isCompleted || isCurrent ? "white" : inactiveFg) + ";" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 3 8;");

            Label title = new Label(stepTitles[i]);
            title.setFont(Font.font("Segoe UI", isCurrent ? FontWeight.BOLD : FontWeight.NORMAL, 11));
            title.setTextFill(Color.web(isCurrent ? AppTheme.getTextPrimary() : AppTheme.getTextMuted()));

            stepNode.getChildren().addAll(circle, title);
            stepper.getChildren().add(stepNode);

            if (i < stepTitles.length - 1) {
                Label arrow = new Label("➔");
                arrow.setTextFill(Color.web(AppTheme.getTextMuted()));
                arrow.setFont(Font.font("Segoe UI", 10));
                stepper.getChildren().add(arrow);
            }
        }

        return stepper;
    }

    public static javafx.scene.SubScene createSubScene(javafx.scene.Parent root, double width, double height) {
        return new javafx.scene.SubScene(root, width, height, false, javafx.scene.SceneAntialiasing.BALANCED);
    }
                    
                    
    public static LineChart<String, Number> createLineChart(String title, String xLabel, String yLabel, Map<String, Double> data) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (data != null) {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        chart.getData().add(series);
        return chart;
    }

    public static PieChart createPieChart(String title, Map<String, Double> data) {
        PieChart chart = new PieChart();
        chart.setTitle(title);
            
        chart.setStyle("-fx-background-color: transparent;");
        if (data != null) {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                chart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        return chart;
    }

    public static BarChart<String, Number> createBarChart(String title, String xLabel, String yLabel, Map<String, Double> data) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (data != null) {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        chart.getData().add(series);
        return chart;
    }

    public static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Runnable action = () -> {
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.72);");
            overlay.setPadding(new Insets(20));

            VBox card = new VBox(14);
            card.setMaxWidth(460);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(24));
            card.setStyle("-fx-background-color: " + AppTheme.getBgCard() + ";"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: " + AppTheme.getBorderCard() + ";"
                    + "-fx-border-radius: 12;"
                    + "-fx-border-width: 1.5;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 24, 0, 0, 8);");

            String iconSymbol = "ℹ";
            String iconColor = "#3B82F6";
            if (type == Alert.AlertType.ERROR) {
                iconSymbol = "✕";
                iconColor = "#EF4444";
            } else if (type == Alert.AlertType.WARNING) {
                iconSymbol = "⚠";
                iconColor = "#F59E0B";
            } else if (type == Alert.AlertType.CONFIRMATION) {
                iconSymbol = "?";
                iconColor = "#8B5CF6";
            }

            Label lblIcon = new Label(iconSymbol);
            lblIcon.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white; "
                    + "-fx-background-color: " + iconColor + "; -fx-background-radius: 28; "
                    + "-fx-min-width: 48px; -fx-min-height: 48px; -fx-alignment: center;");

            Label lblTitle = new Label(title != null ? title : "Notification");
            lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            lblTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

            VBox textGroup = new VBox(6);
            textGroup.setAlignment(Pos.CENTER);
            if (header != null && !header.trim().isEmpty()) {
                Label lblHeader = new Label(header);
                lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                lblHeader.setTextFill(Color.web(AppTheme.getTextPrimary()));
                lblHeader.setWrapText(true);
                lblHeader.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                textGroup.getChildren().add(lblHeader);
            }

            if (content != null && !content.trim().isEmpty()) {
                Label lblContent = new Label(content);
                lblContent.setFont(Font.font("Segoe UI", 13));
                lblContent.setTextFill(Color.web(AppTheme.getTextMuted()));
                lblContent.setWrapText(true);
                lblContent.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                textGroup.getChildren().add(lblContent);
            }

            Button btnOk = createPrimaryButton("OK");
            btnOk.setPrefWidth(120);
            btnOk.setOnAction(e -> NavigationManager.getInstance().closeModal());

            overlay.setOnMouseClicked(e -> {
                if (e.getTarget() == overlay) {
                    NavigationManager.getInstance().closeModal();
                }
            });

            card.getChildren().addAll(lblIcon, lblTitle, textGroup, btnOk);
            overlay.getChildren().add(card);

            NavigationManager.getInstance().showModal(overlay);
        };

        if (javafx.application.Platform.isFxApplicationThread()) {
            action.run();
        } else {
            try {
                javafx.application.Platform.runLater(action);
            } catch (Exception e) {
                System.out.println("[SkillBridge-Alert] " + type + " | " + title + ": " + header + " - " + content);
            }
        }
    }

    public static void showConfirmDialog(String title, String message, Runnable onConfirm) {
        Runnable action = () -> {
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.72);");
            overlay.setPadding(new Insets(20));

            VBox card = new VBox(16);
            card.setMaxWidth(460);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(24));
            card.setStyle("-fx-background-color: " + AppTheme.getBgCard() + ";"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: " + AppTheme.getBorderCard() + ";"
                    + "-fx-border-radius: 12;"
                    + "-fx-border-width: 1.5;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 24, 0, 0, 8);");

            Label lblIcon = new Label("?");
            lblIcon.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white; "
                    + "-fx-background-color: #8B5CF6; -fx-background-radius: 28; "
                    + "-fx-min-width: 48px; -fx-min-height: 48px; -fx-alignment: center;");

            Label lblTitle = new Label(title != null ? title : "Confirmation");
            lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            lblTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

            Label lblMsg = new Label(message);
            lblMsg.setFont(Font.font("Segoe UI", 13));
            lblMsg.setTextFill(Color.web(AppTheme.getTextMuted()));
            lblMsg.setWrapText(true);
            lblMsg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            HBox actions = new HBox(12);
            actions.setAlignment(Pos.CENTER);

            Button btnCancel = createSecondaryButton("Cancel");
            btnCancel.setOnAction(e -> NavigationManager.getInstance().closeModal());

            Button btnYes = createPrimaryButton("Confirm");
            btnYes.setOnAction(e -> {
                NavigationManager.getInstance().closeModal();
                if (onConfirm != null) onConfirm.run();
            });

            actions.getChildren().addAll(btnCancel, btnYes);
            card.getChildren().addAll(lblIcon, lblTitle, lblMsg, actions);
            overlay.getChildren().add(card);

            NavigationManager.getInstance().showModal(overlay);
        };

        if (javafx.application.Platform.isFxApplicationThread()) {
            action.run();
        } else {
            try {
                javafx.application.Platform.runLater(action);
            } catch (Exception e) {
                if (onConfirm != null) onConfirm.run();
            }
        }
    }

    public static void showPromptDialog(String title, String header, String prompt, String defaultValue, Consumer<String> onResult) {
        Runnable action = () -> {
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.72);");
            overlay.setPadding(new Insets(20));

            VBox card = new VBox(14);
            card.setMaxWidth(480);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(24));
            card.setStyle("-fx-background-color: " + AppTheme.getBgCard() + ";"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: " + AppTheme.getBorderCard() + ";"
                    + "-fx-border-radius: 12;"
                    + "-fx-border-width: 1.5;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 24, 0, 0, 8);");

            Label lblTitle = new Label(title != null ? title : "Input Required");
            lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            lblTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

            if (header != null && !header.trim().isEmpty()) {
                Label lblHeader = new Label(header);
                lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                lblHeader.setTextFill(Color.web(AppTheme.getTextMuted()));
                lblHeader.setWrapText(true);
                card.getChildren().addAll(lblTitle, lblHeader);
            } else {
                card.getChildren().add(lblTitle);
            }

            if (prompt != null && !prompt.trim().isEmpty()) {
                Label lblPrompt = new Label(prompt);
                lblPrompt.setFont(Font.font("Segoe UI", 12));
                lblPrompt.setTextFill(Color.web(AppTheme.getTextPrimary()));
                card.getChildren().add(lblPrompt);
            }

            TextField txtInput = createTextField("Enter text...");
            if (defaultValue != null) {
                txtInput.setText(defaultValue);
            }
            card.getChildren().add(txtInput);

            HBox actions = new HBox(12);
            actions.setAlignment(Pos.CENTER_RIGHT);

            Button btnCancel = createSecondaryButton("Cancel");
            btnCancel.setOnAction(e -> NavigationManager.getInstance().closeModal());

            Button btnSubmit = createPrimaryButton("Submit");
            btnSubmit.setOnAction(e -> {
                String val = txtInput.getText();
                NavigationManager.getInstance().closeModal();
                if (onResult != null) onResult.accept(val);
            });
            txtInput.setOnAction(e -> btnSubmit.fire());

            actions.getChildren().addAll(btnCancel, btnSubmit);
            card.getChildren().add(actions);
            overlay.getChildren().add(card);

            NavigationManager.getInstance().showModal(overlay);
            javafx.application.Platform.runLater(txtInput::requestFocus);
        };

        if (javafx.application.Platform.isFxApplicationThread()) {
            action.run();
        } else {
            try {
                javafx.application.Platform.runLater(action);
            } catch (Exception ignored) {}
        }
    }

    public static void showModalOverlay(String title, Node content, String confirmText, Runnable onConfirm) {
        Runnable action = () -> {
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.72);");
            overlay.setPadding(new Insets(24));

            VBox card = new VBox(14);
            card.setMaxWidth(620);
            card.setMaxHeight(680);
            card.setPadding(new Insets(20));
            card.setStyle("-fx-background-color: " + AppTheme.getBgCard() + ";"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: " + AppTheme.getBorderCard() + ";"
                    + "-fx-border-radius: 12;"
                    + "-fx-border-width: 1.5;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 28, 0, 0, 10);");

            // Header Bar
            HBox headerBar = new HBox(12);
            headerBar.setAlignment(Pos.CENTER_LEFT);
            Label lblTitle = new Label(title != null ? title : "Dialog");
            lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            lblTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btnClose = new Button("✕");
            btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-size: 14px; -fx-cursor: hand;");
            btnClose.setOnAction(e -> NavigationManager.getInstance().closeModal());
            headerBar.getChildren().addAll(lblTitle, spacer, btnClose);

            // Body
            ScrollPane scrollBody = createScrollPane(content);
            VBox.setVgrow(scrollBody, Priority.ALWAYS);

            // Footer
            HBox footer = new HBox(12);
            footer.setAlignment(Pos.CENTER_RIGHT);

            Button btnCancel = createSecondaryButton("Cancel");
            btnCancel.setOnAction(e -> NavigationManager.getInstance().closeModal());
            footer.getChildren().add(btnCancel);

            if (confirmText != null && onConfirm != null) {
                Button btnOk = createPrimaryButton(confirmText);
                btnOk.setOnAction(e -> {
                    NavigationManager.getInstance().closeModal();
                    onConfirm.run();
                });
                footer.getChildren().add(btnOk);
            }

            card.getChildren().addAll(headerBar, scrollBody, footer);
            overlay.getChildren().add(card);

            NavigationManager.getInstance().showModal(overlay);
        };

        if (javafx.application.Platform.isFxApplicationThread()) {
            action.run();
        } else {
            try {
                javafx.application.Platform.runLater(action);
            } catch (Exception ignored) {}
        }
    }

    public static void closeModalOverlay() {
        NavigationManager.getInstance().closeModal();
    }

    public static ScrollPane createScrollPane(Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        styleScrollPane(sp);
        return sp;
    }

    public static void styleScrollPane(ScrollPane sp) {
        if (sp == null) return;
        sp.setFitToWidth(true);
        sp.setStyle(AppTheme.getScrollpaneStyle());
        sp.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Node vp = sp.lookup(".viewport");
                if (vp != null) {
                    vp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
                }
            }
        });
        javafx.application.Platform.runLater(() -> {
            Node vp = sp.lookup(".viewport");
            if (vp != null) {
                vp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            }
        });
    }
}

            
