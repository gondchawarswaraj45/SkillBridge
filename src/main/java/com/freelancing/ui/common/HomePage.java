package com.freelancing.ui.common;

import com.freelancing.config.AppTheme;
import com.freelancing.model.common.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;

/**
 * SkillBridge Clean & Professional Landing Experience.
 * Engineered for maximum performance, responsive clarity, and zero-hang execution.
 */
public class HomePage extends Application {

    private static Stage primaryStage;
    private static HomePage instance;

    // Pre-cached static hero image to prevent repeated resource stream lookups
    private static Image cachedHeroImage;
    static {
        try {
            InputStream is = HomePage.class.getResourceAsStream("/images/skillbridge_hero.jpg");
            if (is != null) {
                cachedHeroImage = new Image(is);
            }
        } catch (Exception ignored) {}
    }

    private ScrollPane mainScrollPane;
    private VBox mainContentBox;
    private VBox aboutSectionRef;

    public HomePage() {
        instance = this;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static HomePage getInstance() {
        return instance;
    }

    public void start(Stage stage) {
        primaryStage = stage;
        try {
            primaryStage.initStyle(StageStyle.UNDECORATED);
        } catch (Exception ignored) {}
        primaryStage.setTitle("SkillBridge - Freelancing & Skill Exchange Platform");

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double initialWidth = Math.min(1360, visualBounds.getWidth() * 0.92);
        double initialHeight = Math.min(880, visualBounds.getHeight() * 0.92);

        primaryStage.setWidth(initialWidth);
        primaryStage.setHeight(initialHeight);
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(640);

        primaryStage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - initialWidth) / 2);
        primaryStage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - initialHeight) / 2);

        com.freelancing.app.NavigationManager.getInstance().setPrimaryStage(primaryStage);

        showHomeView();
        primaryStage.show();
    }

    public void stop() {
        // Clean shutdown without background threads hanging
    }

    public static void setScene(Scene scene) {
        if (scene != null) {
            com.freelancing.app.NavigationManager.getInstance().setScene(scene);
        }
    }

    // =========================================================================
    // ROUTE DELEGATES FOR SEAMLESS PLATFORM NAVIGATION
    // =========================================================================

    public static void showHomeView() {
        com.freelancing.app.NavigationManager.getInstance().showHome();
    }

    public static void showLoginView() {
        com.freelancing.app.NavigationManager.getInstance().showLogin();
    }

    public static void showRegisterView() {
        com.freelancing.app.NavigationManager.getInstance().showRegister();
    }

    public static void showAdminLoginView() {
        com.freelancing.app.NavigationManager.getInstance().showAdminLogin();
    }

    public static void showRoleView(User user) {
        com.freelancing.app.NavigationManager.getInstance().showRoleDashboard(user);
    }

    public static void showFreelancerView(User user) {
        com.freelancing.app.NavigationManager.getInstance().showFreelancerDashboard(user);
    }

    public static void showClientView(User user) {
        com.freelancing.app.NavigationManager.getInstance().showClientDashboard(user);
    }

    public static void showAdminView(User user) {
        com.freelancing.app.NavigationManager.getInstance().showAdminDashboard(user);
    }

    public static void showCalendarView(User user) {
        com.freelancing.app.NavigationManager.getInstance().showCalendar(user);
    }

    public static void showSettingsView(User user) {
        com.freelancing.app.NavigationManager.getInstance().showSettings(user);
    }

    public static void showSkillExchangeView() {
        com.freelancing.app.NavigationManager.getInstance().showSkillExchange();
    }

    public static void showContractsAndEscrowView() {
        com.freelancing.app.NavigationManager.getInstance().showContractsAndEscrow();
    }

    public static void showAnalyticsDashboardView() {
        com.freelancing.app.NavigationManager.getInstance().showAnalyticsDashboard();
    }

    public static void showCommunityForumView() {
        com.freelancing.app.NavigationManager.getInstance().showCommunityForum();
    }

    // =========================================================================
    // CLEAN & PROFESSIONAL HOMEPAGE SCENE
    // =========================================================================

    public Parent createHomeView() {
        BorderPane layout = new BorderPane();
        layout.setStyle(AppTheme.getRootStyle());

        // 1. Top Navbar (Clean, with right side: About Us, Login, Create Account, Admin Login)
        HBox navbar = createNavbar();
        layout.setTop(navbar);

        // 2. Main Scrollable Content
        mainScrollPane = new ScrollPane();
        UIComponents.styleScrollPane(mainScrollPane);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setFitToHeight(false);
        mainScrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        mainContentBox = new VBox(45);
        mainContentBox.setPadding(new Insets(35, 60, 60, 60));
        mainContentBox.setAlignment(Pos.TOP_CENTER);
        mainContentBox.setMaxWidth(1280);

        // Section 1: Hero Banner
        HBox heroSection = createHeroSection();

        // Section 2: Core Three Pillars (Clean, focused feature blocks)
        VBox corePillars = createCorePillarsSection();

        // Section 3: About Us Anchor Section
        aboutSectionRef = createAboutUsSection();

        // Section 4: Call to Action Card
        VBox ctaBanner = createCallToActionSection();

        // Section 5: Clean Footer
        VBox footer = createFooter();

        mainContentBox.getChildren().addAll(
                heroSection,
                corePillars,
                aboutSectionRef,
                ctaBanner,
                footer
        );

        // Center the content container nicely on wide monitors
        StackPane contentWrapper = new StackPane(mainContentBox);
        contentWrapper.setAlignment(Pos.TOP_CENTER);
        contentWrapper.setStyle(AppTheme.getRootStyle());

        mainScrollPane.setContent(contentWrapper);
        layout.setCenter(mainScrollPane);
        mainContentBox.setOpacity(1.0);

        return layout;
    }

    public Scene createHomeScene() {
        Parent layout = createHomeView();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1360, bounds.getWidth() * 0.92);
        double sceneHeight = Math.min(880, bounds.getHeight() * 0.92);

        Scene scene = new Scene(layout, sceneWidth, sceneHeight);
        setupKeyboardShortcuts(scene);

        return scene;
    }

    // =========================================================================
    // NAVBAR: BRAND ON LEFT | RIGHT: ABOUT US, LOGIN, CREATE ACCOUNT, ADMIN LOGIN
    // =========================================================================

    private HBox createNavbar() {
        HBox nav = new HBox(16);
        nav.setPadding(new Insets(14, 45, 14, 45));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle(AppTheme.getNavbarStyle());

        // Left: Brand Logo
        Label logo = new Label("⚡ SkillBridge");
        logo.setFont(Font.font("Segoe UI", FontWeight.BLACK, 23));
        logo.setTextFill(Color.web("#FFFFFF"));
        logo.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 4, 0, 0, 1);");
        logo.setOnMouseClicked(e -> {
            if (mainScrollPane != null) {
                mainScrollPane.setVvalue(0.0);
            }
        });

        Label logoTagline = new Label("Freelance & Engineering Network");
        logoTagline.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        logoTagline.setTextFill(Color.web("#DBEAFE"));

        HBox logoBox = new HBox(12, logo, logoTagline);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right side: About Us, Login, Create Account, Admin Login
        // 1. About Us Button
        Button btnAboutUs = createNavTextButton("About Us", this::scrollToAboutSection);

        // 2. Login Button
        Button btnLogin = createNavOutlineButton("Login", HomePage::showLoginView);

        // 3. Create Account Button
        Button btnCreateAccount = createNavPrimaryButton("Create Account", HomePage::showRegisterView);

        // 4. Admin Login Button
        Button btnAdminLogin = createNavAdminButton("🛡️ Admin Login", HomePage::showAdminLoginView);

        // Theme Toggle (Dark / Light)
        Button themeBtn = UIComponents.createThemeToggle(() -> {
            showHomeView();
        });

        HBox rightNav = new HBox(14, btnAboutUs, btnLogin, btnCreateAccount, btnAdminLogin, themeBtn);
        rightNav.setAlignment(Pos.CENTER_RIGHT);

        nav.getChildren().addAll(logoBox, spacer, rightNav);
        return nav;
    }

    private void scrollToAboutSection() {
        if (aboutSectionRef != null && mainScrollPane != null) {
            Platform.runLater(() -> {
                double contentHeight = mainContentBox.getBoundsInLocal().getHeight();
                double aboutY = aboutSectionRef.getBoundsInParent().getMinY();
                double viewportHeight = mainScrollPane.getViewportBounds().getHeight();
                double scrollableHeight = contentHeight - viewportHeight;
                if (scrollableHeight > 0) {
                    double targetV = Math.max(0.0, Math.min(1.0, aboutY / scrollableHeight));
                    mainScrollPane.setVvalue(targetV);
                }
            });
        }
    }

    private Button createNavTextButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        String base = "-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-padding: 8 14; -fx-background-radius: 6;";
        String hover = "-fx-background-color: rgba(255, 255, 255, 0.18); -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-padding: 8 14; -fx-background-radius: 6;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Button createNavOutlineButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        String base = "-fx-background-color: rgba(255, 255, 255, 0.12); -fx-border-color: rgba(255, 255, 255, 0.7); -fx-border-width: 1.5; -fx-border-radius: 7; -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-padding: 7 16; -fx-background-radius: 7;";
        String hover = "-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; -fx-border-width: 1.5; -fx-border-radius: 7; -fx-text-fill: #1D4ED8; -fx-cursor: hand; -fx-padding: 7 16; -fx-background-radius: 7; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 2);";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Button createNavPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        String base = "-fx-background-color: #FFFFFF; -fx-text-fill: #1D4ED8; -fx-cursor: hand; -fx-padding: 8 18; -fx-background-radius: 7; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 2);";
        String hover = "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-cursor: hand; -fx-padding: 8 18; -fx-background-radius: 7; -fx-font-weight: bold;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Button createNavAdminButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        String bg = "rgba(239, 68, 68, 0.28)";
        String border = "#FCA5A5";
        String textCol = "#FFFFFF";
        String hoverBg = "rgba(239, 68, 68, 0.5)";

        String base = "-fx-background-color: " + bg + "; -fx-border-color: " + border + "; -fx-border-width: 1.2; -fx-border-radius: 7; -fx-text-fill: " + textCol + "; -fx-cursor: hand; -fx-padding: 7 14; -fx-background-radius: 7;";
        String hover = "-fx-background-color: " + hoverBg + "; -fx-border-color: #FFFFFF; -fx-border-width: 1.2; -fx-border-radius: 7; -fx-text-fill: " + textCol + "; -fx-cursor: hand; -fx-padding: 7 14; -fx-background-radius: 7; -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.4), 6, 0, 0, 2);";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    // =========================================================================
    // SECTION 1: ELEGANT HERO SECTION
    // =========================================================================

    private HBox createHeroSection() {
        HBox hero = new HBox(45);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(20, 0, 10, 0));

        // Left Column: Headline, subtext, and clear action buttons
        VBox leftCol = new VBox(22);
        leftCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label badge = UIComponents.createBadge("NEXT-GENERATION FREELANCING & ESCROW", "rgba(99, 102, 241, 0.15)", AppTheme.COLOR_PRIMARY);

        Label title = new Label("Where Elite Developers Meet High-Impact Projects");
        title.setFont(Font.font("Segoe UI", FontWeight.BLACK, 40));
        title.setTextFill(Color.web(AppTheme.getTextPrimary()));
        title.setWrapText(true);
        title.setMaxWidth(600);

        Label subtitle = new Label("Collaborate securely with verified engineering talent, smart milestone escrow, and zero-friction payouts. Built with precision for top engineers and ambitious companies.");
        subtitle.setFont(Font.font("Segoe UI", 16));
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(560);
        subtitle.setLineSpacing(5);

        // Clean action buttons (just 2 clean buttons)
        HBox actionRow = new HBox(16);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        Button btnGetStarted = UIComponents.createPrimaryButton("Get Started →");
        btnGetStarted.setOnAction(e -> showRegisterView());
        btnGetStarted.setPrefHeight(44);
        btnGetStarted.setStyle(btnGetStarted.getStyle() + "; -fx-font-size: 14px; -fx-padding: 10 24;");

        Button btnSignIn = UIComponents.createSecondaryButton("Sign In to Account");
        btnSignIn.setOnAction(e -> showLoginView());
        btnSignIn.setPrefHeight(44);
        btnSignIn.setStyle(btnSignIn.getStyle() + "; -fx-font-size: 14px; -fx-padding: 10 24;");

        actionRow.getChildren().addAll(btnGetStarted, btnSignIn);

        // Trust indicators row
        HBox trustRow = new HBox(22);
        trustRow.setAlignment(Pos.CENTER_LEFT);
        trustRow.setPadding(new Insets(10, 0, 0, 0));

        Label t1 = createTrustBadge("✓ Pre-Vetted Talent");
        Label t2 = createTrustBadge("✓ 100% Escrow Protected");
        Label t3 = createTrustBadge("✓ Direct Milestone Releases");

        trustRow.getChildren().addAll(t1, t2, t3);

        leftCol.getChildren().addAll(badge, title, subtitle, actionRow, trustRow);

        // Right Column: High-Res Framed Artwork (Loaded once and cached)
        VBox rightCol = new VBox();
        rightCol.setAlignment(Pos.CENTER);

        if (cachedHeroImage != null) {
            ImageView heroImageView = new ImageView(cachedHeroImage);
            heroImageView.setFitWidth(500);
            heroImageView.setFitHeight(300);
            heroImageView.setPreserveRatio(true);

            Rectangle clip = new Rectangle(500, 300);
            clip.setArcWidth(18);
            clip.setArcHeight(18);
            heroImageView.setClip(clip);

            StackPane frame = new StackPane(heroImageView);
            frame.setStyle("-fx-background-color: transparent; -fx-border-color: " + (AppTheme.isDarkMode() ? "rgba(255,255,255,0.08)" : "rgba(0,0,0,0.08)") + "; -fx-border-radius: 18; -fx-border-width: 1;");
            rightCol.getChildren().add(frame);
        }

        hero.getChildren().addAll(leftCol, rightCol);
        return hero;
    }

    private Label createTrustBadge(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lbl.setTextFill(Color.web(AppTheme.COLOR_SUCCESS));
        return lbl;
    }

    // =========================================================================
    // SECTION 2: THREE CORE PILLARS (Clean, focused, no clutter)
    // =========================================================================

    private VBox createCorePillarsSection() {
        VBox section = new VBox(24);
        section.setAlignment(Pos.CENTER);

        VBox titles = new VBox(6);
        titles.setAlignment(Pos.CENTER);

        Label badge = UIComponents.createBadge("WHY SKILLBRIDGE", "rgba(99, 102, 241, 0.15)", AppTheme.COLOR_PRIMARY);
        Label title = UIComponents.createTitle("Engineered for Clarity and Trust");
        Label sub = new Label("A transparent collaboration model designed to eliminate risk for both clients and freelancers.");
        sub.setFont(Font.font("Segoe UI", 15));
        sub.setTextFill(Color.web(AppTheme.getTextMuted()));

        titles.getChildren().addAll(badge, title, sub);

        HBox cardsRow = new HBox(24);
        cardsRow.setAlignment(Pos.CENTER);

        VBox c1 = createPillarCard(
                "🛡️",
                "Safe Milestone Escrow",
                "Project funds are deposited securely into escrow before development begins. Payouts disburse directly to the freelancer only after each milestone deliverable is reviewed and approved.",
                AppTheme.COLOR_PRIMARY
        );

        VBox c2 = createPillarCard(
                "⚡",
                "Pre-Vetted Engineering Talent",
                "Work with verified professionals across Full-Stack, Mobile, AI/ML, and Cloud Systems with authentic ratings, completed project metrics, and skill-backed certifications.",
                AppTheme.COLOR_ACCENT
        );

        VBox c3 = createPillarCard(
                "🔄",
                "Peer Skill Barter Network",
                "Exchange technical knowledge and collaborate 1-on-1 with fellow community engineers. Trade hours in backend architecture for UI design with zero platform fees.",
                AppTheme.COLOR_SUCCESS
        );

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);

        cardsRow.getChildren().addAll(c1, c2, c3);
        section.getChildren().addAll(titles, cardsRow);
        return section;
    }

    private VBox createPillarCard(String icon, String title, String desc, String accentColor) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(26));
        card.setStyle(AppTheme.getCardGlassStyle());
        card.setAlignment(Pos.TOP_LEFT);
        card.setMinHeight(230);
        card.setPrefHeight(230);
        card.setMaxHeight(230);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font("Segoe UI", 32));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Segoe UI", 14));
        descLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        descLbl.setWrapText(true);
        descLbl.setLineSpacing(4);
        VBox.setVgrow(descLbl, Priority.ALWAYS);

        card.getChildren().addAll(iconLbl, titleLbl, descLbl);
        UIComponents.setupHoverGlow(card);
        return card;
    }

    // =========================================================================
    // SECTION 3: ABOUT US (Clean anchor target for the navbar button)
    // =========================================================================

    private VBox createAboutUsSection() {
        VBox section = new VBox(22);
        section.setAlignment(Pos.CENTER);
        section.setId("about-us-section");

        VBox titles = new VBox(6);
        titles.setAlignment(Pos.CENTER);

        Label badge = UIComponents.createBadge("ABOUT US", "rgba(16, 185, 129, 0.15)", AppTheme.COLOR_SUCCESS);
        Label title = UIComponents.createTitle("About SkillBridge Platform");
        Label sub = new Label("Empowering Independent Engineers and Innovative Companies Globally");
        sub.setFont(Font.font("Segoe UI", 15));
        sub.setTextFill(Color.web(AppTheme.getTextMuted()));

        titles.getChildren().addAll(badge, title, sub);

        VBox contentCard = new VBox(22);
        contentCard.setPadding(new Insets(32));
        contentCard.setStyle(AppTheme.getCardGlassStyle());
        contentCard.setMaxWidth(1100);

        Label intro = new Label(
                "SkillBridge is an enterprise-grade technical freelancing platform built to eliminate friction between top talent and innovative clients. " +
                "Founded on the principles of transparency, fair pricing, and secure transactions, SkillBridge replaces opaque broker fees with direct smart contracts, " +
                "automated milestone escrow, and collaborative skill bartering."
        );
        intro.setFont(Font.font("Segoe UI", 15));
        intro.setTextFill(Color.web(AppTheme.getTextPrimary()));
        intro.setWrapText(true);
        intro.setLineSpacing(5);

        // 4 Value Pillars in 2 columns
        GridPane grid = new GridPane();
        grid.setHgap(28);
        grid.setVgap(18);
        grid.setPadding(new Insets(10, 0, 0, 0));

        VBox f1 = createAboutFeatureBox("🔒 Zero-Trust Security", "Argon2/SHA-256 password hashing, SSL encryption, and isolated local SQLite database persistence.");
        VBox f2 = createAboutFeatureBox("⚖️ Fair Dispute Mediation", "Multi-tier arbitration protocol with full audit logging and impartial administrative resolution.");
        VBox f3 = createAboutFeatureBox("💬 Direct Real-Time Collaboration", "Integrated messaging, sprint handoffs, file transfers, and milestone progress synchronization.");
        VBox f4 = createAboutFeatureBox("📊 Transparent Financials", "Clear milestone escrow releases, instant withdrawal receipts, and verifiable transaction ledgers.");

        grid.add(f1, 0, 0);
        grid.add(f2, 1, 0);
        grid.add(f3, 0, 1);
        grid.add(f4, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        contentCard.getChildren().addAll(intro, new Separator(), grid);
        section.getChildren().addAll(titles, contentCard);
        return section;
    }

    private VBox createAboutFeatureBox(String title, String desc) {
        VBox box = new VBox(6);
        box.setMinHeight(84);
        box.setPrefHeight(84);
        box.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-padding: 14 18; -fx-background-radius: 8; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 8;");

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Segoe UI", 13));
        descLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        descLbl.setWrapText(true);

        box.getChildren().addAll(titleLbl, descLbl);
        return box;
    }

    // =========================================================================
    // SECTION 4: CALL TO ACTION CARD
    // =========================================================================

    private VBox createCallToActionSection() {
        VBox banner = new VBox(18);
        banner.setAlignment(Pos.CENTER);
        banner.setPadding(new Insets(36, 40, 36, 40));
        banner.setStyle(AppTheme.getCardGlassStyle() + "; -fx-border-color: " + AppTheme.COLOR_PRIMARY + "; -fx-border-width: 1.2;");
        banner.setMaxWidth(1100);

        Label heading = new Label("Ready to Start Your Next Engineering Engagement?");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        heading.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label sub = new Label("Join thousands of verified developers and high-growth companies collaborating on SkillBridge.");
        sub.setFont(Font.font("Segoe UI", 15));
        sub.setTextFill(Color.web(AppTheme.getTextMuted()));

        HBox btnRow = new HBox(16);
        btnRow.setAlignment(Pos.CENTER);

        Button btnCreate = UIComponents.createPrimaryButton("Create Free Account");
        btnCreate.setOnAction(e -> showRegisterView());
        btnCreate.setStyle(btnCreate.getStyle() + "; -fx-font-size: 14px; -fx-padding: 10 26;");

        Button btnLogin = UIComponents.createSecondaryButton("Sign In to Existing Account");
        btnLogin.setOnAction(e -> showLoginView());
        btnLogin.setStyle(btnLogin.getStyle() + "; -fx-font-size: 14px; -fx-padding: 10 26;");

        btnRow.getChildren().addAll(btnCreate, btnLogin);
        banner.getChildren().addAll(heading, sub, btnRow);
        return banner;
    }

    // =========================================================================
    // SECTION 5: MINIMALIST PROFESSIONAL FOOTER
    // =========================================================================

    private VBox createFooter() {
        VBox footer = new VBox(14);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(30, 20, 20, 20));

        Separator sep = new Separator();
        sep.setMaxWidth(1100);

        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setMaxWidth(1100);

        Label copyLbl = new Label("© 2026 SkillBridge Platform. All rights reserved. • Desktop Edition v1.0.0");
        copyLbl.setFont(Font.font("Segoe UI", 12));
        copyLbl.setTextFill(Color.web(AppTheme.getTextMuted()));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Hyperlink linkAbout = new Hyperlink("About Us");
        linkAbout.setFont(Font.font("Segoe UI", 12));
        linkAbout.setTextFill(Color.web(AppTheme.getTextMuted()));
        linkAbout.setOnAction(e -> scrollToAboutSection());

        Hyperlink linkLogin = new Hyperlink("Login");
        linkLogin.setFont(Font.font("Segoe UI", 12));
        linkLogin.setTextFill(Color.web(AppTheme.getTextMuted()));
        linkLogin.setOnAction(e -> showLoginView());

        Hyperlink linkRegister = new Hyperlink("Create Account");
        linkRegister.setFont(Font.font("Segoe UI", 12));
        linkRegister.setTextFill(Color.web(AppTheme.getTextMuted()));
        linkRegister.setOnAction(e -> showRegisterView());

        Hyperlink linkAdmin = new Hyperlink("Admin Portal");
        linkAdmin.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        linkAdmin.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
        linkAdmin.setOnAction(e -> showAdminLoginView());

        bottomRow.getChildren().addAll(copyLbl, sp, linkAbout, linkLogin, linkRegister, linkAdmin);
        footer.getChildren().addAll(sep, bottomRow);
        return footer;
    }

    // =========================================================================
    // KEYBOARD SHORTCUTS
    // =========================================================================

    private void setupKeyboardShortcuts(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.T) {
                // Ctrl+T: Toggle Theme
                AppTheme.toggleTheme();
                showHomeView();
                event.consume();
            } else if (event.isControlDown() && event.getCode() == KeyCode.L) {
                // Ctrl+L: Login
                showLoginView();
                event.consume();
            }
        });
    }
}
