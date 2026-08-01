package com.freelancing.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

public class UIComponents {

    public static final String COLOR_BG_DARK = "#0F172A";
    public static final String COLOR_BG_SIDEBAR = "#1E293B";
    public static final String COLOR_BG_CARD = "#1E293B";
    public static final String COLOR_BG_INPUT = "#334155";
    public static final String COLOR_BORDER = "#475569";

    public static final String COLOR_PRIMARY = "#6366F1";
    public static final String COLOR_PRIMARY_HOVER = "#4F46E5";
    public static final String COLOR_SUCCESS = "#10B981";
    public static final String COLOR_AMBER = "#F59E0B";
    public static final String COLOR_DANGER = "#EF4444";
    public static final String COLOR_PURPLE = "#A855F7";

    public static final String COLOR_TEXT_PRIMARY = "#F8FAFC";
    public static final String COLOR_TEXT_MUTED = "#94A3B8";

    public static Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        label.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        return label;
    }

    public static Label createHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        label.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        return label;
    }

    public static Label createSubHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web(COLOR_TEXT_MUTED));
        return label;
    }

    public static Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: " + COLOR_PRIMARY + ";" +
                     "-fx-text-fill: white;" +
                     "-fx-background-radius: 8;" +
                     "-fx-padding: 8 16 8 16;" +
                     "-fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + COLOR_PRIMARY_HOVER + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand;"));
        return btn;
    }

    public static Button createSuccessButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: " + COLOR_SUCCESS + ";" +
                     "-fx-text-fill: white;" +
                     "-fx-background-radius: 8;" +
                     "-fx-padding: 8 16 8 16;" +
                     "-fx-cursor: hand;");
        return btn;
    }

    public static Button createDangerButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: " + COLOR_DANGER + ";" +
                     "-fx-text-fill: white;" +
                     "-fx-background-radius: 8;" +
                     "-fx-padding: 8 16 8 16;" +
                     "-fx-cursor: hand;");
        return btn;
    }

    public static Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        btn.setStyle("-fx-background-color: " + COLOR_BG_INPUT + ";" +
                     "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";" +
                     "-fx-border-color: " + COLOR_BORDER + ";" +
                     "-fx-border-radius: 8;" +
                     "-fx-background-radius: 8;" +
                     "-fx-padding: 8 16 8 16;" +
                     "-fx-cursor: hand;");
        return btn;
    }

    public static TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font("Segoe UI", 13));
        tf.setStyle("-fx-background-color: " + COLOR_BG_INPUT + ";" +
                    "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + COLOR_TEXT_MUTED + ";" +
                    "-fx-border-color: " + COLOR_BORDER + ";" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8;");
        return tf;
    }

    public static PasswordField createPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setFont(Font.font("Segoe UI", 13));
        pf.setStyle("-fx-background-color: " + COLOR_BG_INPUT + ";" +
                    "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + COLOR_TEXT_MUTED + ";" +
                    "-fx-border-color: " + COLOR_BORDER + ";" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8;");
        return pf;
    }

    public static TextArea createTextArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setFont(Font.font("Segoe UI", 13));
        ta.setWrapText(true);
        ta.setStyle("-fx-control-inner-background: " + COLOR_BG_INPUT + ";" +
                    "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + COLOR_TEXT_MUTED + ";" +
                    "-fx-border-color: " + COLOR_BORDER + ";" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;");
        return ta;
    }

    public static VBox createCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + COLOR_BG_CARD + ";" +
                      "-fx-border-color: " + COLOR_BORDER + ";" +
                      "-fx-border-radius: 10;" +
                      "-fx-background-radius: 10;");
        return card;
    }

    public static VBox createStatCard(String icon, String title, String value, String accentColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: " + COLOR_BG_CARD + ";" +
                      "-fx-border-color: " + accentColor + ";" +
                      "-fx-border-width: 0 0 0 4;" +
                      "-fx-border-radius: 8;" +
                      "-fx-background-radius: 8;");

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font("Segoe UI", 18));

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        valLbl.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", 12));
        titleLbl.setTextFill(Color.web(COLOR_TEXT_MUTED));

        HBox topRow = new HBox(10, iconLbl, titleLbl);
        topRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(topRow, valLbl);
        return card;
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

    // Chart Helpers
    public static PieChart createPieChart(String title, Map<String, Double> dataMap) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle(title);
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        pieChart.setPrefSize(380, 260);
        return pieChart;
    }

    public static BarChart<String, Number> createBarChart(String title, String xLabel, String yLabel, Map<String, Double> dataMap) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
        barChart.setPrefSize(420, 260);
        return barChart;
    }

    public static LineChart<String, Number> createLineChart(String title, String xLabel, String yLabel, Map<String, Double> dataMap) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        lineChart.getData().add(series);
        lineChart.setPrefSize(420, 260);
        return lineChart;
    }

    public static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
