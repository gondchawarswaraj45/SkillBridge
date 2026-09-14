package com.freelancing.ui.freelancer;

import com.freelancing.model.freelancer.SkillExchange;
import com.freelancing.model.freelancer.SkillSwapOffer;
import com.freelancing.service.freelancer.SkillExchangeService;
import com.freelancing.ui.common.HomePage;
import com.freelancing.ui.common.UIComponents;

import com.freelancing.app.SessionManager;
import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Peer-to-Peer Developer Skill Exchange & Barter Hub.
 * Strictly adheres to Single Stage Rule (swaps Scene on HomePage.primaryStage).
 * 100% Inline CSS driven by AppTheme (non-white pastel light mode).
 */
public class SkillExchangeView {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final SkillExchangeService exchangeService = new SkillExchangeService();
    private final ExecutorService asyncPool = Executors.newFixedThreadPool(2);

    private StackPane rootStack;
    private FlowPane offersGrid;
    private TextField searchField;
    private String selectedDomain = "All";

    private Pane modalBackdrop;
    private VBox activeModal;

    private String getEffectiveUserId() {
        String uid = SessionManager.getInstance().getCurrentUserId();
        return uid != null ? uid : "usr_free1";
    }

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

        VBox content = new VBox(28);
        content.setPadding(new Insets(24, 40, 40, 40));
        content.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = UIComponents.createScrollPane(content);

        // Hero Banner: The Peer-to-Peer Barter Concept
        HBox heroBanner = createHeroBanner();

        // Filter Bar (Domain Chips + Search + Post Swap Action)
        VBox filterSection = createFilterSection();

        // Offers Grid
        offersGrid = new FlowPane();
        offersGrid.setHgap(18);
        offersGrid.setVgap(18);
        offersGrid.setPrefWrapLength(1100);

        content.getChildren().addAll(heroBanner, filterSection, offersGrid);
        scrollPane.setContent(content);
        layout.setCenter(scrollPane);

        rootStack.getChildren().add(layout);

        // Initial async data load
        refreshOffersAsync();

        com.freelancing.util.AnimationUtil.applyFadeZoom(layout, 280);
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
        Button linkSwap = createNavLink("🔄 Skill Exchange", true, () -> {});
        Button linkEscrow = createNavLink("🛡️ Escrow & Vault", false, () -> HomePage.showContractsAndEscrowView());
        Button linkAnalytics = createNavLink("📊 Analytics", false, () -> HomePage.showAnalyticsDashboardView());
        Button linkCommunity = createNavLink("💬 Community", false, () -> HomePage.showCommunityForumView());

        navLinks.getChildren().addAll(linkMarket, linkSwap, linkEscrow, linkAnalytics, linkCommunity);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        // Theme Switcher & Post Swap Action
        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showSkillExchangeView();
        });

        Button btnPostSwap = UIComponents.createAccentButton("＋ Post Swap Offer");
        btnPostSwap.setOnAction(e -> openPostSwapModal());

        Button btnBackHome = UIComponents.createExportButton("← Back to Home");
        btnBackHome.setOnAction(e -> HomePage.showHomeView());

        nav.getChildren().addAll(logo, navLinks, sp, btnTheme, btnPostSwap, btnBackHome);
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
    // HERO BANNER & SKILL CREDITS WIDGET
    // =========================================================================

    private HBox createHeroBanner() {
        HBox hero = new HBox(20);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(20, 26, 20, 26));
        hero.setStyle(AppTheme.getHeroBannerStyle());

        VBox left = new VBox(10);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label badge = new Label("🔄 Peer-to-Peer Developer Barter Network");
        badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        badge.setStyle("-fx-background-color: rgba(99, 102, 241, 0.15); -fx-text-fill: " + AppTheme.COLOR_PRIMARY + "; -fx-padding: 4 12; -fx-background-radius: 20; -fx-cursor: hand;");
        badge.setOnMouseClicked(e -> openBarterRulesModal());

        Label heading = new Label("Trade Technical Expertise Without Cash.\nLevel Up Through Reciprocal Engineering.");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label subhead = new Label("SkillBridge Exchange allows engineers to trade hours of 1-on-1 mentorship, pair programming, and code reviews. Teach your strength, learn your target tech stack.");
        subhead.setFont(Font.font("Segoe UI", 12));
        subhead.setTextFill(Color.web(AppTheme.getTextMuted()));
        subhead.setWrapText(true);
        subhead.setMaxWidth(620);

        left.getChildren().addAll(badge, heading, subhead);

        // Right Widget: Developer's Skill Barter Credits Card (Clickable Ledger)
        VBox creditCard = new VBox(8);
        creditCard.setPadding(new Insets(14, 18, 14, 18));
        creditCard.setAlignment(Pos.CENTER);
        creditCard.setPrefWidth(260);
        String baseCreditStyle = AppTheme.isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.4); -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand;"
            : "-fx-background-color: #D6E4F7; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand;";
        creditCard.setStyle(baseCreditStyle);
        creditCard.setOnMouseEntered(e -> creditCard.setStyle(baseCreditStyle + "; -fx-border-color: " + AppTheme.COLOR_PRIMARY + "; -fx-border-width: 2;"));
        creditCard.setOnMouseExited(e -> creditCard.setStyle(baseCreditStyle));
        creditCard.setOnMouseClicked(e -> openTokenLedgerModal());

        Label creditTitle = new Label("⚡ Your Barter Balance");
        creditTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        creditTitle.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        Label creditVal = new Label("14 Hours Available");
        creditVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        creditVal.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label creditTokens = new Label("🪙 6 Verified Skill Tokens");
        creditTokens.setFont(Font.font("Segoe UI", 11));
        creditTokens.setTextFill(Color.web(AppTheme.COLOR_SUCCESS));

        Label clickLedger = new Label("Click for Token Ledger & Claim ➔");
        clickLedger.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 10));
        clickLedger.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        creditCard.getChildren().addAll(creditTitle, creditVal, creditTokens, clickLedger);

        hero.getChildren().addAll(left, creditCard);
        return hero;
    }

    // =========================================================================
    // FILTER SECTION & ASYNC SEARCH
    // =========================================================================

    private VBox createFilterSection() {
        VBox sec = new VBox(14);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        searchField = UIComponents.createTextField("Search offered skills, technologies, or keywords...");
        searchField.setPrefWidth(340);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshOffersAsync());

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnNewSwap = UIComponents.createAccentButton("＋ Post New Swap");
        btnNewSwap.setOnAction(e -> openPostSwapModal());

        topRow.getChildren().addAll(new Label("🔍"), searchField, sp, btnNewSwap);

        // Domain Filter Chips
        HBox chipsBar = new HBox(8);
        String[] domains = {"All", "Backend & Java", "Mobile & Flutter", "AI & Python", "DevOps & Cloud", "UI/UX & Design"};
        for (String d : domains) {
            Button chip = new Button(d);
            chip.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
            applyDomainChipStyle(chip, d.equals(selectedDomain));
            chip.setOnAction(e -> {
                selectedDomain = d;
                for (Node n : chipsBar.getChildren()) {
                    if (n instanceof Button) {
                        applyDomainChipStyle((Button) n, ((Button) n).getText().equals(selectedDomain));
                    }
                }
                refreshOffersAsync();
            });
            chipsBar.getChildren().add(chip);
        }

        sec.getChildren().addAll(topRow, chipsBar);
        return sec;
    }

    private void applyDomainChipStyle(Button chip, boolean active) {
        if (active) {
            chip.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;");
        } else {
            chip.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #1E293B; -fx-text-fill: #CBD5E1; -fx-border-color: #334155; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;"
                : "-fx-background-color: #DCE6F5; -fx-text-fill: #1E293B; -fx-border-color: #BACADB; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;");
        }
    }

    private void refreshOffersAsync() {
        final String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        final String domainFilter = selectedDomain;

        asyncPool.execute(() -> {
            List<SkillExchange> sqliteOffers;
            try {
                sqliteOffers = exchangeService.getAllOffers("ALL", query);
            } catch (Exception e) {
                sqliteOffers = Collections.emptyList();
            }

            // Filter domain if needed
            List<SkillExchange> filteredSqlite = sqliteOffers.stream()
                .filter(o -> {
                    boolean matchesDomain = domainFilter.equals("All") ||
                        (domainFilter.contains("Java") && (o.getOfferedSkill().toLowerCase().contains("java") || o.getRequestedSkill().toLowerCase().contains("java"))) ||
                        (domainFilter.contains("Mobile") && (o.getOfferedSkill().toLowerCase().contains("flutter") || o.getRequestedSkill().toLowerCase().contains("flutter") || o.getOfferedSkill().toLowerCase().contains("mobile"))) ||
                        (domainFilter.contains("AI") && (o.getOfferedSkill().toLowerCase().contains("ai") || o.getRequestedSkill().toLowerCase().contains("ai") || o.getOfferedSkill().toLowerCase().contains("python") || o.getRequestedSkill().toLowerCase().contains("python"))) ||
                        (domainFilter.contains("DevOps") && (o.getOfferedSkill().toLowerCase().contains("kubernetes") || o.getRequestedSkill().toLowerCase().contains("devops") || o.getOfferedSkill().toLowerCase().contains("cloud"))) ||
                        (domainFilter.contains("UI/UX") && (o.getOfferedSkill().toLowerCase().contains("figma") || o.getRequestedSkill().toLowerCase().contains("ui/ux") || o.getOfferedSkill().toLowerCase().contains("design")));
                    return matchesDomain;
                })
                .collect(Collectors.toList());

            Platform.runLater(() -> {
                if (offersGrid == null) return;
                offersGrid.getChildren().clear();

                if (!filteredSqlite.isEmpty()) {
                    for (SkillExchange ex : filteredSqlite) {
                        offersGrid.getChildren().add(createExchangeCard(ex));
                    }
                    com.freelancing.util.AnimationUtil.applyFadeZoom(offersGrid, 240);
                    return;
                }

                // Fallback to legacy in-memory offers
                List<SkillSwapOffer> all = new ArrayList<>(db.getSkillSwapOffers().values());
                List<SkillSwapOffer> filtered = all.stream()
                    .filter(o -> {
                        boolean matchesDomain = domainFilter.equals("All") ||
                            (domainFilter.contains("Java") && (o.getOfferedSkill().contains("Java") || o.getSeekingSkill().contains("Java"))) ||
                            (domainFilter.contains("Mobile") && (o.getOfferedSkill().contains("Flutter") || o.getSeekingSkill().contains("Flutter") || o.getOfferedSkill().contains("Mobile"))) ||
                            (domainFilter.contains("AI") && (o.getOfferedSkill().contains("AI") || o.getSeekingSkill().contains("AI") || o.getOfferedSkill().contains("Python") || o.getSeekingSkill().contains("Python"))) ||
                            (domainFilter.contains("DevOps") && (o.getOfferedSkill().contains("Kubernetes") || o.getSeekingSkill().contains("DevOps") || o.getOfferedSkill().contains("Cloud"))) ||
                            (domainFilter.contains("UI/UX") && (o.getOfferedSkill().contains("Figma") || o.getSeekingSkill().contains("UI/UX") || o.getOfferedSkill().contains("Design")));

                        boolean matchesQuery = query.isEmpty() ||
                            o.getOfferedSkill().toLowerCase().contains(query) ||
                            o.getSeekingSkill().toLowerCase().contains(query) ||
                            o.getAuthorName().toLowerCase().contains(query) ||
                            o.getDescription().toLowerCase().contains(query);

                        return matchesDomain && matchesQuery;
                    })
                    .collect(Collectors.toList());

                if (filtered.isEmpty()) {
                    VBox emptyBox = new VBox(10);
                    emptyBox.setAlignment(Pos.CENTER);
                    emptyBox.setPadding(new Insets(40));
                    Label emptyLbl = new Label("No matching skill swap offers found. Try adjusting your search query or domain filter.");
                    emptyLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                    emptyBox.getChildren().add(emptyLbl);
                    offersGrid.getChildren().add(emptyBox);
                    return;
                }

                for (SkillSwapOffer offer : filtered) {
                    offersGrid.getChildren().add(createSwapOfferCard(offer));
                }
                com.freelancing.util.AnimationUtil.applyFadeZoom(offersGrid, 240);
            });
        });
    }

    // =========================================================================
    // SQLITE SKILL EXCHANGE CARD COMPONENT
    // =========================================================================

    private VBox createExchangeCard(SkillExchange exchange) {
        VBox card = new VBox(12);
        card.setPrefWidth(350);
        card.setPadding(new Insets(18));
        card.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);");

        // Top Author Row
        HBox authorRow = new HBox(10);
        authorRow.setAlignment(Pos.CENTER_LEFT);

        Node avatar = createThematicAvatar(38);

        VBox authorMeta = new VBox(2);
        String offererName = exchange.getOffererName() != null ? exchange.getOffererName() : "Specialist (" + exchange.getOffererId() + ")";
        Label nameLbl = new Label(offererName);
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label titleLbl = new Label(exchange.getCreatedAt() != null ? exchange.getCreatedAt() : "Active");
        titleLbl.setFont(Font.font("Segoe UI", 11));
        titleLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        authorMeta.getChildren().addAll(nameLbl, titleLbl);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label statusBadge = getStatusBadge(exchange.getStatus());
        authorRow.getChildren().addAll(avatar, authorMeta, sp, statusBadge);

        // Trade Match Box (Offering ➔ Seeking)
        VBox tradeBox = new VBox(6);
        tradeBox.setPadding(new Insets(10));
        tradeBox.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #0F172A; -fx-background-radius: 8;"
            : "-fx-background-color: #CFDAEC; -fx-background-radius: 8;");

        Label offLabel = new Label("🟢 TEACHING: " + exchange.getOfferedSkill());
        offLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        offLabel.setTextFill(Color.web(AppTheme.isDarkMode() ? "#34D399" : "#065F46"));

        Label seekLabel = new Label("🟣 LEARNING: " + exchange.getRequestedSkill());
        seekLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        seekLabel.setTextFill(Color.web(AppTheme.isDarkMode() ? "#A78BFA" : "#5B21B6"));

        tradeBox.getChildren().addAll(offLabel, seekLabel);

        // Description
        Label desc = new Label(exchange.getDescription() != null ? exchange.getDescription() : "");
        desc.setFont(Font.font("Segoe UI", 12));
        desc.setTextFill(Color.web(AppTheme.getTextMuted()));
        desc.setWrapText(true);
        desc.setMaxHeight(40);

        // Bottom Details & Action
        HBox botRow = new HBox(8);
        botRow.setAlignment(Pos.CENTER_LEFT);

        Label hoursBadge = new Label("⏱️ " + exchange.getHoursPerWeek() + "h/wk");
        hoursBadge.setFont(Font.font("Segoe UI", 10));
        hoursBadge.setTextFill(Color.web(AppTheme.getTextMuted()));

        Region botSp = new Region();
        HBox.setHgrow(botSp, Priority.ALWAYS);

        final String currentUserId = getEffectiveUserId();
        Node actionNode = buildExchangeActionNode(exchange, currentUserId);

        botRow.getChildren().addAll(hoursBadge, botSp, actionNode);
        card.getChildren().addAll(authorRow, tradeBox, desc, botRow);
        return card;
    }

    private Label getStatusBadge(SkillExchange.Status status) {
        if (status == null) status = SkillExchange.Status.OPEN;
        switch (status) {
            case OPEN:
                return UIComponents.createBadge("OPEN", "rgba(16, 185, 129, 0.2)", AppTheme.COLOR_SUCCESS);
            case REQUESTED:
                return UIComponents.createBadge("REQUESTED", "rgba(245, 158, 11, 0.2)", AppTheme.COLOR_AMBER);
            case IN_PROGRESS:
                return UIComponents.createBadge("IN PROGRESS", "rgba(99, 102, 241, 0.2)", AppTheme.COLOR_PRIMARY);
            case COMPLETED:
                return UIComponents.createBadge("COMPLETED ✓", "rgba(139, 92, 246, 0.2)", "#8B5CF6");
            case CANCELLED:
            default:
                return UIComponents.createBadge("CANCELLED", "rgba(148, 163, 184, 0.2)", "#94A3B8");
        }
    }

    private Node buildExchangeActionNode(SkillExchange ex, String currentUserId) {
        if (ex.getStatus() == SkillExchange.Status.OPEN) {
            if (currentUserId.equals(ex.getOffererId())) {
                Label lbl = new Label("Your Active Listing");
                lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 10));
                lbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                return lbl;
            } else {
                Button btnReq = UIComponents.createClientDemoButton("Request Swap");
                btnReq.setOnAction(e -> openRequestExchangeModal(ex));
                return btnReq;
            }
        } else if (ex.getStatus() == SkillExchange.Status.REQUESTED) {
            if (currentUserId.equals(ex.getOffererId())) {
                HBox actions = new HBox(6);
                Button btnAccept = UIComponents.createSuccessButton("Accept");
                btnAccept.setOnAction(e -> {
                    boolean ok = exchangeService.acceptExchange(ex.getId(), currentUserId);
                    if (ok) {
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Accepted", "Barter Started", "You accepted this skill barter collaboration!");
                        refreshOffersAsync();
                    }
                });
                Button btnReject = UIComponents.createDangerButton("Decline");
                btnReject.setOnAction(e -> {
                    boolean ok = exchangeService.rejectExchange(ex.getId(), currentUserId);
                    if (ok) {
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Declined", "Request Reset", "You declined the request; offer is now open.");
                        refreshOffersAsync();
                    }
                });
                actions.getChildren().addAll(btnAccept, btnReject);
                return actions;
            } else if (currentUserId.equals(ex.getRequesterId())) {
                Label lbl = new Label("Your Request Pending");
                lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 10));
                lbl.setTextFill(Color.web(AppTheme.COLOR_AMBER));
                return lbl;
            } else {
                Label lbl = new Label("Pending Review");
                lbl.setFont(Font.font("Segoe UI", 10));
                lbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                return lbl;
            }
        } else if (ex.getStatus() == SkillExchange.Status.IN_PROGRESS) {
            if (currentUserId.equals(ex.getOffererId()) || currentUserId.equals(ex.getRequesterId())) {
                Button btnComp = UIComponents.createAccentButton("Complete Barter ✓");
                btnComp.setOnAction(e -> {
                    boolean ok = exchangeService.completeExchange(ex.getId(), currentUserId);
                    if (ok) {
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Collaboration Completed", "Congratulations!", "You successfully completed this skill barter session.");
                        refreshOffersAsync();
                    }
                });
                return btnComp;
            } else {
                Label lbl = new Label("Exchange In Progress");
                lbl.setFont(Font.font("Segoe UI", 10));
                lbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                return lbl;
            }
        } else if (ex.getStatus() == SkillExchange.Status.COMPLETED) {
            Label lbl = new Label("Completed ✓");
            lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            lbl.setTextFill(Color.web("#8B5CF6"));
            return lbl;
        }

        Label lbl = new Label("Closed");
        lbl.setFont(Font.font("Segoe UI", 10));
        lbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        return lbl;
    }

    private void openRequestExchangeModal(SkillExchange exchange) {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(500);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        String name = exchange.getOffererName() != null ? exchange.getOffererName() : "Partner";
        Label title = UIComponents.createHeader("🤝 Request Barter Session with " + name);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        Label info = new Label("Trading for: " + exchange.getOfferedSkill() + "\nOffering in return: " + exchange.getRequestedSkill());
        info.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        info.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        TextArea taMsg = UIComponents.createTextArea("Introduce your background and propose collaboration goals...");
        taMsg.setPrefRowCount(4);

        Button btnSend = UIComponents.createPrimaryButton("Dispatch Barter Request");
        btnSend.setOnAction(e -> {
            String note = taMsg.getText().trim();
            String currentUserId = getEffectiveUserId();
            boolean success = exchangeService.requestExchange(exchange.getId(), currentUserId, note);
            closeModal();
            if (success) {
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Request Sent", "Barter Invitation Dispatched",
                        "Your skill barter request has been delivered to " + name + ". You will receive an in-app notification when accepted!");
                refreshOffersAsync();
            } else {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Request Error", "Unable to Send",
                        "Could not submit barter request. You cannot request your own offer or request an inactive barter.");
            }
        });

        modal.getChildren().addAll(header, info, new Label("Invitation Message:"), taMsg, btnSend);
        showModal(modal);
    }

    // Legacy card for fallback
    private VBox createSwapOfferCard(SkillSwapOffer offer) {
        VBox card = new VBox(12);
        card.setPrefWidth(350);
        card.setPadding(new Insets(18));
        card.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12;"
            : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox authorRow = new HBox(10);
        authorRow.setAlignment(Pos.CENTER_LEFT);
        Node avatar = createThematicAvatar(38);

        VBox authorMeta = new VBox(2);
        Label nameLbl = new Label(offer.getAuthorName());
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));
        authorMeta.getChildren().add(nameLbl);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ratingBadge = new Label("⭐ " + String.format("%.2f", offer.getAuthorRating()));
        ratingBadge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        authorRow.getChildren().addAll(avatar, authorMeta, sp, ratingBadge);

        VBox tradeBox = new VBox(6);
        tradeBox.setPadding(new Insets(10));
        tradeBox.setStyle(AppTheme.isDarkMode() ? "-fx-background-color: #0F172A; -fx-background-radius: 8;" : "-fx-background-color: #CFDAEC; -fx-background-radius: 8;");
        Label offLabel = new Label("🟢 TEACHING: " + offer.getOfferedSkill());
        offLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        offLabel.setTextFill(Color.web(AppTheme.isDarkMode() ? "#34D399" : "#065F46"));
        Label seekLabel = new Label("🟣 LEARNING: " + offer.getSeekingSkill());
        seekLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        seekLabel.setTextFill(Color.web(AppTheme.isDarkMode() ? "#A78BFA" : "#5B21B6"));
        tradeBox.getChildren().addAll(offLabel, seekLabel);

        Label desc = new Label(offer.getDescription());
        desc.setFont(Font.font("Segoe UI", 12));
        desc.setTextFill(Color.web(AppTheme.getTextMuted()));
        desc.setWrapText(true);

        card.getChildren().addAll(authorRow, tradeBox, desc);
        return card;
    }

    private Node createThematicAvatar(double size) {
        try {
            InputStream st = getClass().getResourceAsStream("/images/skillbridge_avatar.jpg");
            if (st != null) {
                ImageView iv = new ImageView(new Image(st));
                iv.setFitWidth(size);
                iv.setFitHeight(size);
                iv.setPreserveRatio(true);
                Rectangle clip = new Rectangle(size, size);
                clip.setArcWidth(size);
                clip.setArcHeight(size);
                iv.setClip(clip);
                return iv;
            }
        } catch (Exception ignored) {}
        Label fallback = new Label("👤");
        fallback.setFont(Font.font("Segoe UI", size * 0.6));
        return fallback;
    }

    // =========================================================================
    // IN-SCENE MODALS (POST SWAP)
    // =========================================================================

    private void openPostSwapModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(520);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🔄 Create a Skill Barter Offer");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        TextField tfOffer = UIComponents.createTextField("Skill You Offer (e.g. Java Spring Boot & Docker)...");
        TextField tfSeek = UIComponents.createTextField("Skill You Seek (e.g. React Native Mobile & UI/UX)...");

        ComboBox<String> cbHours = new ComboBox<>();
        cbHours.getItems().addAll("2 Hours / Week", "3 Hours / Week", "4 Hours / Week", "5+ Hours / Week");
        cbHours.setValue("3 Hours / Week");
        UIComponents.styleComboBox(cbHours);

        TextArea taDesc = UIComponents.createTextArea("Describe your experience and what you'd like to collaborate on...");
        taDesc.setPrefRowCount(3);

        Button btnSubmit = UIComponents.createAccentButton("Publish to Skill Barter Hub");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> {
            String off = tfOffer.getText().trim();
            String seek = tfSeek.getText().trim();
            if (off.isEmpty() || seek.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Missing Info", "Incomplete Swap Offer", "Please specify both the skill you offer and what you seek.");
                return;
            }

            int hours = 3;
            try {
                hours = Integer.parseInt(cbHours.getValue().substring(0, 1));
            } catch (Exception ignored) {}

            String currentUserId = getEffectiveUserId();
            try {
                exchangeService.createOffer(currentUserId, off, seek, taDesc.getText().trim(), hours);
            } catch (Exception ex) {
                // Log/fallback
            }

            // Sync legacy
            try {
                SkillSwapOffer newOffer = new SkillSwapOffer(
                    "swap_" + UUID.randomUUID().toString().substring(0, 8),
                    currentUserId, SessionManager.getInstance().getCurrentUsername(), "Specialist",
                    off, seek, "Senior (5+ Years)",
                    hours, 2, SkillSwapOffer.Status.OPEN,
                    taDesc.getText().trim(),
                    "2026-03-01", 5.0
                );
                db.getSkillSwapOffers().put(newOffer.getId(), newOffer);
                db.saveData();
            } catch (Exception ignored) {}

            closeModal();
            refreshOffersAsync();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Offer Published", "Skill Swap Active", "Your barter offer has been posted to the developer network!");
        });

        modal.getChildren().addAll(header, new Label("What You Teach:"), tfOffer, new Label("What You Learn:"), tfSeek, new Label("Weekly Commitment:"), cbHours, new Label("Description:"), taDesc, btnSubmit);
        showModal(modal);
    }

    public void openRequestSwapModal(SkillSwapOffer offer) {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(500);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🤝 Request Barter Session with " + offer.getAuthorName());
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        Label info = new Label("Trading for: " + offer.getOfferedSkill() + "\nOffering in return: " + offer.getSeekingSkill());
        info.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        info.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        ComboBox<String> cbLength = new ComboBox<>();
        cbLength.getItems().addAll("1 Hour Initial Code Review", "2 Hours Pair Programming", "Weekly Sprint (4 Hours)");
        cbLength.setValue("1 Hour Initial Code Review");
        UIComponents.styleComboBox(cbLength);

        TextArea taMsg = UIComponents.createTextArea("Introduce your background and propose a preliminary session date...");
        taMsg.setPrefRowCount(3);

        Button btnSend = UIComponents.createPrimaryButton("Dispatch Swap Request (Costs 2 Credits)");
        btnSend.setOnAction(e -> {
            closeModal();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Request Sent", "Swap Invitation Dispatched",
                    "Your skill barter request has been delivered to " + offer.getAuthorName() + ". You will be notified when accepted!");
        });

        modal.getChildren().addAll(header, info, new Label("Proposed Session Length:"), cbLength, new Label("Invitation Message:"), taMsg, btnSend);
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

    private void openBarterRulesModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🔄 Peer-to-Peer Barter Protocol & Rules");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox rulesList = new VBox(8);
        String[] rules = {
            "1. Zero Currency Required: Every exchange is hour-for-hour reciprocity or backed by verified Skill Tokens.",
            "2. Professional Courtesy: Complete agreed code reviews, architecture feedback, or pairing sessions within 7 days.",
            "3. Mutual Evaluation: Both participants submit mutual feedback; ratings affect peer matching algorithms.",
            "4. Anti-Ghosting Protocol: Unanswered swap invitations automatically expire and refund escrow credits in 48 hours."
        };
        for (String r : rules) {
            Label l = new Label(r);
            l.setFont(Font.font("Segoe UI", 12));
            l.setTextFill(Color.web(AppTheme.getTextPrimary()));
            l.setWrapText(true);
            rulesList.getChildren().add(l);
        }

        modal.getChildren().addAll(header, rulesList);
        showModal(modal);
    }

    private void openTokenLedgerModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🪙 Skill Tokens Ledger & Barter Balance");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createLedgerRow("Current Available Barter Hours", "14 Hours", AppTheme.COLOR_PRIMARY),
                createLedgerRow("Verified Skill Tokens Held", "6 Tokens", AppTheme.COLOR_SUCCESS),
                createLedgerRow("Completed Peer Barter Sessions", "8 Sessions (100% 5-Star Reviews)", AppTheme.COLOR_AMBER),
                createLedgerRow("Token Staking Rewards", "+0.5 Tokens / month active community mentor", AppTheme.COLOR_ACCENT)
        );

        Button btnClaimDaily = UIComponents.createAccentButton("🎁 Claim Daily Community Token (+1 Token)");
        btnClaimDaily.setOnAction(e -> {
            closeModal();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Daily Token Claimed", "Reward Credited",
                    "You claimed 1 Verified Skill Token for being an active contributor to the developer network! New balance: 7 Tokens.");
        });

        modal.getChildren().addAll(header, rows, btnClaimDaily);
        showModal(modal);
    }

    private HBox createLedgerRow(String label, String value, String accentColor) {
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
