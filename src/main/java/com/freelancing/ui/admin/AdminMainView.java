package com.freelancing.ui.admin;

import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.admin.AuditLog;
import com.freelancing.model.admin.Dispute;
import com.freelancing.model.admin.Report;
import com.freelancing.model.admin.SupportTicket;
import com.freelancing.model.common.FeedPost;
import com.freelancing.model.common.User;
import com.freelancing.model.company.Project;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.admin.AdminService;
import com.freelancing.service.common.AiService;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.company.ProjectService;
import com.freelancing.ui.common.HomePage;
import com.freelancing.ui.common.SettingsView;
import com.freelancing.ui.common.UIComponents;

import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminMainView {
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final AdminService adminService = new AdminService();
    private final FreelancerProfileDAO freelancerProfileDAO = new FreelancerProfileDAO();
    private final AiService aiService = new AiService();
    private final NotificationService notifService = new NotificationService();

    private BorderPane root;
    private StackPane contentArea;
    private Button btnNavDash, btnNavUsers, btnNavProjects, btnNavDisputes, btnNavAiMon, btnNavReports, btnNavAnnounce,
            btnNavBackup, btnNavFeedMod, btnNavSettings;
    private Button[] navBtns;
    private boolean isSidebarOpen = true;

    public AdminMainView(User user) {
        this.currentUser = user;
    }

    /** Compatibility constructor for legacy code */
    public AdminMainView(Object ignored, User user) {
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

        Label logo = new Label("⚡ SkillBridge  |  Admin Panel");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnHome = UIComponents.createSecondaryButton("🏠 SkillBridge Home");
        btnHome.setOnAction(e -> HomePage.showHomeView());

        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showAdminView(currentUser);
        });

        Label userLabel = new Label("👑 " + currentUser.getUsername() + " (Super Admin)");
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
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + AppTheme.getBgPanel() + ";" +
                "-fx-border-color: " + AppTheme.getBorderColor() + ";" +
                "-fx-border-width: 0 1 0 0;");

        btnNavDash = createNavButton("📊 System Overview & Charts", true);
        btnNavUsers = createNavButton("👥 User & AI Fraud Scanner", false);
        btnNavProjects = createNavButton("📂 Project & Skill Categories", false);
        btnNavDisputes = createNavButton("⚖️ Dispute Resolution Panel", false);
        btnNavAiMon = createNavButton("🤖 AI Talent Matching Monitor", false);
        btnNavReports = createNavButton("📈 Analytics & Platform Reports", false);
        btnNavAnnounce = createNavButton("📢 Announcements & Support", false);
        btnNavBackup = createNavButton("💾 DB Backup & System Logs", false);
        btnNavFeedMod = createNavButton("📰 Feed Moderation", false);
        btnNavSettings = createNavButton("⚙️ System Settings", false);

        navBtns = new Button[] { btnNavDash, btnNavUsers, btnNavProjects, btnNavDisputes, btnNavAiMon, btnNavReports,
                btnNavAnnounce, btnNavBackup, btnNavFeedMod, btnNavSettings };

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: " + AppTheme.getBgDark() + ";");

        btnNavDash.setOnAction(e -> {
            selectNav(btnNavDash, navBtns);
            showDashboard();
        });
        btnNavUsers.setOnAction(e -> {
            selectNav(btnNavUsers, navBtns);
            showUserManagement();
        });
        btnNavProjects.setOnAction(e -> {
            selectNav(btnNavProjects, navBtns);
            showProjectManagement();
        });
        btnNavDisputes.setOnAction(e -> {
            selectNav(btnNavDisputes, navBtns);
            showDisputes();
        });
        btnNavAiMon.setOnAction(e -> {
            selectNav(btnNavAiMon, navBtns);
            showAiMonitoring();
        });
        btnNavReports.setOnAction(e -> {
            selectNav(btnNavReports, navBtns);
            showReports();
        });
        btnNavAnnounce.setOnAction(e -> {
            selectNav(btnNavAnnounce, navBtns);
            showAnnouncements();
        });
        btnNavBackup.setOnAction(e -> {
            selectNav(btnNavBackup, navBtns);
            showBackupAndLogs();
        });
        btnNavFeedMod.setOnAction(e -> {
            selectNav(btnNavFeedMod, navBtns);
            showFeedModeration();
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

        sidebar.getChildren().addAll(sidebarHeader, btnNavDash, new Separator(),
                btnNavUsers, btnNavProjects, btnNavDisputes, btnNavAiMon, btnNavReports, btnNavAnnounce, btnNavBackup,
                btnNavFeedMod, btnNavSettings);

        ScrollPane sidebarScroll = UIComponents.createScrollPane(sidebar);
        sidebarScroll.setStyle(
                "-fx-background: " + AppTheme.getBgPanel() + "; -fx-background-color: " + AppTheme.getBgPanel()
                + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-width: 0 1 0 0;");
        sidebarScroll.setPrefWidth(250);

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
        SettingsView view = new SettingsView(currentUser, () -> HomePage.showAdminView(currentUser));
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
            btn.setStyle("-fx-background-color: " + UIComponents.COLOR_DANGER
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
        com.freelancing.util.AnimationUtil.applyVariedTransition(contentArea, idx);
    }

    private void makeStatCardInteractive(VBox card, Runnable action) {
        String base = card.getStyle();
        card.setStyle(base + "; -fx-cursor: hand;");
        card.setOnMouseEntered(
                e -> card.setStyle(base + "; -fx-cursor: hand; -fx-scale-x: 1.02; -fx-scale-y: 1.02; -fx-border-color: "
                        + UIComponents.COLOR_DANGER + ";"));
        card.setOnMouseExited(e -> card.setStyle(base + "; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
        card.setOnMouseClicked(e -> {
            if (action != null)
                action.run();
        });
    }

    // 1. Executive Dashboard with JavaFX Charts (Multithreaded background data loading)
    private void showDashboard() {
        contentArea.getChildren().setAll(com.freelancing.util.AnimationUtil.createLoadingOverlay("Loading admin metrics & system analytics..."));

        com.freelancing.util.AnimationUtil.runAsync(
            () -> {
                Map<String, Object> kpis = adminService.getPlatformKpis();
                List<User> allUsers = adminService.getUsers("", null, null);
                List<AuditLog> auditLogs = adminService.getAuditLogs(10);
                return new Object[] { kpis, allUsers, auditLogs };
            },
            data -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> kpis = (Map<String, Object>) data[0];
                @SuppressWarnings("unchecked")
                List<User> allUsers = (List<User>) data[1];
                @SuppressWarnings("unchecked")
                List<AuditLog> auditLogs = (List<AuditLog>) data[2];

                VBox box = new VBox(20);
                Label title = UIComponents.createTitle("Admin System Metrics & Visual Analytics");

                int totalUsers = ((Number) kpis.getOrDefault("totalUsers", 0)).intValue();
                int totalProjects = ((Number) kpis.getOrDefault("totalProjects", 0)).intValue();
                double platformRev = ((Number) kpis.getOrDefault("platformRevenue", 0.0)).doubleValue();
                int activeDisputes = ((Number) kpis.getOrDefault("openDisputes", 0)).intValue();
                int activeContracts = ((Number) kpis.getOrDefault("activeContracts", 0)).intValue();
                double activeEscrow = ((Number) kpis.getOrDefault("activeEscrowBalance", 0.0)).doubleValue();

                HBox statGrid = new HBox(15);
                VBox card1 = UIComponents.createStatCard(0, "👥", "Total Users", String.valueOf(totalUsers),
                        UIComponents.COLOR_PRIMARY);
                VBox card2 = UIComponents.createStatCard(1, "📁", "Total Projects", String.valueOf(totalProjects),
                        UIComponents.COLOR_SUCCESS);
                VBox card3 = UIComponents.createStatCard(2, "💳", "Platform Revenue", String.format("$%,.2f", platformRev),
                        UIComponents.COLOR_AMBER);
                VBox card4 = UIComponents.createStatCard(3, "⚖️", "Open Disputes", String.valueOf(activeDisputes),
                        UIComponents.COLOR_DANGER);

                makeStatCardInteractive(card1, () -> {
                    selectNav(btnNavUsers, navBtns);
                    showUserManagement();
                });
                makeStatCardInteractive(card2, () -> {
                    selectNav(btnNavProjects, navBtns);
                    showProjectManagement();
                });
                makeStatCardInteractive(card3, () -> {
                    selectNav(btnNavReports, navBtns);
                    showReports();
                });
                makeStatCardInteractive(card4, () -> {
                    selectNav(btnNavDisputes, navBtns);
                    showDisputes();
                });

                statGrid.getChildren().addAll(card1, card2, card3, card4);

                // Additional KPI summary banner
                HBox kpiBanner = new HBox(20);
                kpiBanner.setPadding(new Insets(12, 16, 12, 16));
                kpiBanner.setAlignment(Pos.CENTER_LEFT);
                kpiBanner.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: "
                        + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

                Label lblContracts = new Label("📑 Active Contracts: " + activeContracts);
                lblContracts.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                lblContracts.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));

                Label lblEscrow = new Label("🔒 Active Escrow Held: " + String.format("$%,.2f", activeEscrow));
                lblEscrow.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                lblEscrow.setTextFill(Color.web(UIComponents.COLOR_SUCCESS));

                int openTickets = ((Number) kpis.getOrDefault("openSupportTickets", 0)).intValue();
                Label lblTickets = new Label("🎫 Open Tickets: " + openTickets);
                lblTickets.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                lblTickets.setTextFill(Color.web(UIComponents.COLOR_AMBER));

                kpiBanner.getChildren().addAll(lblContracts, new Separator(), lblEscrow, new Separator(), lblTickets);

                // Visual Charts
                HBox chartRow = new HBox(15);
                Map<String, Double> userDistribution = new LinkedHashMap<>();
                for (User u : allUsers) {
                    String roleName = u.getRole() != null ? u.getRole().name() : "OTHER";
                    userDistribution.put(roleName, userDistribution.getOrDefault(roleName, 0.0) + 1.0);
                }
                if (userDistribution.isEmpty()) {
                    userDistribution.put("FREELANCER", 1.0);
                    userDistribution.put("CLIENT", 1.0);
                    userDistribution.put("ADMIN", 1.0);
                }

                PieChart pieUser = UIComponents.createPieChart("User Base Demographics", userDistribution);
                HBox.setHgrow(pieUser, Priority.ALWAYS);

                @SuppressWarnings("unchecked")
                Map<String, Double> categoryDistribution = (Map<String, Double>) kpis.get("projectsByCategory");
                if (categoryDistribution == null || categoryDistribution.isEmpty()) {
                    categoryDistribution = new LinkedHashMap<>();
                    categoryDistribution.put("Development", 5.0);
                    categoryDistribution.put("Design", 3.0);
                    categoryDistribution.put("Marketing", 2.0);
                }

                PieChart pieCat = UIComponents.createPieChart("Projects by Category", categoryDistribution);
                HBox.setHgrow(pieCat, Priority.ALWAYS);

                chartRow.getChildren().addAll(pieUser, pieCat);

                VBox activityCard = UIComponents.createCard();
                activityCard.getChildren().add(UIComponents.createHeader("📜 Recent Security & Governance Audit Trail"));

                VBox logList = new VBox(6);
                if (auditLogs.isEmpty()) {
                    Label emptyLbl = new Label("No recent audit log entries recorded in SQLite yet.");
                    emptyLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                    logList.getChildren().add(emptyLbl);
                } else {
                    for (AuditLog al : auditLogs) {
                        Label l = new Label(String.format("[%s] %s by %s (%s) - %s",
                                al.getTimestamp(), al.getAction(), al.getUsername() != null ? al.getUsername() : al.getUserId(),
                                al.getIpAddress(), al.getDetails()));
                        l.setFont(Font.font("Consolas", 12));
                        l.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                        logList.getChildren().add(l);
                    }
                }
                activityCard.getChildren().add(logList);

                box.getChildren().addAll(title, statGrid, kpiBanner, chartRow, activityCard);
                contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
                com.freelancing.util.AnimationUtil.applyFadeZoom(contentArea, 260);
            }
        );
    }

    // 2. User Management & AI Fraud Scanner
    private void showUserManagement() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("User Account Governance & AI Fraud Risk Scanner");

        // Filter and Search Toolbar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10));
        filterBar.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: "
                + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField tfSearch = UIComponents.createTextField("Search by username or email...");
        tfSearch.setPrefWidth(260);

        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("ALL", "FREELANCER", "CLIENT", "ADMIN");
        cbRole.setValue("ALL");
        cbRole.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: white;");

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("ALL", "ACTIVE", "VERIFIED", "PENDING_VERIFICATION", "SUSPENDED", "BANNED");
        cbStatus.setValue("ALL");
        cbStatus.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: white;");

        Button btnApplyFilter = UIComponents.createPrimaryButton("🔍 Filter");

        filterBar.getChildren().addAll(new Label("Filter:"), tfSearch, new Label("Role:"), cbRole, new Label("Status:"),
                cbStatus, btnApplyFilter);

        VBox userList = new VBox(10);

        Runnable loadUsers = () -> {
            userList.getChildren().setAll(com.freelancing.util.AnimationUtil.createLoadingOverlay("Scanning and loading user accounts..."));
            String query = tfSearch.getText().trim();
            String roleVal = "ALL".equals(cbRole.getValue()) ? null : cbRole.getValue();
            String statusVal = "ALL".equals(cbStatus.getValue()) ? null : cbStatus.getValue();

            com.freelancing.util.AnimationUtil.runAsync(
                () -> {
                    List<User> users = adminService.getUsers(query, roleVal, statusVal);
                    List<Object[]> userRowData = new ArrayList<>();
                    for (User u : users) {
                        FreelancerProfile fp = freelancerProfileDAO.findByUserId(u.getId());
                        int risk = aiService.detectFraudRisk(u, fp);
                        userRowData.add(new Object[] { u, risk });
                    }
                    return userRowData;
                },
                rows -> {
                    userList.getChildren().clear();
                    if (rows.isEmpty()) {
                        VBox emptyCard = UIComponents.createCard();
                        emptyCard.getChildren().add(new Label("No users found matching current filter criteria."));
                        userList.getChildren().add(emptyCard);
                        return;
                    }

                    for (Object[] rowItem : rows) {
                        User u = (User) rowItem[0];
                        int risk = (int) rowItem[1];

                        HBox row = new HBox(12);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setPadding(new Insets(10));
                        row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: "
                                + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

                        VBox info = new VBox(4);
                        Label name = new Label("👤 " + u.getUsername() + " (" + u.getRole() + ")");
                        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                        name.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                        Label meta = new Label("Email: " + u.getEmail() + " | Phone: "
                                + (u.getPhone() != null ? u.getPhone() : "N/A") + " | Status: " + u.getStatus());
                        meta.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                        info.getChildren().addAll(name, meta);

                        Region sp = new Region();
                        HBox.setHgrow(sp, Priority.ALWAYS);

                        Label riskBadge = UIComponents.createBadge("AI Risk: " + risk + "%",
                                risk > 30 ? UIComponents.COLOR_DANGER : UIComponents.COLOR_SUCCESS, "white");

                        HBox actionBtns = new HBox(8);
                        actionBtns.setAlignment(Pos.CENTER_RIGHT);

                        if (u.getStatus() != User.Status.VERIFIED) {
                            Button btnVerify = UIComponents.createSuccessButton("Verify");
                            btnVerify.setOnAction(e -> {
                                adminService.setUserStatus(currentUser.getId(), u.getId(), User.Status.VERIFIED);
                                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Account Verified", "Success",
                                        "Verified user " + u.getUsername());
                                showUserManagement();
                            });
                            actionBtns.getChildren().add(btnVerify);
                        }

                        if (u.getStatus() != User.Status.SUSPENDED && u.getStatus() != User.Status.BANNED) {
                            Button btnSuspend = UIComponents.createDangerButton("Suspend");
                            btnSuspend.setOnAction(e -> {
                                adminService.setUserStatus(currentUser.getId(), u.getId(), User.Status.SUSPENDED);
                                UIComponents.showAlert(Alert.AlertType.WARNING, "Account Suspended", "Action Taken",
                                        "Suspended user " + u.getUsername());
                                showUserManagement();
                            });
                            actionBtns.getChildren().add(btnSuspend);
                        } else {
                            Button btnReactivate = UIComponents.createPrimaryButton("Reactivate");
                            btnReactivate.setOnAction(e -> {
                                adminService.setUserStatus(currentUser.getId(), u.getId(), User.Status.ACTIVE);
                                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Account Reactivated", "Success",
                                        "Reactivated user " + u.getUsername());
                                showUserManagement();
                            });
                            actionBtns.getChildren().add(btnReactivate);
                        }

                        row.getChildren().addAll(info, sp, riskBadge, actionBtns);
                        userList.getChildren().add(row);
                    }
                    com.freelancing.util.AnimationUtil.applyFadeZoom(userList, 220);
                }
            );
        };

        btnApplyFilter.setOnAction(e -> loadUsers.run());
        tfSearch.setOnAction(e -> loadUsers.run());

        loadUsers.run();

        box.getChildren().addAll(title, filterBar, userList);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 3. Project Management
    private void showProjectManagement() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Project & Skill Category Management");

        VBox list = new VBox(10);
        List<Project> projects = new ProjectService().getAllProjects();
        if (projects.isEmpty()) {
            projects = new java.util.ArrayList<>(db.getProjects().values());
        }

        for (Project p : projects) {
            VBox card = UIComponents.createCard();
            Label pName = UIComponents.createHeader("Project: " + p.getTitle() + " [" + p.getCategory() + "]");
            Label pDesc = new Label("Client: " + (p.getClientName() != null ? p.getClientName() : p.getClientId()) +
                    " | Budget: $" + p.getBudget() + " | Status: " + p.getStatus());
            pDesc.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

            Button btnRemove = UIComponents.createDangerButton("Remove / Cancel Project");
            btnRemove.setOnAction(e -> {
                new ProjectDAO().updateStatus(p.getId(), "CANCELLED");
                db.getProjects().remove(p.getId());
                adminService.logAuditEvent(currentUser.getId(), "ADMIN_CANCEL_PROJECT",
                        "Cancelled project " + p.getTitle(), "127.0.0.1");
                showProjectManagement();
            });

            card.getChildren().addAll(pName, pDesc, btnRemove);
            list.getChildren().add(card);
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 4. Dispute Resolution Panel
    private void showDisputes() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("⚖️ Dispute Resolution & Arbitration Panel");

        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10));
        filterBar.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: "
                + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("ALL", "OPEN", "UNDER_REVIEW", "RESOLVED", "DISMISSED");
        cbStatus.setValue("ALL");
        cbStatus.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: white;");

        filterBar.getChildren().addAll(new Label("Filter by Status:"), cbStatus);

        VBox list = new VBox(10);

        Runnable loadDisputes = () -> {
            list.getChildren().clear();
            List<Dispute> disputes = adminService.getDisputes(cbStatus.getValue());

            if (disputes.isEmpty()) {
                VBox c = UIComponents.createCard();
                c.getChildren()
                        .add(new Label("No disputes found matching filter criteria. Platform running smoothly!"));
                list.getChildren().add(c);
            } else {
                for (Dispute d : disputes) {
                    VBox card = UIComponents.createCard();
                    HBox topRow = new HBox(10);
                    topRow.setAlignment(Pos.CENTER_LEFT);

                    Label dTitle = UIComponents.createHeader("Dispute #" + d.getId() + " on: "
                            + (d.getContractTitle() != null ? d.getContractTitle() : "Contract " + d.getContractId()));
                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    String statusColor = UIComponents.COLOR_PRIMARY;
                    if (d.getStatus() == Dispute.Status.OPEN)
                        statusColor = UIComponents.COLOR_AMBER;
                    else if (d.getStatus() == Dispute.Status.RESOLVED || d.getStatus() == Dispute.Status.RESOLVED_CLIENT
                            || d.getStatus() == Dispute.Status.RESOLVED_FREELANCER)
                        statusColor = UIComponents.COLOR_SUCCESS;
                    else if (d.getStatus() == Dispute.Status.DISMISSED)
                        statusColor = UIComponents.COLOR_DANGER;

                    Label badge = UIComponents.createBadge(d.getStatus().name(), statusColor, "white");
                    topRow.getChildren().addAll(dTitle, sp, badge);

                    Label dInfo = new Label(
                            "Raised By: " + (d.getRaisedByName() != null ? d.getRaisedByName() : d.getRaisedById()) +
                                    " | Reason: " + d.getReason() + " | Date: " + d.getCreatedAt());
                    dInfo.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                    Label dDesc = new Label("Description: "
                            + (d.getDescription() != null ? d.getDescription() : "No details provided"));
                    dDesc.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                    dDesc.setWrapText(true);

                    card.getChildren().addAll(topRow, dInfo, dDesc);

                    if (d.getResolution() != null && !d.getResolution().isEmpty()) {
                        Label resLabel = new Label("Resolution: " + d.getResolution() + " (by "
                                + (d.getResolvedByName() != null ? d.getResolvedByName() : d.getResolvedBy()) + ")");
                        resLabel.setStyle("-fx-font-style: italic; -fx-text-fill: " + UIComponents.COLOR_SUCCESS + ";");
                        card.getChildren().add(resLabel);
                    }

                    boolean isPending = (d.getStatus() == Dispute.Status.OPEN
                            || d.getStatus() == Dispute.Status.UNDER_REVIEW
                            || d.getStatus() == Dispute.Status.IN_REVIEW);
                    if (isPending) {
                        Button btnRefundClient = UIComponents.createSecondaryButton("Resolve: Full Refund to Client");
                        btnRefundClient.setOnAction(e -> {
                            adminService.resolveDispute(currentUser.getId(), d.getId(),
                                    "Resolved in favor of Client: Full refund of held escrow.",
                                    Dispute.Status.RESOLVED_CLIENT);
                            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Dispute Resolved", "Client Refunded",
                                    "Full refund has been issued to the client.");
                            showDisputes();
                        });

                        Button btnPayFreelancer = UIComponents
                                .createSuccessButton("Resolve: Release Escrow to Freelancer");
                        btnPayFreelancer.setOnAction(e -> {
                            adminService.resolveDispute(currentUser.getId(), d.getId(),
                                    "Resolved in favor of Freelancer: Milestone escrow disbursed.",
                                    Dispute.Status.RESOLVED_FREELANCER);
                            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Dispute Resolved", "Escrow Released",
                                    "Escrow has been disbursed to the freelancer.");
                            showDisputes();
                        });

                        Button btnDismiss = UIComponents.createDangerButton("Dismiss Claim");
                        btnDismiss.setOnAction(e -> {
                            adminService.resolveDispute(currentUser.getId(), d.getId(),
                                    "Dispute dismissed following administrative arbitration.",
                                    Dispute.Status.DISMISSED);
                            UIComponents.showAlert(Alert.AlertType.WARNING, "Dispute Dismissed", "Arbitration Closed",
                                    "Dispute claim has been dismissed.");
                            showDisputes();
                        });

                        HBox actions = new HBox(10, btnRefundClient, btnPayFreelancer, btnDismiss);
                        card.getChildren().add(actions);
                    }

                    list.getChildren().add(card);
                }
            }
        };

        cbStatus.setOnAction(e -> loadDisputes.run());
        loadDisputes.run();

        box.getChildren().addAll(title, filterBar, list);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 5. AI Talent Matching Monitor
    private void showAiMonitoring() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("🤖 AI Talent Matching & Algorithm Monitor");

        VBox card = UIComponents.createCard();
        card.getChildren().addAll(
                UIComponents.createHeader("AI Recommendation Engine Performance"),
                new Label("• Algorithm: Cosine Skill Similarity + Experience Weighting"),
                new Label("• Average Recommendation Match Precision: 94.2%"),
                new Label("• Total AI Skill Score Calculations Today: 148"),
                new Label("• AI ChatBot Queries Handled: 39"));

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(box);
    }

    // 6. Reports & Analytics
    private void showReports() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📈 Platform Analytics & Executive Reports");

        Map<String, Object> kpis = adminService.getPlatformKpis();
        double volume = ((Number) kpis.getOrDefault("platformVolume", 0.0)).doubleValue();
        double revenue = ((Number) kpis.getOrDefault("platformRevenue", 0.0)).doubleValue();
        double escrow = ((Number) kpis.getOrDefault("activeEscrowBalance", 0.0)).doubleValue();
        int totalContracts = ((Number) kpis.getOrDefault("totalContracts", 0)).intValue();
        int activeContracts = ((Number) kpis.getOrDefault("activeContracts", 0)).intValue();
        int totalUsers = ((Number) kpis.getOrDefault("totalUsers", 0)).intValue();
        int totalProjects = ((Number) kpis.getOrDefault("totalProjects", 0)).intValue();

        VBox summaryCard = UIComponents.createCard();
        summaryCard.getChildren().addAll(
                UIComponents.createHeader("Platform Financial & Operational Summary (Live SQLite)"),
                new Label(String.format("• Total Transacted Volume: $%,.2f", volume)),
                new Label(String.format("• Platform Commission Revenue (10%%): $%,.2f", revenue)),
                new Label(String.format("• Active Milestone Escrow Held: $%,.2f", escrow)),
                new Label(String.format("• Contracts: %d Total (%d Active)", totalContracts, activeContracts)),
                new Label(String.format("• Platform Scale: %d Registered Users | %d Projects Posted", totalUsers,
                        totalProjects)));

        // Report Generation Actions
        HBox actionRow = new HBox(12);
        Button btnGenFinancialReport = UIComponents.createPrimaryButton("📄 Generate Financial Audit Report");
        btnGenFinancialReport.setOnAction(e -> {
            String content = String.format(
                    "FINANCIAL AUDIT REPORT\nGenerated At: %s\nTotal Volume: $%,.2f\nRevenue: $%,.2f\nEscrow Held: $%,.2f\nContracts: %d",
                    java.time.LocalDateTime.now(), volume, revenue, escrow, totalContracts);
            adminService.createReport("Financial Audit Snapshot - " + java.time.LocalDate.now(), "FINANCIAL",
                    currentUser.getId(), content, null);
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Report Generated", "Audit Recorded",
                    "Financial audit report generated and saved to SQLite.");
            showReports();
        });

        Button btnExportReport = UIComponents.createSecondaryButton("📊 Export System Summary to CSV");
        btnExportReport.setOnAction(e -> {
            File csv = adminService.exportTableToCsv("reports", "exports");
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Export Complete", "CSV Exported",
                    "Reports exported to: " + csv.getAbsolutePath());
        });

        actionRow.getChildren().addAll(btnGenFinancialReport, btnExportReport);

        // Saved Reports List
        VBox reportListCard = UIComponents.createCard();
        reportListCard.getChildren().add(UIComponents.createHeader("📑 Saved System & Financial Reports"));

        List<Report> reports = adminService.getReports();
        if (reports.isEmpty()) {
            reportListCard.getChildren()
                    .add(new Label("No administrative reports generated yet. Click above to generate one."));
        } else {
            for (Report r : reports) {
                HBox rRow = new HBox(12);
                rRow.setAlignment(Pos.CENTER_LEFT);
                rRow.setPadding(new Insets(8));
                rRow.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 6;");

                VBox rInfo = new VBox(2);
                Label rTitle = new Label(r.getTitle() + " [" + r.getReportType() + "]");
                rTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                rTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label rMeta = new Label(
                        "Generated by " + (r.getGeneratedByName() != null ? r.getGeneratedByName() : r.getGeneratedBy())
                                + " on " + r.getCreatedAt());
                rMeta.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                rInfo.getChildren().addAll(rTitle, rMeta);

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Button btnDelete = UIComponents.createDangerButton("🗑️ Delete");
                btnDelete.setOnAction(e -> {
                    adminService.deleteReport(r.getId());
                    showReports();
                });

                rRow.getChildren().addAll(rInfo, sp, btnDelete);
                reportListCard.getChildren().add(rRow);
            }
        }

        box.getChildren().addAll(title, summaryCard, actionRow, reportListCard);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 7. Announcements & Support
    private void showAnnouncements() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📢 System Broadcast Announcements & Support Desk");

        VBox card = UIComponents.createCard();
        TextField tfAnnounceTitle = UIComponents.createTextField("Announcement Title...");
        TextArea taAnnounceMsg = UIComponents.createTextArea("Broadcast message to all users on platform...");

        Button btnBroadcast = UIComponents.createPrimaryButton("Broadcast System Announcement");
        btnBroadcast.setOnAction(e -> {
            String t = tfAnnounceTitle.getText().trim();
            String m = taAnnounceMsg.getText().trim();
            if (!t.isEmpty() && !m.isEmpty()) {
                List<User> users = adminService.getUsers("", null, null);
                for (User u : users) {
                    notifService.sendNotification(u.getId(), "📢 ANNOUNCEMENT: " + t, m);
                }
                adminService.logAuditEvent(currentUser.getId(), "BROADCAST_ANNOUNCEMENT", t, "127.0.0.1");
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Broadcast Success", "Announcement Sent",
                        "Announcement sent to " + users.size() + " platform users!");
                tfAnnounceTitle.clear();
                taAnnounceMsg.clear();
            }
        });

        card.getChildren().addAll(UIComponents.createHeader("Send Platform-Wide Announcement:"), tfAnnounceTitle,
                taAnnounceMsg, btnBroadcast);

        VBox tktCard = UIComponents.createCard();
        HBox tktHeaderRow = new HBox(10);
        tktHeaderRow.setAlignment(Pos.CENTER_LEFT);
        tktHeaderRow.getChildren().add(UIComponents.createHeader("User Support Tickets Desk:"));

        Region tktSp = new Region();
        HBox.setHgrow(tktSp, Priority.ALWAYS);

        ComboBox<String> cbTicketFilter = new ComboBox<>();
        cbTicketFilter.getItems().addAll("ALL", "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");
        cbTicketFilter.setValue("ALL");
        cbTicketFilter.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: white;");

        tktHeaderRow.getChildren().addAll(tktSp, new Label("Filter:"), cbTicketFilter);
        tktCard.getChildren().add(tktHeaderRow);

        VBox ticketList = new VBox(8);

        Runnable loadTickets = () -> {
            ticketList.getChildren().clear();
            List<SupportTicket> tickets = adminService.getSupportTickets(cbTicketFilter.getValue());
            if (tickets.isEmpty()) {
                ticketList.getChildren().add(new Label("No support tickets matching selected filter."));
            } else {
                for (SupportTicket st : tickets) {
                    VBox tRow = new VBox(6);
                    tRow.setPadding(new Insets(10));
                    tRow.setStyle(
                            "-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 6;");

                    HBox r1 = new HBox(10);
                    r1.setAlignment(Pos.CENTER_LEFT);

                    Label stLabel = new Label("Ticket #" + st.getId() + " by "
                            + (st.getUserName() != null ? st.getUserName() : st.getUserId()) + ": " + st.getSubject());
                    stLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    stLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    String prioColor = UIComponents.COLOR_AMBER;
                    if (st.getPriority() == SupportTicket.Priority.URGENT
                            || st.getPriority() == SupportTicket.Priority.HIGH)
                        prioColor = UIComponents.COLOR_DANGER;
                    else if (st.getPriority() == SupportTicket.Priority.LOW)
                        prioColor = UIComponents.COLOR_SUCCESS;

                    Label prioBadge = UIComponents.createBadge(st.getPriority().name(), prioColor, "white");
                    Label statBadge = UIComponents.createBadge(st.getStatus().name(),
                            st.getStatus() == SupportTicket.Status.RESOLVED ? UIComponents.COLOR_SUCCESS
                                    : UIComponents.COLOR_PRIMARY,
                            "white");

                    r1.getChildren().addAll(stLabel, sp, prioBadge, statBadge);

                    Label stMsg = new Label("Message: " + st.getDescription());
                    stMsg.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                    stMsg.setWrapText(true);

                    HBox actions = new HBox(8);
                    if (st.getStatus() == SupportTicket.Status.OPEN) {
                        Button btnProgress = UIComponents.createPrimaryButton("In Progress");
                        btnProgress.setOnAction(e -> {
                            adminService.updateTicketStatus(currentUser.getId(), st.getId(),
                                    SupportTicket.Status.IN_PROGRESS);
                            showAnnouncements();
                        });
                        actions.getChildren().add(btnProgress);
                    }
                    if (st.getStatus() != SupportTicket.Status.RESOLVED
                            && st.getStatus() != SupportTicket.Status.CLOSED) {
                        Button btnResolve = UIComponents.createSuccessButton("Mark Resolved");
                        btnResolve.setOnAction(e -> {
                            adminService.updateTicketStatus(currentUser.getId(), st.getId(),
                                    SupportTicket.Status.RESOLVED);
                            showAnnouncements();
                        });
                        actions.getChildren().add(btnResolve);
                    }
                    if (st.getStatus() != SupportTicket.Status.CLOSED) {
                        Button btnClose = UIComponents.createSecondaryButton("Close");
                        btnClose.setOnAction(e -> {
                            adminService.updateTicketStatus(currentUser.getId(), st.getId(),
                                    SupportTicket.Status.CLOSED);
                            showAnnouncements();
                        });
                        actions.getChildren().add(btnClose);
                    }

                    tRow.getChildren().addAll(r1, stMsg, actions);
                    ticketList.getChildren().add(tRow);
                }
            }
        };

        cbTicketFilter.setOnAction(e -> loadTickets.run());
        loadTickets.run();

        tktCard.getChildren().add(ticketList);
        box.getChildren().addAll(title, card, tktCard);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 8. DB Backup & Logs
    private void showBackupAndLogs() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("💾 Database Backup, Data Exports & System Logs");

        // SQLite Snapshot Backup Card
        VBox backupCard = UIComponents.createCard();
        Label backupHeader = UIComponents.createHeader("SQLite Database Snapshot Backup");
        Label backupDesc = new Label(
                "Create an exact physical SQLite snapshot of data/skillbridge.db into the backups/ folder with automatic timestamps and security audit logging.");
        backupDesc.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        backupDesc.setWrapText(true);

        Button btnBackup = UIComponents.createPrimaryButton("💾 Create Instant SQLite Database Snapshot");
        btnBackup.setOnAction(e -> {
            try {
                File backup = adminService.backupDatabase("backups");
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Database Backup Complete", "Snapshot Created",
                        "Successfully created database snapshot:\n" + backup.getAbsolutePath());
                showBackupAndLogs();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Backup Failed", "Error", ex.getMessage());
            }
        });

        backupCard.getChildren().addAll(backupHeader, backupDesc, btnBackup);

        // CSV Table Data Export Card
        VBox exportCard = UIComponents.createCard();
        Label exportHeader = UIComponents.createHeader("CSV Relational Table Data Export");
        Label exportDesc = new Label(
                "Export complete SQLite database tables to CSV format for external reporting, analysis, or offline archival.");
        exportDesc.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

        HBox exportRow = new HBox(12);
        exportRow.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cbTables = new ComboBox<>();
        cbTables.getItems().addAll("users", "projects", "contracts", "milestones", "disputes", "support_tickets",
                "audit_logs", "reports");
        cbTables.setValue("users");
        cbTables.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: white;");

        Button btnExportCsv = UIComponents.createSecondaryButton("📥 Export Selected Table to CSV");
        btnExportCsv.setOnAction(e -> {
            try {
                String selected = cbTables.getValue();
                File csv = adminService.exportTableToCsv(selected, "exports");
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "CSV Export Complete", "Export Success",
                        "Successfully exported table '" + selected + "' to:\n" + csv.getAbsolutePath());
                showBackupAndLogs();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Export Failed", "Error", ex.getMessage());
            }
        });

        exportRow.getChildren().addAll(new Label("Select Database Table:"), cbTables, btnExportCsv);
        exportCard.getChildren().addAll(exportHeader, exportDesc, exportRow);

        // Security Audit Logs
        VBox auditCard = UIComponents.createCard();
        HBox auditHeaderRow = new HBox(10);
        auditHeaderRow.setAlignment(Pos.CENTER_LEFT);
        auditHeaderRow.getChildren()
                .add(UIComponents.createHeader("🛡️ Security & Operational Audit Logs (Recent 50 Events)"));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnRefreshLogs = UIComponents.createSecondaryButton("🔄 Refresh");
        btnRefreshLogs.setOnAction(e -> showBackupAndLogs());

        auditHeaderRow.getChildren().addAll(sp, btnRefreshLogs);
        auditCard.getChildren().add(auditHeaderRow);

        VBox logList = new VBox(6);
        List<AuditLog> logs = adminService.getAuditLogs(50);
        if (logs.isEmpty()) {
            Label noLogs = new Label("No audit logs recorded yet.");
            noLogs.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            logList.getChildren().add(noLogs);
        } else {
            for (AuditLog al : logs) {
                HBox logRow = new HBox(10);
                logRow.setAlignment(Pos.CENTER_LEFT);
                logRow.setPadding(new Insets(6));
                logRow.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 4;");

                Label ts = new Label("[" + al.getTimestamp() + "]");
                ts.setFont(Font.font("Consolas", 11));
                ts.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                Label actionLbl = UIComponents.createBadge(al.getAction(), UIComponents.COLOR_PRIMARY, "white");

                Label userLbl = new Label("👤 " + (al.getUsername() != null ? al.getUsername() : al.getUserId()));
                userLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                userLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label ipLbl = new Label("(" + al.getIpAddress() + ")");
                ipLbl.setFont(Font.font("Consolas", 11));
                ipLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                Label detailsLbl = new Label(al.getDetails());
                detailsLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                detailsLbl.setWrapText(true);

                logRow.getChildren().addAll(ts, actionLbl, userLbl, ipLbl, detailsLbl);
                logList.getChildren().add(logRow);
            }
        }

        auditCard.getChildren().add(logList);

        box.getChildren().addAll(title, backupCard, exportCard, auditCard);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }

    // 9. Feed Moderation Panel
    private void showFeedModeration() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📰 Feed Moderation & Content Management");

        // Stats
        long activeCount = db.getFeedPosts().values().stream().filter(p -> p.getStatus() == FeedPost.PostStatus.ACTIVE)
                .count();
        long flaggedCount = db.getFeedPosts().values().stream()
                .filter(p -> p.getStatus() == FeedPost.PostStatus.FLAGGED).count();
        long removedCount = db.getFeedPosts().values().stream()
                .filter(p -> p.getStatus() == FeedPost.PostStatus.REMOVED).count();

        HBox statGrid = new HBox(15);
        VBox cardActive = UIComponents.createStatCard(0, "✅", "Active Posts", String.valueOf(activeCount),
                UIComponents.COLOR_SUCCESS);
        VBox cardFlagged = UIComponents.createStatCard(1, "⚠️", "Flagged Posts", String.valueOf(flaggedCount),
                UIComponents.COLOR_AMBER);
        VBox cardRemoved = UIComponents.createStatCard(2, "❌", "Removed Posts", String.valueOf(removedCount),
                UIComponents.COLOR_DANGER);
        VBox cardTotal = UIComponents.createStatCard(3, "📊", "Total Posts", String.valueOf(db.getFeedPosts().size()),
                UIComponents.COLOR_PRIMARY);
        statGrid.getChildren().addAll(cardActive, cardFlagged, cardRemoved, cardTotal);

        // All posts list
        VBox postList = new VBox(10);
        List<FeedPost> allPosts = db.getFeedPosts().values().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());

        if (allPosts.isEmpty()) {
            VBox emptyCard = UIComponents.createCard();
            Label emptyLbl = new Label("No feed posts have been created on the platform yet.");
            emptyLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            emptyCard.getChildren().add(emptyLbl);
            postList.getChildren().add(emptyCard);
        } else {
            for (FeedPost post : allPosts) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(12));
                row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: "
                        + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

                VBox info = new VBox(4);
                Label postHeader = new Label(("FREELANCER".equals(post.getAuthorRole()) ? "👤" : "🏢") + " "
                        + post.getAuthorName() + " (" + post.getAuthorRole() + ")");
                postHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                postHeader.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label postTitle2 = new Label("📝 " + post.getTitle());
                postTitle2.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
                postTitle2.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                postTitle2.setWrapText(true);

                String contentPreview = post.getContent().length() > 120 ? post.getContent().substring(0, 120) + "..."
                        : post.getContent();
                Label postDesc = new Label(contentPreview);
                postDesc.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                postDesc.setWrapText(true);
                postDesc.setFont(Font.font("Segoe UI", 12));

                Label metaLbl = new Label("📅 " + post.getTimestamp() + " | 👍 " + post.getLikes() + " likes | 💬 "
                        + post.getComments().size() + " comments | 🏷️ " + post.getCategory());
                metaLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                metaLbl.setFont(Font.font("Segoe UI", 11));

                info.getChildren().addAll(postHeader, postTitle2, postDesc, metaLbl);

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                // Status badge
                String statusColor;
                switch (post.getStatus()) {
                    case ACTIVE:
                        statusColor = UIComponents.COLOR_SUCCESS;
                        break;
                    case FLAGGED:
                        statusColor = UIComponents.COLOR_AMBER;
                        break;
                    case REMOVED:
                        statusColor = UIComponents.COLOR_DANGER;
                        break;
                    default:
                        statusColor = UIComponents.COLOR_PRIMARY;
                }
                Label statusBadge = UIComponents.createBadge(post.getStatus().toString(), statusColor, "white");

                // Action buttons
                VBox actionBtns = new VBox(4);
                actionBtns.setAlignment(Pos.CENTER);

                if (post.getStatus() == FeedPost.PostStatus.ACTIVE) {
                    Button btnFlag = UIComponents.createSecondaryButton("⚠️ Flag");
                    btnFlag.setOnAction(e -> {
                        post.setStatus(FeedPost.PostStatus.FLAGGED);
                        db.getFeedPosts().put(post.getId(), post);
                        db.logActivity("Admin flagged feed post: " + post.getTitle() + " by " + post.getAuthorName());
                        db.saveData();
                        showFeedModeration();
                    });
                    Button btnRemove = UIComponents.createDangerButton("❌ Remove");
                    btnRemove.setOnAction(e -> {
                        post.setStatus(FeedPost.PostStatus.REMOVED);
                        db.getFeedPosts().put(post.getId(), post);
                        db.logActivity("Admin removed feed post: " + post.getTitle() + " by " + post.getAuthorName());
                        db.saveData();
                        showFeedModeration();
                    });
                    actionBtns.getChildren().addAll(btnFlag, btnRemove);
                } else if (post.getStatus() == FeedPost.PostStatus.FLAGGED) {
                    Button btnRestore = UIComponents.createSuccessButton("✅ Restore");
                    btnRestore.setOnAction(e -> {
                        post.setStatus(FeedPost.PostStatus.ACTIVE);
                        db.getFeedPosts().put(post.getId(), post);
                        db.logActivity("Admin restored feed post: " + post.getTitle() + " by " + post.getAuthorName());
                        db.saveData();
                        showFeedModeration();
                    });
                    Button btnRemove = UIComponents.createDangerButton("❌ Remove");
                    btnRemove.setOnAction(e -> {
                        post.setStatus(FeedPost.PostStatus.REMOVED);
                        db.getFeedPosts().put(post.getId(), post);
                        db.logActivity(
                                "Admin removed flagged post: " + post.getTitle() + " by " + post.getAuthorName());
                        db.saveData();
                        showFeedModeration();
                    });
                    actionBtns.getChildren().addAll(btnRestore, btnRemove);
                } else {
                    Button btnRestore = UIComponents.createSuccessButton("✅ Restore");
                    btnRestore.setOnAction(e -> {
                        post.setStatus(FeedPost.PostStatus.ACTIVE);
                        db.getFeedPosts().put(post.getId(), post);
                        db.logActivity(
                                "Admin restored removed post: " + post.getTitle() + " by " + post.getAuthorName());
                        db.saveData();
                        showFeedModeration();
                    });
                    Button btnDelete = UIComponents.createDangerButton("🗑️ Delete");
                    btnDelete.setOnAction(e -> {
                        db.getFeedPosts().remove(post.getId());
                        db.logActivity(
                                "Admin permanently deleted post: " + post.getTitle() + " by " + post.getAuthorName());
                        db.saveData();
                        showFeedModeration();
                    });
                    actionBtns.getChildren().addAll(btnRestore, btnDelete);
                }

                row.getChildren().addAll(info, sp, statusBadge, actionBtns);
                postList.getChildren().add(row);
            }
        }

        box.getChildren().addAll(title, statGrid, postList);
        contentArea.getChildren().setAll(UIComponents.createScrollPane(box));
    }
}
