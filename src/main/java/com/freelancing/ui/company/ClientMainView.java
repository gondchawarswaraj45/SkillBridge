package com.freelancing.ui.company;

import com.freelancing.model.common.ChatMessage;
import com.freelancing.model.common.Conversation;
import com.freelancing.model.common.FeedPost;
import com.freelancing.model.common.Notification;
import com.freelancing.model.common.Transaction;
import com.freelancing.model.common.User;
import com.freelancing.model.company.ClientProfile;
import com.freelancing.model.company.Contract;
import com.freelancing.model.company.Deliverable;
import com.freelancing.model.company.Milestone;
import com.freelancing.model.company.Project;
import com.freelancing.model.company.Proposal;
import com.freelancing.model.company.Rating;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.common.AiService;
import com.freelancing.service.common.CalendarService;
import com.freelancing.service.common.ChatService;
import com.freelancing.service.common.CommunityService;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.common.PaymentService;
import com.freelancing.service.company.ClientService;
import com.freelancing.service.company.ContractService;
import com.freelancing.service.company.MilestoneService;
import com.freelancing.service.company.ProjectService;
import com.freelancing.service.company.ProposalService;
import com.freelancing.ui.common.CalendarView;
import com.freelancing.ui.common.HomePage;
import com.freelancing.ui.common.SettingsView;
import com.freelancing.ui.common.UIComponents;

import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;
import com.freelancing.util.AnimationUtil;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.SubScene;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientMainView {
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final AiService aiService = new AiService();
    private final PaymentService paymentService = new PaymentService();
    private final NotificationService notifService = new NotificationService();
    private final ClientService clientService = new ClientService();
    private final ProjectService projectService = new ProjectService();
    private final ProposalService proposalService = new ProposalService();
    private final ContractService contractService = new ContractService();
    private final MilestoneService milestoneService = new MilestoneService();
    private final ChatService chatService = new ChatService();
    private final CalendarService calendarService = new CalendarService();

    private BorderPane root;
    private StackPane contentArea;
    private Button btnNavDash, btnNavPost, btnNavBids, btnNavProps, btnNavSearch, btnNavProgress, btnNavChat,
            btnNavCalendar, btnNavNotif, btnNavFeed, btnNavProfile, btnNavSettings;
    private Button[] navBtns;
    private String selectedChatConversationId = null;
    private boolean isSidebarOpen = true;

    public ClientMainView(User user) {
        this.currentUser = user;
    }

    /** Compatibility constructor for legacy code */
    public ClientMainView(Object ignored, User user) {
        this(user);
    }

    public Parent createContent() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + AppTheme.getBgDark() + ";");

        // Top Header
        HBox header = new HBox(15);
        header.setPadding(new Insets(12, 24, 12, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(AppTheme.getTitleBarStyle());

        Button btnToggleSidebar = new Button("☰");
        btnToggleSidebar.setTooltip(new Tooltip("Toggle / Close Sidebar"));
        btnToggleSidebar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnToggleSidebar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.18); -fx-text-fill: #FFFFFF; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 11; -fx-border-color: rgba(255, 255, 255, 0.35); -fx-border-radius: 6;");
        btnToggleSidebar.setOnMouseEntered(e -> btnToggleSidebar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.32); -fx-text-fill: #FFFFFF; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 11; -fx-border-color: #FFFFFF; -fx-border-radius: 6;"));
        btnToggleSidebar.setOnMouseExited(e -> btnToggleSidebar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.18); -fx-text-fill: #FFFFFF; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 11; -fx-border-color: rgba(255, 255, 255, 0.35); -fx-border-radius: 6;"));

        Label logo = new Label("⚡ SkillBridge  |  Client Portal");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web("#FFFFFF"));
        logo.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 4, 0, 0, 1);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnHome = UIComponents.createTitleBarButton("🏠 SkillBridge Home", () -> HomePage.showHomeView());

        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showClientView(currentUser);
        });

        Label userLabel = new Label("🏢 " + currentUser.getUsername() + " (Verified Client)");
        userLabel.setTextFill(Color.web("#FFFFFF"));
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        userLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.18); -fx-padding: 6 14; -fx-background-radius: 20; -fx-border-color: rgba(255, 255, 255, 0.35); -fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 1);");

        Button btnLogout = UIComponents.createDangerButton("Logout");
        btnLogout.setStyle("-fx-background-color: #DC2626; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-border-color: #EF4444; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;");
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

        btnNavDash = createNavButton("📊 Client Dashboard", true);
        btnNavPost = createNavButton("➕ Post New Project", false);
        btnNavBids = createNavButton("📋 Job Bids & Assign", false);
        btnNavProps = createNavButton("📩 Proposal Review", false);
        btnNavSearch = createNavButton("🔍 Find Freelancers & AI", false);
        btnNavProgress = createNavButton("💳 Milestones & Escrow", false);
        btnNavChat = createNavButton("💬 Chat & Messaging", false);
        btnNavCalendar = createNavButton("📅 Project Calendar", false);
        btnNavNotif = createNavButton("🔔 Notifications", false);
        btnNavFeed = createNavButton("📰 Community Feed", false);
        btnNavProfile = createNavButton("🏢 Company Profile", false);
        btnNavSettings = createNavButton("⚙️ Settings", false);

        navBtns = new Button[] { btnNavDash, btnNavPost, btnNavBids, btnNavProps, btnNavSearch, btnNavProgress,
                btnNavChat, btnNavCalendar, btnNavNotif, btnNavFeed, btnNavProfile, btnNavSettings };

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: " + AppTheme.getBgDark() + ";");

        btnNavDash.setOnAction(e -> {
            selectNav(btnNavDash, navBtns);
            showDashboard();
        });
        btnNavPost.setOnAction(e -> {
            selectNav(btnNavPost, navBtns);
            showPostProject();
        });
        btnNavBids.setOnAction(e -> {
            selectNav(btnNavBids, navBtns);
            showJobBidsAndAssign();
        });
        btnNavProps.setOnAction(e -> {
            selectNav(btnNavProps, navBtns);
            showProposals();
        });
        btnNavSearch.setOnAction(e -> {
            selectNav(btnNavSearch, navBtns);
            showFreelancerSearch();
        });
        btnNavProgress.setOnAction(e -> {
            selectNav(btnNavProgress, navBtns);
            showMilestonesAndPayments();
        });
        btnNavChat.setOnAction(e -> {
            selectNav(btnNavChat, navBtns);
            showChat();
        });
        btnNavCalendar.setOnAction(e -> {
            selectNav(btnNavCalendar, navBtns);
            showCalendar();
        });
        btnNavNotif.setOnAction(e -> {
            selectNav(btnNavNotif, navBtns);
            showNotifications();
        });
        btnNavFeed.setOnAction(e -> {
            selectNav(btnNavFeed, navBtns);
            showFeed();
        });
        btnNavProfile.setOnAction(e -> {
            selectNav(btnNavProfile, navBtns);
            showCompanyProfile();
        });
        btnNavSettings.setOnAction(e -> {
            selectNav(btnNavSettings, navBtns);
            showSettings();
        });

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

        sidebar.getChildren().addAll(sidebarHeader, btnNavDash, btnNavPost, btnNavBids, btnNavProps, btnNavSearch, btnNavProgress,
                new Separator(), btnNavChat, btnNavCalendar, btnNavNotif, btnNavFeed, btnNavProfile, btnNavSettings);

        ScrollPane sidebarScroll = UIComponents.createScrollPane(sidebar);
        sidebarScroll.setStyle(
                "-fx-background: " + AppTheme.getBgPanel() + "; -fx-background-color: " + AppTheme.getBgPanel()
                        + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-width: 0 1 0 0;");
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
        return root;
    }

    public Scene createScene() {
        Parent content = createContent();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1360, bounds.getWidth() * 0.94);
        double sceneHeight = Math.min(880, bounds.getHeight() * 0.94);
        Scene scene = new Scene(content, sceneWidth, sceneHeight);
        AppTheme.applyAppStylesheet(scene);
        return scene;
    }

    private void showCalendar() {
        contentArea.getChildren().clear();
        CalendarView view = new CalendarView(currentUser);
        contentArea.getChildren().add(view);
    }

    private void showSettings() {
        contentArea.getChildren().clear();
        SettingsView view = new SettingsView(currentUser, () -> HomePage.showClientView(currentUser));
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
            btn.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY
                    + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 10 14; -fx-font-weight: bold;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted()
                    + "; -fx-background-radius: 6; -fx-padding: 10 14;");
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
        card.setOnMouseEntered(
                e -> card.setStyle(base + "; -fx-cursor: hand; -fx-scale-x: 1.02; -fx-scale-y: 1.02; -fx-border-color: "
                        + AppTheme.COLOR_PRIMARY + ";"));
        card.setOnMouseExited(e -> card.setStyle(base + "; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
        card.setOnMouseClicked(e -> {
            if (action != null)
                action.run();
        });
    }

    private static class ClientDashboardData {
        final List<Project> clientProjects;
        final double spent;
        final long activeContractsCount;
        final int totalBids;
        final Map<String, Double> catMap;

        ClientDashboardData(List<Project> clientProjects, double spent, long activeContractsCount, int totalBids, Map<String, Double> catMap) {
            this.clientProjects = clientProjects;
            this.spent = spent;
            this.activeContractsCount = activeContractsCount;
            this.totalBids = totalBids;
            this.catMap = catMap;
        }
    }

    // 1. Dashboard View with Budget Distribution PieChart (Multithreaded background data loading)
    private void showDashboard() {
        contentArea.getChildren().setAll(AnimationUtil.createLoadingOverlay("Loading your client dashboard & metrics..."));

        AnimationUtil.runAsync(
            () -> {
                ClientProfile cp = clientService.getProfile(currentUser.getId());
                double spent = cp != null ? cp.getTotalSpent() : 0.0;

                List<Project> clientProjects = projectService.getProjectsByClient(currentUser.getId());
                List<Contract> clientContracts = contractService.getContractsByClientUserId(currentUser.getId());
                long activeContractsCount = clientContracts.stream().filter(c -> c.getStatus() == Contract.Status.ACTIVE).count();
                if (spent <= 0.0) {
                    spent = clientContracts.stream().filter(c -> c.getStatus() == Contract.Status.COMPLETED).mapToDouble(Contract::getTotalAmount).sum();
                }

                int totalBids = 0;
                for (Project p : clientProjects) {
                    totalBids += p.getProposalsCount();
                }
                if (totalBids == 0) {
                    totalBids = db.countBidsForClient(currentUser.getId());
                }

                Map<String, Double> catMap = new HashMap<>();
                for (Project p : clientProjects) {
                    String cat = p.getCategory() != null ? p.getCategory() : "Other";
                    catMap.put(cat, catMap.getOrDefault(cat, 0.0) + p.getBudget());
                }
                if (catMap.isEmpty()) {
                    catMap.put("Desktop Dev", 4500.0);
                    catMap.put("AI/ML", 3500.0);
                }

                return new ClientDashboardData(clientProjects, spent, activeContractsCount, totalBids, catMap);
            },
            data -> {
                int totalProjectsCount = data.clientProjects.size();

                VBox box = new VBox(20);
                Label title = UIComponents.createTitle("Client Dashboard");

                HBox statGrid = new HBox(15);
                VBox card1 = UIComponents.createStatCard(0, "📢", "Projects Posted", String.valueOf(totalProjectsCount),
                        UIComponents.COLOR_PRIMARY);
                VBox card2 = UIComponents.createStatCard(1, "🤝", "Active Contracts",
                        String.valueOf(data.activeContractsCount), UIComponents.COLOR_SUCCESS);
                VBox card3 = UIComponents.createStatCard(2, "💰", "Total Capital Spent", "$" + String.format("%.2f", data.spent),
                        UIComponents.COLOR_AMBER);
                VBox card4 = UIComponents.createStatCard(3, "🎯", "Bids Received", String.valueOf(data.totalBids),
                        UIComponents.COLOR_PURPLE);

                makeStatCardInteractive(card1, () -> {
                    selectNav(btnNavPost, navBtns);
                    showPostProject();
                });
                makeStatCardInteractive(card2, () -> {
                    selectNav(btnNavProgress, navBtns);
                    showMilestonesAndPayments();
                });
                makeStatCardInteractive(card3, () -> {
                    selectNav(btnNavProgress, navBtns);
                    showMilestonesAndPayments();
                });
                makeStatCardInteractive(card4, () -> {
                    selectNav(btnNavBids, navBtns);
                    showJobBidsAndAssign();
                });

                statGrid.getChildren().addAll(card1, card2, card3, card4);

                PieChart chart = UIComponents.createPieChart("📊 Project Capital Allocation by Category", data.catMap);
                VBox chartCard = UIComponents.createCard();
                chartCard.getChildren().addAll(UIComponents.createHeader("Capital Allocation Analysis"), chart);

                VBox projCard = UIComponents.createCard();
                HBox pHeader = new HBox(10);
                pHeader.setAlignment(Pos.CENTER_LEFT);
                Label pHeaderLbl = UIComponents.createHeader("📁 Your Posted Projects (" + totalProjectsCount + ")");
                Region pSp = new Region();
                HBox.setHgrow(pSp, Priority.ALWAYS);
                Button btnNewProj = UIComponents.createPrimaryButton("➕ Post Project");
                btnNewProj.setOnAction(e -> {
                    selectNav(btnNavPost, navBtns);
                    showPostProject();
                });
                pHeader.getChildren().addAll(pHeaderLbl, pSp, btnNewProj);
                projCard.getChildren().add(pHeader);

                if (data.clientProjects.isEmpty()) {
                    VBox emptyBox = new VBox(10);
                    emptyBox.setAlignment(Pos.CENTER);
                    emptyBox.setPadding(new Insets(30));
                    Label emptyLbl = new Label("No projects posted yet. Click 'Post Project' to create your first brief!");
                    emptyLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                    emptyLbl.setFont(Font.font("Segoe UI", 14));
                    emptyBox.getChildren().addAll(emptyLbl, btnNewProj);
                    projCard.getChildren().add(emptyBox);
                } else {
                    for (Project p : data.clientProjects) {

                        HBox row = new HBox(15);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setPadding(new Insets(12));
                        row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT
                                + "; -fx-background-radius: 8; -fx-cursor: hand;");
                        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT
                                + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-scale-x: 1.01; -fx-scale-y: 1.01;"));
                        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT
                                + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
                        row.setOnMouseClicked(e -> {
                            selectNav(btnNavBids, navBtns);
                            showJobBidsAndAssign();
                        });

                        VBox det = new VBox(4);
                        Label pTitle = new Label(p.getTitle());
                        pTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                        pTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                        Label pMeta = new Label("Category: " + p.getCategory() + " | Budget: " + p.getBudgetDisplay()
                                + " | Level: " + p.getExperienceLevel());
                        pMeta.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                        det.getChildren().addAll(pTitle, pMeta);

                        Region sp = new Region();
                        HBox.setHgrow(sp, Priority.ALWAYS);

                        Label badge = UIComponents.createBadge(p.getStatus().toString(), UIComponents.COLOR_PRIMARY, "white");
                        row.getChildren().addAll(det, sp, badge);
                        projCard.getChildren().add(row);
                    }
                }

                box.getChildren().addAll(title, statGrid, chartCard, projCard);
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
        TextArea taPostContent = UIComponents
                .createTextArea("Share your thoughts, post hiring needs, or start a discussion...");
        taPostContent.setPrefRowCount(4);

        ComboBox<String> cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("Showcase", "Hiring", "Discussion", "Feedback");
        cbCategory.getSelectionModel().select(1);
        cbCategory.setMaxWidth(Double.MAX_VALUE);
        UIComponents.styleComboBox(cbCategory);

        Button btnPost = UIComponents.createPrimaryButton("🚀 Publish Post");
        btnPost.setOnAction(e -> {
            String pTitle = tfPostTitle.getText().trim();
            String pContent = taPostContent.getText().trim();
            if (pTitle.isEmpty() || pContent.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Post", "Missing Fields",
                        "Please enter both a title and content for your post.");
                return;
            }
            String postId = "feed_" + System.currentTimeMillis();
            String catStr = cbCategory.getValue();
            FeedPost.PostCategory cat = FeedPost.PostCategory.valueOf(catStr.toUpperCase());
            String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());

            FeedPost post = new FeedPost(postId, currentUser.getId(), currentUser.getUsername(), "CLIENT",
                    pTitle, pContent, cat, FeedPost.PostStatus.ACTIVE, ts);
            db.getFeedPosts().put(postId, post);
            db.logActivity("Client " + currentUser.getUsername() + " published feed post: " + pTitle);
            db.saveData();

            try {
                CommunityService cs = new CommunityService();
                cs.createPost(currentUser.getId(), pTitle, pContent, catStr);
            } catch (Exception ignored) {}

            tfPostTitle.clear();
            taPostContent.clear();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Post Published", "Success",
                    "Your post is now live on the community feed!");
            showFeed();
        });

        createCard.getChildren().addAll(
                new Label("Category:"), cbCategory,
                new Label("Title:"), tfPostTitle,
                new Label("Content:"), taPostContent,
                btnPost);

        // Feed Timeline (cached & indexed)
        VBox feedTimeline = new VBox(12);
        java.util.List<FeedPost> posts = db.getActiveFeedPostsSorted();

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
        Label authorLbl = new Label(
                ("FREELANCER".equals(post.getAuthorRole()) ? "👤" : "🏢") + " " + post.getAuthorName());
        authorLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        authorLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

        Label roleBadge = UIComponents.createBadge(post.getAuthorRole(), UIComponents.COLOR_PRIMARY, "white");

        // Use distinct badge for JOB_POST
        String catDisplay = post.getCategory() == FeedPost.PostCategory.JOB_POST ? "💼 JOB POST"
                : post.getCategory().toString();
        String catColor = post.getCategory() == FeedPost.PostCategory.JOB_POST ? UIComponents.COLOR_SUCCESS
                : UIComponents.COLOR_PURPLE;
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
                projInfoBar.setStyle("-fx-background-color: #1a2744; -fx-background-radius: 8; -fx-border-color: "
                        + UIComponents.COLOR_PRIMARY + "; -fx-border-radius: 8; -fx-border-width: 1;");

                Label budgetLbl = new Label("💰 $" + String.format("%.0f", linkedProj.getBudget()));
                budgetLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                budgetLbl.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));

                Label deadlineLbl = new Label("📅 " + linkedProj.getDeadline());
                deadlineLbl.setFont(Font.font("Segoe UI", 12));
                deadlineLbl.setTextFill(Color.web(UIComponents.COLOR_AMBER));

                Label statusLbl = UIComponents.createBadge(linkedProj.getStatus().toString(),
                        UIComponents.COLOR_PRIMARY, "white");

                // Skills as badges
                HBox skillsRow = new HBox(4);
                for (String skill : linkedProj.getRequiredSkills()) {
                    Label skillBadge = UIComponents.createBadge(skill, UIComponents.COLOR_BG_INPUT,
                            UIComponents.COLOR_TEXT_PRIMARY);
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

        // Show bids count for JOB_POST
        if (post.getCategory() == FeedPost.PostCategory.JOB_POST) {
            Label bidCountBadge = UIComponents.createBadge("🎯 " + post.getBids().size() + " Bids",
                    UIComponents.COLOR_AMBER, "white");
            actionBar.getChildren().add(bidCountBadge);
        }

        Button btnFeedback = UIComponents.createSuccessButton("⭐ Give Feedback");
        btnFeedback.setOnAction(e -> {
            if (post.getAuthorId().equals(currentUser.getId())) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Feedback", "Not Allowed",
                        "You cannot give feedback on your own post.");
                return;
            }
            UIComponents.showPromptDialog("Give Feedback", "Write feedback for " + post.getAuthorName(), "Your feedback:", "", feedback -> {
                if (feedback != null && !feedback.trim().isEmpty()) {
                    String rId = "rating_" + System.currentTimeMillis();
                    String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
                    Rating rating = new Rating(rId, "", currentUser.getId(), currentUser.getUsername(),
                            post.getAuthorId(), 5.0, feedback.trim(), ts);
                    db.getRatings().put(rId, rating);
                    db.logActivity(currentUser.getUsername() + " gave feedback to " + post.getAuthorName());
                    db.saveData();
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Feedback Sent", "Thank You",
                            "Your feedback has been sent to " + post.getAuthorName() + "!");
                }
            });
        });

        // Only show feedback button for non-own posts
        if (!post.getAuthorId().equals(currentUser.getId())) {
            actionBar.getChildren().add(btnFeedback);
        }

        card.getChildren().add(actionBar);

        // Bids section for JOB_POST (visible to post owner — the client)
        if (post.getCategory() == FeedPost.PostCategory.JOB_POST
                && post.getAuthorId().equals(currentUser.getId())
                && !post.getBids().isEmpty()) {

            VBox bidsSection = new VBox(8);
            bidsSection.setPadding(new Insets(10, 0, 0, 0));

            Label bidsHeader = new Label("📋 Bids Received (" + post.getBids().size() + "):");
            bidsHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            bidsHeader.setTextFill(Color.web(UIComponents.COLOR_AMBER));
            bidsSection.getChildren().add(bidsHeader);

            Project linkedProj = post.getLinkedProjectId() != null ? db.getProjects().get(post.getLinkedProjectId())
                    : null;

            for (FeedPost.FeedBid bid : post.getBids()) {
                VBox bidCard = new VBox(6);
                bidCard.setPadding(new Insets(10, 12, 10, 12));
                bidCard.setStyle("-fx-background-color: #1a2744; -fx-background-radius: 8; -fx-border-color: "
                        + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8;");

                // Bid header: name, amount, days, timestamp
                HBox bidTop = new HBox(10);
                bidTop.setAlignment(Pos.CENTER_LEFT);
                Label bidderName = new Label("👤 " + bid.getFreelancerName());
                bidderName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                bidderName.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label bidAmount = UIComponents.createBadge("$" + String.format("%.0f", bid.getBidAmount()),
                        UIComponents.COLOR_SUCCESS, "white");
                Label bidDays = UIComponents.createBadge(bid.getEstimatedDays() + " days", UIComponents.COLOR_PRIMARY,
                        "white");

                Region bidSp = new Region();
                HBox.setHgrow(bidSp, Priority.ALWAYS);

                Label bidTime = new Label("🕒 Bid placed: " + bid.getTimestamp());
                bidTime.setFont(Font.font("Segoe UI", 11));
                bidTime.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                bidTop.getChildren().addAll(bidderName, bidAmount, bidDays, bidSp, bidTime);

                // Skills row
                HBox skillsRow = new HBox(4);
                skillsRow.setPadding(new Insets(2, 0, 2, 0));
                for (String skill : bid.getFreelancerSkills()) {
                    String skillColor = UIComponents.COLOR_BG_INPUT;
                    // Highlight matching skills
                    if (linkedProj != null && linkedProj.getRequiredSkills().stream()
                            .anyMatch(rs -> rs.equalsIgnoreCase(skill))) {
                        skillColor = UIComponents.COLOR_SUCCESS;
                    }
                    Label skillBadge = UIComponents.createBadge(skill, skillColor, "white");
                    skillsRow.getChildren().add(skillBadge);
                }

                // Skill match indicator
                if (linkedProj != null) {
                    long matchCount = bid.getFreelancerSkills().stream()
                            .filter(s -> linkedProj.getRequiredSkills().stream().anyMatch(rs -> rs.equalsIgnoreCase(s)))
                            .count();
                    int matchPct = linkedProj.getRequiredSkills().isEmpty() ? 0
                            : (int) ((matchCount * 100) / linkedProj.getRequiredSkills().size());
                    Label matchBadge = UIComponents.createBadge(matchPct + "% Skill Match",
                            matchPct >= 50 ? UIComponents.COLOR_SUCCESS : UIComponents.COLOR_AMBER, "white");
                    skillsRow.getChildren().add(matchBadge);
                }

                // Cover letter preview
                Label coverLbl = new Label(bid.getCoverLetter());
                coverLbl.setWrapText(true);
                coverLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                coverLbl.setFont(Font.font("Segoe UI", 12));

                bidCard.getChildren().addAll(bidTop, skillsRow, coverLbl);

                // Assign button (only if project is still OPEN)
                if (linkedProj != null && linkedProj.getStatus() == Project.Status.OPEN) {
                    Button btnAssign = UIComponents.createSuccessButton("✅ Assign " + bid.getFreelancerName());
                    btnAssign.setOnAction(e -> assignFreelancerFromBid(post, bid, linkedProj));
                    bidCard.getChildren().add(btnAssign);
                } else if (linkedProj != null && linkedProj.getAssignedFreelancerId() != null
                        && linkedProj.getAssignedFreelancerId().equals(bid.getFreelancerId())) {
                    Label assignedBadge = UIComponents.createBadge("✅ ASSIGNED", UIComponents.COLOR_SUCCESS, "white");
                    bidCard.getChildren().add(assignedBadge);
                }

                bidsSection.getChildren().add(bidCard);
            }

            card.getChildren().add(bidsSection);
        }

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
                post.addComment(
                        new FeedPost.FeedComment(cmtId, currentUser.getId(), currentUser.getUsername(), cmtText, ts));
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

    // Assign freelancer from a bid
    private void assignFreelancerFromBid(FeedPost post, FeedPost.FeedBid bid, Project project) {
        project.setStatus(Project.Status.IN_PROGRESS);
        project.setAssignedFreelancerId(bid.getFreelancerId());
        project.setAssignedFreelancerName(bid.getFreelancerName());

        // Create initial milestone
        Milestone ms = new Milestone("ms_" + System.currentTimeMillis(), project.getId(),
                "Project Deliverables & Source Code Handover",
                "Complete project build and documentation",
                bid.getBidAmount(), project.getDeadline(), Milestone.Status.IN_PROGRESS);
        db.getMilestones().put(ms.getId(), ms);

        // Also create a formal Proposal record
        String propId = "prop_" + System.currentTimeMillis();
        Proposal prop = new Proposal(propId, project.getId(), project.getTitle(),
                bid.getFreelancerId(), bid.getFreelancerName(),
                bid.getCoverLetter(), bid.getBidAmount(), bid.getEstimatedDays(),
                Proposal.Status.ACCEPTED,
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        db.getProposals().put(propId, prop);

        db.getProjects().put(project.getId(), project);

        // Send notification
        notifService.sendNotification(bid.getFreelancerId(), "🎉 You're Hired!",
                currentUser.getUsername() + " has assigned you to project '" + project.getTitle()
                        + "' based on your bid of $" + String.format("%.0f", bid.getBidAmount()) + ".");

        db.logActivitySilent("Client " + currentUser.getUsername() + " assigned freelancer " + bid.getFreelancerName()
                + " to project " + project.getTitle() + " (from bid)");
        db.invalidateCaches();
        db.saveData();

        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Freelancer Assigned", "Success!",
                bid.getFreelancerName() + " has been assigned to '" + project.getTitle()
                        + "'!\n\nBid Amount: $" + String.format("%.0f", bid.getBidAmount())
                        + "\nEstimated Delivery: " + bid.getEstimatedDays() + " days"
                        + "\nMilestone contract initialized.");

        showFeed();
    }

    public int getPostedProjectCount() {
        return (int) db.getProjects().values().stream().filter(p -> p.getClientId().equals(currentUser.getId()))
                .count();
    }

    public int getActiveContractCount() {
        return (int) db.getProjects().values().stream()
                .filter(p -> p.getClientId().equals(currentUser.getId()) && p.getStatus() == Project.Status.IN_PROGRESS)
                .count();
    }

    // 2. Post New Project Wizard with Skills Picker, Budget Range & SQLite
    // Persistence
    private void showPostProject() {
        VBox box = new VBox(20);
        Label title = UIComponents.createTitle("⚡ Post a New Project Brief");
        Label subtitle = new Label(
                "Define requirements, select tech stack skills, set milestone budget, and publish to the SkillBridge marketplace.");
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));
        subtitle.setFont(Font.font("Segoe UI", 13));

        // 1. Identity & Domain
        VBox idCard = UIComponents.createCard();
        idCard.getChildren().add(UIComponents.createHeader("1. Project Title & Category"));

        TextField tfTitle = UIComponents
                .createTextField("Project Title (e.g. Enterprise Financial Terminal in JavaFX)...");
        tfTitle.setPrefHeight(38);

        HBox metaRow = new HBox(15);
        VBox catBox = new VBox(6);
        catBox.getChildren().add(new Label("Industry Category:"));
        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll("Desktop Development", "Software & Desktop App", "Web Development",
                "Mobile Development", "AI & Machine Learning", "Data & AI", "UI/UX Design", "Cloud & DevOps",
                "Cybersecurity", "Database");
        cbCat.getSelectionModel().select(0);
        cbCat.setMaxWidth(Double.MAX_VALUE);
        UIComponents.styleComboBox(cbCat);
        catBox.getChildren().add(cbCat);
        HBox.setHgrow(catBox, Priority.ALWAYS);

        VBox expBox = new VBox(6);
        expBox.getChildren().add(new Label("Experience Level Required:"));
        ComboBox<String> cbExp = new ComboBox<>();
        cbExp.getItems().addAll("INTERMEDIATE", "ENTRY", "EXPERT");
        cbExp.getSelectionModel().select("INTERMEDIATE");
        cbExp.setMaxWidth(Double.MAX_VALUE);
        UIComponents.styleComboBox(cbExp);
        expBox.getChildren().add(cbExp);
        HBox.setHgrow(expBox, Priority.ALWAYS);

        metaRow.getChildren().addAll(catBox, expBox);
        idCard.getChildren().addAll(new Label("Project Title:"), tfTitle, metaRow);

        // 2. Requirements & AI Scope Generator
        VBox reqCard = UIComponents.createCard();
        reqCard.getChildren().add(UIComponents.createHeader("2. Technical Scope & Deliverables"));

        TextArea taDesc = UIComponents.createTextArea(
                "Detail technical requirements, architecture constraints, and key project milestones...");
        taDesc.setPrefRowCount(7);

        Button btnAiGen = UIComponents.createSecondaryButton("🤖 AI Generate Detailed Technical Scope");
        btnAiGen.setOnAction(e -> {
            String pTitle = tfTitle.getText().trim();
            String cat = cbCat.getValue();
            if (pTitle.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "AI Scope Assistant", "Title Required",
                        "Please enter a project title first so the AI can generate accurate technical deliverables.");
                return;
            }
            String generated = aiService.generateProjectDescription(pTitle, cat, taDesc.getText());
            taDesc.setText(generated);
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "AI Scope Assistant", "Specification Generated",
                    "AI successfully drafted structured milestones, technical stack requirements, and acceptance criteria.");
        });

        reqCard.getChildren().addAll(new Label("Detailed Description:"), taDesc, btnAiGen);

        // 3. Skills Picker & Tags
        VBox skillsCard = UIComponents.createCard();
        skillsCard.getChildren().add(UIComponents.createHeader("3. Required Skills & Technologies"));

        Label skillNote = new Label(
                "Click skills below to toggle required tags or type custom tags separated by commas:");
        skillNote.setTextFill(Color.web(AppTheme.getTextMuted()));
        skillNote.setFont(Font.font("Segoe UI", 12));

        FlowPane chipsPane = new FlowPane(8, 8);
        chipsPane.setPadding(new Insets(6, 0, 10, 0));

        java.util.Set<String> selectedSkills = new java.util.LinkedHashSet<>();
        String[] popularSkills = { "Java 17", "JavaFX", "SQLite", "Python", "Machine Learning", "Spring Boot",
                "REST APIs", "Docker", "UI/UX Design", "Concurrency" };
        for (String skillName : popularSkills) {
            Button chip = new Button("+ " + skillName);
            chip.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
            chip.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: "
                    + AppTheme.getTextPrimary() + "; -fx-background-radius: 14; -fx-padding: 5 12; -fx-cursor: hand;");
            chip.setOnAction(e -> {
                if (selectedSkills.contains(skillName)) {
                    selectedSkills.remove(skillName);
                    chip.setText("+ " + skillName);
                    chip.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: "
                            + AppTheme.getTextPrimary()
                            + "; -fx-background-radius: 14; -fx-padding: 5 12; -fx-cursor: hand;");
                } else {
                    selectedSkills.add(skillName);
                    chip.setText("✓ " + skillName);
                    chip.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY
                            + "; -fx-text-fill: white; -fx-background-radius: 14; -fx-padding: 5 12; -fx-cursor: hand; -fx-font-weight: bold;");
                }
            });
            chipsPane.getChildren().add(chip);
        }

        TextField tfCustomSkills = UIComponents
                .createTextField("Additional skills (comma separated, e.g. CSS, Git, Maven)...");
        skillsCard.getChildren().addAll(skillNote, chipsPane, new Label("Custom Skills:"), tfCustomSkills);

        // 4. Budget & Timeline
        VBox budgetCard = UIComponents.createCard();
        budgetCard.getChildren().add(UIComponents.createHeader("4. Budget Type, Compensation & Timeline"));

        HBox budgetRow = new HBox(15);

        VBox typeBox = new VBox(6);
        typeBox.getChildren().add(new Label("Pricing Structure:"));
        ComboBox<String> cbBudgetType = new ComboBox<>();
        cbBudgetType.getItems().addAll("FIXED", "HOURLY");
        cbBudgetType.getSelectionModel().select("FIXED");
        UIComponents.styleComboBox(cbBudgetType);
        typeBox.getChildren().add(cbBudgetType);
        HBox.setHgrow(typeBox, Priority.ALWAYS);

        VBox minBox = new VBox(6);
        minBox.getChildren().add(new Label("Minimum Budget ($):"));
        TextField tfBudgetMin = UIComponents.createTextField("e.g. 2500");
        minBox.getChildren().add(tfBudgetMin);
        HBox.setHgrow(minBox, Priority.ALWAYS);

        VBox maxBox = new VBox(6);
        maxBox.getChildren().add(new Label("Maximum Budget ($):"));
        TextField tfBudgetMax = UIComponents.createTextField("e.g. 4000");
        maxBox.getChildren().add(tfBudgetMax);
        HBox.setHgrow(maxBox, Priority.ALWAYS);

        VBox dlBox = new VBox(6);
        dlBox.getChildren().add(new Label("Target Deadline (YYYY-MM-DD):"));
        TextField tfDeadline = UIComponents.createTextField("2026-10-31");
        dlBox.getChildren().add(tfDeadline);
        HBox.setHgrow(dlBox, Priority.ALWAYS);

        budgetRow.getChildren().addAll(typeBox, minBox, maxBox, dlBox);
        budgetCard.getChildren().addAll(budgetRow);

        // Submit Action Button
        Button btnPost = UIComponents.createPrimaryButton("🚀 Publish Project to SkillBridge Marketplace");
        btnPost.setPrefHeight(44);
        btnPost.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnPost.setMaxWidth(Double.MAX_VALUE);

        btnPost.setOnAction(e -> {
            String pTitle = tfTitle.getText().trim();
            String desc = taDesc.getText().trim();
            String cat = cbCat.getValue();
            String exp = cbExp.getValue();
            String bType = cbBudgetType.getValue();
            String minStr = tfBudgetMin.getText().trim();
            String maxStr = tfBudgetMax.getText().trim();
            String dl = tfDeadline.getText().trim();

            if (pTitle.isEmpty() || desc.isEmpty() || maxStr.isEmpty() || dl.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Validation Error", "Incomplete Fields",
                        "Please provide a title, technical description, maximum budget, and target deadline.");
                return;
            }

            try {
                double bMax = Double.parseDouble(maxStr);
                double bMin = minStr.isEmpty() ? bMax * 0.75 : Double.parseDouble(minStr);

                if (bMin > bMax) {
                    double tmp = bMin;
                    bMin = bMax;
                    bMax = tmp;
                }

                // Gather skills
                List<String> allSkills = new ArrayList<>(selectedSkills);
                String custom = tfCustomSkills.getText().trim();
                if (!custom.isEmpty()) {
                    for (String s : custom.split(",")) {
                        String clean = s.trim();
                        if (!clean.isEmpty() && !allSkills.contains(clean)) {
                            allSkills.add(clean);
                        }
                    }
                }

                String projId = "proj_" + UUID.randomUUID().toString().substring(0, 8);
                String nowDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

                Project project = new Project();
                project.setId(projId);
                project.setClientId(currentUser.getId());
                project.setClientName(currentUser.getUsername());
                project.setTitle(pTitle);
                project.setDescription(desc);
                project.setCategory(cat);
                project.setBudgetType(bType);
                project.setBudgetMin(bMin);
                project.setBudgetMax(bMax);
                project.setBudget(bMax);
                project.setDeadline(dl);
                project.setExperienceLevel(exp);
                project.setStatus(Project.Status.OPEN);
                project.setCreatedAt(nowDate);

                // Persist into SQLite via ProjectService
                projectService.createProject(project, allSkills);

                // Auto-create community feed post
                String feedId = "feed_job_" + System.currentTimeMillis();
                StringBuilder jobContent = new StringBuilder();
                jobContent.append(desc).append("\n\n");
                jobContent.append("💰 Budget: ").append(project.getBudgetDisplay());
                jobContent.append("\n📅 Deadline: ").append(dl);
                jobContent.append("\n🎯 Level: ").append(exp);
                if (!allSkills.isEmpty()) {
                    jobContent.append("\n🛠 Tech Stack: ").append(String.join(", ", allSkills));
                }
                jobContent.append("\n📂 Category: ").append(cat);

                FeedPost jobPost = new FeedPost(feedId, currentUser.getId(), currentUser.getUsername(), "CLIENT",
                        "💼 " + pTitle, jobContent.toString(),
                        FeedPost.PostCategory.JOB_POST, FeedPost.PostStatus.ACTIVE,
                        new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
                jobPost.setLinkedProjectId(projId);
                db.getFeedPosts().put(feedId, jobPost);

                notifService.sendNotification(currentUser.getId(), "Project Published",
                        "Your project '" + pTitle + "' is live in the SkillBridge SQLite Marketplace!");

                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Project Published", "Live in Marketplace",
                        "Your project brief has been successfully persisted to SQLite and published to the marketplace!\n\nFreelancers can now discover, inspect, and submit proposals for it.");

                selectNav(btnNavDash, navBtns);
                showDashboard();
            } catch (NumberFormatException nfe) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Budget Number",
                        "Please enter valid numeric figures for the project budget values.");
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Database Error", "Publication Failed",
                        "Could not save project to SQLite: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        box.getChildren().addAll(title, subtitle, idCard, reqCard, skillsCard, budgetCard, btnPost);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 3. Job Bids & Assign — Dedicated view for reviewing bids and assigning
    // freelancers
    private void showJobBidsAndAssign() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📋 Job Bids & Freelancer Assignment");
        Label subtitle = new Label(
                "Review all bids on your job posts, compare freelancer skills, and assign the best candidate.");
        subtitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        subtitle.setWrapText(true);
        subtitle.setFont(Font.font("Segoe UI", 13));

        VBox jobList = new VBox(15);

        // Get all JOB_POST feed posts by this client (indexed & cached)
        List<FeedPost> myJobPosts = db.getJobPostsByClient(currentUser.getId());

        if (myJobPosts.isEmpty()) {
            VBox emptyCard = UIComponents.createCard();
            Label emptyLbl = new Label(
                    "You haven't posted any jobs yet. Go to 'Post New Project' to create a job that freelancers can bid on!");
            emptyLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            emptyLbl.setWrapText(true);
            emptyCard.getChildren().add(emptyLbl);
            jobList.getChildren().add(emptyCard);
        } else {
            for (FeedPost jobPost : myJobPosts) {
                Project linkedProj = jobPost.getLinkedProjectId() != null
                        ? db.getProjects().get(jobPost.getLinkedProjectId())
                        : null;

                VBox jobCard = UIComponents.createCard();

                // Job header
                HBox jobHeader = new HBox(10);
                jobHeader.setAlignment(Pos.CENTER_LEFT);
                Label jobTitle = new Label(jobPost.getTitle());
                jobTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
                jobTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                jobTitle.setWrapText(true);

                Region hsp = new Region();
                HBox.setHgrow(hsp, Priority.ALWAYS);

                Label statusBadge;
                if (linkedProj != null && linkedProj.getStatus() == Project.Status.IN_PROGRESS) {
                    statusBadge = UIComponents.createBadge("✅ ASSIGNED", UIComponents.COLOR_SUCCESS, "white");
                } else {
                    statusBadge = UIComponents.createBadge("🟢 OPEN", UIComponents.COLOR_PRIMARY, "white");
                }

                Label bidsCountBadge = UIComponents.createBadge("🎯 " + jobPost.getBids().size() + " Bids",
                        UIComponents.COLOR_AMBER, "white");
                Label likesCountBadge = UIComponents.createBadge("👍 " + jobPost.getLikes() + " Likes",
                        UIComponents.COLOR_BG_INPUT, UIComponents.COLOR_TEXT_PRIMARY);
                Label commentsCountBadge = UIComponents.createBadge("💬 " + jobPost.getComments().size(),
                        UIComponents.COLOR_BG_INPUT, UIComponents.COLOR_TEXT_PRIMARY);

                jobHeader.getChildren().addAll(jobTitle, hsp, likesCountBadge, commentsCountBadge, bidsCountBadge,
                        statusBadge);

                // Project details row
                HBox detailsRow = new HBox(12);
                detailsRow.setPadding(new Insets(6, 0, 6, 0));
                if (linkedProj != null) {
                    Label budgetLbl = new Label("💰 Budget: $" + String.format("%.0f", linkedProj.getBudget()));
                    budgetLbl.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));
                    budgetLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                    Label deadlineLbl = new Label("📅 Deadline: " + linkedProj.getDeadline());
                    deadlineLbl.setTextFill(Color.web(UIComponents.COLOR_AMBER));
                    deadlineLbl.setFont(Font.font("Segoe UI", 12));

                    Label catLbl = new Label("📂 " + linkedProj.getCategory());
                    catLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                    Label postedLbl = new Label("🕒 Posted: " + jobPost.getTimestamp());
                    postedLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                    postedLbl.setFont(Font.font("Segoe UI", 11));

                    detailsRow.getChildren().addAll(budgetLbl, deadlineLbl, catLbl, postedLbl);
                }

                // Required skills
                if (linkedProj != null && !linkedProj.getRequiredSkills().isEmpty()) {
                    HBox reqSkills = new HBox(4);
                    reqSkills.setAlignment(Pos.CENTER_LEFT);
                    Label reqLbl = new Label("Required: ");
                    reqLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                    reqLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                    reqSkills.getChildren().add(reqLbl);
                    for (String sk : linkedProj.getRequiredSkills()) {
                        reqSkills.getChildren().add(UIComponents.createBadge(sk, UIComponents.COLOR_PRIMARY, "white"));
                    }
                    jobCard.getChildren().addAll(jobHeader, detailsRow, reqSkills);
                } else {
                    jobCard.getChildren().addAll(jobHeader, detailsRow);
                }

                // Bids list
                if (jobPost.getBids().isEmpty()) {
                    Label noBids = new Label(
                            "No bids yet. Freelancers will see this job in their feed and can place bids.");
                    noBids.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                    noBids.setFont(Font.font("Segoe UI", 12));
                    jobCard.getChildren().add(noBids);
                } else {
                    Label bidsLabel = new Label("📋 Freelancer Bids:");
                    bidsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                    bidsLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                    jobCard.getChildren().add(bidsLabel);

                    for (FeedPost.FeedBid bid : jobPost.getBids()) {
                        VBox bidCard = new VBox(6);
                        bidCard.setPadding(new Insets(12));
                        bidCard.setStyle(
                                "-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 8;");

                        // Top row: name + amount + days + timestamp
                        HBox bidTop = new HBox(10);
                        bidTop.setAlignment(Pos.CENTER_LEFT);

                        Label nameLabel = new Label("👤 " + bid.getFreelancerName());
                        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                        nameLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                        Label amtBadge = UIComponents.createBadge("💰 $" + String.format("%.0f", bid.getBidAmount()),
                                UIComponents.COLOR_SUCCESS, "white");
                        Label daysBadge = UIComponents.createBadge("📅 " + bid.getEstimatedDays() + " days",
                                UIComponents.COLOR_PRIMARY, "white");

                        Region bidSpacer = new Region();
                        HBox.setHgrow(bidSpacer, Priority.ALWAYS);

                        Label timestampLbl = new Label("🕒 " + bid.getTimestamp());
                        timestampLbl.setFont(Font.font("Segoe UI", 11));
                        timestampLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                        bidTop.getChildren().addAll(nameLabel, amtBadge, daysBadge, bidSpacer, timestampLbl);

                        // Skills row with match highlighting
                        HBox skillsRow = new HBox(4);
                        skillsRow.setPadding(new Insets(4, 0, 4, 0));
                        int matchedSkills = 0;
                        for (String skill : bid.getFreelancerSkills()) {
                            boolean isMatch = linkedProj != null && linkedProj.getRequiredSkills().stream()
                                    .anyMatch(rs -> rs.equalsIgnoreCase(skill));
                            if (isMatch)
                                matchedSkills++;
                            Label skillBadge = UIComponents.createBadge(
                                    (isMatch ? "✓ " : "") + skill,
                                    isMatch ? UIComponents.COLOR_SUCCESS : "#2a3a5c",
                                    "white");
                            skillsRow.getChildren().add(skillBadge);
                        }

                        // Match percentage
                        int matchPct = (linkedProj != null && !linkedProj.getRequiredSkills().isEmpty())
                                ? (matchedSkills * 100 / linkedProj.getRequiredSkills().size())
                                : 0;
                        Label matchLabel = UIComponents.createBadge(matchPct + "% Match",
                                matchPct >= 75 ? UIComponents.COLOR_SUCCESS
                                        : matchPct >= 50 ? UIComponents.COLOR_AMBER : UIComponents.COLOR_DANGER,
                                "white");
                        skillsRow.getChildren().add(matchLabel);

                        // Cover letter
                        Label coverLetter = new Label("📝 " + bid.getCoverLetter());
                        coverLetter.setWrapText(true);
                        coverLetter.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                        coverLetter.setFont(Font.font("Segoe UI", 12));

                        bidCard.getChildren().addAll(bidTop, skillsRow, coverLetter);

                        // Assign button
                        if (linkedProj != null && linkedProj.getStatus() == Project.Status.OPEN) {
                            Button btnAssign = UIComponents
                                    .createSuccessButton("✅ Assign " + bid.getFreelancerName() + " to Project");
                            btnAssign.setOnAction(ev -> {
                                assignFreelancerFromBid(jobPost, bid, linkedProj);
                                showJobBidsAndAssign(); // refresh
                            });
                            bidCard.getChildren().add(btnAssign);
                        } else if (linkedProj != null && linkedProj.getAssignedFreelancerId() != null
                                && linkedProj.getAssignedFreelancerId().equals(bid.getFreelancerId())) {
                            Label assignedLabel = UIComponents.createBadge("✅ ASSIGNED TO THIS FREELANCER",
                                    UIComponents.COLOR_SUCCESS, "white");
                            bidCard.getChildren().add(assignedLabel);
                        }

                        jobCard.getChildren().add(bidCard);
                    }
                }

                jobList.getChildren().add(jobCard);
            }
        }

        box.getChildren().addAll(title, subtitle, jobList);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 4. Proposal Review & Hiring
    private void showProposals() {
        VBox box = new VBox(18);
        Label title = UIComponents.createTitle("Review Proposals & Hire Freelancers");

        List<Proposal> allProposals = proposalService.getProposalsForClient(currentUser.getId());

        long countShortlisted = allProposals.stream().filter(p -> p.getStatus() == Proposal.Status.SHORTLISTED).count();
        long countAccepted = allProposals.stream().filter(p -> p.getStatus() == Proposal.Status.ACCEPTED).count();
        long countPending = allProposals.stream()
                .filter(p -> p.getStatus() == Proposal.Status.SUBMITTED || p.getStatus() == Proposal.Status.PENDING)
                .count();

        HBox stats = new HBox(15);
        stats.getChildren().addAll(
                UIComponents.createStatCard(0, "📬", "Proposals Received", String.valueOf(allProposals.size()),
                        UIComponents.COLOR_PRIMARY),
                UIComponents.createStatCard(1, "⭐", "Shortlisted", String.valueOf(countShortlisted),
                        UIComponents.COLOR_AMBER),
                UIComponents.createStatCard(2, "🤝", "Hired / Accepted", String.valueOf(countAccepted),
                        UIComponents.COLOR_SUCCESS),
                UIComponents.createStatCard(3, "⏳", "Awaiting Review", String.valueOf(countPending),
                        UIComponents.COLOR_PURPLE));

        // Filter and Sort Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cbFilter = new ComboBox<>();
        cbFilter.getItems().addAll("All Proposals", "Under Review", "Shortlisted", "Accepted / Hired", "Declined");
        cbFilter.setValue("All Proposals");

        ComboBox<String> cbSort = new ComboBox<>();
        cbSort.getItems().addAll("AI Match Score (High to Low)", "Bid Amount (Lowest first)",
                "Bid Amount (Highest first)", "Delivery (Fastest first)");
        cbSort.setValue("AI Match Score (High to Low)");

        filterBar.getChildren().addAll(new Label("Filter Status:"), cbFilter, new Label("Sort By:"), cbSort);

        VBox listContainer = new VBox(15);

        Runnable renderProposals = () -> {
            listContainer.getChildren().clear();
            String selectedFilter = cbFilter.getValue();
            String selectedSort = cbSort.getValue();

            List<Proposal> filtered = new ArrayList<>(allProposals);

            if ("Under Review".equals(selectedFilter)) {
                filtered.removeIf(
                        p -> p.getStatus() != Proposal.Status.SUBMITTED && p.getStatus() != Proposal.Status.PENDING);
            } else if ("Shortlisted".equals(selectedFilter)) {
                filtered.removeIf(p -> p.getStatus() != Proposal.Status.SHORTLISTED);
            } else if ("Accepted / Hired".equals(selectedFilter)) {
                filtered.removeIf(p -> p.getStatus() != Proposal.Status.ACCEPTED);
            } else if ("Declined".equals(selectedFilter)) {
                filtered.removeIf(p -> p.getStatus() != Proposal.Status.REJECTED);
            }

            if ("AI Match Score (High to Low)".equals(selectedSort)) {
                filtered.sort((a, b) -> Double.compare(b.getAiMatchScore(), a.getAiMatchScore()));
            } else if ("Bid Amount (Lowest first)".equals(selectedSort)) {
                filtered.sort((a, b) -> Double.compare(a.getBidAmount(), b.getBidAmount()));
            } else if ("Bid Amount (Highest first)".equals(selectedSort)) {
                filtered.sort((a, b) -> Double.compare(b.getBidAmount(), a.getBidAmount()));
            } else if ("Delivery (Fastest first)".equals(selectedSort)) {
                filtered.sort((a, b) -> Integer.compare(a.getDeliveryDays(), b.getDeliveryDays()));
            }

            if (filtered.isEmpty()) {
                VBox empty = UIComponents.createCard();
                empty.setAlignment(Pos.CENTER);
                empty.setPadding(new Insets(40));
                Label emptyLbl = new Label("No proposals match the selected filter.");
                emptyLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                emptyLbl.setTextFill(Color.web(AppTheme.getTextSecondary()));
                empty.getChildren().add(emptyLbl);
                listContainer.getChildren().add(empty);
                return;
            }

            for (Proposal p : filtered) {
                VBox card = UIComponents.createCard();

                HBox cardHeader = new HBox(12);
                cardHeader.setAlignment(Pos.CENTER_LEFT);

                Label pTitle = UIComponents.createHeader(
                        "Project: " + (p.getProjectTitle() != null ? p.getProjectTitle() : "Project Proposal"));
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Status badge
                String badgeBg = "#1e293b";
                String badgeFg = "#38bdf8";
                if (p.getStatus() == Proposal.Status.ACCEPTED) {
                    badgeBg = "#1e3a2b";
                    badgeFg = "#34d399";
                } else if (p.getStatus() == Proposal.Status.SHORTLISTED) {
                    badgeBg = "#3d2e14";
                    badgeFg = "#fbbf24";
                } else if (p.getStatus() == Proposal.Status.REJECTED) {
                    badgeBg = "#3b1e1e";
                    badgeFg = "#f87171";
                } else if (p.getStatus() == Proposal.Status.WITHDRAWN) {
                    badgeBg = "#27272a";
                    badgeFg = "#94a3b8";
                }

                Label badgeStatus = UIComponents.createBadge(p.getStatusDisplayName(), badgeBg, badgeFg);
                Label badgeScore = UIComponents.createBadge("★ " + (int) p.getAiMatchScore() + "% Match", "#1e293b",
                        "#a78bfa");
                cardHeader.getChildren().addAll(pTitle, spacer, badgeScore, badgeStatus);

                // Freelancer details row
                HBox freeRow = new HBox(16);
                freeRow.setAlignment(Pos.CENTER_LEFT);
                Label freeName = new Label(
                        "👤 " + (p.getFreelancerName() != null ? p.getFreelancerName() : "Freelancer")
                                + (p.getFreelancerTitle() != null ? " (" + p.getFreelancerTitle() + ")" : ""));
                freeName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                freeName.setTextFill(Color.web(AppTheme.getTextPrimary()));

                Label freeRating = new Label(
                        String.format("⭐ %.1f (%d reviews)", p.getFreelancerRating(), p.getFreelancerTotalReviews()));
                freeRating.setTextFill(Color.web(UIComponents.COLOR_AMBER));

                Label freeJobs = new Label("📦 " + p.getFreelancerCompletedProjects() + " jobs completed");
                freeJobs.setTextFill(Color.web(AppTheme.getTextSecondary()));

                Label freeBid = new Label("💰 Proposed Bid: $" + String.format("%.0f", p.getBidAmount()));
                freeBid.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                freeBid.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));

                Label freeDays = new Label("⏱ " + p.getDeliveryDays() + " days");
                freeDays.setTextFill(Color.web(AppTheme.getTextSecondary()));

                freeRow.getChildren().addAll(freeName, freeRating, freeJobs, freeBid, freeDays);

                Label cover = new Label("Cover Letter:\n" + p.getCoverLetter());
                cover.setWrapText(true);
                cover.setFont(Font.font("Segoe UI", 12));
                cover.setTextFill(Color.web(AppTheme.getTextPrimary()));

                HBox actions = new HBox(10);
                actions.setAlignment(Pos.CENTER_RIGHT);

                if (p.getStatus() == Proposal.Status.SUBMITTED || p.getStatus() == Proposal.Status.PENDING) {
                    Button btnShortlist = UIComponents.createSecondaryButton("⭐ Shortlist Proposal");
                    btnShortlist.setOnAction(e -> {
                        proposalService.shortlistProposal(p.getId(), currentUser.getId());
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Shortlisted", "Proposal Shortlisted",
                                "Candidate shortlisted for consideration.");
                        showProposals();
                    });

                    Button btnDecline = UIComponents.createDangerButton("❌ Decline");
                    btnDecline.setOnAction(e -> {
                        proposalService.rejectProposal(p.getId(), currentUser.getId());
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Declined", "Proposal Declined",
                                "Candidate notification sent.");
                        showProposals();
                    });

                    Button btnAccept = UIComponents.createSuccessButton("✅ Accept Proposal & Hire");
                    btnAccept.setOnAction(e -> {
                        proposalService.acceptProposal(p.getId(), currentUser.getId());
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Candidate Hired", "Proposal Accepted",
                                "Congratulations! You have accepted the proposal from " + p.getFreelancerName()
                                        + ". Project status is now IN PROGRESS.");
                        showProposals();
                    });

                    actions.getChildren().addAll(btnShortlist, btnDecline, btnAccept);
                } else if (p.getStatus() == Proposal.Status.SHORTLISTED) {
                    Button btnDecline = UIComponents.createDangerButton("❌ Decline");
                    btnDecline.setOnAction(e -> {
                        proposalService.rejectProposal(p.getId(), currentUser.getId());
                        showProposals();
                    });

                    Button btnAccept = UIComponents.createSuccessButton("✅ Accept Proposal & Hire");
                    btnAccept.setOnAction(e -> {
                        proposalService.acceptProposal(p.getId(), currentUser.getId());
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Candidate Hired", "Proposal Accepted",
                                "Congratulations! You have accepted the proposal from " + p.getFreelancerName()
                                        + ". Project status is now IN PROGRESS.");
                        showProposals();
                    });

                    actions.getChildren().addAll(btnDecline, btnAccept);
                }

                card.getChildren().addAll(cardHeader, freeRow, cover);
                if (!actions.getChildren().isEmpty()) {
                    card.getChildren().add(actions);
                }
                listContainer.getChildren().add(card);
            }
        };

        cbFilter.setOnAction(e -> renderProposals.run());
        cbSort.setOnAction(e -> renderProposals.run());
        renderProposals.run();

        box.getChildren().addAll(title, stats, filterBar, listContainer);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 5. Find Freelancers
    private void showFreelancerSearch() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Search Freelancers & AI Talent Recommendations");

        VBox list = new VBox(15);
        for (FreelancerProfile fp : db.getFreelancerProfiles().values()) {
            User u = db.getUsers().get(fp.getUserId());
            if (u != null) {
                VBox card = UIComponents.createCard();
                Label name = UIComponents.createHeader("👤 " + u.getUsername() + " - " + fp.getTitle());
                Label bio = new Label(fp.getBio());
                bio.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                bio.setWrapText(true);

                Label skills = new Label(
                        "Skills: " + String.join(", ", fp.getSkills()) + " | Rating: ⭐ " + fp.getRating() + " / 5.0");
                skills.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Button btnInvite = UIComponents.createPrimaryButton("Invite to Post Project");
                btnInvite.setOnAction(e -> {
                    notifService.sendNotification(u.getId(), "Project Invitation",
                            currentUser.getUsername() + " invited you to view their posted projects!");
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Invite Sent", "Freelancer Invited",
                            "Sent project invitation notification to " + u.getUsername());
                });

                card.getChildren().addAll(name, bio, skills, btnInvite);
                list.getChildren().add(card);
            }
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 6. Contracts, Milestones & Escrow Release
    private void showMilestonesAndPayments() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(20));

        Label title = UIComponents.createTitle("Active Contracts, Milestones & Escrow");
        Label subtitle = new Label(
                "Review milestone progress, inspect submitted deliverables, request revisions, and release escrow payments.");
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));

        PaymentService.ClientFinancialSummary fin = paymentService.getFinancialSummaryForClient(currentUser.getId());
        HBox kpiBox = new HBox(15);
        kpiBox.getChildren().addAll(
                UIComponents.createMetricCard("Total Spent (Demo)", "$" + String.format("%.0f", fin.totalSpent),
                        "#3B82F6"),
                UIComponents.createMetricCard("Active Escrow Committed",
                        "$" + String.format("%.0f", fin.activeEscrowCommitted), "#10B981"),
                UIComponents.createMetricCard("Completed Contracts", String.valueOf(fin.completedContractsCount),
                        "#6366F1"),
                UIComponents.createMetricCard("Ledger Records", String.valueOf(fin.transactionsCount), "#F59E0B"));

        List<Contract> contracts = contractService.getContractsByClientUserId(currentUser.getId());

        VBox list = new VBox(20);
        if (contracts.isEmpty()) {
            VBox emptyBox = UIComponents.createCard();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            Label emptyLbl = new Label(
                    "No active contracts found.\nAccept a candidate proposal in 'Proposal Review' to create a contract with milestone tracking.");
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
                Label cTitle = UIComponents.createHeader("📜 " + c.getProjectTitle());
                Region spH = new Region();
                HBox.setHgrow(spH, Priority.ALWAYS);

                Label statusBadge = UIComponents.createBadge(c.getStatus().name(),
                        c.getStatus() == Contract.Status.ACTIVE ? "#10B981" : "#6B7280", "white");
                cardHeader.getChildren().addAll(cTitle, spH, statusBadge);

                HBox metaRow = new HBox(20);
                metaRow.setAlignment(Pos.CENTER_LEFT);
                Label freeLbl = new Label("👤 Freelancer: "
                        + (c.getFreelancerName() != null ? c.getFreelancerName() : "Assigned Developer"));
                freeLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));
                Label valLbl = new Label("💰 Total: $" + String.format("%.0f", c.getTotalAmount()) + " | Escrow: $"
                        + String.format("%.0f", c.getEscrowBalance()));
                valLbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                Label dateLbl = new Label("📅 Timeline: " + (c.getStartDate() != null ? c.getStartDate() : "-") + " to "
                        + (c.getEndDate() != null ? c.getEndDate() : "-"));
                dateLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                metaRow.getChildren().addAll(freeLbl, valLbl, dateLbl);

                VBox msContainer = new VBox(10);
                msContainer.setPadding(new Insets(10, 0, 0, 0));
                msContainer.getChildren().add(UIComponents.createSubHeader("Contract Milestones & Deliverables:"));

                List<Milestone> milestones = milestoneService.getMilestonesByContract(c.getId());
                for (Milestone m : milestones) {
                    VBox msRow = new VBox(8);
                    msRow.setPadding(new Insets(12));
                    msRow.setStyle("-fx-background-color: " + AppTheme.getBgInput()
                            + "; -fx-background-radius: 8; -fx-border-color: " + AppTheme.getBorderColor()
                            + "; -fx-border-width: 1; -fx-border-radius: 8;");

                    HBox topRow = new HBox(10);
                    topRow.setAlignment(Pos.CENTER_LEFT);

                    Label mOrder = new Label("#" + m.getSequenceOrder() + " " + m.getTitle());
                    mOrder.setTextFill(Color.web(AppTheme.getTextPrimary()));
                    mOrder.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                    Label mAmount = new Label("— $" + String.format("%.0f", m.getAmount()));
                    mAmount.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                    mAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                    Region spM = new Region();
                    HBox.setHgrow(spM, Priority.ALWAYS);

                    String statusColor = switch (m.getStatus()) {
                        case PAID -> "#10B981";
                        case APPROVED -> "#3B82F6";
                        case SUBMITTED -> "#F59E0B";
                        case REVISION_REQUESTED -> "#EF4444";
                        case IN_PROGRESS -> "#6366F1";
                        default -> "#9CA3AF";
                    };
                    Label mStatusBadge = UIComponents.createBadge(m.getStatus().name(), statusColor, "white");
                    topRow.getChildren().addAll(mOrder, mAmount, spM, mStatusBadge);

                    Label mDesc = new Label(
                            m.getDescription() != null ? m.getDescription() : "Milestone deliverable specification.");
                    mDesc.setTextFill(Color.web(AppTheme.getTextMuted()));
                    mDesc.setWrapText(true);

                    msRow.getChildren().addAll(topRow, mDesc);

                    // Deliverable preview & action buttons
                    Deliverable del = m.getLatestDeliverable();
                    if (del != null) {
                        VBox delBox = new VBox(4);
                        delBox.setPadding(new Insets(8));
                        delBox.setStyle("-fx-background-color: rgba(99, 102, 241, 0.08); -fx-background-radius: 6;");

                        Label delTitle = new Label("📦 Submitted Deliverable: " + del.getTitle());
                        delTitle.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
                        delTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                        Label delDesc = new Label(
                                del.getDescription() != null && !del.getDescription().isBlank() ? del.getDescription()
                                        : "No notes provided.");
                        delDesc.setTextFill(Color.web(AppTheme.getTextPrimary()));
                        delDesc.setWrapText(true);

                        Label delFile = new Label(
                                del.getFilePath() != null ? "📁 File: " + del.getFilePath() : "📁 No file attachment");
                        delFile.setTextFill(Color.web(AppTheme.getTextMuted()));
                        delFile.setFont(Font.font("Segoe UI", 11));

                        delBox.getChildren().addAll(delTitle, delDesc, delFile);
                        msRow.getChildren().add(delBox);
                    }

                    HBox actionsRow = new HBox(10);
                    actionsRow.setAlignment(Pos.CENTER_RIGHT);

                    if (m.getStatus() == Milestone.Status.SUBMITTED) {
                        Button btnRevision = UIComponents.createSecondaryButton("🔄 Request Revision");
                        btnRevision.setOnAction(e -> {
                            UIComponents.showPromptDialog(
                                    "Request Milestone Revision",
                                    "Feedback for Milestone: " + m.getTitle(),
                                    "Feedback / Changes requested:",
                                    "Please review requirements and update the submission.",
                                    notes -> {
                                        if (notes != null && !notes.trim().isEmpty()) {
                                            milestoneService.requestRevision(m.getId(), currentUser.getId(), notes.trim());
                                            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Revision Requested",
                                                    "Freelancer Notified", "Revision request sent with your feedback.");
                                            showMilestonesAndPayments();
                                        }
                                    });
                        });

                        Button btnApprove = UIComponents.createSuccessButton("✅ Approve Deliverable");
                        btnApprove.setOnAction(e -> {
                            milestoneService.approveMilestone(m.getId(), currentUser.getId());
                            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Milestone Approved",
                                    "Approval Recorded",
                                    "Deliverable approved! Next milestone is now active, and payment can be released.");
                            showMilestonesAndPayments();
                        });

                        actionsRow.getChildren().addAll(btnRevision, btnApprove);
                    } else if (m.getStatus() == Milestone.Status.APPROVED) {
                        Button btnPay = UIComponents.createSuccessButton(
                                "💳 Release Escrow Payment ($" + String.format("%.0f", m.getAmount()) + ")");
                        btnPay.setOnAction(e -> {
                            try {
                                PaymentService.PaymentReceipt receipt = paymentService
                                        .processMilestonePayment(m.getId(), currentUser.getId());
                                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Escrow Released (DEMO PAYMENT)",
                                        "Payment Success", receipt.toString());
                                showMilestonesAndPayments();
                            } catch (Exception ex) {
                                UIComponents.showAlert(Alert.AlertType.ERROR, "Payment Failed",
                                        "Error Releasing Payment", ex.getMessage());
                            }
                        });
                        actionsRow.getChildren().add(btnPay);
                    }

                    if (!actionsRow.getChildren().isEmpty()) {
                        msRow.getChildren().add(actionsRow);
                    }

                    msContainer.getChildren().add(msRow);
                }

                card.getChildren().addAll(cardHeader, metaRow, msContainer);
                list.getChildren().add(card);
            }
        }

        // Financial Ledger & Escrow Transactions Table
        List<Transaction> txHistory = paymentService.getTransactionHistory(currentUser.getId());
        if (!txHistory.isEmpty()) {
            VBox ledgerCard = UIComponents.createCard();
            ledgerCard.setPadding(new Insets(15));
            ledgerCard.getChildren().add(UIComponents.createHeader("📜 Financial Ledger & Escrow Transactions (Demo)"));
            for (Transaction tx : txHistory) {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-background-radius: 6;");

                Label lType = UIComponents.createBadge(tx.getType().name(), "#3B82F6", "white");
                Label lRef = new Label(tx.getReference() != null ? tx.getReference() : tx.getId());
                lRef.setTextFill(Color.web(AppTheme.getTextPrimary()));
                lRef.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                Label lMilestone = new Label(
                        tx.getMilestoneTitle() != null ? "Milestone: " + tx.getMilestoneTitle() : "");
                lMilestone.setTextFill(Color.web(AppTheme.getTextMuted()));

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Label lAmount = new Label("-$" + String.format("%.2f", tx.getAmount()));
                lAmount.setTextFill(Color.web("#EF4444"));
                lAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

                Label lDate = new Label(tx.getCreatedAt());
                lDate.setTextFill(Color.web(AppTheme.getTextMuted()));

                row.getChildren().addAll(lType, lRef, lMilestone, sp, lAmount, lDate);
                ledgerCard.getChildren().add(row);
            }
            list.getChildren().add(ledgerCard);
        }

        box.getChildren().addAll(title, subtitle, kpiBox, list);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 7. Chat & Messaging
    private void showChat() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10, 20, 20, 20));

        Label title = UIComponents.createTitle("💬 Real-Time Client & Freelancer Communication");
        Label subtitle = new Label(
                "Collaborate directly with awarded freelancers with full project context, encrypted history, and virtual meetings.");
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));

        // Quick Virtual Meeting Action Bar
        HBox meetBar = new HBox(15);
        meetBar.setPadding(new Insets(12, 16, 12, 16));
        meetBar.setAlignment(Pos.CENTER_LEFT);
        meetBar.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: "
                + AppTheme.getBorderColor() + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label meetIcon = new Label("📹 Virtual Meeting Hub:");
        meetIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        meetIcon.setTextFill(Color.web(AppTheme.getTextPrimary()));
        Label meetSub = new Label("Instantly launch or schedule video conferences with freelancers.");
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
            List<Contract> contracts = contractService.getContractsByClientUserId(currentUser.getId());
            for (Contract c : contracts) {
                if (c.getFreelancerUserId() != null) {
                    chatService.getOrCreateConversation(currentUser.getId(), c.getFreelancerUserId(), c.getProjectId());
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
        convListPanel.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: "
                + AppTheme.getBorderColor() + "; -fx-border-radius: 10; -fx-background-radius: 10;");

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
            Label noProj = new Label(
                    "No active chat threads.\nAccept a proposal or hire a freelancer to start chatting!");
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
                item.setStyle("-fx-background-color: "
                        + (isSelected ? AppTheme.COLOR_PRIMARY + "22" : AppTheme.getBgInput()) + "; "
                        + "-fx-border-color: " + (isSelected ? AppTheme.COLOR_PRIMARY : "transparent") + "; "
                        + "-fx-border-width: 1.5px; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

                HBox nameRow = new HBox(8);
                nameRow.setAlignment(Pos.CENTER_LEFT);
                Label nameLbl = new Label("👤 " + (c.getOtherUsername() != null ? c.getOtherUsername() : "Freelancer"));
                nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                nameLbl.setTextFill(Color
                        .web(isSelected ? (AppTheme.isDarkMode() ? "#A5B4FC" : "#4338CA") : AppTheme.getTextPrimary()));

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
        chatPanel.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: "
                + AppTheme.getBorderColor() + "; -fx-border-radius: 10; -fx-background-radius: 10;");

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

            Label chatUserLbl = new Label(
                    "💬 " + (activeConv.getOtherUsername() != null ? activeConv.getOtherUsername() : "Freelancer"));
            chatUserLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            chatUserLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

            Label rolePill = UIComponents.createBadge(
                    activeConv.getOtherUserRole() != null ? activeConv.getOtherUserRole() : "FREELANCER", "#10B981",
                    "white");

            Region chSp = new Region();
            HBox.setHgrow(chSp, Priority.ALWAYS);

            Label encBadge = UIComponents.createBadge("🔒 SQLite Stored & Verified", "#6366F1", "white");

            Button btnScheduleMeet = UIComponents.createSuccessButton("📹 Schedule Meeting");
            btnScheduleMeet.setOnAction(e -> showScheduleMeetingDialog(
                    currentActiveConv.getOtherUserId(),
                    currentActiveConv.getOtherUsername(),
                    currentActiveConv.getProjectId(),
                    currentActiveConv.getProjectTitle()));

            chatHeader.getChildren().addAll(chatUserLbl, rolePill, chSp, encBadge, btnScheduleMeet);

            // Messages Container
            VBox msgContainer = new VBox(10);
            msgContainer.setPadding(new Insets(10));

            List<ChatMessage> messages = chatService.getConversationMessages(selectedChatConversationId,
                    currentUser.getId());

            if (messages.isEmpty()) {
                VBox emptyMsgBox = new VBox(8);
                emptyMsgBox.setAlignment(Pos.CENTER);
                emptyMsgBox.setPadding(new Insets(60, 20, 60, 20));
                Label noMsg = new Label(
                        "No messages yet in this thread.\nSay hello and kick off your sprint collaboration!");
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

                    Label authorLbl = new Label(
                            isMine ? "You" : (m.getSenderName() != null ? m.getSenderName() : "Partner"));
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

            final String[] attachedPath = { null };
            Button btnAttach = UIComponents.createSecondaryButton("📎 File");
            btnAttach.setOnAction(e -> {
                javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
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
            Label lblSelect = new Label(
                    "Select a conversation from the left to start messaging,\nor create a contract in 'Proposal Review'.");
            lblSelect.setFont(Font.font("Segoe UI", 14));
            lblSelect.setTextFill(Color.web(AppTheme.getTextMuted()));
            emptyChat.getChildren().add(lblSelect);
            chatPanel.getChildren().add(emptyChat);
        }

        chatLayout.getChildren().addAll(convListPanel, chatPanel);
        box.getChildren().addAll(title, subtitle, meetBar, chatLayout);
        contentArea.getChildren().setAll(box);
    }

    private void showScheduleMeetingDialog(String otherUserId, String otherUserName, String projectId,
            String projectTitle) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: " + AppTheme.getBgCard() + ";");

        TextField tfTopic = UIComponents
                .createTextField(projectTitle != null ? "Sync: " + projectTitle : "Sprint Planning & Sync");
        DatePicker dp = new DatePicker(java.time.LocalDate.now().plusDays(1));
        TextField tfTime = UIComponents.createTextField("15:00");
        String demoUrl = "https://meet.google.com/sb-" + UUID.randomUUID().toString().substring(0, 6);
        TextField tfUrl = UIComponents.createTextField(demoUrl);

        form.getChildren().addAll(
                new Label("Meeting Topic / Agenda:"), tfTopic,
                new Label("Meeting Date:"), dp,
                new Label("Meeting Time (e.g. 14:30):"), tfTime,
                new Label("Virtual Room URL:"), tfUrl);

        UIComponents.showModalOverlay("Schedule Virtual Meeting with " + (otherUserName != null ? otherUserName : "freelancer"),
                form, "Schedule & Notify", () -> {
                    if (!tfTopic.getText().trim().isEmpty() && dp.getValue() != null) {
                        String topic = tfTopic.getText().trim();
                        String date = dp.getValue().toString();
                        String time = tfTime.getText().trim();
                        String url = tfUrl.getText().trim();

                        calendarService.createMeetingEvent(currentUser.getId(), otherUserId, topic, date, time, url, projectId);

                        if (selectedChatConversationId != null) {
                            chatService.sendMessage(selectedChatConversationId, currentUser.getId(),
                                    "📹 Scheduled Virtual Meeting: " + topic + "\n🗓 Date: " + date + " at " + time
                                            + "\n🔗 Link: " + url,
                                    null);
                        }

                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Meeting Scheduled", "Virtual Meeting Confirmed",
                                "Meeting saved to calendar and shared with " + otherUserName + "!");
                        showChat();
                    }
                });
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
        Label badge = UIComponents.createBadge(unreadCount + " Unread", unreadCount > 0 ? "#EF4444" : "#10B981",
                "white");

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
            Label emptyLbl = new Label(
                    "No notifications yet.\nYou will receive real-time updates for proposals, contracts, deliverables, payments, and meetings here.");
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
                if ("MESSAGE".equals(n.getType())) {
                    icon = "💬";
                    color = "#3B82F6";
                } else if ("PAYMENT".equals(n.getType())) {
                    icon = "💳";
                    color = "#10B981";
                } else if ("CONTRACT".equals(n.getType())) {
                    icon = "📜";
                    color = "#8B5CF6";
                } else if ("PROPOSAL".equals(n.getType())) {
                    icon = "📩";
                    color = "#F59E0B";
                } else if ("MILESTONE".equals(n.getType())) {
                    icon = "🚩";
                    color = "#EC4899";
                } else if ("MEETING".equals(n.getType())) {
                    icon = "📹";
                    color = "#06B6D4";
                }

                Label iconBadge = UIComponents.createBadge(icon + " " + n.getType(), color, "white");

                Label titleLbl = new Label(n.getTitle());
                titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                titleLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

                Region topSp = new Region();
                HBox.setHgrow(topSp, Priority.ALWAYS);

                Label timeLbl = new Label(n.getCreatedAt() != null ? n.getCreatedAt() : "");
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

    // 8. Company Profile
    private void showCompanyProfile() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Company & Client Profile");

        ClientProfile cp = clientService.getProfileByUserId(currentUser.getId());
        if (cp == null) {
            cp = new ClientProfile();
            cp.setId("cp_" + currentUser.getId().replace("usr_", ""));
            cp.setUserId(currentUser.getId());
            cp.setCompanyName(currentUser.getUsername() + " Enterprises");
            cp.setIndustry("Technology & Software");
            clientService.updateProfile(cp);
        }

        VBox card = UIComponents.createCard();

        TextField tfCompany = UIComponents.createTextField("Company / Organization Name...");
        tfCompany.setText(cp.getCompanyName() != null ? cp.getCompanyName() : "");

        TextField tfIndustry = UIComponents
                .createTextField("Industry Sector (e.g. FinTech, Healthcare, E-Commerce)...");
        tfIndustry.setText(cp.getIndustry() != null ? cp.getIndustry() : "");

        TextField tfWeb = UIComponents.createTextField("Website URL (e.g. https://example.com)...");
        tfWeb.setText(cp.getCompanyWebsite() != null ? cp.getCompanyWebsite() : "");

        TextArea taDesc = UIComponents.createTextArea("Company Overview, Mission, & Hiring Needs...");
        taDesc.setText(cp.getAbout() != null ? cp.getAbout() : "");

        // Telemetry Stats Row
        HBox statsRow = new HBox(15);
        statsRow.setPadding(new Insets(10));
        statsRow.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-background-radius: 8;");
        Label lSpent = new Label("💰 Total Spent: $" + String.format("%.2f", cp.getTotalSpent()));
        lSpent.setTextFill(Color.web(AppTheme.COLOR_SUCCESS));
        lSpent.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        Label lProjects = new Label("📂 Projects Posted: " + cp.getPostedProjects());
        lProjects.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
        lProjects.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        statsRow.getChildren().addAll(lSpent, new Separator(javafx.geometry.Orientation.VERTICAL), lProjects);

        // Logo / Avatar Upload
        Label logoHeader = UIComponents.createHeader("🖼️ Company Brand Logo (Local Storage)");
        Label logoLbl = new Label(
                "Logo: " + (cp.getAvatarPath() != null ? cp.getAvatarPath() : "Default branding active"));
        logoLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        Button btnUploadLogo = UIComponents.createSecondaryButton("📁 Upload Company Logo");
        btnUploadLogo.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Select Company Logo");
            fc.getExtensionFilters()
                    .add(new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
            java.io.File file = fc.showOpenDialog(null);
            if (file != null) {
                try {
                    String savedPath = clientService.uploadAvatar(currentUser.getId(), file);
                    logoLbl.setText("Logo: " + savedPath);
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Logo Uploaded",
                            "Saved to local storage:\n" + savedPath);
                } catch (Exception ex) {
                    UIComponents.showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to save logo",
                            ex.getMessage());
                }
            }
        });

        ClientProfile finalCp = cp;
        Button btnSave = UIComponents.createPrimaryButton("Save Company Details to Database");
        btnSave.setOnAction(e -> {
            finalCp.setCompanyName(tfCompany.getText().trim());
            finalCp.setIndustry(tfIndustry.getText().trim());
            finalCp.setCompanyWebsite(tfWeb.getText().trim());
            finalCp.setAbout(taDesc.getText().trim());

            clientService.updateProfile(finalCp);
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Saved",
                    "Company details saved successfully to SQLite!");
            showCompanyProfile();
        });

        card.getChildren().addAll(
                new Label("Company Name:"), tfCompany,
                new Label("Industry Sector:"), tfIndustry,
                new Label("Website URL:"), tfWeb,
                new Label("Company Overview & About:"), taDesc,
                new Separator(),
                statsRow,
                new Separator(),
                logoHeader, logoLbl, btnUploadLogo,
                new Separator(),
                btnSave);

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }
}
