package com.freelancing.config;

import java.awt.Color;
import java.awt.Font;

public class AppTheme {
    // Primary palette - Modern Dark Theme
    public static final Color BG_DARK = new Color(15, 23, 42);       // Slate 900
    public static final Color BG_CARD = new Color(30, 41, 59);       // Slate 800
    public static final Color BG_INPUT = new Color(51, 65, 85);      // Slate 700
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);    // Dark sidebar

    // Accent Colors
    public static final Color PRIMARY = new Color(99, 102, 241);     // Indigo 500
    public static final Color PRIMARY_HOVER = new Color(79, 70, 229);// Indigo 600
    public static final Color ACCENT_GREEN = new Color(16, 185, 129); // Emerald 500
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);  // Blue 500
    public static final Color ACCENT_AMBER = new Color(245, 158, 11); // Amber 500
    public static final Color ACCENT_RED = new Color(239, 68, 68);    // Red 500
    public static final Color ACCENT_PURPLE = new Color(168, 85, 247); // Purple 500

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252); // Slate 50
    public static final Color TEXT_MUTED = new Color(148, 163, 184);   // Slate 400
    public static final Color TEXT_SECONDARY = new Color(203, 213, 225); // Slate 300
    public static final Color BORDER_COLOR = new Color(51, 65, 85);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
}
