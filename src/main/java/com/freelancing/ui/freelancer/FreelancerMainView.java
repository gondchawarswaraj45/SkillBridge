package com.freelancing.ui.freelancer;

import com.freelancing.dao.common.SkillDAO;
import com.freelancing.model.common.ChatMessage;
import com.freelancing.model.common.Conversation;
import com.freelancing.model.common.FeedPost;
import com.freelancing.model.common.Notification;
import com.freelancing.model.common.Skill;
import com.freelancing.model.common.Transaction;
import com.freelancing.model.common.User;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Deliverable;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.company.Rating;
import com.freelancing.model.freelancer.Certification;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.model.freelancer.MatchingResult;
import com.freelancing.model.freelancer.PortfolioItem;
import com.freelancing.service.common.AiService;
import com.freelancing.service.common.CalendarService;
import com.freelancing.service.common.ChatService;
import com.freelancing.service.common.CommunityService;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.common.PaymentService;
import com.freelancing.service.common.SkillService;
import com.freelancing.service.company.ContractService;
import com.freelancing.service.company.MilestoneService;
import com.freelancing.service.company.ProjectService;
import com.freelancing.service.company.ProposalService;
import com.freelancing.service.freelancer.AIProposalAssistant;
import com.freelancing.service.freelancer.FreelancerService;
import com.freelancing.ui.common.CalendarView;
import com.freelancing.ui.common.HomePage;
import com.freelancing.ui.common.SettingsView;
import com.freelancing.ui.common.UIComponents;

import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;
import com.freelancing.util.AnimationUtil;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.SubScene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FreelancerMainView {
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final AiService aiService = new AiService();
    private final NotificationService notifService = new NotificationService();
    private final FreelancerService freelancerService = new FreelancerService();
    private final SkillService skillService = new SkillService();
    private final ProjectService projectService = new ProjectService();
    private final ProposalService proposalService = new ProposalService();
    private final ContractService contractService = new ContractService();
    private final MilestoneService milestoneService = new MilestoneService();
    private final PaymentService paymentService = new PaymentService();
    private final ChatService chatService = new ChatService();
    private final CalendarService calendarService = new CalendarService();
    private final SkillDAO skillDAO = new SkillDAO();

    private String selectedChatConversationId = null;

    private BorderPane root;
    private StackPane contentArea;
    private Button btnNavDash, btnNavProjects, btnNavProposals, btnNavSearch, btnNavProfile, btnNavCal, btnNavChat, btnNavAiBot, btnNavFeed, btnNavNotif, btnNavSettings;
    private Button[] navBtns;
    private boolean isSidebarOpen = true;

    public FreelancerMainView(User user) {
        this.currentUser = user;
    }

    /** Compatibility constructor for legacy code */
    public FreelancerMainView(Object ignored, User user) {
        this(user);
    }

    public Scene createScene() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + AppTheme.getBgDark() + ";");

        // Top Header
        HBox header = new HBox(15);
        header.setPadding(new Insets(12, 24, 12, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + AppTheme.getBgPanel() + ";" +
                        "-fx-border-color: " + AppTheme.getBorderColor() + ";" +
                        "-fx-border-width: 0 0 1 0;");

        Button btnToggleSidebar = new Button("☰");
        btnToggleSidebar.setTooltip(new Tooltip("Toggle / Close Sidebar"));
        btnToggleSidebar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnToggleSidebar.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-text-fill: " + AppTheme.COLOR_PRIMARY + "; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 11; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 6;");

        Label logo = new Label("⚡ SkillBridge  |  Freelancer Portal");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnHome = UIComponents.createSecondaryButton("🏠 SkillBridge Home");
        btnHome.setOnAction(e -> HomePage.showHomeView());

        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showFreelancerView(currentUser);
        });

        Label userLabel = new Label("👤 " + currentUser.getUsername() + " (Verified Freelancer)");
        userLabel.setTextFill(Color.web(AppTheme.getTextPrimary()));
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        Button btnLogout = UIComponents.createDangerButton("Logout");
        btnLogout.setOnAction(e -> {
            HomePage.showLoginView();
        });

        header.getChildren().addAll(btnToggleSidebar, logo, spacer, btnHome, btnTheme, userLabel, btnLogout);
        root.setTop(header);

        // Sidebar Navigation
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: " + AppTheme.getBgPanel() + ";" +
                        "-fx-border-color: " + AppTheme.getBorderColor() + ";" +
                        "-fx-border-width: 0 1 0 0;");

        btnNavDash = createNavButton("📊 Dashboard Analytics", true);
        btnNavProjects = createNavButton("📂 Active Projects", false);
        btnNavProposals = createNavButton("📑 My Submitted Proposals", false);
        btnNavSearch = createNavButton("🔍 Search & AI Matches", false);
        btnNavProfile = createNavButton("👤 Profile & Portfolio", false);
        btnNavCal = createNavButton("📅 Calendar & Schedule", false);
        btnNavChat = createNavButton("💬 Encrypted Chat & Meet", false);
        btnNavAiBot = createNavButton("🤖 AI Career Coach", false);
        btnNavFeed = createNavButton("📰 Community Feed", false);
        btnNavNotif = createNavButton("🔔 Notifications", false);
        btnNavSettings = createNavButton("⚙️ Settings", false);

        navBtns = new Button[]{btnNavDash, btnNavProjects, btnNavProposals, btnNavSearch, btnNavProfile, btnNavCal, btnNavChat, btnNavAiBot, btnNavFeed, btnNavNotif, btnNavSettings};

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: " + AppTheme.getBgDark() + ";");

        btnNavDash.setOnAction(e -> { selectNav(btnNavDash, navBtns); showDashboard(); });
        btnNavProjects.setOnAction(e -> { selectNav(btnNavProjects, navBtns); showActiveProjects(); });
        btnNavProposals.setOnAction(e -> { selectNav(btnNavProposals, navBtns); showMyProposals(); });
        btnNavSearch.setOnAction(e -> { selectNav(btnNavSearch, navBtns); showProjectSearch(); });
        btnNavProfile.setOnAction(e -> { selectNav(btnNavProfile, navBtns); showProfile(); });
        btnNavCal.setOnAction(e -> { selectNav(btnNavCal, navBtns); showCalendar(); });
        btnNavChat.setOnAction(e -> { selectNav(btnNavChat, navBtns); showChat(); });
        btnNavAiBot.setOnAction(e -> { selectNav(btnNavAiBot, navBtns); showAiCareerCoach(); });
        btnNavFeed.setOnAction(e -> { selectNav(btnNavFeed, navBtns); showFeed(); });
        btnNavNotif.setOnAction(e -> { selectNav(btnNavNotif, navBtns); showNotifications(); });
        btnNavSettings.setOnAction(e -> { selectNav(btnNavSettings, navBtns); showSettings(); });

        HBox sidebarHeader = new HBox(8);
        sidebarHeader.setAlignment(Pos.CENTER_LEFT);
        sidebarHeader.setPadding(new Insets(0, 4, 8, 4));

        Label lblNav = new Label("PORTAL NAVIGATION");
        lblNav.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lblNav.setTextFill(Color.web(AppTheme.getTextMuted()));

        Region shSp = new Region();
        HBox.setHgrow(shSp, Priority.ALWAYS);

        Button btnCloseSidebar = new Button("✕ Close");
        btnCloseSidebar.setTooltip(new Tooltip("Close Sidebar"));
        btnCloseSidebar.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
        btnCloseSidebar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-cursor: hand; -fx-padding: 3 8; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 4; -fx-background-radius: 4;");
        btnCloseSidebar.setOnMouseEntered(e -> btnCloseSidebar.setStyle("-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-padding: 3 8; -fx-border-color: #ef4444; -fx-border-radius: 4; -fx-background-radius: 4;"));
        btnCloseSidebar.setOnMouseExited(e -> btnCloseSidebar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-cursor: hand; -fx-padding: 3 8; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 4; -fx-background-radius: 4;"));

        sidebarHeader.getChildren().addAll(lblNav, shSp, btnCloseSidebar);

        sidebar.getChildren().addAll(sidebarHeader, btnNavDash, btnNavProjects, btnNavProposals, btnNavSearch, btnNavProfile, btnNavCal,
                new Separator(), btnNavChat, btnNavAiBot, btnNavFeed, btnNavNotif, btnNavSettings);

        ScrollPane sidebarScroll = UIComponents.createScrollPane(sidebar);
        sidebarScroll.setStyle("-fx-background: " + AppTheme.getBgPanel() + "; -fx-background-color: " + AppTheme.getBgPanel() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-width: 0 1 0 0;");
        sidebarScroll.setPrefWidth(240);

        root.setLeft(sidebarScroll);
        root.setCenter(contentArea);

        btnToggleSidebar.setOnAction(e -> {
            isSidebarOpen = !isSidebarOpen;
            root.setLeft(isSidebarOpen ? sidebarScroll : null);
            btnToggleSidebar.setText(isSidebarOpen ? "◀" : "☰");
        });
        btnCloseSidebar.setOnAction(e -> {
            isSidebarOpen = false;
            root.setLeft(null);
            btnToggleSidebar.setText("☰");
        });

        showDashboard();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1360, bounds.getWidth() * 0.94);
        double sceneHeight = Math.min(880, bounds.getHeight() * 0.94);
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        AppTheme.applyAppStylesheet(scene);
        return scene;
    }

    private void showSettings() {
        contentArea.getChildren().clear();
        SettingsView view = new SettingsView(currentUser, () -> HomePage.showFreelancerView(currentUser));
        contentArea.getChildren().add(view);
    }

    public SubScene createSubScene(double width, double height) {
        if (root == null) {
            createScene();
        }
        return new SubScene(root, width, height);
    }

    private Button createNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        setNavStyle(btn, active);
        return btn;
    }

    private void setNavStyle(Button btn, boolean active) {
        if (active) {
            btn.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 10 14; -fx-font-weight: bold;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-background-radius: 6; -fx-padding: 10 14;");
        }
    }

    private void selectNav(Button selected, Button[] all) {
        int idx = 0;
        for (int i = 0; i < all.length; i++) {
            boolean active = (all[i] == selected);
            setNavStyle(all[i], active);
            if (active) idx = i;
        }
        AnimationUtil.applyVariedTransition(contentArea, idx);
    }

    private void makeStatCardInteractive(VBox card, Runnable action) {
        String base = card.getStyle();
        card.setStyle(base + "; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle(base + "; -fx-cursor: hand; -fx-scale-x: 1.02; -fx-scale-y: 1.02; -fx-border-color: " + AppTheme.COLOR_PRIMARY + ";"));
        card.setOnMouseExited(e -> card.setStyle(base + "; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
        card.setOnMouseClicked(e -> {
            if (action != null) action.run();
        });
    }

    // 1. Dashboard View with JavaFX Charts (Multithreaded background data loading)
    private void showDashboard() {
        contentArea.getChildren().setAll(AnimationUtil.createLoadingOverlay("Loading your freelancer dashboard & analytics..."));

        AnimationUtil.runAsync(
            () -> {
                FreelancerProfile profile = freelancerService.getProfile(currentUser.getId());
                if (profile == null) {
                    profile = db.getFreelancerProfiles().get(currentUser.getId());
                }
                double rating = profile != null ? profile.getRating() : 5.0;
                int completed = profile != null ? profile.getCompletedProjects() : 0;

                PaymentService.FreelancerFinancialSummary fin = paymentService.getFinancialSummaryForFreelancer(currentUser.getId());
                double earnedVal = fin.totalEarned > 0 ? fin.totalEarned : 4850.0;
                String earnedStr = "$" + String.format("%.2f", earnedVal);

                List<Project> openProjects = projectService.getOpenProjects();
                if (openProjects.isEmpty()) {
                    openProjects = db.getProjects().values().stream()
                            .filter(p -> p.getStatus() == Project.Status.OPEN)
                            .collect(Collectors.toList());
                }

                List<Object[]> matchedProjects = new ArrayList<>();
                for (Project p : openProjects) {
                    int matchScore = aiService.calculateMatchScore(profile, p);
                    matchedProjects.add(new Object[] { p, matchScore });
                }

                return new Object[] { rating, completed, earnedVal, earnedStr, matchedProjects };
            },
            data -> {
                double rating = (double) data[0];
                int completed = (int) data[1];
                double earnedVal = (double) data[2];
                String earnedStr = (String) data[3];
                @SuppressWarnings("unchecked")
                List<Object[]> matchedProjects = (List<Object[]>) data[4];

                VBox box = new VBox(20);
                Label title = UIComponents.createTitle("Freelancer Dashboard & Analytics");

                HBox statGrid = new HBox(15);
                VBox card1 = UIComponents.createStatCard(0, "💵", "Total Earnings", earnedStr, UIComponents.COLOR_SUCCESS);
                VBox card2 = UIComponents.createStatCard(1, "📂", "Completed Projects", String.valueOf(completed), UIComponents.COLOR_PRIMARY);
                VBox card3 = UIComponents.createStatCard(2, "⭐", "Client Rating", String.format("%.2f / 5.0", rating), UIComponents.COLOR_AMBER);
                VBox card4 = UIComponents.createStatCard(3, "⚡", "AI Skill Score", "96%", UIComponents.COLOR_PURPLE);

                makeStatCardInteractive(card1, () -> {
                    selectNav(btnNavProjects, navBtns);
                    showActiveProjects();
                });
                makeStatCardInteractive(card2, () -> {
                    selectNav(btnNavProjects, navBtns);
                    showActiveProjects();
                });
                makeStatCardInteractive(card3, () -> {
                    selectNav(btnNavProfile, navBtns);
                    showProfile();
                });
                makeStatCardInteractive(card4, () -> {
                    selectNav(btnNavAiBot, navBtns);
                    showAiCareerCoach();
                });

                statGrid.getChildren().addAll(card1, card2, card3, card4);

                // Earnings Growth Chart
                Map<String, Double> chartData = new LinkedHashMap<>();
                chartData.put("Feb", 1200.0);
                chartData.put("Mar", 1800.0);
                chartData.put("Apr", 2400.0);
                chartData.put("May", 3100.0);
                chartData.put("Jun", 3900.0);
                chartData.put("Jul", earnedVal);

                LineChart<String, Number> earningsChart = UIComponents.createLineChart("📈 Earnings Growth Trend ($)", "Month", "Revenue ($)", chartData);
                VBox chartCard = UIComponents.createCard();
                chartCard.getChildren().addAll(UIComponents.createHeader("Financial & Performance Growth"), earningsChart);

                VBox recCard = UIComponents.createCard();
                recCard.getChildren().add(UIComponents.createHeader("🎯 AI Recommended Projects for You"));

                if (matchedProjects.isEmpty()) {
                    Label noProjLbl = new Label("No open projects available right now. Check back soon!");
                    noProjLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                    recCard.getChildren().add(noProjLbl);
                } else {
                    for (Object[] item : matchedProjects) {
                        Project p = (Project) item[0];
                        int matchScore = (int) item[1];

                        HBox row = new HBox(15);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setPadding(new Insets(10));
                        row.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-background-radius: 8;");

                        VBox details = new VBox(4);
                        Label pTitle = new Label(p.getTitle());
                        pTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                        pTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

                        Label pMeta = new Label("Client: " + (p.getClientName() != null ? p.getClientName() : "Client") + " | Budget: $" + p.getBudget() + " | Category: " + p.getCategory());
                        pMeta.setTextFill(Color.web(AppTheme.getTextMuted()));
                        details.getChildren().addAll(pTitle, pMeta);

                        Region sp = new Region();
                        HBox.setHgrow(sp, Priority.ALWAYS);

                        Label badgeMatch = UIComponents.createBadge(matchScore + "% AI Match", AppTheme.COLOR_PRIMARY, "white");
                        Button btnApply = UIComponents.createSuccessButton("Submit Proposal");
                        btnApply.setOnAction(e -> showProposalDialog(p));

                        row.getChildren().addAll(details, sp, badgeMatch, btnApply);
                        recCard.getChildren().add(row);
                    }
                }

                box.getChildren().addAll(title, statGrid, chartCard, recCard);
                contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
                AnimationUtil.applyFadeZoom(contentArea, 260);
            }
        );
    }

    // Community Feed
    private void showFeed() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📰 Community Feed");

        // Create Post Card
        VBox createCard = UIComponents.createCard();
        createCard.getChildren().add(UIComponents.createHeader("✍️ Create a New Post"));

        TextField tfPostTitle = UIComponents.createTextField("Post title...");
        TextArea taPostContent = UIComponents.createTextArea("Share your thoughts, showcase work, or start a discussion...");
        taPostContent.setPrefRowCount(4);

        ComboBox<String> cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("Showcase", "Hiring", "Discussion", "Feedback");
        cbCategory.getSelectionModel().select(0);
        cbCategory.setMaxWidth(Double.MAX_VALUE);
        UIComponents.styleComboBox(cbCategory);

        Button btnPost = UIComponents.createPrimaryButton("🚀 Publish Post");
        btnPost.setOnAction(e -> {
            String pTitle = tfPostTitle.getText().trim();
            String pContent = taPostContent.getText().trim();
            if (pTitle.isEmpty() || pContent.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Post", "Missing Fields", "Please enter both a title and content for your post.");
                return;
            }
            String postId = "feed_" + System.currentTimeMillis();
            String catStr = cbCategory.getValue();
            FeedPost.PostCategory cat = FeedPost.PostCategory.valueOf(catStr.toUpperCase());
            String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());

            FeedPost post = new FeedPost(postId, currentUser.getId(), currentUser.getUsername(), "FREELANCER",
                    pTitle, pContent, cat, FeedPost.PostStatus.ACTIVE, ts);
            db.getFeedPosts().put(postId, post);
            db.logActivity("Freelancer " + currentUser.getUsername() + " published feed post: " + pTitle);
            db.saveData();

            try {
                CommunityService cs = new CommunityService();
                cs.createPost(currentUser.getId(), pTitle, pContent, catStr);
            } catch (Exception ignored) {}

            tfPostTitle.clear();
            taPostContent.clear();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Post Published", "Success", "Your post is now live on the community feed!");
            showFeed();
        });

        createCard.getChildren().addAll(
                new Label("Category:"), cbCategory,
                new Label("Title:"), tfPostTitle,
                new Label("Content:"), taPostContent,
                btnPost
        );

        // Feed Timeline (cached & indexed)
        VBox feedTimeline = new VBox(12);
        List<FeedPost> posts = db.getActiveFeedPostsSorted();

        if (posts.isEmpty()) {
            VBox emptyCard = UIComponents.createCard();
            Label emptyLbl = new Label("No posts yet. Be the first to share something with the community!");
            emptyLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            emptyCard.getChildren().add(emptyLbl);
            feedTimeline.getChildren().add(emptyCard);
        } else {
            for (FeedPost post : posts) {
                feedTimeline.getChildren().add(buildFeedPostCard(post));
            }
        }

        box.getChildren().addAll(title, createCard, UIComponents.createHeader("📡 Latest Posts"), feedTimeline);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    private VBox buildFeedPostCard(FeedPost post) {
        VBox card = UIComponents.createCard();

        // Author header
        HBox authorRow = new HBox(10);
        authorRow.setAlignment(Pos.CENTER_LEFT);
        Label authorLbl = new Label(("FREELANCER".equals(post.getAuthorRole()) ? "👤" : "🏢") + " " + post.getAuthorName());
        authorLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        authorLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

        Label roleBadge = UIComponents.createBadge(post.getAuthorRole(), UIComponents.COLOR_PRIMARY, "white");

        // Use distinct badge for JOB_POST
        String catDisplay = post.getCategory() == FeedPost.PostCategory.JOB_POST ? "💼 JOB POST" : post.getCategory().toString();
        String catColor = post.getCategory() == FeedPost.PostCategory.JOB_POST ? UIComponents.COLOR_SUCCESS : UIComponents.COLOR_PURPLE;
        Label catBadge = UIComponents.createBadge(catDisplay, catColor, "white");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label timeLbl = new Label("🕒 " + post.getTimestamp());
        timeLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        timeLbl.setFont(Font.font("Segoe UI", 11));

        authorRow.getChildren().addAll(authorLbl, roleBadge, catBadge, sp, timeLbl);

        // Post content
        Label postTitle = new Label(post.getTitle());
        postTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        postTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
        postTitle.setWrapText(true);

        Label postContent = new Label(post.getContent());
        postContent.setWrapText(true);
        postContent.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        postContent.setFont(Font.font("Segoe UI", 13));

        // Show linked project details for JOB_POST
        if (post.getCategory() == FeedPost.PostCategory.JOB_POST && post.getLinkedProjectId() != null) {
            Project linkedProj = db.getProjects().get(post.getLinkedProjectId());
            if (linkedProj != null) {
                HBox projInfoBar = new HBox(8);
                projInfoBar.setAlignment(Pos.CENTER_LEFT);
                projInfoBar.setPadding(new Insets(8, 12, 8, 12));
                projInfoBar.setStyle("-fx-background-color: #1a2744; -fx-background-radius: 8; -fx-border-color: " + UIComponents.COLOR_SUCCESS + "; -fx-border-radius: 8; -fx-border-width: 1;");

                Label budgetLbl = new Label("💰 $" + String.format("%.0f", linkedProj.getBudget()));
                budgetLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                budgetLbl.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));

                Label deadlineLbl = new Label("📅 " + linkedProj.getDeadline());
                deadlineLbl.setFont(Font.font("Segoe UI", 12));
                deadlineLbl.setTextFill(Color.web(UIComponents.COLOR_AMBER));

                Label statusLbl = UIComponents.createBadge(linkedProj.getStatus().toString(), UIComponents.COLOR_PRIMARY, "white");

                // Skills as badges
                HBox skillsRow = new HBox(4);
                for (String skill : linkedProj.getRequiredSkills()) {
                    Label skillBadge = UIComponents.createBadge(skill, UIComponents.COLOR_BG_INPUT, UIComponents.COLOR_TEXT_PRIMARY);
                    skillsRow.getChildren().add(skillBadge);
                }

                projInfoBar.getChildren().addAll(budgetLbl, deadlineLbl, statusLbl, skillsRow);
                card.getChildren().addAll(authorRow, postTitle, postContent, projInfoBar);
            } else {
                card.getChildren().addAll(authorRow, postTitle, postContent);
            }
        } else {
            card.getChildren().addAll(authorRow, postTitle, postContent);
        }

        // Action buttons
        HBox actionBar = new HBox(12);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        boolean liked = post.getLikedByUserIds().contains(currentUser.getId());
        Button btnLike = liked ? UIComponents.createPrimaryButton("👍 Liked (" + post.getLikes() + ")")
                               : UIComponents.createSecondaryButton("👍 Like (" + post.getLikes() + ")");
        btnLike.setOnAction(e -> {
            post.toggleLike(currentUser.getId());
            db.getFeedPosts().put(post.getId(), post);
            db.saveData();
            showFeed();
        });

        Button btnComment = UIComponents.createSecondaryButton("💬 Comments (" + post.getComments().size() + ")");

        actionBar.getChildren().addAll(btnLike, btnComment);

        // JOB_POST specific: Show bid button or bid status
        if (post.getCategory() == FeedPost.PostCategory.JOB_POST) {
            // Show bids count
            Label bidCountBadge = UIComponents.createBadge("🎯 " + post.getBids().size() + " Bids", UIComponents.COLOR_AMBER, "white");
            actionBar.getChildren().add(bidCountBadge);

            Project linkedProj = post.getLinkedProjectId() != null ? db.getProjects().get(post.getLinkedProjectId()) : null;

            if (post.hasUserBid(currentUser.getId())) {
                // Already bid — show status
                Label bidPlaced = UIComponents.createBadge("✅ Bid Placed", UIComponents.COLOR_SUCCESS, "white");
                actionBar.getChildren().add(bidPlaced);
            } else if (linkedProj != null && linkedProj.getStatus() == Project.Status.OPEN) {
                // Can bid — show bid button
                Button btnBid = UIComponents.createSuccessButton("🎯 Place Bid");
                btnBid.setOnAction(e -> showBidDialog(post, linkedProj));
                actionBar.getChildren().add(btnBid);
            } else if (linkedProj != null && linkedProj.getStatus() != Project.Status.OPEN) {
                Label closedBadge = UIComponents.createBadge("🔒 " + linkedProj.getStatus().toString(), UIComponents.COLOR_BG_INPUT, UIComponents.COLOR_TEXT_MUTED);
                actionBar.getChildren().add(closedBadge);
            }
        }

        Button btnFeedback = UIComponents.createSuccessButton("⭐ Give Feedback");
        btnFeedback.setOnAction(e -> {
            if (post.getAuthorId().equals(currentUser.getId())) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Feedback", "Not Allowed", "You cannot give feedback on your own post.");
                return;
            }
            UIComponents.showPromptDialog("Give Feedback", "Write feedback for " + post.getAuthorName(), "Your feedback:", "", feedback -> {
                if (feedback != null && !feedback.trim().isEmpty()) {
                    String rId = "rating_" + System.currentTimeMillis();
                    String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
                    Rating rating = new Rating(rId, "", currentUser.getId(), currentUser.getUsername(), post.getAuthorId(), 5.0, feedback.trim(), ts);
                    db.getRatings().put(rId, rating);
                    db.logActivity(currentUser.getUsername() + " gave feedback to " + post.getAuthorName());
                    db.saveData();
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Feedback Sent", "Thank You", "Your feedback has been sent to " + post.getAuthorName() + "!");
                }
            });
        });

        // Only show feedback for non-own posts that are not JOB_POST
        if (!post.getAuthorId().equals(currentUser.getId()) && post.getCategory() != FeedPost.PostCategory.JOB_POST) {
            actionBar.getChildren().add(btnFeedback);
        }

        card.getChildren().add(actionBar);

        // Comments section
        VBox commentsBox = new VBox(6);
        commentsBox.setPadding(new Insets(8, 0, 0, 0));

        if (!post.getComments().isEmpty()) {
            Label cmtHeader = new Label("💬 Comments:");
            cmtHeader.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
            cmtHeader.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            commentsBox.getChildren().add(cmtHeader);

            for (FeedPost.FeedComment cmt : post.getComments()) {
                HBox cmtRow = new HBox(8);
                cmtRow.setPadding(new Insets(6, 10, 6, 10));
                cmtRow.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 6;");

                Label cmtAuthor = new Label(cmt.getAuthorName() + ":");
                cmtAuthor.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                cmtAuthor.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label cmtText = new Label(cmt.getContent());
                cmtText.setWrapText(true);
                cmtText.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                cmtText.setFont(Font.font("Segoe UI", 12));

                cmtRow.getChildren().addAll(cmtAuthor, cmtText);
                commentsBox.getChildren().add(cmtRow);
            }
        }

        // Add comment input
        HBox addCmtBar = new HBox(8);
        addCmtBar.setAlignment(Pos.CENTER_LEFT);
        TextField tfComment = UIComponents.createTextField("Write a comment...");
        HBox.setHgrow(tfComment, Priority.ALWAYS);
        Button btnAddCmt = UIComponents.createPrimaryButton("Post");
        btnAddCmt.setOnAction(e -> {
            String cmtText = tfComment.getText().trim();
            if (!cmtText.isEmpty()) {
                String cmtId = "cmt_" + System.currentTimeMillis();
                String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
                post.addComment(new FeedPost.FeedComment(cmtId, currentUser.getId(), currentUser.getUsername(), cmtText, ts));
                db.getFeedPosts().put(post.getId(), post);
                db.saveData();
                showFeed();
            }
        });
        tfComment.setOnAction(e -> btnAddCmt.fire());
        addCmtBar.getChildren().addAll(tfComment, btnAddCmt);
        commentsBox.getChildren().add(addCmtBar);

        card.getChildren().add(commentsBox);
        return card;
    }

    // Bid Dialog for JOB_POST
    private void showBidDialog(FeedPost jobPost, Project project) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.setPrefWidth(500);

        // Show required skills
        if (!project.getRequiredSkills().isEmpty()) {
            HBox reqRow = new HBox(4);
            reqRow.setAlignment(Pos.CENTER_LEFT);
            Label reqLbl = new Label("Required Skills: ");
            reqLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            reqRow.getChildren().add(reqLbl);
            for (String sk : project.getRequiredSkills()) {
                reqRow.getChildren().add(new Label("[" + sk + "]"));
            }
            content.getChildren().add(reqRow);
        }

        TextArea taCover = UIComponents.createTextArea("Explain why you are the best fit for this project...");
        taCover.setPrefRowCount(6);

        Button btnAiGen = UIComponents.createSecondaryButton("🤖 AI Auto-Generate Winning Cover Letter");
        btnAiGen.setOnAction(e -> {
            FreelancerProfile fp = db.getFreelancerProfiles().get(currentUser.getId());
            String cover = aiService.generateProposalCoverLetter(project, fp);
            taCover.setText(cover);
        });

        TextField tfBid = UIComponents.createTextField("Your Bid Amount ($)...");
        tfBid.setText(String.valueOf((int) project.getBudget()));

        TextField tfDays = UIComponents.createTextField("Estimated Delivery Days...");
        tfDays.setText("14");

        content.getChildren().addAll(
                new Label("Cover Letter:"), taCover, btnAiGen,
                new Label("Bid Amount ($):"), tfBid,
                new Label("Estimated Days:"), tfDays
        );

        UIComponents.showModalOverlay("🎯 Place Bid - " + project.getTitle(), content, "🎯 Submit Bid", () -> {
            try {
                double bid = Double.parseDouble(tfBid.getText().trim());
                int days = Integer.parseInt(tfDays.getText().trim());
                String coverLetter = taCover.getText().trim();

                if (coverLetter.isEmpty()) {
                    UIComponents.showAlert(Alert.AlertType.WARNING, "Validation", "Cover Letter Required", "Please write a cover letter explaining your qualifications.");
                    return;
                }

                // Get freelancer skills
                FreelancerProfile fp = db.getFreelancerProfiles().get(currentUser.getId());
                List<String> mySkills = fp != null ? fp.getSkills() : new java.util.ArrayList<>();

                // Record exact timestamp
                String bidTimestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

                // Add bid to feed post
                String bidId = "bid_" + System.currentTimeMillis();
                FeedPost.FeedBid feedBid = new FeedPost.FeedBid(bidId, currentUser.getId(), currentUser.getUsername(),
                        bid, days, coverLetter, mySkills, bidTimestamp);
                jobPost.addBid(feedBid);
                db.getFeedPosts().put(jobPost.getId(), jobPost);

                // Also create formal Proposal record
                String propId = "prop_" + System.currentTimeMillis();
                Proposal prop = new Proposal(propId, project.getId(), project.getTitle(),
                        currentUser.getId(), currentUser.getUsername(),
                        coverLetter, bid, days, Proposal.Status.PENDING,
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                db.getProposals().put(propId, prop);

                // Send notification to client
                notifService.sendNotification(project.getClientId(), "🎯 New Bid Received!",
                        currentUser.getUsername() + " placed a bid of $" + String.format("%.0f", bid)
                        + " for '" + project.getTitle() + "' at " + bidTimestamp);

                db.logActivitySilent("Freelancer " + currentUser.getUsername() + " placed bid of $" + bid
                        + " on project " + project.getTitle() + " at " + bidTimestamp);
                db.invalidateCaches();
                db.saveData();

                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Bid Placed!", "Success",
                        "Your bid has been placed successfully!\n\n"
                        + "💰 Bid Amount: $" + String.format("%.0f", bid) + "\n"
                        + "📅 Estimated Days: " + days + "\n"
                        + "🕒 Bid Time: " + bidTimestamp + "\n\n"
                        + "The client will review your bid and skills.");
                showFeed();
            } catch (NumberFormatException ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Values", "Please enter valid numerical values for Bid Amount and Days.");
            }
        });
    }

    // 2. Profile Management
    private void showProfile() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Profile & Portfolio Management");

        FreelancerProfile fp = freelancerService.getProfileByUserId(currentUser.getId());
        if (fp == null) {
            fp = new FreelancerProfile();
            fp.setId("fp_" + currentUser.getId().replace("usr_", ""));
            fp.setUserId(currentUser.getId());
            fp.setTitle("Freelance Specialist");
            fp.setAvailability("AVAILABLE");
            fp.setHourlyRate(50.0);
            fp.setExperienceYears(2);
            freelancerService.updateProfile(fp);
        }

        VBox card = UIComponents.createCard();

        TextField tfTitle = UIComponents.createTextField("Professional Title (e.g. Senior Java & JavaFX Architect)");
        tfTitle.setText(fp.getTitle() != null ? fp.getTitle() : "");

        HBox rateExpRow = new HBox(15);
        TextField tfRate = UIComponents.createTextField("Hourly Rate ($/hr)");
        tfRate.setText(fp.getHourlyRate() > 0 ? String.valueOf(fp.getHourlyRate()) : "50.0");
        TextField tfExp = UIComponents.createTextField("Experience (Years)");
        tfExp.setText(String.valueOf(fp.getExperienceYears()));
        ComboBox<String> cbAvail = new ComboBox<>();
        cbAvail.getItems().addAll("AVAILABLE", "BUSY", "UNAVAILABLE");
        cbAvail.setValue(fp.getAvailability() != null ? fp.getAvailability() : "AVAILABLE");
        UIComponents.styleComboBox(cbAvail);
        rateExpRow.getChildren().addAll(new Label("Rate:"), tfRate, new Label("Exp (Yrs):"), tfExp, new Label("Status:"), cbAvail);

        TextArea taBio = UIComponents.createTextArea("Bio / Summary...");
        taBio.setText(fp.getBio() != null ? fp.getBio() : "");

        TextField tfSkills = UIComponents.createTextField("Skills (comma separated, e.g. Java 17, JavaFX, SQLite / JDBC, Spring Boot)");
        List<String> currentSkills = freelancerService.getSkillNames(fp.getId());
        tfSkills.setText(String.join(", ", currentSkills));

        // Portfolio Showcase Section
        Label portHeader = UIComponents.createHeader("📁 Portfolio Showcase & Work Samples");
        VBox portList = new VBox(8);
        List<PortfolioItem> portItems = freelancerService.getPortfolioItems(fp.getId());
        if (portItems.isEmpty()) {
            Label noPort = new Label("No portfolio items added yet. Showcase your work to attract high-paying clients!");
            noPort.setTextFill(Color.web(AppTheme.getTextMuted()));
            portList.getChildren().add(noPort);
        } else {
            for (PortfolioItem item : portItems) {
                HBox pRow = new HBox(12);
                pRow.setAlignment(Pos.CENTER_LEFT);
                pRow.setPadding(new Insets(8));
                pRow.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-background-radius: 6;");
                Label pTitle = new Label("💼 " + item.getTitle() + " (" + (item.getProjectUrl() != null ? item.getProjectUrl() : "No URL") + ")");
                pTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                Button btnDelPort = UIComponents.createDangerButton("✕ Remove");
                btnDelPort.setOnAction(e -> {
                    freelancerService.deletePortfolioItem(item.getId());
                    showProfile();
                });
                pRow.getChildren().addAll(pTitle, sp, btnDelPort);
                portList.getChildren().add(pRow);
            }
        }

        Button btnAddPort = UIComponents.createSecondaryButton("+ Add Portfolio Project");
        FreelancerProfile finalFp1 = fp;
        btnAddPort.setOnAction(e -> {
            UIComponents.showPromptDialog("Add Portfolio Item", "Showcase a project to clients", "Project Title:", "Enterprise JavaFX Platform", projTitle -> {
                if (projTitle != null && !projTitle.trim().isEmpty()) {
                    try {
                        freelancerService.addPortfolioItem(finalFp1.getId(), projTitle.trim(), "Enterprise project built with clean architecture.", "https://github.com", null);
                        showProfile();
                    } catch (Exception ex) {
                        UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add portfolio", ex.getMessage());
                    }
                }
            });
        });

        // Certifications Section
        Label certHeader = UIComponents.createHeader("🎓 Verified Certifications & Credentials");
        VBox certList = new VBox(8);
        List<Certification> certs = freelancerService.getCertifications(fp.getId());
        if (certs.isEmpty()) {
            Label noCert = new Label("No certifications uploaded yet.");
            noCert.setTextFill(Color.web(AppTheme.getTextMuted()));
            certList.getChildren().add(noCert);
        } else {
            for (Certification cert : certs) {
                HBox cRow = new HBox(12);
                cRow.setAlignment(Pos.CENTER_LEFT);
                cRow.setPadding(new Insets(8));
                cRow.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-background-radius: 6;");
                Label cTitle = new Label("📜 " + cert.getName() + " - " + cert.getIssuer());
                cTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                Button btnDelCert = UIComponents.createDangerButton("✕ Remove");
                btnDelCert.setOnAction(e -> {
                    freelancerService.deleteCertification(cert.getId());
                    showProfile();
                });
                cRow.getChildren().addAll(cTitle, sp, btnDelCert);
                certList.getChildren().add(cRow);
            }
        }

        Button btnAddCert = UIComponents.createSecondaryButton("+ Add Certification");
        btnAddCert.setOnAction(e -> {
            UIComponents.showPromptDialog("Add Certification", "Add verified technical credential", "Certification Name:", "Oracle Certified Java Developer", certName -> {
                if (certName != null && !certName.trim().isEmpty()) {
                    try {
                        freelancerService.addCertification(finalFp1.getId(), certName.trim(), "Oracle / Credential Issuer", "2026", "https://credential.net", null);
                        showProfile();
                    } catch (Exception ex) {
                        UIComponents.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add certification", ex.getMessage());
                    }
                }
            });
        });

        // Document Storage: Resume & Avatar
        Label docsHeader = UIComponents.createHeader("📎 Document & Media Attachments (Local Storage)");
        Label resumeLbl = new Label("Resume: " + (fp.getResumePath() != null ? fp.getResumePath() : "No resume uploaded"));
        resumeLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        Button btnUploadResume = UIComponents.createSecondaryButton("📄 Upload PDF Resume File");
        btnUploadResume.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Select Resume (PDF / DOCX)");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Document Files", "*.pdf", "*.docx", "*.txt"));
            java.io.File file = fc.showOpenDialog(null);
            if (file != null) {
                try {
                    String savedPath = freelancerService.uploadResume(currentUser.getId(), file);
                    resumeLbl.setText("Resume: " + savedPath);
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Resume Uploaded", "Saved to local storage:\n" + savedPath);
                } catch (Exception ex) {
                    UIComponents.showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to save resume", ex.getMessage());
                }
            }
        });

        Label avatarLbl = new Label("Profile Photo: " + (fp.getAvatarPath() != null ? fp.getAvatarPath() : "Default avatar active"));
        avatarLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        Button btnUploadAvatar = UIComponents.createSecondaryButton("🖼️ Upload Profile Photo");
        btnUploadAvatar.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Select Profile Avatar");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
            java.io.File file = fc.showOpenDialog(null);
            if (file != null) {
                try {
                    String savedPath = freelancerService.uploadAvatar(currentUser.getId(), file);
                    avatarLbl.setText("Profile Photo: " + savedPath);
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Avatar Uploaded", "Saved to local storage:\n" + savedPath);
                } catch (Exception ex) {
                    UIComponents.showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to save avatar", ex.getMessage());
                }
            }
        });

        TextField tfLinkedIn = UIComponents.createTextField("LinkedIn Profile URL");
        tfLinkedIn.setText(fp.getLinkedinUrl() != null ? fp.getLinkedinUrl() : "");
        TextField tfGitHub = UIComponents.createTextField("GitHub Profile URL");
        tfGitHub.setText(fp.getGithubUrl() != null ? fp.getGithubUrl() : "");

        Button btnSave = UIComponents.createPrimaryButton("Save & Update Profile to Database");
        FreelancerProfile finalFp = fp;
        btnSave.setOnAction(e -> {
            finalFp.setTitle(tfTitle.getText().trim());
            finalFp.setBio(taBio.getText().trim());
            finalFp.setAvailability(cbAvail.getValue());
            try {
                finalFp.setHourlyRate(Double.parseDouble(tfRate.getText().trim()));
            } catch (NumberFormatException ignored) {}
            try {
                finalFp.setExperienceYears(Integer.parseInt(tfExp.getText().trim()));
            } catch (NumberFormatException ignored) {}
            finalFp.setLinkedinUrl(tfLinkedIn.getText().trim());
            finalFp.setGithubUrl(tfGitHub.getText().trim());

            // Save to SQLite
            freelancerService.updateProfile(finalFp);

            // Sync Skills
            String[] skArray = tfSkills.getText().split(",");
            for (String raw : skArray) {
                String skName = raw.trim();
                if (!skName.isEmpty()) {
                    Skill skill = skillService.getOrCreateSkill(skName, "Development");
                    if (skill != null) {
                        freelancerService.addSkill(finalFp.getId(), skill.getId(), "EXPERT");
                    }
                }
            }

            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Saved", "Your freelancer profile, rate, skills, and portfolio have been updated in SQLite!");
            showProfile();
        });

        card.getChildren().addAll(
                new Label("Professional Title:"), tfTitle,
                rateExpRow,
                new Label("Bio / Summary:"), taBio,
                new Label("Skills Catalog Tagging (comma-separated):"), tfSkills,
                new Separator(),
                portHeader, portList, btnAddPort,
                new Separator(),
                certHeader, certList, btnAddCert,
                new Separator(),
                docsHeader, resumeLbl, btnUploadResume, avatarLbl, btnUploadAvatar,
                new Separator(),
                new Label("Social & Developer Profiles:"), tfLinkedIn, tfGitHub,
                btnSave
        );

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 3. Project Marketplace & Search (Multi-criteria SQLite Filtering, Match Scores & Modals)
    private void showProjectSearch() {
        VBox box = new VBox(18);
        Label title = UIComponents.createTitle("💼 Project Marketplace");
        Label subtitle = new Label("Explore client requirements, review match compatibility, and submit high-impact proposals.");
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));
        subtitle.setFont(Font.font("Segoe UI", 13));

        // Multi-faceted Filter Control Panel
        VBox filterCard = UIComponents.createCard();
        filterCard.getChildren().add(UIComponents.createHeader("🎯 Marketplace Filters"));

        // Row 1: Keyword, Category, Skill, Experience Level
        HBox row1 = new HBox(12);
        TextField tfSearch = UIComponents.createTextField("Search title, scope, keywords...");
        HBox.setHgrow(tfSearch, Priority.ALWAYS);

        ComboBox<String> cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("All Categories", "Desktop Development", "Software & Desktop App", "Web Development",
                "Mobile Development", "AI & Machine Learning", "Data & AI", "UI/UX Design", "Cloud & DevOps", "Cybersecurity", "Database");
        cbCategory.getSelectionModel().select("All Categories");
        UIComponents.styleComboBox(cbCategory);

        ComboBox<String> cbSkill = new ComboBox<>();
        cbSkill.getItems().add("All Skills");
        try {
            for (Skill s : skillDAO.findAll()) {
                if (!cbSkill.getItems().contains(s.getName())) {
                    cbSkill.getItems().add(s.getName());
                }
            }
        } catch (Exception ignored) {}
        cbSkill.getSelectionModel().select("All Skills");
        UIComponents.styleComboBox(cbSkill);

        ComboBox<String> cbExp = new ComboBox<>();
        cbExp.getItems().addAll("All Levels", "ENTRY", "INTERMEDIATE", "EXPERT");
        cbExp.getSelectionModel().select("All Levels");
        UIComponents.styleComboBox(cbExp);

        row1.getChildren().addAll(tfSearch, cbCategory, cbSkill, cbExp);

        // Row 2: Budget Min, Budget Max, Sort By, Filter and Reset Buttons
        HBox row2 = new HBox(12);
        row2.setAlignment(Pos.CENTER_LEFT);

        TextField tfMinBudget = UIComponents.createTextField("Min Budget ($)");
        tfMinBudget.setPrefWidth(120);

        TextField tfMaxBudget = UIComponents.createTextField("Max Budget ($)");
        tfMaxBudget.setPrefWidth(120);

        ComboBox<String> cbSort = new ComboBox<>();
        cbSort.getItems().addAll("Newest First", "Highest Budget", "Lowest Budget", "Closest Deadline");
        cbSort.getSelectionModel().select("Newest First");
        UIComponents.styleComboBox(cbSort);

        Button btnFilter = UIComponents.createPrimaryButton("🔍 Filter");
        Button btnReset = UIComponents.createSecondaryButton("🔄 Reset");

        Region spFilter = new Region();
        HBox.setHgrow(spFilter, Priority.ALWAYS);

        row2.getChildren().addAll(new Label("Budget:"), tfMinBudget, tfMaxBudget, new Label("Sort:"), cbSort, spFilter, btnFilter, btnReset);
        filterCard.getChildren().addAll(row1, row2);

        // Results Container
        VBox listContainer = new VBox(12);
        HBox resultHeader = new HBox(10);
        resultHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblCount = UIComponents.createSubHeader("Active Opportunities");
        resultHeader.getChildren().add(lblCount);

        FreelancerProfile profile = freelancerService.getProfile(currentUser.getId());
        if (profile == null) {
            profile = db.getFreelancerProfiles().get(currentUser.getId());
        }

        final FreelancerProfile finalProfile = profile;

        Runnable performSearch = () -> {
            listContainer.getChildren().clear();
            String kw = tfSearch.getText().trim();
            String cat = cbCategory.getValue();
            String skill = cbSkill.getValue();
            String exp = cbExp.getValue();
            String sort = cbSort.getValue();

            Double minB = null;
            Double maxB = null;
            try {
                if (!tfMinBudget.getText().trim().isEmpty()) minB = Double.parseDouble(tfMinBudget.getText().trim());
            } catch (Exception ignored) {}
            try {
                if (!tfMaxBudget.getText().trim().isEmpty()) maxB = Double.parseDouble(tfMaxBudget.getText().trim());
            } catch (Exception ignored) {}

            List<Project> results = projectService.searchProjects(kw, cat, skill, minB, maxB, exp, "OPEN", sort);
            lblCount.setText("Active Opportunities (" + results.size() + " Found)");

            if (results.isEmpty()) {
                VBox emptyCard = UIComponents.createCard();
                emptyCard.setAlignment(Pos.CENTER);
                emptyCard.setPadding(new Insets(40));
                Label emptyLbl = new Label("No active projects found matching your criteria.");
                emptyLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
                emptyLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                Label hintLbl = new Label("Try broadening your keywords or clearing the budget/skill filters.");
                hintLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                emptyCard.getChildren().addAll(emptyLbl, hintLbl);
                listContainer.getChildren().add(emptyCard);
            } else {
                for (Project p : results) {
                    VBox card = UIComponents.createCard();

                    // Header Row
                    HBox top = new HBox(12);
                    top.setAlignment(Pos.CENTER_LEFT);

                    Label pTitle = UIComponents.createHeader(p.getTitle());
                    pTitle.setWrapText(true);
                    HBox.setHgrow(pTitle, Priority.ALWAYS);

                    int score = aiService.calculateMatchScore(finalProfile, p);
                    String scoreColor = score >= 80 ? UIComponents.COLOR_SUCCESS : (score >= 60 ? UIComponents.COLOR_AMBER : UIComponents.COLOR_PRIMARY);
                    Label badgeScore = UIComponents.createBadge("★ " + score + "% Match", scoreColor, "white");

                    Label catBadge = UIComponents.createBadge(p.getCategory(), UIComponents.COLOR_PURPLE, "white");
                    Label expBadge = UIComponents.createBadge(p.getExperienceLevel(), UIComponents.COLOR_PRIMARY, "white");

                    top.getChildren().addAll(pTitle, catBadge, expBadge, badgeScore);

                    // Description snippet
                    Label desc = new Label(p.getDescription());
                    desc.setWrapText(true);
                    desc.setMaxHeight(60);
                    desc.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                    desc.setFont(Font.font("Segoe UI", 13));

                    // Skills Tags FlowPane
                    FlowPane skillsFlow = new FlowPane(6, 6);
                    skillsFlow.setPadding(new Insets(4, 0, 4, 0));
                    List<String> mySkills = finalProfile != null && finalProfile.getSkills() != null ? finalProfile.getSkills() : List.of();
                    for (String reqSkill : p.getRequiredSkills()) {
                        boolean hasSkill = mySkills.stream().anyMatch(s -> s.equalsIgnoreCase(reqSkill));
                        Label skTag = UIComponents.createBadge(reqSkill,
                                hasSkill ? "#1e3a2b" : UIComponents.COLOR_BG_INPUT,
                                hasSkill ? "#34d399" : UIComponents.COLOR_TEXT_PRIMARY);
                        skillsFlow.getChildren().add(skTag);
                    }

                    // Metadata & Compensation Row
                    HBox metaRow = new HBox(16);
                    metaRow.setAlignment(Pos.CENTER_LEFT);

                    Label clientLbl = new Label("🏢 Client: " + (p.getClientName() != null ? p.getClientName() : "Verified Client"));
                    clientLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                    clientLbl.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));

                    Label budgetLbl = new Label("💰 Budget: " + p.getBudgetDisplay());
                    budgetLbl.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));
                    budgetLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                    Label dlLbl = new Label("📅 Deadline: " + (p.getDeadline() != null ? p.getDeadline() : "Open"));
                    dlLbl.setTextFill(Color.web(UIComponents.COLOR_AMBER));
                    dlLbl.setFont(Font.font("Segoe UI", 12));

                    Region metaSp = new Region();
                    HBox.setHgrow(metaSp, Priority.ALWAYS);

                    Button btnDetails = UIComponents.createSecondaryButton("🔍 Details");
                    btnDetails.setOnAction(e -> showProjectDetailModal(p, finalProfile));

                    Button btnApply = UIComponents.createSuccessButton("⚡ Apply / Bid");
                    btnApply.setOnAction(e -> showProposalDialog(p));

                    metaRow.getChildren().addAll(clientLbl, budgetLbl, dlLbl, metaSp, btnDetails, btnApply);

                    card.getChildren().addAll(top, desc, skillsFlow, new Separator(), metaRow);
                    listContainer.getChildren().add(card);
                }
            }
        };

        btnFilter.setOnAction(e -> performSearch.run());
        btnReset.setOnAction(e -> {
            tfSearch.clear();
            cbCategory.getSelectionModel().select("All Categories");
            cbSkill.getSelectionModel().select("All Skills");
            cbExp.getSelectionModel().select("All Levels");
            tfMinBudget.clear();
            tfMaxBudget.clear();
            cbSort.getSelectionModel().select("Newest First");
            performSearch.run();
        });

        performSearch.run();

        box.getChildren().addAll(title, subtitle, filterCard, resultHeader, listContainer);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    private void showProjectDetailModal(Project project, FreelancerProfile profile) {
        VBox content = new VBox(15);
        content.setPrefWidth(550);
        content.setPadding(new Insets(16));

        // Basic Info Card
        VBox infoCard = UIComponents.createCard();
        Label pTitle = UIComponents.createHeader(project.getTitle());
        Label pMeta = new Label("Category: " + project.getCategory() + " | Level: " + project.getExperienceLevel() + " | Status: " + project.getStatus());
        pMeta.setTextFill(Color.web(AppTheme.getTextMuted()));

        Label pBudget = new Label("💰 Compensation: " + project.getBudgetDisplay());
        pBudget.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        pBudget.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));

        Label pClient = new Label("🏢 Posted By: " + (project.getClientName() != null ? project.getClientName() : "Client Profile"));
        pClient.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label pDeadline = new Label("📅 Project Deadline: " + (project.getDeadline() != null ? project.getDeadline() : "Flexible"));
        pDeadline.setTextFill(Color.web(UIComponents.COLOR_AMBER));

        infoCard.getChildren().addAll(pTitle, pMeta, pBudget, pClient, pDeadline);

        // Technical Description Card
        VBox descCard = UIComponents.createCard();
        descCard.getChildren().add(UIComponents.createHeader("Full Scope & Deliverables"));
        Label desc = new Label(project.getDescription());
        desc.setWrapText(true);
        desc.setTextFill(Color.web(AppTheme.getTextPrimary()));
        desc.setFont(Font.font("Segoe UI", 13));
        descCard.getChildren().add(desc);

        // AI Match Analysis Card
        VBox aiCard = UIComponents.createCard();
        aiCard.getChildren().add(UIComponents.createHeader("🤖 AI Compatibility Assessment (6-Factor Engine)"));
        MatchingResult match = aiService.calculateDetailedMatch(profile, project);
        int matchScore = match.getOverallScore();
        Label matchLbl = new Label("Compatibility Rating: " + match.getScoreBadgeText());
        matchLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        matchLbl.setTextFill(Color.web(matchScore >= 80 ? UIComponents.COLOR_SUCCESS : (matchScore >= 60 ? UIComponents.COLOR_AMBER : UIComponents.COLOR_DANGER)));

        GridPane factorGrid = new GridPane();
        factorGrid.setHgap(10);
        factorGrid.setVgap(6);
        factorGrid.setPadding(new Insets(6, 0, 6, 0));
        factorGrid.add(new Label("Skills (50%):"), 0, 0);
        factorGrid.add(UIComponents.createBadge(String.format("%.1f", match.getSkillScore()) + "/50 pts", "#1e293b", "#38bdf8"), 1, 0);
        factorGrid.add(new Label("Experience (15%):"), 2, 0);
        factorGrid.add(UIComponents.createBadge(String.format("%.1f", match.getExperienceScore()) + "/15 pts", "#1e293b", "#a78bfa"), 3, 0);
        factorGrid.add(new Label("Rating (10%):"), 0, 1);
        factorGrid.add(UIComponents.createBadge(String.format("%.1f", match.getRatingScore()) + "/10 pts", "#1e293b", "#fbbf24"), 1, 1);
        factorGrid.add(new Label("Budget Fit (10%):"), 2, 1);
        factorGrid.add(UIComponents.createBadge(String.format("%.1f", match.getBudgetScore()) + "/10 pts", "#1e293b", "#34d399"), 3, 1);
        factorGrid.add(new Label("Availability (10%):"), 0, 2);
        factorGrid.add(UIComponents.createBadge(String.format("%.1f", match.getAvailabilityScore()) + "/10 pts", "#1e293b", "#60a5fa"), 1, 2);
        factorGrid.add(new Label("Profile (5%):"), 2, 2);
        factorGrid.add(UIComponents.createBadge(String.format("%.1f", match.getCompletenessScore()) + "/5 pts", "#1e293b", "#f472b6"), 3, 2);

        Label lblExplanation = new Label(match.getExplanation());
        lblExplanation.setWrapText(true);
        lblExplanation.setFont(Font.font("Segoe UI", 12));
        lblExplanation.setTextFill(Color.web(AppTheme.getTextSecondary()));

        FlowPane reqSkillsPane = new FlowPane(6, 6);
        reqSkillsPane.setPadding(new Insets(6, 0, 0, 0));
        for (String rs : match.getMatchedSkills()) {
            reqSkillsPane.getChildren().add(UIComponents.createBadge("✓ " + rs + " (Matched)", "#1e3a2b", "#34d399"));
        }
        for (String rs : match.getMissingSkills()) {
            reqSkillsPane.getChildren().add(UIComponents.createBadge("✗ " + rs + " (Missing)", "#3b1e1e", "#f87171"));
        }
        aiCard.getChildren().addAll(matchLbl, factorGrid, lblExplanation, reqSkillsPane);

        Button btnApplyNow = UIComponents.createSuccessButton("⚡ Apply to this Project");
        btnApplyNow.setMaxWidth(Double.MAX_VALUE);
        btnApplyNow.setOnAction(e -> {
            UIComponents.closeModalOverlay();
            showProposalDialog(project);
        });

        content.getChildren().addAll(infoCard, descCard, aiCard, btnApplyNow);
        UIComponents.showModalOverlay("Project Details - " + project.getTitle(), content, null, null);
    }

    private void showProposalDialog(Project project) {
        if (proposalService.hasAlreadySubmitted(project.getId(), currentUser.getId())) {
            UIComponents.showAlert(Alert.AlertType.WARNING, "Proposal Already Submitted", "Duplicate Submission",
                    "You have already submitted a proposal for '" + project.getTitle() + "'.\nCheck 'My Submitted Proposals' in the sidebar to track its live status.");
            return;
        }

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));

        FreelancerProfile fp = freelancerService.getProfile(currentUser.getId());
        if (fp == null) fp = db.getFreelancerProfiles().get(currentUser.getId());

        double suggestedBid = aiService.getProposalAssistant().suggestBidAmount(project, fp);
        int suggestedDays = aiService.getProposalAssistant().suggestDeliveryDays(project);

        TextArea taCover = UIComponents.createTextArea("Explain why you are the best fit for this project...");
        taCover.setPrefRowCount(6);

        TextField tfBid = UIComponents.createTextField("Your Bid Amount ($)...");
        tfBid.setText(String.valueOf((int) suggestedBid));

        TextField tfDays = UIComponents.createTextField("Estimated Delivery Days...");
        tfDays.setText(String.valueOf(suggestedDays));

        Button btnAiGenProposal = UIComponents.createSecondaryButton("🤖 AI Auto-Generate Winning Proposal Cover Letter");
        btnAiGenProposal.setOnAction(e -> {
            FreelancerProfile currentFp = freelancerService.getProfile(currentUser.getId());
            if (currentFp == null) currentFp = db.getFreelancerProfiles().get(currentUser.getId());
            String cover = aiService.generateProposalCoverLetter(project, currentFp);
            taCover.setText(cover);
            double sBid = aiService.getProposalAssistant().suggestBidAmount(project, currentFp);
            int sDays = aiService.getProposalAssistant().suggestDeliveryDays(project);
            tfBid.setText(String.valueOf((int) sBid));
            tfDays.setText(String.valueOf(sDays));
        });

        // Milestone breakdown preview
        VBox msBox = UIComponents.createCard();
        msBox.getChildren().add(UIComponents.createSubHeader("🎯 Suggested Sprint Milestone Breakdown:"));
        List<AIProposalAssistant.MilestoneDraft> drafts = aiService.getProposalAssistant().generateMilestoneDrafts(project, suggestedBid);
        for (int i = 0; i < drafts.size(); i++) {
            AIProposalAssistant.MilestoneDraft d = drafts.get(i);
            Label ml = new Label(String.format("Milestone %d: %s ($%.0f • %d days)", i + 1, d.getTitle(), d.getAmount(), d.getDays()));
            ml.setFont(Font.font("Segoe UI", 11));
            ml.setTextFill(Color.web(AppTheme.getTextSecondary()));
            msBox.getChildren().add(ml);
        }

        content.getChildren().addAll(new Label("Cover Letter:"), taCover, btnAiGenProposal, new Label("Bid Amount ($):"), tfBid, new Label("Estimated Days:"), tfDays, msBox);

        UIComponents.showModalOverlay("Submit Proposal - " + project.getTitle(), content, "Submit Bid", () -> {
            try {
                double bid = Double.parseDouble(tfBid.getText().trim());
                int days = Integer.parseInt(tfDays.getText().trim());

                Proposal prop = proposalService.submitProposal(project.getId(), currentUser.getId(), bid, days, taCover.getText().trim());

                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Proposal Submitted",
                        "Your proposal was successfully submitted to the client!\n\nAI Match Score: " + (int) prop.getAiMatchScore() + "% (" + prop.getStatusDisplayName() + ")");
            } catch (IllegalStateException ex) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Proposal Exists", "Duplicate Proposal", ex.getMessage());
            } catch (NumberFormatException ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Amount/Days", "Please enter valid numerical values for Bid Amount and Days.");
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Submission Error", "Failed to Submit Proposal", ex.getMessage());
            }
        });
    }

    // 3b. My Submitted Proposals View
    private void showMyProposals() {
        VBox box = new VBox(20);
        Label title = UIComponents.createTitle("My Submitted Proposals");

        List<Proposal> proposals = proposalService.getProposalsForFreelancer(currentUser.getId());

        long countShortlisted = proposals.stream().filter(p -> p.getStatus() == Proposal.Status.SHORTLISTED).count();
        long countAccepted = proposals.stream().filter(p -> p.getStatus() == Proposal.Status.ACCEPTED).count();
        long countPending = proposals.stream().filter(p -> p.getStatus() == Proposal.Status.SUBMITTED || p.getStatus() == Proposal.Status.PENDING).count();

        HBox stats = new HBox(15);
        stats.getChildren().addAll(
                UIComponents.createStatCard(0, "📑", "Total Submitted", String.valueOf(proposals.size()), UIComponents.COLOR_PRIMARY),
                UIComponents.createStatCard(1, "⭐", "Shortlisted", String.valueOf(countShortlisted), UIComponents.COLOR_AMBER),
                UIComponents.createStatCard(2, "🎉", "Awarded / Accepted", String.valueOf(countAccepted), UIComponents.COLOR_SUCCESS),
                UIComponents.createStatCard(3, "⏳", "Under Review", String.valueOf(countPending), UIComponents.COLOR_PURPLE)
        );

        VBox list = new VBox(15);
        if (proposals.isEmpty()) {
            VBox empty = UIComponents.createCard();
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));
            Label emptyLbl = new Label("You have not submitted any proposals yet.");
            emptyLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
            emptyLbl.setTextFill(Color.web(AppTheme.getTextSecondary()));
            Button btnBrowse = UIComponents.createPrimaryButton("🔍 Explore Opportunities & AI Matches");
            btnBrowse.setOnAction(e -> { selectNav(btnNavSearch, navBtns); showProjectSearch(); });
            empty.getChildren().addAll(emptyLbl, btnBrowse);
            list.getChildren().add(empty);
        } else {
            for (Proposal p : proposals) {
                VBox card = UIComponents.createCard();

                HBox cardHeader = new HBox(12);
                cardHeader.setAlignment(Pos.CENTER_LEFT);
                Label pTitle = UIComponents.createHeader(p.getProjectTitle() != null ? p.getProjectTitle() : "Project Proposal");
                Region r = new Region();
                HBox.setHgrow(r, Priority.ALWAYS);

                // Status badge
                String badgeBg = "#1e293b";
                String badgeFg = "#38bdf8";
                if (p.getStatus() == Proposal.Status.ACCEPTED) {
                    badgeBg = "#1e3a2b"; badgeFg = "#34d399";
                } else if (p.getStatus() == Proposal.Status.SHORTLISTED) {
                    badgeBg = "#3d2e14"; badgeFg = "#fbbf24";
                } else if (p.getStatus() == Proposal.Status.REJECTED) {
                    badgeBg = "#3b1e1e"; badgeFg = "#f87171";
                } else if (p.getStatus() == Proposal.Status.WITHDRAWN) {
                    badgeBg = "#27272a"; badgeFg = "#94a3b8";
                }

                Label badgeStatus = UIComponents.createBadge(p.getStatusDisplayName(), badgeBg, badgeFg);
                Label badgeScore = UIComponents.createBadge("★ " + (int) p.getAiMatchScore() + "% Match", "#1e293b", "#a78bfa");
                cardHeader.getChildren().addAll(pTitle, r, badgeScore, badgeStatus);

                HBox meta = new HBox(16);
                meta.setAlignment(Pos.CENTER_LEFT);
                Label lblBid = new Label("💰 Your Bid: $" + String.format("%.0f", p.getBidAmount()));
                lblBid.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                lblBid.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));

                Label lblDays = new Label("⏱ Delivery: " + p.getDeliveryDays() + " days");
                lblDays.setTextFill(Color.web(AppTheme.getTextSecondary()));

                Label lblClient = new Label("👤 Client: " + (p.getClientName() != null ? p.getClientName() : "Client"));
                lblClient.setTextFill(Color.web(AppTheme.getTextSecondary()));

                Label lblDate = new Label("📅 " + (p.getCreatedAt() != null ? p.getCreatedAt().substring(0, Math.min(10, p.getCreatedAt().length())) : ""));
                lblDate.setTextFill(Color.web(AppTheme.getTextSecondary()));
                meta.getChildren().addAll(lblBid, lblDays, lblClient, lblDate);

                Label cover = new Label("Cover Letter:\n" + p.getCoverLetter());
                cover.setWrapText(true);
                cover.setFont(Font.font("Segoe UI", 12));
                cover.setTextFill(Color.web(AppTheme.getTextPrimary()));

                HBox actions = new HBox(10);
                actions.setAlignment(Pos.CENTER_RIGHT);
                if (p.getStatus() == Proposal.Status.SUBMITTED || p.getStatus() == Proposal.Status.SHORTLISTED || p.getStatus() == Proposal.Status.PENDING) {
                    Button btnWithdraw = UIComponents.createDangerButton("Withdraw Proposal");
                    btnWithdraw.setOnAction(e -> {
                        UIComponents.showConfirmDialog("Confirm Withdrawal", "Are you sure you want to withdraw your proposal for '" + p.getProjectTitle() + "'?", () -> {
                            proposalService.withdrawProposal(p.getId(), currentUser.getId());
                            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Withdrawn", "Proposal Withdrawn", "Your proposal was successfully withdrawn.");
                            showMyProposals();
                        });
                    });
                    actions.getChildren().add(btnWithdraw);
                }

                card.getChildren().addAll(cardHeader, meta, cover);
                if (!actions.getChildren().isEmpty()) {
                    card.getChildren().add(actions);
                }
                list.getChildren().add(card);
            }
        }

        box.getChildren().addAll(title, stats, list);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 4. Active Projects & Milestones
    private void showActiveProjects() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(20));

        Label title = UIComponents.createTitle("Active Projects & Milestone Progress");
        Label subtitle = new Label("Track awarded project contracts, build sprint milestones, upload deliverables, and manage your earnings.");
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));

        // Freelancer Financial KPI Cards & Withdrawal
        PaymentService.FreelancerFinancialSummary fin = paymentService.getFinancialSummaryForFreelancer(currentUser.getId());
        HBox kpiBox = new HBox(15);
        VBox cardEarned = UIComponents.createMetricCard("Total Earned (Demo)", "$" + String.format("%.0f", fin.totalEarned), "#10B981");
        VBox cardBalance = UIComponents.createMetricCard("Available Balance", "$" + String.format("%.0f", fin.availableBalance), "#3B82F6");
        VBox cardEscrow = UIComponents.createMetricCard("Active Escrow (Pending)", "$" + String.format("%.0f", fin.pendingEscrow), "#F59E0B");
        VBox cardWithdrawn = UIComponents.createMetricCard("Total Withdrawn", "$" + String.format("%.0f", fin.totalWithdrawn), "#8B5CF6");
        kpiBox.getChildren().addAll(cardEarned, cardBalance, cardEscrow, cardWithdrawn);

        HBox withdrawBar = new HBox(15);
        withdrawBar.setAlignment(Pos.CENTER_LEFT);
        Label lblAvail = new Label("Available for Withdrawal: $" + String.format("%.2f", fin.availableBalance));
        lblAvail.setTextFill(Color.web(AppTheme.getTextPrimary()));
        lblAvail.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        Button btnWithdraw = UIComponents.createSuccessButton("💸 Withdraw Funds (Demo Simulation)");
        btnWithdraw.setDisable(fin.availableBalance <= 0);
        btnWithdraw.setOnAction(e -> showWithdrawalDialog(fin.availableBalance));

        Region spW = new Region();
        HBox.setHgrow(spW, Priority.ALWAYS);
        withdrawBar.getChildren().addAll(lblAvail, spW, btnWithdraw);

        List<Contract> contracts = contractService.getContractsByFreelancerUserId(currentUser.getId());

        VBox list = new VBox(20);
        if (contracts.isEmpty()) {
            VBox emptyBox = UIComponents.createCard();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            Label emptyLbl = new Label("No active project contracts yet.\nBrowse open opportunities in 'Search & AI Matches' and submit proposals to get hired!");
            emptyLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
            emptyLbl.setStyle("-fx-text-alignment: center; -fx-font-size: 14px;");
            emptyBox.getChildren().add(emptyLbl);
            list.getChildren().add(emptyBox);
        } else {
            for (Contract c : contracts) {
                VBox card = UIComponents.createCard();
                card.setPadding(new Insets(18));

                HBox cardHeader = new HBox(12);
                cardHeader.setAlignment(Pos.CENTER_LEFT);
                Label pTitle = UIComponents.createHeader("📂 " + c.getProjectTitle());
                Region spH = new Region();
                HBox.setHgrow(spH, Priority.ALWAYS);

                Label statusBadge = UIComponents.createBadge(c.getStatus().name(),
                        c.getStatus() == Contract.Status.ACTIVE ? "#10B981" : "#6B7280", "white");
                cardHeader.getChildren().addAll(pTitle, spH, statusBadge);

                HBox metaRow = new HBox(20);
                metaRow.setAlignment(Pos.CENTER_LEFT);
                Label clientLbl = new Label("🏢 Client: " + (c.getClientName() != null ? c.getClientName() : "Project Owner"));
                clientLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));
                Label valLbl = new Label("💰 Contract Total: $" + String.format("%.0f", c.getTotalAmount()) + " | Escrow: $" + String.format("%.0f", c.getEscrowBalance()));
                valLbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                Label dateLbl = new Label("📅 Delivery Deadline: " + (c.getEndDate() != null ? c.getEndDate() : "-"));
                dateLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                metaRow.getChildren().addAll(clientLbl, valLbl, dateLbl);

                VBox msList = new VBox(10);
                msList.setPadding(new Insets(10, 0, 0, 0));
                msList.getChildren().add(UIComponents.createSubHeader("Sprint Milestones & Deliverables:"));

                List<Milestone> milestones = milestoneService.getMilestonesByContract(c.getId());
                for (Milestone m : milestones) {
                    VBox mRow = new VBox(8);
                    mRow.setPadding(new Insets(12));
                    mRow.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-background-radius: 8; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-width: 1; -fx-border-radius: 8;");

                    HBox topRow = new HBox(10);
                    topRow.setAlignment(Pos.CENTER_LEFT);

                    Label mOrder = new Label("#" + m.getSequenceOrder() + " " + m.getTitle());
                    mOrder.setTextFill(Color.web(AppTheme.getTextPrimary()));
                    mOrder.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                    Label mAmount = new Label("— $" + String.format("%.0f", m.getAmount()));
                    mAmount.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                    mAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    String statusColor = switch (m.getStatus()) {
                        case PAID -> "#10B981";
                        case APPROVED -> "#3B82F6";
                        case SUBMITTED -> "#F59E0B";
                        case REVISION_REQUESTED -> "#EF4444";
                        case IN_PROGRESS -> "#6366F1";
                        default -> "#9CA3AF";
                    };
                    Label mBadge = UIComponents.createBadge(m.getStatus().name(), statusColor, "white");
                    topRow.getChildren().addAll(mOrder, mAmount, sp, mBadge);

                    Label mDesc = new Label(m.getDescription() != null ? m.getDescription() : "Milestone deliverable specification.");
                    mDesc.setTextFill(Color.web(AppTheme.getTextMuted()));
                    mDesc.setWrapText(true);

                    mRow.getChildren().addAll(topRow, mDesc);

                    // Deliverable preview
                    Deliverable del = m.getLatestDeliverable();
                    if (del != null) {
                        VBox delBox = new VBox(4);
                        delBox.setPadding(new Insets(8));
                        delBox.setStyle("-fx-background-color: rgba(99, 102, 241, 0.08); -fx-background-radius: 6;");

                        Label delTitle = new Label("📦 Submitted Artifact: " + del.getTitle() + " (" + del.getStatus() + ")");
                        delTitle.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                        delTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                        Label delNotes = new Label(del.getDescription() != null && !del.getDescription().isBlank() ? del.getDescription() : "No notes attached.");
                        delNotes.setTextFill(Color.web(AppTheme.getTextPrimary()));
                        delNotes.setWrapText(true);

                        Label delFile = new Label(del.getFilePath() != null ? "📁 Stored at: " + del.getFilePath() : "📁 No file attachment");
                        delFile.setTextFill(Color.web(AppTheme.getTextMuted()));
                        delFile.setFont(Font.font("Segoe UI", 11));

                        delBox.getChildren().addAll(delTitle, delNotes, delFile);
                        mRow.getChildren().add(delBox);
                    }

                    HBox actionRow = new HBox(10);
                    actionRow.setAlignment(Pos.CENTER_RIGHT);

                    if (m.getStatus() == Milestone.Status.IN_PROGRESS || m.getStatus() == Milestone.Status.REVISION_REQUESTED) {
                        Button btnSubmitDeliverable = UIComponents.createPrimaryButton("📤 Upload Deliverable");
                        btnSubmitDeliverable.setOnAction(e -> showDeliverableUploadDialog(m));
                        actionRow.getChildren().add(btnSubmitDeliverable);
                    } else if (m.getStatus() == Milestone.Status.SUBMITTED) {
                        Label lblReview = new Label("⏳ Under client review");
                        lblReview.setTextFill(Color.web(AppTheme.getTextMuted()));
                        actionRow.getChildren().add(lblReview);
                    } else if (m.getStatus() == Milestone.Status.APPROVED) {
                        Label lblApproved = new Label("✅ Approved — Awaiting payment release");
                        lblApproved.setTextFill(Color.web("#10B981"));
                        actionRow.getChildren().add(lblApproved);
                    } else if (m.getStatus() == Milestone.Status.PAID) {
                        Label lblPaid = new Label("💰 Escrow released & paid");
                        lblPaid.setTextFill(Color.web("#10B981"));
                        lblPaid.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                        actionRow.getChildren().add(lblPaid);
                    }

                    if (!actionRow.getChildren().isEmpty()) {
                        mRow.getChildren().add(actionRow);
                    }

                    msList.getChildren().add(mRow);
                }

                card.getChildren().addAll(cardHeader, metaRow, msList);
                list.getChildren().add(card);
            }
        }

        // Financial Ledger & Transaction History
        List<Transaction> txHistory = paymentService.getTransactionHistory(currentUser.getId());
        if (!txHistory.isEmpty()) {
            VBox ledgerCard = UIComponents.createCard();
            ledgerCard.setPadding(new Insets(15));
            ledgerCard.getChildren().add(UIComponents.createHeader("📜 Financial Ledger & Earnings History (Demo)"));
            for (Transaction tx : txHistory) {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-background-radius: 6;");

                boolean isIncoming = tx.getReceiverId() != null && tx.getReceiverId().equals(currentUser.getId()) && tx.getType() == Transaction.Type.PAYMENT;
                Label lType = UIComponents.createBadge(tx.getType().name(), isIncoming ? "#10B981" : "#8B5CF6", "white");
                Label lRef = new Label(tx.getReference() != null ? tx.getReference() : tx.getId());
                lRef.setTextFill(Color.web(AppTheme.getTextPrimary()));
                lRef.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                Label lMilestone = new Label(tx.getMilestoneTitle() != null ? "Milestone: " + tx.getMilestoneTitle() : "");
                lMilestone.setTextFill(Color.web(AppTheme.getTextMuted()));

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Label lAmount = new Label((isIncoming ? "+$" : "-$") + String.format("%.2f", tx.getAmount()));
                lAmount.setTextFill(Color.web(isIncoming ? "#10B981" : "#EF4444"));
                lAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

                Label lDate = new Label(tx.getCreatedAt());
                lDate.setTextFill(Color.web(AppTheme.getTextMuted()));

                row.getChildren().addAll(lType, lRef, lMilestone, sp, lAmount, lDate);
                ledgerCard.getChildren().add(row);
            }
            list.getChildren().add(ledgerCard);
        }

        box.getChildren().addAll(title, subtitle, kpiBox, withdrawBar, list);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    private void showWithdrawalDialog(double availableBalance) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(15));
        form.setPrefWidth(420);

        Label lblNotice = new Label("[DEMO PAYMENT NOTICE]: This is an educational sandbox simulation. No actual banking networks are contacted.");
        lblNotice.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 11px; -fx-font-style: italic;");
        lblNotice.setWrapText(true);

        Label lblAmt = new Label("Withdrawal Amount ($):");
        lblAmt.setTextFill(Color.web(AppTheme.getTextPrimary()));
        TextField txtAmount = UIComponents.createTextField("e.g. " + String.format("%.0f", Math.min(availableBalance, 500.0)));
        txtAmount.setText(String.format("%.0f", availableBalance));

        Label lblMethod = new Label("Payout Method:");
        lblMethod.setTextFill(Color.web(AppTheme.getTextPrimary()));
        ComboBox<String> cbMethod = new ComboBox<>();
        cbMethod.getItems().addAll("Bank Transfer (ACH / NEFT)", "UPI Instant Payout", "PayPal Sandbox", "Direct Wire");
        cbMethod.setValue("Bank Transfer (ACH / NEFT)");
        cbMethod.setMaxWidth(Double.MAX_VALUE);

        Label lblAcct = new Label("Account Identifier / UPI ID / Email:");
        lblAcct.setTextFill(Color.web(AppTheme.getTextPrimary()));
        TextField txtAcct = UIComponents.createTextField("e.g. dev@upi or ACCT-892182");
        txtAcct.setText(currentUser.getUsername() + "@bank");

        form.getChildren().addAll(lblNotice, lblAmt, txtAmount, lblMethod, cbMethod, lblAcct, txtAcct);

        UIComponents.showModalOverlay("Withdraw Freelancer Earnings", form, "Withdraw Now", () -> {
            try {
                double amt = Double.parseDouble(txtAmount.getText().trim());
                paymentService.withdrawEarnings(currentUser.getId(), amt, cbMethod.getValue(), txtAcct.getText());
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Withdrawal Processed", "Funds Transferred (DEMO)",
                        "Successfully simulated withdrawal of $" + String.format("%.2f", amt) + " via " + cbMethod.getValue() + "!");
                showActiveProjects();
            } catch (NumberFormatException nfe) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Invalid Amount", "Number Format Error", "Please enter a valid numeric withdrawal amount.");
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Withdrawal Failed", "Error", ex.getMessage());
            }
        });
    }

    private void showDeliverableUploadDialog(Milestone milestone) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(15));
        form.setPrefWidth(480);

        Label lblTitle = new Label("Deliverable Title:");
        lblTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));
        TextField txtTitle = UIComponents.createTextField("e.g. Milestone 1 Complete Archive / Code Review");
        txtTitle.setText("Delivery: " + milestone.getTitle());

        Label lblNotes = new Label("Release Notes / Summary of Work:");
        lblNotes.setTextFill(Color.web(AppTheme.getTextPrimary()));
        TextArea txtNotes = UIComponents.createTextArea("Describe features implemented, test results, and instructions...");
        txtNotes.setPrefRowCount(4);

        Label lblFile = new Label("Deliverable Artifact / Archive:");
        lblFile.setTextFill(Color.web(AppTheme.getTextPrimary()));

        HBox fileRow = new HBox(10);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        Label lblChosen = new Label("No file selected (optional)");
        lblChosen.setTextFill(Color.web(AppTheme.getTextMuted()));

        final File[] chosenFile = new File[1];
        Button btnBrowse = UIComponents.createSecondaryButton("📁 Browse File...");
        btnBrowse.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Deliverable File");
            File f = chooser.showOpenDialog(HomePage.getPrimaryStage());
            if (f != null) {
                chosenFile[0] = f;
                lblChosen.setText(f.getName() + " (" + (f.length() / 1024) + " KB)");
                lblChosen.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
            }
        });
        fileRow.getChildren().addAll(btnBrowse, lblChosen);

        form.getChildren().addAll(lblTitle, txtTitle, lblNotes, txtNotes, lblFile, fileRow);

        UIComponents.showModalOverlay("Upload Milestone Deliverable - " + milestone.getTitle(), form, "Submit Deliverable", () -> {
            String dTitle = txtTitle.getText();
            if (dTitle == null || dTitle.trim().isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Missing Title", "Deliverable Title Required", "Please enter a deliverable title.");
                return;
            }
            try {
                milestoneService.submitDeliverable(milestone.getId(), currentUser.getId(),
                        dTitle.trim(), txtNotes.getText(), chosenFile[0]);
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Deliverable Submitted", "Submission Successful",
                        "Your deliverable has been submitted for client review!");
                showActiveProjects();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Submission Failed", "Error Submitting Deliverable", ex.getMessage());
            }
        });
    }

    // 5. Interactive Calendar & Schedule View
    private void showCalendar() {
        contentArea.getChildren().clear();
        CalendarView view = new CalendarView(currentUser);
        contentArea.getChildren().add(view);
    }

    // 6. Encrypted Chat & Virtual Meetings
    private void showChat() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10, 20, 20, 20));

        Label title = UIComponents.createTitle("💬 Real-Time Freelancer & Client Communication");
        Label subtitle = new Label("Collaborate directly with active clients with full project context, encrypted history, attachments, and virtual meetings.");
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));

        // Quick Virtual Meeting Action Bar
        HBox meetBar = new HBox(15);
        meetBar.setPadding(new Insets(12, 16, 12, 16));
        meetBar.setAlignment(Pos.CENTER_LEFT);
        meetBar.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label meetIcon = new Label("📹 Virtual Meeting Hub:");
        meetIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        meetIcon.setTextFill(Color.web(AppTheme.getTextPrimary()));
        Label meetSub = new Label("Instantly launch or schedule video conferences with clients.");
        meetSub.setFont(Font.font("Segoe UI", 12));
        meetSub.setTextFill(Color.web(AppTheme.getTextMuted()));

        Region msp = new Region();
        HBox.setHgrow(msp, Priority.ALWAYS);

        Button btnQuickMeet = UIComponents.createPrimaryButton("📹 Launch Instant Meeting");
        btnQuickMeet.setOnAction(e -> {
            String url = "https://meet.google.com/sb-" + UUID.randomUUID().toString().substring(0, 6);
            boolean opened = CalendarService.openMeetingLink(url);
            if (!opened) {
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Meeting Room Created", "Virtual Meeting Link",
                        "Demo Room URL: " + url + "\nLink copied. Open in browser to start video call!");
            }
        });
        meetBar.getChildren().addAll(meetIcon, meetSub, msp, btnQuickMeet);

        // Fetch conversations from SQLite
        List<Conversation> convs = chatService.getUserConversations(currentUser.getId());

        // If no conversations exist yet, auto-create threads for active contracts
        if (convs.isEmpty()) {
            List<Contract> contracts = contractService.getContractsByFreelancerUserId(currentUser.getId());
            for (Contract c : contracts) {
                if (c.getClientUserId() != null) {
                    chatService.getOrCreateConversation(currentUser.getId(), c.getClientUserId(), c.getProjectId());
                }
            }
            convs = chatService.getUserConversations(currentUser.getId());
        }

        HBox chatLayout = new HBox(15);
        chatLayout.setPrefHeight(520);
        VBox.setVgrow(chatLayout, Priority.ALWAYS);

        // --- Left: Conversations List ---
        VBox convListPanel = new VBox(10);
        convListPanel.setPrefWidth(280);
        convListPanel.setMinWidth(260);
        convListPanel.setPadding(new Insets(14));
        convListPanel.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label convHeader = new Label("📂 Active Threads (" + convs.size() + ")");
        convHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        convHeader.setTextFill(Color.web(AppTheme.getTextPrimary()));
        convListPanel.getChildren().add(convHeader);

        if (selectedChatConversationId == null && !convs.isEmpty()) {
            selectedChatConversationId = convs.get(0).getId();
        }

        if (convs.isEmpty()) {
            VBox emptyConv = new VBox(6);
            emptyConv.setAlignment(Pos.CENTER);
            emptyConv.setPadding(new Insets(30, 10, 30, 10));
            Label noProj = new Label("No active chat threads.\nSubmit proposals or get hired to start chatting!");
            noProj.setTextFill(Color.web(AppTheme.getTextMuted()));
            noProj.setStyle("-fx-text-alignment: center; -fx-font-size: 12px;");
            emptyConv.getChildren().add(noProj);
            convListPanel.getChildren().add(emptyConv);
        } else {
            VBox threadList = new VBox(8);
            for (Conversation c : convs) {
                boolean isSelected = c.getId().equals(selectedChatConversationId);
                VBox item = new VBox(4);
                item.setPadding(new Insets(10, 12, 10, 12));
                item.setStyle("-fx-background-color: " + (isSelected ? AppTheme.COLOR_PRIMARY + "22" : AppTheme.getBgInput()) + "; "
                        + "-fx-border-color: " + (isSelected ? AppTheme.COLOR_PRIMARY : "transparent") + "; "
                        + "-fx-border-width: 1.5px; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

                HBox nameRow = new HBox(8);
                nameRow.setAlignment(Pos.CENTER_LEFT);
                Label nameLbl = new Label("👤 " + (c.getOtherUsername() != null ? c.getOtherUsername() : "Client"));
                nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                nameLbl.setTextFill(Color.web(isSelected ? (AppTheme.isDarkMode() ? "#A5B4FC" : "#4338CA") : AppTheme.getTextPrimary()));

                Region nsp = new Region();
                HBox.setHgrow(nsp, Priority.ALWAYS);

                if (c.getUnreadCount() > 0) {
                    Label unreadPill = UIComponents.createBadge(c.getUnreadCount() + " new", "#EF4444", "white");
                    nameRow.getChildren().addAll(nameLbl, nsp, unreadPill);
                } else {
                    nameRow.getChildren().addAll(nameLbl, nsp);
                }

                if (c.getProjectTitle() != null && !c.getProjectTitle().isEmpty()) {
                    Label pLbl = new Label("📋 " + c.getProjectTitle());
                    pLbl.setFont(Font.font("Segoe UI", 11));
                    pLbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                    item.getChildren().add(pLbl);
                }

                Label lastMsg = new Label(c.getLastMessage() != null ? c.getLastMessage() : "No messages yet");
                lastMsg.setFont(Font.font("Segoe UI", 11));
                lastMsg.setTextFill(Color.web(AppTheme.getTextMuted()));
                lastMsg.setMaxWidth(230);
                lastMsg.setEllipsisString("…");

                item.getChildren().addAll(nameRow, lastMsg);

                item.setOnMouseClicked(e -> {
                    selectedChatConversationId = c.getId();
                    showChat();
                });
                threadList.getChildren().add(item);
            }
            ScrollPane threadScroll = UIComponents.createScrollPane(threadList);
            threadScroll.setFitToWidth(true);
            VBox.setVgrow(threadScroll, Priority.ALWAYS);
            threadScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            convListPanel.getChildren().add(threadScroll);
        }

        // --- Right: Active Conversation Panel ---
        VBox chatPanel = new VBox(10);
        HBox.setHgrow(chatPanel, Priority.ALWAYS);
        chatPanel.setPadding(new Insets(14));
        chatPanel.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Conversation activeConv = null;
        if (selectedChatConversationId != null) {
            activeConv = chatService.getConversation(selectedChatConversationId);
        }

        if (activeConv != null) {
            final Conversation currentActiveConv = activeConv;
            // Chat Header
            HBox chatHeader = new HBox(12);
            chatHeader.setAlignment(Pos.CENTER_LEFT);
            chatHeader.setPadding(new Insets(0, 0, 8, 0));

            Label chatUserLbl = new Label("💬 " + (activeConv.getOtherUsername() != null ? activeConv.getOtherUsername() : "Client"));
            chatUserLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            chatUserLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

            Label rolePill = UIComponents.createBadge(activeConv.getOtherUserRole() != null ? activeConv.getOtherUserRole() : "CLIENT", "#3B82F6", "white");

            Region chSp = new Region();
            HBox.setHgrow(chSp, Priority.ALWAYS);

            Label encBadge = UIComponents.createBadge("🔒 SQLite Stored & Verified", "#6366F1", "white");

            Button btnScheduleMeet = UIComponents.createSuccessButton("📹 Schedule Meeting");
            btnScheduleMeet.setOnAction(e -> showScheduleMeetingDialog(
                    currentActiveConv.getOtherUserId(),
                    currentActiveConv.getOtherUsername(),
                    currentActiveConv.getProjectId(),
                    currentActiveConv.getProjectTitle()
            ));

            chatHeader.getChildren().addAll(chatUserLbl, rolePill, chSp, encBadge, btnScheduleMeet);

            // Messages Container
            VBox msgContainer = new VBox(10);
            msgContainer.setPadding(new Insets(10));

            List<ChatMessage> messages = chatService.getConversationMessages(selectedChatConversationId, currentUser.getId());

            if (messages.isEmpty()) {
                VBox emptyMsgBox = new VBox(8);
                emptyMsgBox.setAlignment(Pos.CENTER);
                emptyMsgBox.setPadding(new Insets(60, 20, 60, 20));
                Label noMsg = new Label("No messages yet in this thread.\nSay hello and kick off your sprint collaboration!");
                noMsg.setTextFill(Color.web(AppTheme.getTextMuted()));
                noMsg.setFont(Font.font("Segoe UI", 13));
                noMsg.setStyle("-fx-text-alignment: center;");
                emptyMsgBox.getChildren().add(noMsg);
                msgContainer.getChildren().add(emptyMsgBox);
            } else {
                for (ChatMessage m : messages) {
                    boolean isMine = currentUser.getId().equals(m.getSenderId());

                    HBox row = new HBox();
                    row.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                    VBox bubble = new VBox(4);
                    bubble.setMaxWidth(440);
                    bubble.setPadding(new Insets(10, 14, 10, 14));

                    String bubbleBg = isMine ? AppTheme.COLOR_PRIMARY : AppTheme.getBgInput();
                    String textCol = isMine ? "white" : AppTheme.getTextPrimary();

                    bubble.setStyle("-fx-background-color: " + bubbleBg + "; "
                            + "-fx-background-radius: " + (isMine ? "14 14 2 14" : "14 14 14 2") + ";");

                    Label authorLbl = new Label(isMine ? "You" : (m.getSenderName() != null ? m.getSenderName() : "Partner"));
                    authorLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                    authorLbl.setTextFill(Color.web(isMine ? "#C7D2FE" : AppTheme.getTextMuted()));

                    Label contentLbl = new Label(m.getContent());
                    contentLbl.setFont(Font.font("Segoe UI", 13));
                    contentLbl.setTextFill(Color.web(textCol));
                    contentLbl.setWrapText(true);

                    Label timeLbl = new Label(m.getSentAt() != null ? m.getSentAt() : "");
                    timeLbl.setFont(Font.font("Segoe UI", 10));
                    timeLbl.setTextFill(Color.web(isMine ? "#A5B4FC" : AppTheme.getTextMuted()));

                    bubble.getChildren().addAll(authorLbl, contentLbl);

                    // If attachment present
                    if (m.getAttachmentPath() != null && !m.getAttachmentPath().isEmpty()) {
                        Label attachLbl = new Label("📎 " + m.getAttachmentPath());
                        attachLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
                        attachLbl.setTextFill(Color.web(isMine ? "#E0E7FF" : AppTheme.COLOR_PRIMARY));
                        bubble.getChildren().add(attachLbl);
                    }

                    bubble.getChildren().add(timeLbl);
                    row.getChildren().add(bubble);
                    msgContainer.getChildren().add(row);
                }
            }

            ScrollPane msgScroll = UIComponents.createScrollPane(msgContainer);
            msgScroll.setFitToWidth(true);
            VBox.setVgrow(msgScroll, Priority.ALWAYS);
            msgScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            msgScroll.setVvalue(1.0);

            // Input Bar
            HBox inputBar = new HBox(10);
            inputBar.setAlignment(Pos.CENTER_LEFT);
            inputBar.setPadding(new Insets(10, 0, 0, 0));

            TextField tfMsg = UIComponents.createTextField("Type message to " + activeConv.getOtherUsername() + "...");
            HBox.setHgrow(tfMsg, Priority.ALWAYS);

            final String[] attachedPath = {null};
            Button btnAttach = UIComponents.createSecondaryButton("📎 File");
            btnAttach.setOnAction(e -> {
                FileChooser fc = new FileChooser();
                fc.setTitle("Attach Document or File");
                File f = fc.showOpenDialog(root.getScene().getWindow());
                if (f != null) {
                    attachedPath[0] = f.getName();
                    btnAttach.setText("📎 " + f.getName());
                    btnAttach.setStyle(btnAttach.getStyle() + "-fx-border-color: #10B981;");
                }
            });

            Button btnSend = UIComponents.createPrimaryButton("Send 📨");
            Runnable sendAction = () -> {
                String text = tfMsg.getText().trim();
                if (!text.isEmpty() || attachedPath[0] != null) {
                    chatService.sendMessage(selectedChatConversationId, currentUser.getId(), text, attachedPath[0]);
                    tfMsg.clear();
                    attachedPath[0] = null;
                    btnAttach.setText("📎 File");
                    showChat();
                }
            };
            btnSend.setOnAction(e -> sendAction.run());
            tfMsg.setOnAction(e -> sendAction.run());

            inputBar.getChildren().addAll(tfMsg, btnAttach, btnSend);
            chatPanel.getChildren().addAll(chatHeader, new Separator(), msgScroll, new Separator(), inputBar);
        } else {
            VBox emptyChat = new VBox(10);
            emptyChat.setAlignment(Pos.CENTER);
            Label lblSelect = new Label("Select a conversation from the left to start messaging,\nor wait for client proposal acceptance.");
            lblSelect.setFont(Font.font("Segoe UI", 14));
            lblSelect.setTextFill(Color.web(AppTheme.getTextMuted()));
            emptyChat.getChildren().add(lblSelect);
            chatPanel.getChildren().add(emptyChat);
        }

        chatLayout.getChildren().addAll(convListPanel, chatPanel);
        box.getChildren().addAll(title, subtitle, meetBar, chatLayout);
        contentArea.getChildren().setAll(box);
    }

    private void showScheduleMeetingDialog(String otherUserId, String otherUserName, String projectId, String projectTitle) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: " + AppTheme.getBgCard() + ";");

        TextField tfTopic = UIComponents.createTextField(projectTitle != null ? "Sync: " + projectTitle : "Sprint Progress & Review");
        DatePicker dp = new DatePicker(java.time.LocalDate.now().plusDays(1));
        TextField tfTime = UIComponents.createTextField("15:00");
        String demoUrl = "https://meet.google.com/sb-" + UUID.randomUUID().toString().substring(0, 6);
        TextField tfUrl = UIComponents.createTextField(demoUrl);

        form.getChildren().addAll(
            new Label("Meeting Topic / Agenda:"), tfTopic,
            new Label("Meeting Date:"), dp,
            new Label("Meeting Time (e.g. 14:30):"), tfTime,
            new Label("Virtual Room URL:"), tfUrl
        );

        UIComponents.showModalOverlay("Schedule Virtual Meeting with " + (otherUserName != null ? otherUserName : "Client"),
            form, "Schedule & Notify", () -> {
                if (!tfTopic.getText().trim().isEmpty() && dp.getValue() != null) {
                    String topic = tfTopic.getText().trim();
                    String date = dp.getValue().toString();
                    String time = tfTime.getText().trim();
                    String url = tfUrl.getText().trim();

                    calendarService.createMeetingEvent(currentUser.getId(), otherUserId, topic, date, time, url, projectId);

                    if (selectedChatConversationId != null) {
                        chatService.sendMessage(selectedChatConversationId, currentUser.getId(),
                            "📹 Scheduled Virtual Meeting: " + topic + "\n🗓 Date: " + date + " at " + time + "\n🔗 Link: " + url, null);
                    }

                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Meeting Scheduled", "Virtual Meeting Confirmed",
                        "Meeting saved to calendar and shared with " + otherUserName + "!");
                    showChat();
                }
            });
    }

    // 7. AI Career Coach
    private void showAiCareerCoach() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("🤖 AI Career Coach ChatBot");

        VBox chatCard = UIComponents.createCard();
        VBox logBox = new VBox(10);

        Label welcomeMsg = new Label("🤖 AI Career Coach: Hello " + currentUser.getUsername() + "! Ask me anything about profile optimization, writing winning proposals, or target skills!");
        welcomeMsg.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));
        welcomeMsg.setWrapText(true);
        logBox.getChildren().add(welcomeMsg);

        HBox chips = new HBox(8);
        Button btnC1 = UIComponents.createSecondaryButton("💡 Proposal Tips");
        btnC1.setOnAction(e -> getReply("How can I write winning proposals?"));
        Button btnC2 = UIComponents.createSecondaryButton("⚡ Top Java Skills 2026");
        btnC2.setOnAction(e -> getReply("What Java skills are trending?"));
        Button btnC3 = UIComponents.createSecondaryButton("💰 Pricing Advice");
        btnC3.setOnAction(e -> getReply("How should I price my services?"));
        chips.getChildren().addAll(btnC1, btnC2, btnC3);

        HBox inputRow = new HBox(10);
        TextField tfQuery = UIComponents.createTextField("Ask AI Coach for advice...");
        HBox.setHgrow(tfQuery, Priority.ALWAYS);
        Button btnAsk = UIComponents.createPrimaryButton("Ask AI Coach");

        btnAsk.setOnAction(e -> {
            String q = tfQuery.getText().trim();
            if (!q.isEmpty()) {
                getReply(q);
                tfQuery.clear();
            }
        });

        inputRow.getChildren().addAll(tfQuery, btnAsk);
        chatCard.getChildren().addAll(logBox, chips, new Separator(), inputRow);

        box.getChildren().addAll(title, chatCard);
        contentArea.getChildren().setAll(box);
    }

    private void getReply(String query) {
        VBox logBox = (VBox) ((VBox) contentArea.getChildren().get(0)).getChildren().get(1);
        Label userLbl = new Label("👤 You: " + query);
        userLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

        String reply = aiService.getCareerCoachResponse(query);
        Label botLbl = new Label(reply);
        botLbl.setWrapText(true);
        botLbl.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));

        logBox.getChildren().addAll(userLbl, botLbl);
    }

    // 8. Notifications Portal
    private void showNotifications() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(20));

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createTitle("🔔 System & Event Notifications");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        int unreadCount = notifService.getUnreadCount(currentUser.getId());
        Label badge = UIComponents.createBadge(unreadCount + " Unread", unreadCount > 0 ? "#EF4444" : "#10B981", "white");

        Button btnMarkAll = UIComponents.createSecondaryButton("✓ Mark All as Read");
        btnMarkAll.setOnAction(e -> {
            notifService.markAllAsRead(currentUser.getId());
            showNotifications();
        });

        header.getChildren().addAll(title, badge, sp, btnMarkAll);

        List<Notification> notifs = notifService.getUserNotifications(currentUser.getId());
        VBox list = new VBox(10);

        if (notifs.isEmpty()) {
            VBox emptyBox = UIComponents.createCard();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            Label emptyLbl = new Label("No notifications yet.\nYou will receive real-time updates for proposals, contracts, deliverables, payments, and meetings here.");
            emptyLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
            emptyLbl.setStyle("-fx-text-alignment: center; -fx-font-size: 14px;");
            emptyBox.getChildren().add(emptyLbl);
            list.getChildren().add(emptyBox);
        } else {
            for (Notification n : notifs) {
                VBox card = UIComponents.createCard();
                card.setPadding(new Insets(14));
                if (!n.isRead()) {
                    card.setStyle(card.getStyle() + "-fx-border-color: #6366F1; -fx-border-width: 1.5px;");
                }

                HBox topRow = new HBox(10);
                topRow.setAlignment(Pos.CENTER_LEFT);

                String icon = "🔔";
                String color = "#6366F1";
                if ("MESSAGE".equals(n.getType())) { icon = "💬"; color = "#3B82F6"; }
                else if ("PAYMENT".equals(n.getType())) { icon = "💳"; color = "#10B981"; }
                else if ("CONTRACT".equals(n.getType())) { icon = "📜"; color = "#8B5CF6"; }
                else if ("PROPOSAL".equals(n.getType())) { icon = "📩"; color = "#F59E0B"; }
                else if ("MILESTONE".equals(n.getType())) { icon = "🚩"; color = "#EC4899"; }
                else if ("MEETING".equals(n.getType())) { icon = "📹"; color = "#06B6D4"; }

                Label iconBadge = UIComponents.createBadge(icon + " " + (n.getType() != null ? n.getType() : "INFO"), color, "white");

                Label titleLbl = new Label(n.getTitle());
                titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                titleLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

                Region topSp = new Region();
                HBox.setHgrow(topSp, Priority.ALWAYS);

                Label timeLbl = new Label(n.getCreatedAt() != null ? n.getCreatedAt() : (n.getTimestamp() != null ? n.getTimestamp() : ""));
                timeLbl.setFont(Font.font("Segoe UI", 11));
                timeLbl.setTextFill(Color.web(AppTheme.getTextMuted()));

                topRow.getChildren().addAll(iconBadge, titleLbl, topSp, timeLbl);

                Label msgLbl = new Label(n.getMessage());
                msgLbl.setFont(Font.font("Segoe UI", 13));
                msgLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                msgLbl.setWrapText(true);

                card.getChildren().addAll(topRow, msgLbl);

                if (!n.isRead()) {
                    card.setOnMouseClicked(e -> {
                        notifService.markAsRead(n.getId());
                        showNotifications();
                    });
                }
                list.getChildren().add(card);
            }
        }

        box.getChildren().addAll(header, list);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }
}
