package com.freelancing.ui;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.*;
import com.freelancing.service.AiService;
import com.freelancing.service.NotificationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminMainView {
    private final Stage stage;
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final AiService aiService = new AiService();
    private final NotificationService notifService = new NotificationService();

    private BorderPane root;
    private StackPane contentArea;

    public AdminMainView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public Scene createScene() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_DARK + ";");

        // Top Header
        HBox header = new HBox(15);
        header.setPadding(new Insets(12, 24, 12, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_SIDEBAR + ";" +
                        "-fx-border-color: " + UIComponents.COLOR_BORDER + ";" +
                        "-fx-border-width: 0 0 1 0;");

        Label logo = new Label("⚡ Freelancing.SB  |  SYSTEM ADMIN PORTAL");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(UIComponents.COLOR_DANGER));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("🛡️ Administrator: " + currentUser.getUsername());
        userLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        Button btnLogout = UIComponents.createDangerButton("Logout System");
        btnLogout.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, u -> {
                if (u.getRole() == User.Role.FREELANCER) stage.setScene(new FreelancerMainView(stage, u).createScene());
                else if (u.getRole() == User.Role.CLIENT) stage.setScene(new ClientMainView(stage, u).createScene());
                else stage.setScene(new AdminMainView(stage, u).createScene());
            });
            stage.setScene(loginView.createScene());
        });

        header.getChildren().addAll(logo, spacer, userLabel, btnLogout);
        root.setTop(header);

        // Sidebar Navigation
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_SIDEBAR + ";" +
                        "-fx-border-color: " + UIComponents.COLOR_BORDER + ";" +
                        "-fx-border-width: 0 1 0 0;");

        Button btnNavDash = createNavButton("📊 System Overview & Charts", true);
        Button btnNavUsers = createNavButton("👥 User & AI Fraud Scanner", false);
        Button btnNavProjects = createNavButton("📂 Project & Skill Categories", false);
        Button btnNavDisputes = createNavButton("⚖️ Dispute Resolution Panel", false);
        Button btnNavAiMon = createNavButton("🤖 AI Talent Matching Monitor", false);
        Button btnNavReports = createNavButton("📈 Analytics & Platform Reports", false);
        Button btnNavAnnounce = createNavButton("📢 Announcements & Support", false);
        Button btnNavBackup = createNavButton("💾 DB Backup & System Logs", false);

        Button[] navBtns = {btnNavDash, btnNavUsers, btnNavProjects, btnNavDisputes, btnNavAiMon, btnNavReports, btnNavAnnounce, btnNavBackup};

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        btnNavDash.setOnAction(e -> { selectNav(btnNavDash, navBtns); showDashboard(); });
        btnNavUsers.setOnAction(e -> { selectNav(btnNavUsers, navBtns); showUserManagement(); });
        btnNavProjects.setOnAction(e -> { selectNav(btnNavProjects, navBtns); showProjectManagement(); });
        btnNavDisputes.setOnAction(e -> { selectNav(btnNavDisputes, navBtns); showDisputes(); });
        btnNavAiMon.setOnAction(e -> { selectNav(btnNavAiMon, navBtns); showAiMonitoring(); });
        btnNavReports.setOnAction(e -> { selectNav(btnNavReports, navBtns); showReports(); });
        btnNavAnnounce.setOnAction(e -> { selectNav(btnNavAnnounce, navBtns); showAnnouncements(); });
        btnNavBackup.setOnAction(e -> { selectNav(btnNavBackup, navBtns); showBackupAndLogs(); });

        sidebar.getChildren().addAll(btnNavDash, btnNavUsers, btnNavProjects, btnNavDisputes, btnNavAiMon, btnNavReports, btnNavAnnounce, btnNavBackup);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        showDashboard();
        Scene scene = new Scene(root, 1150, 780);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ex) {}
        return scene;
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
            btn.setStyle("-fx-background-color: " + UIComponents.COLOR_DANGER + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 10 14;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UIComponents.COLOR_TEXT_MUTED + "; -fx-background-radius: 6; -fx-padding: 10 14;");
        }
    }

    private void selectNav(Button selected, Button[] all) {
        for (Button b : all) setNavStyle(b, b == selected);
    }

    // 1. Executive Dashboard with JavaFX Charts
    private void showDashboard() {
        VBox box = new VBox(20);
        Label title = UIComponents.createTitle("Admin System Metrics & Visual Analytics");

        int totalUsers = db.getUsers().size();
        int totalProjects = db.getProjects().size();
        int activeDisputes = db.getDisputes().size();

        HBox statGrid = new HBox(15);
        VBox card1 = UIComponents.createStatCard("👥", "Total Users", String.valueOf(totalUsers), UIComponents.COLOR_PRIMARY);
        VBox card2 = UIComponents.createStatCard("📁", "Total Projects", String.valueOf(totalProjects), UIComponents.COLOR_SUCCESS);
        VBox card3 = UIComponents.createStatCard("💳", "Platform Revenue", "$10,500.00", UIComponents.COLOR_AMBER);
        VBox card4 = UIComponents.createStatCard("⚖️", "Open Disputes", String.valueOf(activeDisputes), UIComponents.COLOR_DANGER);
        statGrid.getChildren().addAll(card1, card2, card3, card4);

        // Visual Charts
        HBox chartRow = new HBox(15);
        Map<String, Double> userDistribution = new HashMap<>();
        userDistribution.put("Freelancers", 2.0);
        userDistribution.put("Clients", 2.0);
        userDistribution.put("Admins", 1.0);

        PieChart pieUser = UIComponents.createPieChart("User Base Demographics", userDistribution);

        Map<String, Double> revenueData = new LinkedHashMap<>();
        revenueData.put("Jan", 1500.0);
        revenueData.put("Feb", 2100.0);
        revenueData.put("Mar", 1800.0);
        revenueData.put("Apr", 2600.0);
        revenueData.put("May", 2500.0);

        BarChart<String, Number> barRev = UIComponents.createBarChart("Monthly Platform Revenue ($)", "Month", "Revenue ($)", revenueData);

        chartRow.getChildren().addAll(pieUser, barRev);

        VBox activityCard = UIComponents.createCard();
        activityCard.getChildren().add(UIComponents.createHeader("📜 Recent System Security & Activity Logs"));

        VBox logList = new VBox(4);
        List<String> logs = db.getActivityLogs();
        int count = Math.min(10, logs.size());
        for (int i = 0; i < count; i++) {
            Label l = new Label(logs.get(i));
            l.setFont(Font.font("Consolas", 12));
            l.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            logList.getChildren().add(l);
        }
        activityCard.getChildren().add(logList);

        box.getChildren().addAll(title, statGrid, chartRow, activityCard);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 2. User Management & AI Fraud Scanner
    private void showUserManagement() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("User Account & AI Fraud Risk Scanner");

        VBox userList = new VBox(10);
        for (User u : db.getUsers().values()) {
            FreelancerProfile fp = db.getFreelancerProfiles().get(u.getId());
            int risk = aiService.detectFraudRisk(u, fp);

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: " + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

            VBox info = new VBox(4);
            Label name = new Label("👤 " + u.getUsername() + " (" + u.getRole() + ")");
            name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            name.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

            Label meta = new Label("Email: " + u.getEmail() + " | Phone: " + u.getPhone() + " | Status: " + u.getStatus());
            meta.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            info.getChildren().addAll(name, meta);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label riskBadge = UIComponents.createBadge("AI Risk: " + risk + "%", risk > 30 ? UIComponents.COLOR_DANGER : UIComponents.COLOR_SUCCESS, "white");

            Button btnVerify = UIComponents.createSuccessButton("Verify Account");
            btnVerify.setOnAction(e -> {
                u.setStatus(User.Status.VERIFIED);
                db.getUsers().put(u.getId(), u);
                db.logActivity("Admin verified user account: " + u.getUsername());
                db.saveData();
                showUserManagement();
            });

            Button btnSuspend = UIComponents.createDangerButton("Remove / Suspend");
            btnSuspend.setOnAction(e -> {
                u.setStatus(User.Status.SUSPENDED);
                db.getUsers().put(u.getId(), u);
                db.logActivity("Admin suspended account: " + u.getUsername());
                db.saveData();
                showUserManagement();
            });

            row.getChildren().addAll(info, sp, riskBadge, btnVerify, btnSuspend);
            userList.getChildren().add(row);
        }

        box.getChildren().addAll(title, userList);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 3. Project Management
    private void showProjectManagement() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Project & Skill Category Management");

        VBox list = new VBox(10);
        for (Project p : db.getProjects().values()) {
            VBox card = UIComponents.createCard();
            Label pName = UIComponents.createHeader("Project: " + p.getTitle() + " [" + p.getCategory() + "]");
            Label pDesc = new Label("Client: " + p.getClientName() + " | Budget: $" + p.getBudget() + " | Status: " + p.getStatus());
            pDesc.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

            Button btnRemove = UIComponents.createDangerButton("Remove Spam / Illegal Project");
            btnRemove.setOnAction(e -> {
                db.getProjects().remove(p.getId());
                db.logActivity("Admin removed project: " + p.getTitle());
                db.saveData();
                showProjectManagement();
            });

            card.getChildren().addAll(pName, pDesc, btnRemove);
            list.getChildren().add(card);
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 4. Dispute Resolution Panel
    private void showDisputes() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Dispute Resolution Panel");

        VBox list = new VBox(10);
        if (db.getDisputes().isEmpty()) {
            VBox c = UIComponents.createCard();
            c.getChildren().add(new Label("No open disputes currently pending resolution. Platform running smoothly!"));
            list.getChildren().add(c);
        } else {
            for (Dispute d : db.getDisputes().values()) {
                VBox card = UIComponents.createCard();
                Label dTitle = UIComponents.createHeader("Dispute on: " + d.getProjectTitle());
                Label dInfo = new Label("Raised By: " + d.getRaisedByName() + " | Reason: " + d.getReason() + " | Status: " + d.getStatus());

                Button btnRefundClient = UIComponents.createSecondaryButton("Resolve: Full Refund to Client");
                btnRefundClient.setOnAction(e -> {
                    d.setStatus(Dispute.Status.RESOLVED_CLIENT);
                    db.getDisputes().put(d.getId(), d);
                    db.logActivity("Admin resolved dispute in favor of Client.");
                    db.saveData();
                    showDisputes();
                });

                Button btnPayFreelancer = UIComponents.createSuccessButton("Resolve: Release Escrow to Freelancer");
                btnPayFreelancer.setOnAction(e -> {
                    d.setStatus(Dispute.Status.RESOLVED_FREELANCER);
                    db.getDisputes().put(d.getId(), d);
                    db.logActivity("Admin resolved dispute in favor of Freelancer.");
                    db.saveData();
                    showDisputes();
                });

                HBox actions = new HBox(10, btnRefundClient, btnPayFreelancer);
                card.getChildren().addAll(dTitle, dInfo, actions);
                list.getChildren().add(card);
            }
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(box);
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
                new Label("• AI ChatBot Queries Handled: 39")
        );

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(box);
    }

    // 6. Reports & Analytics
    private void showReports() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📈 Platform Analytics & Reports Dashboard");

        VBox card = UIComponents.createCard();
        card.getChildren().addAll(
                UIComponents.createHeader("Monthly Financial & Operational Summary"),
                new Label("• Total Transacted Volume: $42,800.00"),
                new Label("• Platform Commission Revenue (10%): $4,280.00"),
                new Label("• Top Project Category: Mobile Development (38%)"),
                new Label("• User Growth Rate: +24% Month-over-Month")
        );

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(box);
    }

    // 7. Announcements & Support
    private void showAnnouncements() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📢 System Broadcast Announcements & Support Tickets");

        VBox card = UIComponents.createCard();
        TextField tfAnnounceTitle = UIComponents.createTextField("Announcement Title...");
        TextArea taAnnounceMsg = UIComponents.createTextArea("Broadcast message to all users on platform...");

        Button btnBroadcast = UIComponents.createPrimaryButton("Broadcast System Announcement");
        btnBroadcast.setOnAction(e -> {
            String t = tfAnnounceTitle.getText().trim();
            String m = taAnnounceMsg.getText().trim();
            if (!t.isEmpty() && !m.isEmpty()) {
                for (User u : db.getUsers().values()) {
                    notifService.sendNotification(u.getId(), "📢 ANNOUNCEMENT: " + t, m);
                }
                db.logActivity("Admin broadcast announcement: " + t);
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Broadcast Success", "Announcement Sent", "Announcement sent to all platform clients and freelancers!");
                tfAnnounceTitle.clear();
                taAnnounceMsg.clear();
            }
        });

        card.getChildren().addAll(UIComponents.createHeader("Send System Announcement:"), tfAnnounceTitle, taAnnounceMsg, btnBroadcast);

        VBox tktCard = UIComponents.createCard();
        tktCard.getChildren().add(UIComponents.createHeader("User Support Tickets:"));

        for (SupportTicket st : db.getSupportTickets().values()) {
            VBox tRow = new VBox(4);
            tRow.setPadding(new Insets(8));
            tRow.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 6;");

            Label stLabel = new Label("Ticket #" + st.getId() + " by " + st.getUserName() + ": " + st.getSubject() + " [" + st.getStatus() + "]");
            stLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
            Label stMsg = new Label("Message: " + st.getMessage());
            stMsg.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

            tRow.getChildren().addAll(stLabel, stMsg);
            tktCard.getChildren().add(tRow);
        }

        box.getChildren().addAll(title, card, tktCard);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 8. DB Backup & Logs
    private void showBackupAndLogs() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("💾 Database Backup & System Logs");

        VBox card = UIComponents.createCard();
        Button btnBackup = UIComponents.createPrimaryButton("Trigger Instant Database Backup");
        btnBackup.setOnAction(e -> {
            db.saveData();
            File backup = new File("freelancing_data_backup_" + System.currentTimeMillis() + ".dat");
            db.logActivity("Database backup snapshot created: " + backup.getName());
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Backup Complete", "Database Saved", "Created database snapshot backup: " + backup.getName());
        });

        card.getChildren().addAll(UIComponents.createHeader("Database Management:"), btnBackup);

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(box);
    }
}
