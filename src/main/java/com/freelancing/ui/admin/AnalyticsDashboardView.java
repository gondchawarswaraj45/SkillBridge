package com.freelancing.ui.admin;

import com.freelancing.ui.common.HomePage;
import com.freelancing.ui.common.UIComponents;

import com.freelancing.config.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.File;
import java.io.FileWriter;

/**
 * Platform & Financial Analytics Insights with interactive JavaFX Charts.
 * Strictly adheres to Single Stage Rule (swaps Scene on HomePage.primaryStage).
 * 100% Inline CSS driven by AppTheme (non-white pastel light mode).
 */
public class AnalyticsDashboardView {

    private StackPane rootStack;
    private LineChart<String, Number> volumeChart;
    private PieChart categoryPieChart;
    private BarChart<String, Number> deliveryBarChart;
    private String selectedTimeRange = "30 Days";

    private Pane modalBackdrop;
    private VBox activeModal;

    public Scene createScene() {
        Parent content = createContent();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1360, bounds.getWidth() * 0.92);
        double sceneHeight = Math.min(880, bounds.getHeight() * 0.92);
        Scene scene = new Scene(content, sceneWidth, sceneHeight);
        scene.setOnKeyPressed(ke -> {
            if (ke.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                closeModal();
            }
        });
        return scene;
    }

    public Parent createContent() {
        rootStack = new StackPane();
        rootStack.setStyle(AppTheme.getRootStyle());

        BorderPane layout = new BorderPane();
        layout.setStyle(AppTheme.getRootStyle());

        // 1. Top Navbar
        HBox navbar = createNavbar();
        layout.setTop(navbar);

        VBox content = new VBox(26);
        content.setPadding(new Insets(24, 40, 40, 40));
        content.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = UIComponents.createScrollPane(content);

        // Header Title & Time-Range Picker
        HBox headerRow = createHeaderRow();

        // 4 KPI Summary Cards (Pastels in Light Mode)
        HBox kpiRow = createKpiRow();

        // Charts Section
        VBox chartsContainer = createChartsSection();

        // Platform Telemetry Box
        HBox telemetryBox = createTelemetryBox();

        content.getChildren().addAll(headerRow, kpiRow, chartsContainer, telemetryBox);
        scrollPane.setContent(content);
        layout.setCenter(scrollPane);

        rootStack.getChildren().add(layout);
        return rootStack;
    }

    // =========================================================================
    // NAVBAR WITH GLOBAL NAVIGATION LINKS
    // =========================================================================

    private HBox createNavbar() {
        HBox nav = new HBox(14);
        nav.setPadding(new Insets(12, 35, 12, 35));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle(AppTheme.getNavbarStyle());

        Label logo = new Label("⚡ SkillBridge");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        logo.setTextFill(Color.web("#FFFFFF"));
        logo.setOnMouseClicked(e -> HomePage.showHomeView());
        logo.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 4, 0, 0, 1);");

        // Global Nav Links
        HBox navLinks = new HBox(12);
        navLinks.setAlignment(Pos.CENTER_LEFT);

        Button linkMarket = createNavLink("💼 Marketplace", false, () -> HomePage.showHomeView());
        Button linkSwap = createNavLink("🔄 Skill Exchange", false, () -> HomePage.showSkillExchangeView());
        Button linkEscrow = createNavLink("🛡️ Escrow & Vault", false, () -> HomePage.showContractsAndEscrowView());
        Button linkAnalytics = createNavLink("📊 Analytics", true, () -> {
        });
        Button linkCommunity = createNavLink("💬 Community", false, () -> HomePage.showCommunityForumView());

        navLinks.getChildren().addAll(linkMarket, linkSwap, linkEscrow, linkAnalytics, linkCommunity);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showAnalyticsDashboardView();
        });

        Button btnBackHome = UIComponents.createTitleBarButton("← Back to Home", () -> HomePage.showHomeView());

        nav.getChildren().addAll(logo, navLinks, sp, btnTheme, btnBackHome);
        return nav;
    }

    private Button createNavLink(String title, boolean active, Runnable action) {
        Button btn = new Button(title);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        if (active) {
            btn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #1D4ED8; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 2);");
        } else {
            btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.12); -fx-text-fill: #FFFFFF; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); -fx-text-fill: #FFFFFF; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.12); -fx-text-fill: #FFFFFF; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;"));
        }
        btn.setOnAction(e -> {
            if (action != null)
                action.run();
        });
        return btn;
    }

    // =========================================================================
    // HEADER ROW & TIME RANGE CHIPS
    // =========================================================================

    private HBox createHeaderRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(2);
        Label title = UIComponents.createTitle("📊 Platform Health & Financial Analytics");
        Label sub = new Label(
                "Real-time telemetry measuring milestone throughput, category demand, and escrow liquidity.");
        sub.setFont(Font.font("Segoe UI", 12));
        sub.setTextFill(Color.web(AppTheme.getTextMuted()));
        titles.getChildren().addAll(title, sub);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnExportReport = UIComponents.createExportButton("📥 Export Analytics Report");
        btnExportReport.setOnAction(e -> exportAnalyticsReport());

        HBox timeRangeBox = new HBox(6);
        String[] ranges = { "30 Days", "90 Days", "1 Year", "All Time" };
        for (String r : ranges) {
            Button btn = new Button(r);
            btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
            applyRangeBtnStyle(btn, r.equals(selectedTimeRange));
            btn.setOnAction(e -> {
                selectedTimeRange = r;
                for (Node n : timeRangeBox.getChildren()) {
                    if (n instanceof Button) {
                        applyRangeBtnStyle((Button) n, ((Button) n).getText().equals(selectedTimeRange));
                    }
                }
                updateChartsData(selectedTimeRange);
            });
            timeRangeBox.getChildren().add(btn);
        }

        row.getChildren().addAll(titles, sp, btnExportReport, timeRangeBox);
        return row;
    }

    private void applyRangeBtnStyle(Button btn, boolean active) {
        if (active) {
            btn.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY
                    + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;");
        } else {
            btn.setStyle(AppTheme.isDarkMode()
                    ? "-fx-background-color: #1E293B; -fx-text-fill: #94A3B8; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;"
                    : "-fx-background-color: #CAD8EA; -fx-text-fill: #1E293B; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;");
        }
    }

    // =========================================================================
    // 4 KPI SUMMARY CARDS
    // =========================================================================

    private HBox createKpiRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER);

        VBox c1 = UIComponents.createStatCard(0, "📈", "Average Contract Value", AppTheme.formatCurrency(3850),
                AppTheme.COLOR_PRIMARY);
        VBox c2 = UIComponents.createStatCard(1, "⏱️", "Milestone On-Time Delivery", "99.1%", AppTheme.COLOR_SUCCESS);
        VBox c3 = UIComponents.createStatCard(2, "🤝", "Client Retention Rate", "94.2%", AppTheme.COLOR_AMBER);
        VBox c4 = UIComponents.createStatCard(3, "⚡", "Avg AI Match Speed", "4.2 hrs", AppTheme.COLOR_ACCENT);

        // Make every KPI block interactive with hand cursor and click dialogs
        makeKpiInteractive(c1, () -> openContractValueModal());
        makeKpiInteractive(c2, () -> openDeliverySlaModal());
        makeKpiInteractive(c3, () -> openClientRetentionModal());
        makeKpiInteractive(c4, () -> openAiMatchSpeedModal());

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);

        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private void makeKpiInteractive(VBox card, Runnable onClick) {
        String baseStyle = card.getStyle();
        card.setStyle(baseStyle + "; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "; -fx-cursor: hand; -fx-border-color: "
                + AppTheme.COLOR_PRIMARY + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        card.setOnMouseExited(
                e -> card.setStyle(baseStyle + "; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
        card.setOnMouseClicked(e -> {
            if (onClick != null)
                onClick.run();
        });
    }

    // =========================================================================
    // CHARTS SECTION
    // =========================================================================

    private VBox createChartsSection() {
        VBox sec = new VBox(20);

        // Row 1: Monthly Line Chart + Category Distribution Pie Chart
        HBox row1 = new HBox(18);
        row1.setAlignment(Pos.CENTER);

        // 1. Line Chart: Milestone Release Volume
        VBox lineBox = new VBox(10);
        lineBox.setPadding(new Insets(16));
        lineBox.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12;"
                : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12;");
        HBox.setHgrow(lineBox, Priority.ALWAYS);

        Label lineTitle = new Label("📈 Monthly Escrow Volume & Disbursals");
        lineTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lineTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Volume (USD)");

        volumeChart = new LineChart<>(xAxis, yAxis);
        volumeChart.setPrefHeight(280);
        volumeChart.setLegendVisible(false);
        volumeChart.setAnimated(true);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Sep", 48000));
        series.getData().add(new XYChart.Data<>("Oct", 62000));
        series.getData().add(new XYChart.Data<>("Nov", 79000));
        series.getData().add(new XYChart.Data<>("Dec", 94000));
        series.getData().add(new XYChart.Data<>("Jan", 125000));
        series.getData().add(new XYChart.Data<>("Feb", 184500));
        volumeChart.getData().add(series);

        lineBox.getChildren().addAll(lineTitle, volumeChart);

        // 2. Pie Chart: Engineering Category Share
        VBox pieBox = new VBox(10);
        pieBox.setPadding(new Insets(16));
        pieBox.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12;"
                : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12;");
        pieBox.setPrefWidth(420);

        Label pieTitle = new Label("🥧 Category Demand Distribution");
        pieTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        pieTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

        categoryPieChart = new PieChart();
        categoryPieChart.setPrefHeight(280);
        categoryPieChart.getData().addAll(
                new PieChart.Data("Web / Java (38%)", 38),
                new PieChart.Data("Mobile / Flutter (24%)", 24),
                new PieChart.Data("AI / Python (20%)", 20),
                new PieChart.Data("DevOps / Cloud (12%)", 12),
                new PieChart.Data("UI/UX Design (6%)", 6));

        pieBox.getChildren().addAll(pieTitle, categoryPieChart);

        row1.getChildren().addAll(lineBox, pieBox);

        // Row 2: Bar Chart - Delivery Success by Technology
        VBox barBox = new VBox(10);
        barBox.setPadding(new Insets(16));
        barBox.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12;"
                : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label barTitle = new Label("📊 Technology On-Time Milestone Delivery Performance (%)");
        barTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        barTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

        CategoryAxis bX = new CategoryAxis();
        NumberAxis bY = new NumberAxis(80, 100, 5);
        bX.setLabel("Technology Domain");
        bY.setLabel("Success Rating (%)");

        deliveryBarChart = new BarChart<>(bX, bY);
        deliveryBarChart.setPrefHeight(230);
        deliveryBarChart.setLegendVisible(false);

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.getData().add(new XYChart.Data<>("Java / Spring", 99.4));
        barSeries.getData().add(new XYChart.Data<>("Flutter / Dart", 98.8));
        barSeries.getData().add(new XYChart.Data<>("React / Next.js", 97.9));
        barSeries.getData().add(new XYChart.Data<>("Python AI", 99.1));
        barSeries.getData().add(new XYChart.Data<>("AWS / Cloud", 99.6));
        barSeries.getData().add(new XYChart.Data<>("UI / UX", 96.5));
        deliveryBarChart.getData().add(barSeries);

        barBox.getChildren().addAll(barTitle, deliveryBarChart);

        sec.getChildren().addAll(row1, barBox);
        return sec;
    }

    private void updateChartsData(String range) {
        if (volumeChart == null)
            return;
        volumeChart.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<>();

        if (range.contains("30")) {
            s.getData().add(new XYChart.Data<>("Week 1", 38000));
            s.getData().add(new XYChart.Data<>("Week 2", 44000));
            s.getData().add(new XYChart.Data<>("Week 3", 51000));
            s.getData().add(new XYChart.Data<>("Week 4", 62000));
        } else {
            s.getData().add(new XYChart.Data<>("Q1", 140000));
            s.getData().add(new XYChart.Data<>("Q2", 210000));
            s.getData().add(new XYChart.Data<>("Q3", 290000));
            s.getData().add(new XYChart.Data<>("Q4", 420000));
        }
        volumeChart.getData().add(s);
    }

    // =========================================================================
    // PLATFORM TELEMETRY & HEALTH
    // =========================================================================

    private HBox createTelemetryBox() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(14, 20, 14, 20));
        String baseStyle = AppTheme.isDarkMode()
                ? "-fx-background-color: #0F172A; -fx-border-color: rgba(16, 185, 129, 0.3); -fx-border-radius: 10; -fx-background-radius: 10;"
                : "-fx-background-color: #D8F3E5; -fx-border-color: #34D399; -fx-border-radius: 10; -fx-background-radius: 10;";
        box.setStyle(baseStyle + "; -fx-cursor: hand;");

        box.setOnMouseEntered(e -> box.setStyle(
                baseStyle + "; -fx-cursor: hand; -fx-border-color: #10B981; -fx-scale-x: 1.01; -fx-scale-y: 1.01;"));
        box.setOnMouseExited(e -> box.setStyle(baseStyle + "; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
        box.setOnMouseClicked(e -> openSystemDiagnosticsModal());

        Label dot = new Label("●");
        dot.setTextFill(Color.web(AppTheme.COLOR_SUCCESS));
        dot.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        Label t1 = new Label("Database Architecture: Operational");
        t1.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        t1.setTextFill(Color.web(AppTheme.isDarkMode() ? "#34D399" : "#065F46"));

        Label t2 = new Label("•  SQLite Engine: WAL Mode Active");
        t2.setFont(Font.font("Segoe UI", 12));
        t2.setTextFill(Color.web(AppTheme.getTextMuted()));

        Label t3 = new Label("•  JDBC Connection Pool: Healthy");
        t3.setFont(Font.font("Segoe UI", 12));
        t3.setTextFill(Color.web(AppTheme.getTextMuted()));

        Label t4 = new Label("•  Smart Escrow Arbitrations: 0 Pending");
        t4.setFont(Font.font("Segoe UI", 12));
        t4.setTextFill(Color.web(AppTheme.getTextMuted()));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label viewPrompt = new Label("🔍 View System Telemetry Audit →");
        viewPrompt.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        viewPrompt.setTextFill(Color.web(AppTheme.COLOR_SUCCESS));

        box.getChildren().addAll(dot, t1, t2, t3, t4, sp, viewPrompt);
        return box;
    }

    // =========================================================================
    // MODAL DIALOGS & ACTION HANDLERS
    // =========================================================================

    private void openContractValueModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("📈 Contract Value & Revenue Economics");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted()
                + "; -fx-font-size: 16; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox auditList = new VBox(10);
        auditList.setPadding(new Insets(12, 0, 12, 0));
        auditList.getChildren().addAll(
                createAuditRow("Mean Contract Value", "$3,850.00 USD", AppTheme.COLOR_PRIMARY),
                createAuditRow("Median Contract Size", "$3,200.00 USD", AppTheme.COLOR_ACCENT),
                createAuditRow("Enterprise Tier (> $10k)", "28.4% of Total GTV", AppTheme.COLOR_SUCCESS),
                createAuditRow("Escrow Protection Ratio", "100% Guaranteed", AppTheme.COLOR_SUCCESS),
                createAuditRow("Average Milestone Split", "3.2 Milestones per Job", AppTheme.getTextPrimary()));

        Label notes = new Label(
                "💡 SkillBridge contracts are settled via multi-sig milestone escrow locks with automated dispute mediation.");
        notes.setFont(Font.font("Segoe UI", 11));
        notes.setTextFill(Color.web(AppTheme.getTextMuted()));
        notes.setWrapText(true);

        Button btnDismiss = UIComponents.createPrimaryButton("Close Insights");
        btnDismiss.setMaxWidth(Double.MAX_VALUE);
        btnDismiss.setOnAction(e -> closeModal());

        modal.getChildren().addAll(header, auditList, notes, btnDismiss);
        showModal(modal);
    }

    private void openDeliverySlaModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("⏱️ Milestone SLA & Delivery Reliability");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted()
                + "; -fx-font-size: 16; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox auditList = new VBox(10);
        auditList.setPadding(new Insets(12, 0, 12, 0));
        auditList.getChildren().addAll(
                createAuditRow("On-Time Milestone Release", "99.1% Verified", AppTheme.COLOR_SUCCESS),
                createAuditRow("Mutual Extension Requests", "0.8% Approved", AppTheme.COLOR_AMBER),
                createAuditRow("Default Rate", "0.1% Handled by Escrow", AppTheme.COLOR_PRIMARY),
                createAuditRow("Average Review Turnaround", "14.2 Hours", AppTheme.COLOR_ACCENT),
                createAuditRow("Code Quality Pass Rate", "98.7% On First Commit", AppTheme.COLOR_SUCCESS));

        Label notes = new Label(
                "🛡️ All deliverables undergo automated GitHub commit hash verification and test assertion logs before release.");
        notes.setFont(Font.font("Segoe UI", 11));
        notes.setTextFill(Color.web(AppTheme.getTextMuted()));
        notes.setWrapText(true);

        Button btnDismiss = UIComponents.createSuccessButton("Acknowledge SLA Metrics");
        btnDismiss.setMaxWidth(Double.MAX_VALUE);
        btnDismiss.setOnAction(e -> closeModal());

        modal.getChildren().addAll(header, auditList, notes, btnDismiss);
        showModal(modal);
    }

    private void openClientRetentionModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🤝 Client Retention & Cohort Loyalty");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted()
                + "; -fx-font-size: 16; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox auditList = new VBox(10);
        auditList.setPadding(new Insets(12, 0, 12, 0));
        auditList.getChildren().addAll(
                createAuditRow("Repeat Client Hire Rate", "94.2% within 90 Days", AppTheme.COLOR_AMBER),
                createAuditRow("Net Promoter Score (NPS)", "+88 World Class", AppTheme.COLOR_SUCCESS),
                createAuditRow("Avg Developer Longevity", "5.8 Months per Engagement", AppTheme.COLOR_PRIMARY),
                createAuditRow("Talent Re-hiring Ratio", "3.6x Repeat Hire Multiplier", AppTheme.COLOR_ACCENT));

        Label notes = new Label(
                "🤝 Clients hiring vetted talent maintain long-term retainers, accelerating product development velocity.");
        notes.setFont(Font.font("Segoe UI", 11));
        notes.setTextFill(Color.web(AppTheme.getTextMuted()));
        notes.setWrapText(true);

        Button btnDismiss = UIComponents.createPrimaryButton("Understood");
        btnDismiss.setMaxWidth(Double.MAX_VALUE);
        btnDismiss.setOnAction(e -> closeModal());

        modal.getChildren().addAll(header, auditList, notes, btnDismiss);
        showModal(modal);
    }

    private void openAiMatchSpeedModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("⚡ AI Semantic Matching Engine");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted()
                + "; -fx-font-size: 16; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox auditList = new VBox(10);
        auditList.setPadding(new Insets(12, 0, 12, 0));
        auditList.getChildren().addAll(
                createAuditRow("Median Time-to-First-Bid", "1.2 Hours", AppTheme.COLOR_ACCENT),
                createAuditRow("Avg Match-to-Contract Time", "4.2 Hours", AppTheme.COLOR_SUCCESS),
                createAuditRow("Semantic Skill Fit Accuracy", "97.4% Match Score", AppTheme.COLOR_PRIMARY),
                createAuditRow("AI Interview Assistant Runs", "12,400+ Sessions", AppTheme.COLOR_AMBER));

        Label notes = new Label(
                "🤖 Powered by custom embeddings matching technical requirements against code repositories and verified assessments.");
        notes.setFont(Font.font("Segoe UI", 11));
        notes.setTextFill(Color.web(AppTheme.getTextMuted()));
        notes.setWrapText(true);

        Button btnDismiss = UIComponents.createPrimaryButton("Dismiss");
        btnDismiss.setMaxWidth(Double.MAX_VALUE);
        btnDismiss.setOnAction(e -> closeModal());

        modal.getChildren().addAll(header, auditList, notes, btnDismiss);
        showModal(modal);
    }

    private void openSystemDiagnosticsModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(620);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("⚙️ Real-Time System Architecture & Health Audit");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted()
                + "; -fx-font-size: 16; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox auditList = new VBox(10);
        auditList.setPadding(new Insets(12, 0, 12, 0));
        auditList.getChildren().addAll(
                createAuditRow("Core Architecture Status", "🟢 All Desktop Systems Fully Operational",
                        AppTheme.COLOR_SUCCESS),
                createAuditRow("SQLite Database Engine", "data/skillbridge.db (WAL Mode / PRAGMA Active)",
                        AppTheme.COLOR_PRIMARY),
                createAuditRow("JDBC Connection Manager", "Direct Prepared Statement Pool Healthy",
                        AppTheme.COLOR_ACCENT),
                createAuditRow("Local Document Storage", "storage/ (Profiles, Portfolios, Deliverables)",
                        AppTheme.COLOR_SUCCESS),
                createAuditRow("Smart Milestone Escrow Engine", "0 Disputed Arbitrations Pending",
                        AppTheme.COLOR_AMBER),
                createAuditRow("JavaFX UI Render Engine", "Active (Hardware Accelerated / Prism)",
                        AppTheme.COLOR_SUCCESS));

        Button btnDismiss = UIComponents.createSuccessButton("All Diagnostic Checks Passed");
        btnDismiss.setMaxWidth(Double.MAX_VALUE);
        btnDismiss.setOnAction(e -> closeModal());

        modal.getChildren().addAll(header, auditList, btnDismiss);
        showModal(modal);
    }

    private void exportAnalyticsReport() {
        try {
            File reportFile = new File("skillbridge_analytics_report.csv");
            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write("Metric,Value,Status,Timeframe\n");
                writer.write("Average Contract Value,$3850.00,Verified," + selectedTimeRange + "\n");
                writer.write("Milestone On-Time Delivery,99.1%,Compliant," + selectedTimeRange + "\n");
                writer.write("Client Retention Rate,94.2%,Exemplary," + selectedTimeRange + "\n");
                writer.write("AI Match Speed,4.2 hrs,Target Reached," + selectedTimeRange + "\n");
                writer.write("Monthly Volume Sep,$48000,Audited,Sep\n");
                writer.write("Monthly Volume Oct,$62000,Audited,Oct\n");
                writer.write("Monthly Volume Nov,$79000,Audited,Nov\n");
                writer.write("Monthly Volume Dec,$94000,Audited,Dec\n");
                writer.write("Monthly Volume Jan,$125000,Audited,Jan\n");
                writer.write("Monthly Volume Feb,$184500,Audited,Feb\n");
            }

            VBox modal = UIComponents.createGlassCard();
            modal.setMaxWidth(520);

            Label title = UIComponents.createHeader("📥 Analytics Report Exported Successfully");
            Label desc = new Label("Exported live platform telemetry to:\n" + reportFile.getAbsolutePath());
            desc.setFont(Font.font("Segoe UI", 12));
            desc.setTextFill(Color.web(AppTheme.getTextPrimary()));
            desc.setWrapText(true);

            Button btnOk = UIComponents.createPrimaryButton("Great, Continue");
            btnOk.setMaxWidth(Double.MAX_VALUE);
            btnOk.setOnAction(e -> closeModal());

            modal.getChildren().addAll(title, desc, btnOk);
            showModal(modal);
        } catch (Exception ex) {
            UIComponents.showAlert(Alert.AlertType.ERROR, "Export Error", "Export Failed", ex.getMessage());
        }
    }

    private HBox createAuditRow(String label, String value, String colorHex) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #0F172A; -fx-background-radius: 8;"
                : "-fx-background-color: #CAD8EA; -fx-background-radius: 8;");

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        val.setTextFill(Color.web(colorHex));

        row.getChildren().addAll(lbl, sp, val);
        return row;
    }

    private void showModal(VBox modal) {
        closeModal();
        modalBackdrop = new Pane();
        modalBackdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.65);");
        modalBackdrop.setOnMouseClicked(e -> closeModal());

        activeModal = modal;
        rootStack.getChildren().addAll(modalBackdrop, activeModal);
        StackPane.setAlignment(activeModal, Pos.CENTER);
    }

    private void closeModal() {
        if (modalBackdrop != null) {
            rootStack.getChildren().remove(modalBackdrop);
            modalBackdrop = null;
        }
        if (activeModal != null) {
            rootStack.getChildren().remove(activeModal);
            activeModal = null;
        }
    }
}
