package com.freelancing.ui.company;

import com.freelancing.model.company.Project;
import com.freelancing.ui.common.HomePage;
import com.freelancing.ui.common.UIComponents;

import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Enterprise Smart Escrow & Financial Management Cockpit.
 * Strictly adheres to Single Stage Rule (swaps Scene on HomePage.primaryStage).
 * 100% Inline CSS driven by AppTheme (non-white pastel light mode).
 */
public class ContractsAndEscrowView {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private StackPane rootStack;
    private VBox contractsContainer;

    private Pane modalBackdrop;
    private VBox activeModal;

    public Scene createScene() {
        Parent content = createContent();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1360, bounds.getWidth() * 0.92);
        double sceneHeight = Math.min(880, bounds.getHeight() * 0.92);
        return new Scene(content, sceneWidth, sceneHeight);
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

        // Vault Summary KPI Cards (4 Pastel Blocks in Light Theme)
        HBox kpiRow = createVaultKpiRow();

        // Action Toolbar (Audit Statement Export + Lock Escrow)
        HBox toolbar = createToolbar();

        // Contracts & Milestones List
        contractsContainer = new VBox(16);
        refreshContractsList();

        content.getChildren().addAll(kpiRow, toolbar, contractsContainer);
        scrollPane.setContent(content);
        layout.setCenter(scrollPane);

        com.freelancing.util.AnimationUtil.applyFadeZoom(layout, 280);
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
        logo.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
        logo.setOnMouseClicked(e -> HomePage.showHomeView());
        logo.setStyle("-fx-cursor: hand;");

        // Global Nav Links
        HBox navLinks = new HBox(12);
        navLinks.setAlignment(Pos.CENTER_LEFT);

        Button linkMarket = createNavLink("💼 Marketplace", false, () -> HomePage.showHomeView());
        Button linkSwap = createNavLink("🔄 Skill Exchange", false, () -> HomePage.showSkillExchangeView());
        Button linkEscrow = createNavLink("🛡️ Escrow & Vault", true, () -> {});
        Button linkAnalytics = createNavLink("📊 Analytics", false, () -> HomePage.showAnalyticsDashboardView());
        Button linkCommunity = createNavLink("💬 Community", false, () -> HomePage.showCommunityForumView());

        navLinks.getChildren().addAll(linkMarket, linkSwap, linkEscrow, linkAnalytics, linkCommunity);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        // Currency Selector
        ComboBox<String> currSelector = UIComponents.createCurrencySelector(code -> {
            refreshContractsList();
        });

        // Theme Switcher
        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showContractsAndEscrowView();
        });

        Button btnBackHome = UIComponents.createExportButton("← Back to Home");
        btnBackHome.setOnAction(e -> HomePage.showHomeView());

        nav.getChildren().addAll(logo, navLinks, sp, new Label("Currency:"), currSelector, btnTheme, btnBackHome);
        return nav;
    }

    private Button createNavLink(String title, boolean active, Runnable action) {
        Button btn = new Button(title);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        if (active) {
            btn.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + (AppTheme.isDarkMode() ? "#CBD5E1" : "#1E293B") + "; -fx-padding: 6 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + (AppTheme.isDarkMode() ? "#1E293B" : "#CAD8EA") + "; -fx-text-fill: " + AppTheme.COLOR_PRIMARY + "; -fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + (AppTheme.isDarkMode() ? "#CBD5E1" : "#1E293B") + "; -fx-padding: 6 12; -fx-cursor: hand;"));
        }
        btn.setOnAction(e -> {
            if (action != null) action.run();
        });
        return btn;
    }

    // =========================================================================
    // VAULT KPI STATS
    // =========================================================================

    private HBox createVaultKpiRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER);

        VBox c1 = UIComponents.createStatCard(0, "🏦", "Total Platform Vault (Audit)", AppTheme.formatCurrency(2450000), AppTheme.COLOR_PRIMARY);
        VBox c2 = UIComponents.createStatCard(1, "🔒", "Active Funds In-Escrow (Locks)", AppTheme.formatCurrency(184500), AppTheme.COLOR_SUCCESS);
        VBox c3 = UIComponents.createStatCard(2, "💸", "Disbursed This Month (Stats)", AppTheme.formatCurrency(78200), AppTheme.COLOR_AMBER);
        VBox c4 = UIComponents.createStatCard(3, "⚖️", "Arbitration Resolution Rate (SLAs)", "100%", AppTheme.COLOR_ACCENT);

        c1.setStyle(c1.getStyle() + "; -fx-cursor: hand;");
        c2.setStyle(c2.getStyle() + "; -fx-cursor: hand;");
        c3.setStyle(c3.getStyle() + "; -fx-cursor: hand;");
        c4.setStyle(c4.getStyle() + "; -fx-cursor: hand;");

        c1.setOnMouseClicked(e -> openVaultSolvencyModal());
        c2.setOnMouseClicked(e -> openActiveEscrowModal());
        c3.setOnMouseClicked(e -> openDisbursementsModal());
        c4.setOnMouseClicked(e -> openArbitrationSlaModal());

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);

        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    // =========================================================================
    // ACTION TOOLBAR
    // =========================================================================

    private HBox createToolbar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = UIComponents.createTitle("🛡️ Active Contracts & Escrow Milestone Ledger");
        Label sub = new Label("All client payments are held in non-custodial smart contracts until milestone acceptance testing.");
        sub.setFont(Font.font("Segoe UI", 12));
        sub.setTextFill(Color.web(AppTheme.getTextMuted()));
        titleBox.getChildren().addAll(title, sub);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnExport = UIComponents.createExportButton("📥 Export Audit Statement");
        btnExport.setOnAction(e -> exportAuditStatement());

        Button btnDeposit = UIComponents.createClientDemoButton("➕ Fund Milestone");
        btnDeposit.setOnAction(e -> openDepositModal());

        bar.getChildren().addAll(titleBox, sp, btnExport, btnDeposit);
        return bar;
    }

    // =========================================================================
    // CONTRACTS & MILESTONE LIST
    // =========================================================================

    private void refreshContractsList() {
        if (contractsContainer == null) return;
        contractsContainer.getChildren().setAll(com.freelancing.util.AnimationUtil.createLoadingOverlay("Loading escrow milestone contracts..."));

        com.freelancing.util.AnimationUtil.runAsync(
            () -> new ArrayList<>(db.getProjects().values()),
            projectList -> {
                contractsContainer.getChildren().clear();
                if (projectList.isEmpty()) {
                    VBox empty = new VBox(8);
                    empty.setPadding(new Insets(30));
                    empty.setAlignment(Pos.CENTER);
                    empty.getChildren().add(new Label("No active contracts currently found in the database."));
                    contractsContainer.getChildren().add(empty);
                    return;
                }

                int idx = 0;
                for (Project p : projectList) {
                    contractsContainer.getChildren().add(createContractCard(idx, p));
                    idx++;
                }
                com.freelancing.util.AnimationUtil.applyFadeZoom(contractsContainer, 240);
            }
        );
    }

    private VBox createContractCard(int index, Project project) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);");

        // Top Row: Title, Category, Budget
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(project.getTitle());
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        titleLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label catBadge = UIComponents.createBadge(project.getCategory() != null ? project.getCategory() : "GENERAL", "rgba(99, 102, 241, 0.2)", AppTheme.COLOR_PRIMARY);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label budgetLbl = new Label(AppTheme.formatCurrency(project.getBudget()));
        budgetLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        budgetLbl.setTextFill(Color.web(AppTheme.COLOR_SUCCESS));

        topRow.getChildren().addAll(titleLbl, catBadge, sp, budgetLbl);

        // Milestone Progress Bar
        VBox progressBox = new VBox(4);
        double progressVal = project.getStatus() == Project.Status.COMPLETED ? 1.0 : (project.getStatus() == Project.Status.IN_PROGRESS ? 0.65 : 0.25);
        ProgressBar pBar = new ProgressBar(progressVal);
        pBar.setMaxWidth(Double.MAX_VALUE);
        pBar.setStyle("-fx-accent: #10B981;");

        HBox pLabels = new HBox();
        Label pText = new Label("Escrow Milestone Fulfillment: " + (int)(progressVal * 100) + "%");
        pText.setFont(Font.font("Segoe UI", 11));
        pText.setTextFill(Color.web(AppTheme.getTextMuted()));
        Region pSp = new Region();
        HBox.setHgrow(pSp, Priority.ALWAYS);
        Label statusLbl = new Label("Status: " + project.getStatus());
        statusLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        statusLbl.setTextFill(Color.web(project.getStatus() == Project.Status.OPEN ? AppTheme.COLOR_PRIMARY : AppTheme.COLOR_SUCCESS));
        pLabels.getChildren().addAll(pText, pSp, statusLbl);
        progressBox.getChildren().addAll(pBar, pLabels);

        // Action Buttons Row
        HBox actRow = new HBox(10);
        actRow.setAlignment(Pos.CENTER_RIGHT);

        Button btnInvoice = UIComponents.createExportButton("📄 Invoice");
        btnInvoice.setOnAction(e -> exportContractInvoice(project));

        Button btnDispute = UIComponents.createDangerButton("Raise Dispute");
        btnDispute.setOnAction(e -> openDisputeModal(project));

        Button btnRelease = UIComponents.createSuccessButton("Approve Milestone ($" + String.format("%.0f", project.getBudget() * 0.5) + ")");
        btnRelease.setOnAction(e -> {
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Milestone Approved", "Funds Released",
                    "Milestone release confirmed! Funds disbursed to freelancer via smart escrow contract.");
        });

        actRow.getChildren().addAll(btnInvoice, btnDispute, btnRelease);

        card.getChildren().addAll(topRow, progressBox, actRow);
        return card;
    }

    // =========================================================================
    // AUDIT STATEMENTS & INVOICE EXPORT
    // =========================================================================

    private void exportAuditStatement() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Escrow Audit Statement");
            fileChooser.setInitialFileName("SkillBridge_Escrow_Statement.txt");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("===============================================================\n");
                    writer.write("           SKILLBRIDGE SMART ESCROW AUDIT STATEMENT           \n");
                    writer.write("===============================================================\n\n");
                    writer.write("Generated Date: " + new Date().toString() + "\n");
                    writer.write("Active Currency: " + AppTheme.getCurrentCurrency() + "\n");
                    writer.write("Platform Total Vault: " + AppTheme.formatCurrency(2450000) + "\n");
                    writer.write("Active Escrow Locks: " + AppTheme.formatCurrency(184500) + "\n\n");
                    writer.write("---------------- CONTRACT TRANSACTIONS ----------------\n");
                    for (Project p : db.getProjects().values()) {
                        writer.write(String.format("CONTRACT ID: %s | TITLE: %s\n", p.getId(), p.getTitle()));
                        writer.write(String.format("BUDGET: %s | STATUS: %s\n", AppTheme.formatCurrency(p.getBudget()), p.getStatus()));
                        writer.write("ESCROW SECURITY: 100% Non-Custodial Multi-Signature Smart Contract\n");
                        writer.write("---------------------------------------------------------------\n");
                    }
                }
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Export Success", "Statement Created", "Audit ledger successfully exported to:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            UIComponents.showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to Export", e.getMessage());
        }
    }

    private void exportContractInvoice(Project project) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Contract Milestone Invoice");
            fileChooser.setInitialFileName("Invoice_" + project.getId() + ".txt");
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("SKILLBRIDGE OFFICIAL ESCROW INVOICE\n");
                    writer.write("Contract ID: " + project.getId() + "\n");
                    writer.write("Project: " + project.getTitle() + "\n");
                    writer.write("Amount: " + AppTheme.formatCurrency(project.getBudget()) + "\n");
                    writer.write("Tax & Fees: $0.00 (Zero Counterparty Escrow)\n");
                    writer.write("Status: Escrow Funded & Cryptographically Verified\n");
                }
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Invoice Downloaded", "Saved", "Invoice file generated.");
            }
        } catch (Exception e) {
            UIComponents.showAlert(Alert.AlertType.ERROR, "Invoice Error", "Could not generate invoice", e.getMessage());
        }
    }

    // =========================================================================
    // MODAL DIALOGS
    // =========================================================================

    private void openDepositModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(500);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("➕ Lock Milestone Funds in Escrow");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        ComboBox<String> cbProject = new ComboBox<>();
        for (Project p : db.getProjects().values()) {
            cbProject.getItems().add(p.getTitle() + " (" + AppTheme.formatCurrency(p.getBudget()) + ")");
        }
        if (!cbProject.getItems().isEmpty()) cbProject.setValue(cbProject.getItems().get(0));
        UIComponents.styleComboBox(cbProject);

        TextField tfAmount = UIComponents.createTextField("Deposit Amount in USD (e.g. 1500)...");
        ComboBox<String> cbPayment = new ComboBox<>();
        cbPayment.getItems().addAll("Direct Bank Wire / ACH", "Stripe Corporate Card", "USDC / Cryptographic Settlement");
        cbPayment.setValue("Direct Bank Wire / ACH");
        UIComponents.styleComboBox(cbPayment);

        Button btnConfirm = UIComponents.createSuccessButton("Deposit & Lock in Smart Escrow");
        btnConfirm.setOnAction(e -> {
            closeModal();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Escrow Funded", "Deposit Completed",
                    "Funds successfully transferred into secure non-custodial escrow vault!");
        });

        modal.getChildren().addAll(header, new Label("Select Active Contract:"), cbProject, new Label("Deposit Amount:"), tfAmount, new Label("Payment Rail:"), cbPayment, btnConfirm);
        showModal(modal);
    }

    private void openDisputeModal(Project project) {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(500);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("⚖️ Raise Dispute Ticket for " + project.getTitle());
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        TextArea taIssue = UIComponents.createTextArea("Explain the deliverable discrepancy or milestone delay...");
        taIssue.setPrefRowCount(4);

        Button btnSubmit = UIComponents.createDangerButton("Submit for Platform Arbitration");
        btnSubmit.setOnAction(e -> {
            closeModal();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Dispute Raised", "Arbitrator Assigned",
                    "A SkillBridge dispute mediator has been allocated. Milestone funds remain securely locked until resolution.");
        });

        modal.getChildren().addAll(header, new Label("Reason for Dispute:"), taIssue, btnSubmit);
        showModal(modal);
    }

    private void showModal(VBox modalCard) {
        if (rootStack == null) return;
        closeModal();

        modalBackdrop = new Pane();
        modalBackdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.65);");
        modalBackdrop.setOnMouseClicked(e -> closeModal());

        activeModal = modalCard;
        modalCard.setAlignment(Pos.CENTER);
        StackPane.setAlignment(modalCard, Pos.CENTER);

        rootStack.getChildren().addAll(modalBackdrop, modalCard);
    }

    private void closeModal() {
        if (rootStack != null) {
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

    private void openVaultSolvencyModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🏦 Total Platform Vault Solvency Audit");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createAuditRow("Total Assets in Multi-Sig Custody", "$2,450,000.00 USD", AppTheme.COLOR_PRIMARY),
                createAuditRow("Cold Storage Allocation", "85% Hardware Security Modules (HSM)", AppTheme.COLOR_SUCCESS),
                createAuditRow("Hot Settlement Liquidity", "15% Instant Milestone Disbursement Buffer", AppTheme.COLOR_AMBER),
                createAuditRow("Proof of Solvency Ratio", "108.4% (Over-collateralized Guarantee)", AppTheme.COLOR_ACCENT)
        );

        modal.getChildren().addAll(header, rows);
        showModal(modal);
    }

    private void openActiveEscrowModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🔒 Active Milestone Escrow Collateral");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createAuditRow("Currently Locked Milestone Funds", "$184,500.00 USD", AppTheme.COLOR_SUCCESS),
                createAuditRow("Active Sprints in Development", String.valueOf(db.getProjects().size()) + " Active Contracts", AppTheme.COLOR_PRIMARY),
                createAuditRow("Average Milestone Lock Period", "12 Business Days", AppTheme.COLOR_AMBER),
                createAuditRow("Cryptographic Custody Standard", "Dual Key Multi-Sig (Client + Escrow Oracle)", AppTheme.COLOR_ACCENT)
        );

        modal.getChildren().addAll(header, rows);
        showModal(modal);
    }

    private void openDisbursementsModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("💸 Monthly Milestone Payouts & Settlement");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createAuditRow("Disbursed to Freelancers (30d)", "$78,200.00 USD", AppTheme.COLOR_AMBER),
                createAuditRow("On-Time Payout Ratio", "99.8% (< 5 seconds post sign-off)", AppTheme.COLOR_SUCCESS),
                createAuditRow("Counterparty Gas & Wire Fees", "$0.00 (SkillBridge Zero-Fee Layer)", AppTheme.COLOR_PRIMARY),
                createAuditRow("Multi-Currency Payout Rails", "USD, EUR, GBP, INR, USDC Supported", AppTheme.COLOR_ACCENT)
        );

        modal.getChildren().addAll(header, rows);
        showModal(modal);
    }

    private void openArbitrationSlaModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("⚖️ Dispute Arbitration & Mediation Directory");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createAuditRow("Arbitration Resolution Success", "100.0% Amicable Resolution", AppTheme.COLOR_ACCENT),
                createAuditRow("Average Mediation Turnaround", "< 24 Hours", AppTheme.COLOR_SUCCESS),
                createAuditRow("Certified Neutral Mediators", "12 Certified Technical Arbitrators on Standby", AppTheme.COLOR_PRIMARY),
                createAuditRow("Escrow Protection Clause", "Milestone funds remain frozen until mutual sign-off", AppTheme.COLOR_AMBER)
        );

        modal.getChildren().addAll(header, rows);
        showModal(modal);
    }

    private HBox createAuditRow(String label, String value, String accentColor) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: " + (AppTheme.isDarkMode() ? "#0F172A" : "#D4E0F0") + ";" +
                     "-fx-border-color: " + (AppTheme.isDarkMode() ? "#334155" : "#BACADB") + ";" +
                     "-fx-border-radius: 8; -fx-background-radius: 8;");

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        lbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        val.setTextFill(Color.web(accentColor));

        row.getChildren().addAll(lbl, sp, val);
        return row;
    }
}
