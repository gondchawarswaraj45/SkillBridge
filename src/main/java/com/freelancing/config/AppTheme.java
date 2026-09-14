package com.freelancing.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Centralized Design System & Dual Theme Engine (Dark 🌙 / Light ☀️).
 * Supports 100% programmatic inline CSS and multi-currency formatting.
 */
public class AppTheme {

    public enum ThemeMode {
        DARK, LIGHT
    }

    private static ThemeMode currentTheme = ThemeMode.DARK;
    private static String currentCurrency = "USD";
    private static final List<Consumer<ThemeMode>> themeListeners = new ArrayList<>();

    // Primary Accents (Harmonious across both dark & light themes)
    public static final String COLOR_PRIMARY = "#6366F1";
    public static final String COLOR_PRIMARY_HOVER = "#4F46E5";
    public static final String COLOR_ACCENT = "#8B5CF6";
    public static final String COLOR_SUCCESS = "#10B981";
    public static final String COLOR_AMBER = "#F59E0B";
    public static final String COLOR_DANGER = "#EF4444";

    // Base Theme Color Fallbacks
    public static final String COLOR_BG_DARK = "#0B0F19";
    public static final String COLOR_BG_PANEL = "#0F172A";
    public static final String COLOR_BG_CARD = "#1E293B";
    public static final String COLOR_BG_INPUT = "#1E293B";
    public static final String COLOR_BORDER = "#334155";

    // Theme Management
    public static ThemeMode getCurrentTheme() {
        return currentTheme;
    }

    public static boolean isDarkMode() {
        return currentTheme == ThemeMode.DARK;
    }

    public static void toggleTheme() {
        setTheme(currentTheme == ThemeMode.DARK ? ThemeMode.LIGHT : ThemeMode.DARK);
    }

    public static void toggleDarkMode() {
        toggleTheme();
    }

    public static void setTheme(ThemeMode mode) {
        currentTheme = mode;
        for (Consumer<ThemeMode> listener : themeListeners) {
            try {
                listener.accept(currentTheme);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void addThemeListener(Consumer<ThemeMode> listener) {
        if (!themeListeners.contains(listener)) {
            themeListeners.add(listener);
        }
    }

    // Currency Management (USD, EUR, INR, GBP)
    public static String getCurrentCurrency() {
        return currentCurrency;
    }

    public static void setCurrentCurrency(String currency) {
        currentCurrency = currency;
    }

    public static String formatCurrency(double usdAmount) {
        switch (currentCurrency) {
            case "EUR":
                return String.format("€%.0f", usdAmount * 0.92);
            case "INR":
                return String.format("₹%.0f", usdAmount * 83.5);
            case "GBP":
                return String.format("£%.0f", usdAmount * 0.79);
            case "USD":
            default:
                return String.format("$%.0f", usdAmount);
        }
    }

    // Dynamic Color Getters (Zero White in Light Mode - Rich Vibrant Palette)
    public static String getBgDark() {
        return isDarkMode() ? "#0B0F19" : "#7C98C2";
    }

    public static String getBgPanel() {
        return isDarkMode() ? "#0F172A" : "#6D89B4";
    }

    public static String getBgCard() {
        return isDarkMode() ? "#1E293B" : "#8BA3C7";
    }

    public static String getBgInput() {
        return isDarkMode() ? "#1E293B" : "#7B96BD";
    }

    public static String getBorderColor() {
        return isDarkMode() ? "#334155" : "#4A6894";
    }

    public static String getBorderCard() {
        return isDarkMode() ? "#334155" : "#CBD5E1";
    }

    public static String getTextPrimary() {
        return isDarkMode() ? "#F8FAFC" : "#070E1E";
    }

    public static String getTextMuted() {
        return isDarkMode() ? "#94A3B8" : "#1B2A44";
    }

    public static String getTextSecondary() {
        return isDarkMode() ? "#CBD5E1" : "#0A1428";
    }

    // Dynamic Inline Style Templates
    public static void applyAppStylesheet(Scene scene) {
        if (scene == null) return;
        try {
            java.net.URL url = AppTheme.class.getResource("/styles.css");
            if (url != null) {
                String css = url.toExternalForm();
                if (!scene.getStylesheets().contains(css)) {
                    scene.getStylesheets().add(css);
                }
            }
        } catch (Exception ignored) {}
    }

    public static void applyAppStylesheet(Parent root) {
        if (root == null) return;
        try {
            java.net.URL url = AppTheme.class.getResource("/styles.css");
            if (url != null) {
                String css = url.toExternalForm();
                if (!root.getStylesheets().contains(css)) {
                    root.getStylesheets().add(css);
                }
            }
        } catch (Exception ignored) {}
    }

    public static String getRootStyle() {
        return isDarkMode()
            ? "-fx-font-family: 'Segoe UI', -apple-system, sans-serif; -fx-background-color: linear-gradient(to bottom right, #0B0F19 0%, #111827 50%, #17182E 100%);"
            : "-fx-font-family: 'Segoe UI', -apple-system, sans-serif; -fx-background-color: linear-gradient(to bottom right, #7C98C2 0%, #9082BF 35%, #6CA2C0 70%, #76A99A 100%);";
    }

    public static String getScrollpaneStyle() {
        return "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;";
    }

    public static String getNavbarStyle() {
        return isDarkMode()
            ? "-fx-background-color: #0F172A; -fx-border-color: #334155; -fx-border-width: 0 0 1 0;"
            : "-fx-background-color: linear-gradient(to right, #4338CA, #6366F1, #7C3AED); -fx-border-color: #3730A3; -fx-border-width: 0 0 1 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 12, 0, 0, 3);";
    }

    public static String getTickerStyle() {
        return isDarkMode()
            ? "-fx-background-color: #111827; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-width: 0 0 1 0;"
            : "-fx-background-color: linear-gradient(to right, #3730A3, #4338CA); -fx-border-color: rgba(99, 102, 241, 0.4); -fx-border-width: 0 0 1 0;";
    }

    public static String getHeroBannerStyle() {
        return isDarkMode()
            ? "-fx-background-color: linear-gradient(to right, rgba(30, 41, 59, 0.7), rgba(15, 23, 42, 0.95)); -fx-border-color: rgba(99, 102, 241, 0.35); -fx-border-radius: 16; -fx-background-radius: 16;"
            : "-fx-background-color: linear-gradient(to bottom right, #93C5FD 0%, #C4B5FD 50%, #A5B4FC 100%); -fx-border-color: #4F46E5; -fx-border-width: 2; -fx-border-radius: 16; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.25), 18, 0, 0, 4);";
    }

    public static String getCardGlassStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(30, 41, 59, 0.75); -fx-border-color: rgba(255, 255, 255, 0.08); -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 18;"
            : "-fx-background-color: linear-gradient(to bottom right, #9CB3D6 0%, #AE9FDB 100%); -fx-border-color: #584ED3; -fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 14, 0, 0, 4);";
    }

    public static String getCardSolidStyle() {
        return isDarkMode()
            ? "-fx-background-color: " + getBgCard() + "; -fx-border-color: " + getBorderColor() + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;"
            : "-fx-background-color: linear-gradient(to bottom right, #95ADD0 0%, #A396CE 100%); -fx-border-color: #4F46E5; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 3);";
    }

    // Diverse Colorful Block Styles (AI, Escrow, Stats, Projects, Freelancers, Trust)
    public static String getBlockColorAi() {
        return isDarkMode()
            ? "rgba(30, 41, 59, 0.85)"
            : "linear-gradient(to bottom right, #C4B5FD 0%, #DDD6FE 100%)";
    }

    public static String getBlockBorderAi() {
        return isDarkMode() ? "#8B5CF6" : "#7C3AED";
    }

    public static String getBlockColorEscrow() {
        return isDarkMode()
            ? "rgba(30, 41, 59, 0.85)"
            : "linear-gradient(to bottom right, #86EFAC 0%, #6EE7B7 100%)";
    }

    public static String getBlockBorderEscrow() {
        return isDarkMode() ? "#10B981" : "#059669";
    }

    public static String getStatBlockStyle(int index) {
        if (isDarkMode()) {
            switch (index % 4) {
                case 0:
                    return "-fx-background-color: linear-gradient(to bottom right, #1E293B 0%, #172554 100%); -fx-border-color: #3B82F6; -fx-border-width: 0 0 0 4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);";
                case 1:
                    return "-fx-background-color: linear-gradient(to bottom right, #1E293B 0%, #064E3B 100%); -fx-border-color: #10B981; -fx-border-width: 0 0 0 4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);";
                case 2:
                    return "-fx-background-color: linear-gradient(to bottom right, #1E293B 0%, #451A03 100%); -fx-border-color: #F59E0B; -fx-border-width: 0 0 0 4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);";
                case 3:
                default:
                    return "-fx-background-color: linear-gradient(to bottom right, #1E293B 0%, #3B0764 100%); -fx-border-color: #8B5CF6; -fx-border-width: 0 0 0 4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);";
            }
        }
        switch (index % 4) {
            case 0: // Sky Blue
                return "-fx-background-color: linear-gradient(to bottom right, #93C5FD 0%, #60A5FA 100%); -fx-border-color: #2563EB; -fx-border-width: 0 0 0 5; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(37,99,235,0.25), 8, 0, 0, 2);";
            case 1: // Mint Green
                return "-fx-background-color: linear-gradient(to bottom right, #86EFAC 0%, #4ADE80 100%); -fx-border-color: #16A34A; -fx-border-width: 0 0 0 5; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(22,163,74,0.25), 8, 0, 0, 2);";
            case 2: // Amber Golden
                return "-fx-background-color: linear-gradient(to bottom right, #FDE047 0%, #FBBF24 100%); -fx-border-color: #D97706; -fx-border-width: 0 0 0 5; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(217,119,6,0.25), 8, 0, 0, 2);";
            case 3: // Purple Lilac
            default:
                return "-fx-background-color: linear-gradient(to bottom right, #C4B5FD 0%, #A78BFA 100%); -fx-border-color: #7C3AED; -fx-border-width: 0 0 0 5; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14 18; -fx-effect: dropshadow(gaussian, rgba(124,58,237,0.25), 8, 0, 0, 2);";
        }
    }

    public static String getProjectCardBlockStyle(String category) {
        if (isDarkMode()) {
            return getCardSolidStyle();
        }
        String cat = category != null ? category.toLowerCase() : "";
        if (cat.contains("web") || cat.contains("full")) {
            return "-fx-background-color: #93C5FD; -fx-border-color: #3B82F6; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.18), 10, 0, 0, 3);";
        } else if (cat.contains("mobile") || cat.contains("flutter")) {
            return "-fx-background-color: #67E8F9; -fx-border-color: #0891B2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(8,145,178,0.18), 10, 0, 0, 3);";
        } else if (cat.contains("ai") || cat.contains("machine")) {
            return "-fx-background-color: #C4B5FD; -fx-border-color: #7C3AED; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(124,58,237,0.18), 10, 0, 0, 3);";
        } else if (cat.contains("ui") || cat.contains("design")) {
            return "-fx-background-color: #FDA4AF; -fx-border-color: #E11D48; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(225,29,72,0.18), 10, 0, 0, 3);";
        } else if (cat.contains("cloud") || cat.contains("devops")) {
            return "-fx-background-color: #86EFAC; -fx-border-color: #16A34A; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(22,163,74,0.18), 10, 0, 0, 3);";
        } else {
            return "-fx-background-color: #A5B4FC; -fx-border-color: #4F46E5; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(79,70,229,0.18), 10, 0, 0, 3);";
        }
    }

    public static String getFreelancerCardBlockStyle(int index) {
        if (isDarkMode()) {
            return getCardSolidStyle();
        }
        switch (index % 3) {
            case 0:
                return "-fx-background-color: #93C5FD; -fx-border-color: #3B82F6; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.18), 10, 0, 0, 3);";
            case 1:
                return "-fx-background-color: #C4B5FD; -fx-border-color: #7C3AED; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(124,58,237,0.18), 10, 0, 0, 3);";
            case 2:
            default:
                return "-fx-background-color: #86EFAC; -fx-border-color: #16A34A; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(22,163,74,0.18), 10, 0, 0, 3);";
        }
    }

    public static String getTrustCardBlockStyle(int index) {
        if (isDarkMode()) {
            return getCardSolidStyle();
        }
        switch (index % 3) {
            case 0: // Escrow Protection
                return "-fx-background-color: #86EFAC; -fx-border-color: #10B981; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(16,185,129,0.2), 10, 0, 0, 3);";
            case 1: // AI Compatibility
                return "-fx-background-color: #C4B5FD; -fx-border-color: #8B5CF6; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(139,92,246,0.2), 10, 0, 0, 3);";
            case 2: // Dispute Center
            default:
                return "-fx-background-color: #FCD34D; -fx-border-color: #F59E0B; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(245,158,11,0.2), 10, 0, 0, 3);";
        }
    }

    public static String getInputStyle() {
        return "-fx-background-color: " + getBgInput() + ";" +
               "-fx-text-fill: " + getTextPrimary() + ";" +
               "-fx-prompt-text-fill: " + getTextMuted() + ";" +
               "-fx-border-color: " + getBorderColor() + ";" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8 12;";
    }

    public static String getComboboxStyle() {
        return getComboBoxStyle();
    }

    public static String getBtnPrimaryStyle() {
        return "-fx-background-color: linear-gradient(to right, #6366F1, #4F46E5);" +
               "-fx-text-fill: white;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8 18 8 18;" +
               "-fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, rgba(99, 102, 241, 0.35), 8, 0, 0, 3);";
    }

    public static String getBtnPrimaryHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #818CF8, #6366F1);" +
               "-fx-text-fill: white;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8 18 8 18;" +
               "-fx-cursor: hand;";
    }

    public static String getBtnSecondaryStyle() {
        return isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9; -fx-border-color: #334155; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"
            : "-fx-background-color: #CAD8EA; -fx-text-fill: #1E293B; -fx-font-weight: 600; -fx-border-color: #A6BCD6; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 1);";
    }

    public static String getBtnSecondaryHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: #334155; -fx-text-fill: #FFFFFF; -fx-border-color: #6366F1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"
            : "-fx-background-color: #BDCDE2; -fx-text-fill: #4338CA; -fx-font-weight: 600; -fx-border-color: #6366F1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;";
    }

    // Diverse Light Pastel Button Styles (Client Demo, Freelancer Demo, Theme Toggle, Export, Chips)
    public static String getBtnClientDemoStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(16, 185, 129, 0.18); -fx-text-fill: #34D399; -fx-font-weight: bold; -fx-border-color: rgba(16, 185, 129, 0.4); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"
            : "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-font-weight: bold; -fx-border-color: #6EE7B7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(16,185,129,0.2), 6, 0, 0, 2);";
    }

    public static String getBtnClientDemoHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(16, 185, 129, 0.32); -fx-text-fill: #6EE7B7; -fx-font-weight: bold; -fx-border-color: #34D399; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"
            : "-fx-background-color: #A7F3D0; -fx-text-fill: #064E3B; -fx-font-weight: bold; -fx-border-color: #34D399; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;";
    }

    public static String getBtnFreelancerDemoStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(139, 92, 246, 0.18); -fx-text-fill: #A78BFA; -fx-font-weight: bold; -fx-border-color: rgba(139, 92, 246, 0.4); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"
            : "-fx-background-color: #EDE9FE; -fx-text-fill: #5B21B6; -fx-font-weight: bold; -fx-border-color: #C4B5FD; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(139,92,246,0.2), 6, 0, 0, 2);";
    }

    public static String getBtnFreelancerDemoHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(139, 92, 246, 0.32); -fx-text-fill: #DDD6FE; -fx-font-weight: bold; -fx-border-color: #A78BFA; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"
            : "-fx-background-color: #DDD6FE; -fx-text-fill: #4C1D95; -fx-font-weight: bold; -fx-border-color: #A78BFA; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;";
    }

    public static String getBtnThemeToggleStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(245, 158, 11, 0.18); -fx-text-fill: #FCD34D; -fx-font-weight: bold; -fx-border-color: rgba(245, 158, 11, 0.4); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"
            : "-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-font-weight: bold; -fx-border-color: #FCD34D; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(245,158,11,0.2), 6, 0, 0, 2);";
    }

    public static String getBtnThemeToggleHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(245, 158, 11, 0.32); -fx-text-fill: #FEF3C7; -fx-font-weight: bold; -fx-border-color: #F59E0B; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"
            : "-fx-background-color: #FDE68A; -fx-text-fill: #78350F; -fx-font-weight: bold; -fx-border-color: #F59E0B; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;";
    }

    public static String getBtnExportStyle() {
        return isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-border-color: #334155; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"
            : "-fx-background-color: #E0F2FE; -fx-text-fill: #075985; -fx-font-weight: bold; -fx-border-color: #7DD3FC; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(14,165,233,0.18), 6, 0, 0, 2);";
    }

    public static String getBtnExportHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: #334155; -fx-text-fill: #7DD3FC; -fx-font-weight: bold; -fx-border-color: #38BDF8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;"
            : "-fx-background-color: #BAE6FD; -fx-text-fill: #0369A1; -fx-font-weight: bold; -fx-border-color: #38BDF8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14; -fx-cursor: hand;";
    }

    public static String getBtnHireHeroStyle() {
        return isDarkMode()
            ? "-fx-background-color: linear-gradient(to right, #6366F1, #4F46E5); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand;"
            : "-fx-background-color: #E0E7FF; -fx-text-fill: #3730A3; -fx-font-weight: bold; -fx-border-color: #A5B4FC; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.2), 6, 0, 0, 2);";
    }

    public static String getBtnHireHeroHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: linear-gradient(to right, #818CF8, #6366F1); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand;"
            : "-fx-background-color: #C7D2FE; -fx-text-fill: #312E81; -fx-font-weight: bold; -fx-border-color: #818CF8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand;";
    }

    public static String getBtnJoinFreelancerHeroStyle() {
        return isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-text-fill: #F43F5E; -fx-font-weight: bold; -fx-border-color: rgba(244, 63, 94, 0.4); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand;"
            : "-fx-background-color: #FFE4E6; -fx-text-fill: #9F1239; -fx-font-weight: bold; -fx-border-color: #FDA4AF; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(244,63,94,0.18), 6, 0, 0, 2);";
    }

    public static String getBtnJoinFreelancerHeroHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: #334155; -fx-text-fill: #FB7185; -fx-font-weight: bold; -fx-border-color: #F43F5E; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand;"
            : "-fx-background-color: #FECDD3; -fx-text-fill: #881337; -fx-font-weight: bold; -fx-border-color: #FB7185; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 18 8 18; -fx-cursor: hand;";
    }

    public static String getBtnQuickViewStyle() {
        return isDarkMode()
            ? "-fx-background-color: #334155; -fx-text-fill: #CBD5E1; -fx-font-weight: bold; -fx-border-color: #475569; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;"
            : "-fx-background-color: #CADAEF; -fx-text-fill: #1E3A8A; -fx-font-weight: bold; -fx-border-color: #93C5FD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;";
    }

    public static String getBtnQuickViewHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: #475569; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-border-color: #6366F1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;"
            : "-fx-background-color: #BED2EB; -fx-text-fill: #172554; -fx-font-weight: bold; -fx-border-color: #60A5FA; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;";
    }

    public static String getBtnPortfolioStyle() {
        return isDarkMode()
            ? "-fx-background-color: #334155; -fx-text-fill: #CBD5E1; -fx-font-weight: bold; -fx-border-color: #475569; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;"
            : "-fx-background-color: #E6DCF8; -fx-text-fill: #5B21B6; -fx-font-weight: bold; -fx-border-color: #C4B5FD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;";
    }

    public static String getBtnPortfolioHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: #475569; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-border-color: #8B5CF6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;"
            : "-fx-background-color: #D8CBF5; -fx-text-fill: #4C1D95; -fx-font-weight: bold; -fx-border-color: #A78BFA; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-cursor: hand;";
    }

    public static String getSkillChipStyle(String chipName, boolean active) {
        if (active) {
            return "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.35), 6, 0, 0, 2);";
        }
        if (isDarkMode()) {
            return "-fx-background-color: #1E293B; -fx-text-fill: #F8FAFC; -fx-font-weight: 600; -fx-border-color: #334155; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        }
        // Diverse Pastel Tints for Each Tech Chip in Light Theme
        String name = chipName != null ? chipName.toLowerCase() : "";
        if (name.contains("all")) {
            return "-fx-background-color: #E0E7FF; -fx-text-fill: #3730A3; -fx-font-weight: bold; -fx-border-color: #A5B4FC; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        } else if (name.contains("java")) {
            return "-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-font-weight: bold; -fx-border-color: #FCD34D; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        } else if (name.contains("react")) {
            return "-fx-background-color: #CFFAFE; -fx-text-fill: #155E75; -fx-font-weight: bold; -fx-border-color: #67E8F9; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        } else if (name.contains("python")) {
            return "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-font-weight: bold; -fx-border-color: #6EE7B7; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        } else if (name.contains("cloud")) {
            return "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-font-weight: bold; -fx-border-color: #93C5FD; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        } else if (name.contains("flutter")) {
            return "-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-font-weight: bold; -fx-border-color: #7DD3FC; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        } else if (name.contains("ui") || name.contains("design")) {
            return "-fx-background-color: #FFE4E6; -fx-text-fill: #9F1239; -fx-font-weight: bold; -fx-border-color: #FDA4AF; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        } else {
            return "-fx-background-color: #DCE6F5; -fx-text-fill: #1E293B; -fx-font-weight: bold; -fx-border-color: #BACADB; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;";
        }
    }

    public static String getBtnAccentStyle() {
        return "-fx-background-color: linear-gradient(to right, #6366F1, #8B5CF6);" +
               "-fx-text-fill: white;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8 18 8 18;" +
               "-fx-cursor: hand;";
    }

    public static String getBtnAccentHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #818CF8, #A78BFA);" +
               "-fx-text-fill: white;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8 18 8 18;" +
               "-fx-cursor: hand;";
    }

    public static String getBtnDangerStyle() {
        return "-fx-background-color: #EF4444;" +
               "-fx-text-fill: white;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8 16 8 16;" +
               "-fx-cursor: hand;";
    }

    public static String getBtnSuccessStyle() {
        return "-fx-background-color: #10B981;" +
               "-fx-text-fill: white;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8 16 8 16;" +
               "-fx-cursor: hand;";
    }

    // ═══════════════════════════════════════════════════════════════
    // CRAZY COMBOBOX / SELECT BAR STYLES (Neon Glow, Gradient Borders)
    // ═══════════════════════════════════════════════════════════════
    public static String getComboBoxStyle() {
        return isDarkMode()
            ? "-fx-background-color: linear-gradient(to bottom, #1E293B, #0F172A);" +
              "-fx-border-color: linear-gradient(to right, #6366F1, #8B5CF6);" +
              "-fx-border-width: 2;" +
              "-fx-border-radius: 10;" +
              "-fx-background-radius: 10;" +
              "-fx-padding: 4 8;" +
              "-fx-cursor: hand;" +
              "-fx-effect: dropshadow(gaussian, rgba(99, 102, 241, 0.4), 12, 0, 0, 0);"
            : "-fx-background-color: linear-gradient(to bottom right, #E0E7FF 0%, #EDE9FE 50%, #DBEAFE 100%);" +
              "-fx-border-color: linear-gradient(to right, #818CF8, #A78BFA);" +
              "-fx-border-width: 2;" +
              "-fx-border-radius: 10;" +
              "-fx-background-radius: 10;" +
              "-fx-padding: 4 8;" +
              "-fx-cursor: hand;" +
              "-fx-effect: dropshadow(gaussian, rgba(99, 102, 241, 0.25), 10, 0, 0, 2);";
    }

    public static String getComboBoxHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: linear-gradient(to bottom, #334155, #1E293B);" +
              "-fx-border-color: linear-gradient(to right, #818CF8, #A78BFA);" +
              "-fx-border-width: 2;" +
              "-fx-border-radius: 10;" +
              "-fx-background-radius: 10;" +
              "-fx-padding: 4 8;" +
              "-fx-cursor: hand;" +
              "-fx-effect: dropshadow(gaussian, rgba(139, 92, 246, 0.55), 18, 0, 0, 0);"
            : "-fx-background-color: linear-gradient(to bottom right, #C7D2FE 0%, #DDD6FE 50%, #BFDBFE 100%);" +
              "-fx-border-color: linear-gradient(to right, #6366F1, #8B5CF6);" +
              "-fx-border-width: 2;" +
              "-fx-border-radius: 10;" +
              "-fx-background-radius: 10;" +
              "-fx-padding: 4 8;" +
              "-fx-cursor: hand;" +
              "-fx-effect: dropshadow(gaussian, rgba(99, 102, 241, 0.4), 14, 0, 0, 2);";
    }

    // Sidebar Styles (ensuring no white in light mode)
    public static String getSidebarStyle() {
        return isDarkMode()
            ? "-fx-background-color: #0F172A; -fx-border-color: #334155; -fx-border-width: 0 1 0 0;"
            : "-fx-background-color: linear-gradient(to bottom, #D0D9EB, #C8D0E8, #D5D0EF); -fx-border-color: #B4BED6; -fx-border-width: 0 1 0 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 4, 0, 2, 0);";
    }

    public static String getSidebarButtonStyle(boolean active) {
        if (isDarkMode()) {
            return active
                ? "-fx-background-color: linear-gradient(to right, rgba(99, 102, 241, 0.25), rgba(139, 92, 246, 0.15)); -fx-text-fill: #A5B4FC; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 14; -fx-cursor: hand; -fx-border-color: #6366F1; -fx-border-width: 0 0 0 3; -fx-border-radius: 8;"
                : "-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-weight: normal; -fx-background-radius: 8; -fx-padding: 10 14; -fx-cursor: hand;";
        }
        return active
            ? "-fx-background-color: linear-gradient(to right, #C7D2FE, #E0E7FF); -fx-text-fill: #3730A3; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 14; -fx-cursor: hand; -fx-border-color: #6366F1; -fx-border-width: 0 0 0 3; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.2), 6, 0, 0, 1);"
            : "-fx-background-color: transparent; -fx-text-fill: #1E3A5F; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 14; -fx-cursor: hand;";
    }

    public static String getSidebarButtonHoverStyle() {
        return isDarkMode()
            ? "-fx-background-color: rgba(99, 102, 241, 0.12); -fx-text-fill: #E0E7FF; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 14; -fx-cursor: hand;"
            : "-fx-background-color: linear-gradient(to right, #DDE5F8, #E8E3FA); -fx-text-fill: #3730A3; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10 14; -fx-cursor: hand;";
    }

    // ═══════════════════════════════════════════════════════════════
    // SCROLLBAR STYLES (custom for both themes, no white)
    // ═══════════════════════════════════════════════════════════════
    public static String getScrollbarTrackStyle() {
        return isDarkMode()
            ? "-fx-background-color: #0F172A;"
            : "-fx-background-color: #D4DFEE;";
    }

    public static String getScrollbarThumbStyle() {
        return isDarkMode()
            ? "-fx-background-color: #475569; -fx-background-radius: 10;"
            : "-fx-background-color: #A6BCD6; -fx-background-radius: 10;";
    }

    // ═══════════════════════════════════════════════════════════════
    // TABLE / LIST STYLES (no white in light mode)
    // ═══════════════════════════════════════════════════════════════
    public static String getTableRowStyle(boolean even) {
        if (isDarkMode()) {
            return even ? "-fx-background-color: #0F172A;" : "-fx-background-color: #1E293B;";
        }
        return even ? "-fx-background-color: #D8E4F3;" : "-fx-background-color: #E2E8F4;";
    }

    // ═══════════════════════════════════════════════════════════════
    // TOOLTIP STYLE
    // ═══════════════════════════════════════════════════════════════
    public static String getTooltipStyle() {
        return isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-text-fill: #F8FAFC; -fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);"
            : "-fx-background-color: #2D3748; -fx-text-fill: #F8FAFC; -fx-border-color: #4A5568; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);";
    }

    // ═══════════════════════════════════════════════════════════════
    // MODAL / DIALOG OVERLAY STYLE (no white in light mode)
    // ═══════════════════════════════════════════════════════════════
    public static String getModalStyle() {
        return isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-border-color: #475569; -fx-border-radius: 16; -fx-background-radius: 16; -fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 30, 0, 0, 8);"
            : "-fx-background-color: linear-gradient(to bottom right, #E0E7FF 0%, #EDE9FE 50%, #E2F0EA 100%); -fx-border-color: #A5B4FC; -fx-border-radius: 16; -fx-background-radius: 16; -fx-padding: 24; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.3), 30, 0, 0, 8);";
    }

    // ═══════════════════════════════════════════════════════════════
    // PROGRESS BAR STYLE
    // ═══════════════════════════════════════════════════════════════
    public static String getProgressBarStyle() {
        return isDarkMode()
            ? "-fx-accent: linear-gradient(to right, #6366F1, #8B5CF6);"
            : "-fx-accent: linear-gradient(to right, #6366F1, #8B5CF6);";
    }

    // ═══════════════════════════════════════════════════════════════
    // ANIMATION DURATION CONSTANTS
    // ═══════════════════════════════════════════════════════════════
    public static final double ANIM_FAST = 150;    // ms
    public static final double ANIM_NORMAL = 300;  // ms
    public static final double ANIM_SLOW = 500;    // ms
    public static final double ANIM_ENTRANCE = 600; // ms
    public static final double ANIM_STAGGER_DELAY = 80; // ms per item
}
