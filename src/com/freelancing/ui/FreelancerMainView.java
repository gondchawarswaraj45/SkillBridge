package com.freelancing.ui;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.*;
import com.freelancing.service.AiService;
import com.freelancing.service.NotificationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FreelancerMainView {
    private final Stage stage;
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final AiService aiService = new AiService();
    private final NotificationService notifService = new NotificationService();

    private BorderPane root;
    private StackPane contentArea;

    public FreelancerMainView(Stage stage, User user) {
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

        Label logo = new Label("⚡ Freelancing.SB  |  Freelancer Portal");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("👤 " + currentUser.getUsername() + " (Verified Freelancer)");
        userLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        Button btnLogout = UIComponents.createDangerButton("Logout");
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
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_SIDEBAR + ";" +
                        "-fx-border-color: " + UIComponents.COLOR_BORDER + ";" +
                        "-fx-border-width: 0 1 0 0;");

        Button btnNavDash = createNavButton("📊 Dashboard Analytics", true);
        Button btnNavProfile = createNavButton("👤 Profile & Portfolio", false);
        Button btnNavSearch = createNavButton("🔍 Search & AI Matches", false);
        Button btnNavProjects = createNavButton("📂 Active Projects", false);
        Button btnNavCal = createNavButton("📅 Calendar & Schedule", false);
        Button btnNavChat = createNavButton("💬 Encrypted Chat & Meet", false);
        Button btnNavAiBot = createNavButton("🤖 AI Career Coach", false);
        Button btnNavNotif = createNavButton("🔔 Notifications", false);

        Button[] navBtns = {btnNavDash, btnNavProfile, btnNavSearch, btnNavProjects, btnNavCal, btnNavChat, btnNavAiBot, btnNavNotif};

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        btnNavDash.setOnAction(e -> { selectNav(btnNavDash, navBtns); showDashboard(); });
        btnNavProfile.setOnAction(e -> { selectNav(btnNavProfile, navBtns); showProfile(); });
        btnNavSearch.setOnAction(e -> { selectNav(btnNavSearch, navBtns); showProjectSearch(); });
        btnNavProjects.setOnAction(e -> { selectNav(btnNavProjects, navBtns); showActiveProjects(); });
        btnNavCal.setOnAction(e -> { selectNav(btnNavCal, navBtns); showCalendar(); });
        btnNavChat.setOnAction(e -> { selectNav(btnNavChat, navBtns); showChat(); });
        btnNavAiBot.setOnAction(e -> { selectNav(btnNavAiBot, navBtns); showAiCareerCoach(); });
        btnNavNotif.setOnAction(e -> { selectNav(btnNavNotif, navBtns); showNotifications(); });

        sidebar.getChildren().addAll(btnNavDash, btnNavProfile, btnNavSearch, btnNavProjects, btnNavCal, btnNavChat, btnNavAiBot, btnNavNotif);
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
            btn.setStyle("-fx-background-color: " + UIComponents.COLOR_PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 10 14;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UIComponents.COLOR_TEXT_MUTED + "; -fx-background-radius: 6; -fx-padding: 10 14;");
        }
    }

    private void selectNav(Button selected, Button[] all) {
        for (Button b : all) setNavStyle(b, b == selected);
    }

    // 1. Dashboard View with JavaFX Charts
    private void showDashboard() {
        VBox box = new VBox(20);
        Label title = UIComponents.createTitle("Freelancer Dashboard & Analytics");

        FreelancerProfile profile = db.getFreelancerProfiles().get(currentUser.getId());
        double rating = profile != null ? profile.getRating() : 5.0;
        int completed = profile != null ? profile.getCompletedProjects() : 0;

        HBox statGrid = new HBox(15);
        VBox card1 = UIComponents.createStatCard("💵", "Total Earnings", "$4,850.00", UIComponents.COLOR_SUCCESS);
        VBox card2 = UIComponents.createStatCard("📂", "Completed Projects", String.valueOf(completed), UIComponents.COLOR_PRIMARY);
        VBox card3 = UIComponents.createStatCard("⭐", "Client Rating", String.format("%.2f / 5.0", rating), UIComponents.COLOR_AMBER);
        VBox card4 = UIComponents.createStatCard("⚡", "AI Skill Score", "96%", UIComponents.COLOR_PURPLE);
        statGrid.getChildren().addAll(card1, card2, card3, card4);

        // Earnings Growth Chart
        Map<String, Double> chartData = new LinkedHashMap<>();
        chartData.put("Feb", 1200.0);
        chartData.put("Mar", 1800.0);
        chartData.put("Apr", 2400.0);
        chartData.put("May", 3100.0);
        chartData.put("Jun", 3900.0);
        chartData.put("Jul", 4850.0);

        LineChart<String, Number> earningsChart = UIComponents.createLineChart("📈 Earnings Growth Trend ($)", "Month", "Revenue ($)", chartData);
        VBox chartCard = UIComponents.createCard();
        chartCard.getChildren().addAll(UIComponents.createHeader("Financial & Performance Growth"), earningsChart);

        VBox recCard = UIComponents.createCard();
        recCard.getChildren().add(UIComponents.createHeader("🎯 AI Recommended Projects for You"));

        for (Project p : db.getProjects().values()) {
            if (p.getStatus() == Project.Status.OPEN) {
                int matchScore = aiService.calculateMatchScore(profile, p);
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 8;");

                VBox details = new VBox(4);
                Label pTitle = new Label(p.getTitle());
                pTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                pTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label pMeta = new Label("Client: " + p.getClientName() + " | Budget: $" + p.getBudget() + " | Category: " + p.getCategory());
                pMeta.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                details.getChildren().addAll(pTitle, pMeta);

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Label badgeMatch = UIComponents.createBadge(matchScore + "% AI Match", UIComponents.COLOR_PRIMARY, "white");
                Button btnApply = UIComponents.createSuccessButton("Submit Proposal");
                btnApply.setOnAction(e -> showProposalDialog(p));

                row.getChildren().addAll(details, sp, badgeMatch, btnApply);
                recCard.getChildren().add(row);
            }
        }

        box.getChildren().addAll(title, statGrid, chartCard, recCard);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 2. Profile Management
    private void showProfile() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Profile & Portfolio Management");

        FreelancerProfile fp = db.getFreelancerProfiles().get(currentUser.getId());
        if (fp == null) fp = new FreelancerProfile();

        VBox card = UIComponents.createCard();

        TextField tfTitle = UIComponents.createTextField("Professional Title (e.g. Senior Java Developer)");
        tfTitle.setText(fp.getTitle() != null ? fp.getTitle() : "");

        TextArea taBio = UIComponents.createTextArea("Bio / Summary...");
        taBio.setText(fp.getBio() != null ? fp.getBio() : "");

        TextField tfSkills = UIComponents.createTextField("Skills (comma separated, e.g. Java, JavaFX, SQL, Git)");
        tfSkills.setText(fp.getSkills() != null ? String.join(", ", fp.getSkills()) : "");

        TextField tfQual = UIComponents.createTextField("Qualification (e.g. B.Tech Computer Science)");
        tfQual.setText(fp.getQualification() != null ? fp.getQualification() : "");

        TextField tfCert = UIComponents.createTextField("Certifications (e.g. Oracle Certified Java SE 17)");
        tfCert.setText(fp.getCertifications() != null ? fp.getCertifications() : "");

        TextField tfLinkedIn = UIComponents.createTextField("LinkedIn Profile URL");
        tfLinkedIn.setText(fp.getLinkedinUrl() != null ? fp.getLinkedinUrl() : "");

        TextField tfGitHub = UIComponents.createTextField("GitHub Profile URL");
        tfGitHub.setText(fp.getGithubUrl() != null ? fp.getGithubUrl() : "");

        TextField tfGitLab = UIComponents.createTextField("GitLab Profile URL");
        tfGitLab.setText(fp.getGitlabUrl() != null ? fp.getGitlabUrl() : "");

        TextField tfPortfolio = UIComponents.createTextField("Personal Portfolio Website URL");
        tfPortfolio.setText(fp.getPortfolioUrl() != null ? fp.getPortfolioUrl() : "");

        Label resumeLbl = new Label("Resume File: " + (fp.getResumeFileName() != null ? fp.getResumeFileName() : "No resume uploaded"));
        resumeLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        Button btnUploadResume = UIComponents.createSecondaryButton("📎 Attach / Update Resume File");

        FreelancerProfile finalFp = fp;
        btnUploadResume.setOnAction(e -> {
            finalFp.setResumeFileName(currentUser.getUsername() + "_resume.pdf");
            resumeLbl.setText("Resume File: " + finalFp.getResumeFileName());
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Resume Attached", "Resume Updated", "Resume file attached successfully!");
        });

        Button btnSave = UIComponents.createPrimaryButton("Save & Update Profile");
        btnSave.setOnAction(e -> {
            finalFp.setTitle(tfTitle.getText().trim());
            finalFp.setBio(taBio.getText().trim());
            finalFp.setQualification(tfQual.getText().trim());
            finalFp.setCertifications(tfCert.getText().trim());
            finalFp.setLinkedinUrl(tfLinkedIn.getText().trim());
            finalFp.setGithubUrl(tfGitHub.getText().trim());
            finalFp.setGitlabUrl(tfGitLab.getText().trim());
            finalFp.setPortfolioUrl(tfPortfolio.getText().trim());

            String[] skArray = tfSkills.getText().split(",");
            List<String> skList = java.util.Arrays.stream(skArray).map(String::trim).filter(s -> !s.isEmpty()).toList();
            finalFp.setSkills(skList);

            db.getFreelancerProfiles().put(currentUser.getId(), finalFp);
            db.logActivity("Freelancer updated profile: " + currentUser.getUsername());
            db.saveData();

            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Saved", "Your freelancer profile, portfolio links, and skills have been updated!");
        });

        card.getChildren().addAll(
                new Label("Professional Title:"), tfTitle,
                new Label("Bio / About Me:"), taBio,
                new Label("Skills:"), tfSkills,
                new Label("Qualification & Certifications:"), tfQual, tfCert,
                new Label("Portfolio Links (LinkedIn, GitHub, GitLab, Website):"), tfLinkedIn, tfGitHub, tfGitLab, tfPortfolio,
                resumeLbl, btnUploadResume, btnSave
        );

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 3. Search Projects & Apply (with AI Auto-Proposal Cover Letter Generator)
    private void showProjectSearch() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Search Projects & Apply");

        HBox searchBar = new HBox(10);
        TextField tfSearch = UIComponents.createTextField("Search by title, skills, category...");
        HBox.setHgrow(tfSearch, Priority.ALWAYS);
        Button btnSearch = UIComponents.createPrimaryButton("Search");

        searchBar.getChildren().addAll(tfSearch, btnSearch);

        VBox listContainer = new VBox(10);
        FreelancerProfile profile = db.getFreelancerProfiles().get(currentUser.getId());

        Runnable populateProjects = () -> {
            listContainer.getChildren().clear();
            String query = tfSearch.getText().trim().toLowerCase();

            for (Project p : db.getProjects().values()) {
                if (p.getStatus() == Project.Status.OPEN) {
                    if (query.isEmpty() || p.getTitle().toLowerCase().contains(query) || p.getCategory().toLowerCase().contains(query)) {
                        int score = aiService.calculateMatchScore(profile, p);

                        VBox card = UIComponents.createCard();
                        HBox top = new HBox(10);
                        Label pTitle = UIComponents.createHeader(p.getTitle());
                        Region sp = new Region();
                        HBox.setHgrow(sp, Priority.ALWAYS);
                        Label badgeScore = UIComponents.createBadge(score + "% Skill Match", UIComponents.COLOR_PRIMARY, "white");
                        top.getChildren().addAll(pTitle, sp, badgeScore);

                        Label desc = new Label(p.getDescription());
                        desc.setWrapText(true);
                        desc.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                        Label info = new Label("Client: " + p.getClientName() + " | Category: " + p.getCategory() + " | Budget: $" + p.getBudget() + " | Deadline: " + p.getDeadline());
                        info.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                        Button btnApply = UIComponents.createSuccessButton("Apply & Submit Proposal");
                        btnApply.setOnAction(e -> showProposalDialog(p));

                        card.getChildren().addAll(top, desc, info, btnApply);
                        listContainer.getChildren().add(card);
                    }
                }
            }
        };

        btnSearch.setOnAction(e -> populateProjects.run());
        populateProjects.run();

        box.getChildren().addAll(title, searchBar, listContainer);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    private void showProposalDialog(Project project) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Submit Proposal - " + project.getTitle());
        dialog.setHeaderText("Project Budget: $" + project.getBudget() + " | Client: " + project.getClientName());

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));

        TextArea taCover = UIComponents.createTextArea("Explain why you are the best fit for this project...");
        taCover.setPrefRowCount(6);

        Button btnAiGenProposal = UIComponents.createSecondaryButton("🤖 AI Auto-Generate Winning Proposal Cover Letter");
        btnAiGenProposal.setOnAction(e -> {
            FreelancerProfile fp = db.getFreelancerProfiles().get(currentUser.getId());
            String cover = aiService.generateProposalCoverLetter(project, fp);
            taCover.setText(cover);
        });

        TextField tfBid = UIComponents.createTextField("Your Bid Amount ($)...");
        tfBid.setText(String.valueOf((int) project.getBudget()));

        TextField tfDays = UIComponents.createTextField("Estimated Delivery Days...");
        tfDays.setText("14");

        content.getChildren().addAll(new Label("Cover Letter:"), taCover, btnAiGenProposal, new Label("Bid Amount ($):"), tfBid, new Label("Estimated Days:"), tfDays);
        dialog.getDialogPane().setContent(content);

        ButtonType btnSubmitType = new ButtonType("Submit Bid", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSubmitType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSubmitType) {
                try {
                    double bid = Double.parseDouble(tfBid.getText().trim());
                    int days = Integer.parseInt(tfDays.getText().trim());
                    String propId = "prop_" + System.currentTimeMillis();
                    Proposal prop = new Proposal(propId, project.getId(), project.getTitle(), currentUser.getId(), currentUser.getUsername(),
                            taCover.getText().trim(), bid, days, Proposal.Status.PENDING, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

                    db.getProposals().put(propId, prop);
                    notifService.sendNotification(project.getClientId(), "New Proposal Received", currentUser.getUsername() + " submitted a bid of $" + bid + " for " + project.getTitle());
                    db.logActivity("Freelancer " + currentUser.getUsername() + " submitted proposal for project " + project.getTitle());
                    db.saveData();

                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Proposal Submitted", "Your proposal was successfully submitted to the client!");
                    return true;
                } catch (Exception ex) {
                    UIComponents.showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Amount/Days", "Please enter valid numerical values for Bid Amount and Days.");
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    // 4. Active Projects
    private void showActiveProjects() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Active Projects & Task Progress");

        VBox list = new VBox(15);
        for (Project p : db.getProjects().values()) {
            if (currentUser.getId().equals(p.getAssignedFreelancerId())) {
                VBox card = UIComponents.createCard();
                Label pTitle = UIComponents.createHeader(p.getTitle() + " (" + p.getStatus() + ")");
                Label pInfo = new Label("Client: " + p.getClientName() + " | Deadline: " + p.getDeadline());
                pInfo.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                VBox msList = new VBox(8);
                msList.getChildren().add(UIComponents.createSubHeader("Project Milestones:"));

                for (Milestone m : db.getMilestones().values()) {
                    if (m.getProjectId().equals(p.getId())) {
                        HBox mRow = new HBox(10);
                        mRow.setAlignment(Pos.CENTER_LEFT);
                        mRow.setPadding(new Insets(8));
                        mRow.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 6;");

                        Label mInfo = new Label(m.getTitle() + " - $" + m.getAmount() + " [" + m.getStatus() + "]");
                        mInfo.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                        Region sp = new Region();
                        HBox.setHgrow(sp, Priority.ALWAYS);

                        Button btnSubmitDeliverable = UIComponents.createPrimaryButton("Upload Deliverable");
                        btnSubmitDeliverable.setOnAction(e -> {
                            m.setStatus(Milestone.Status.SUBMITTED);
                            m.setDeliverableFile("deliverable_v1_" + m.getId() + ".zip");
                            db.getMilestones().put(m.getId(), m);
                            notifService.sendNotification(p.getClientId(), "Deliverable Uploaded", currentUser.getUsername() + " uploaded deliverables for milestone '" + m.getTitle() + "'");
                            db.saveData();
                            showActiveProjects();
                        });

                        mRow.getChildren().addAll(mInfo, sp, btnSubmitDeliverable);
                        msList.getChildren().add(mRow);
                    }
                }

                card.getChildren().addAll(pTitle, pInfo, msList);
                list.getChildren().add(card);
            }
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 5. Interactive Calendar & Schedule View
    private void showCalendar() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📅 Project Deadlines & Meeting Calendar");

        VBox card = UIComponents.createCard();
        card.getChildren().add(UIComponents.createHeader("Upcoming Scheduled Milestones & Meetings:"));

        VBox grid = new VBox(10);
        for (Project p : db.getProjects().values()) {
            if (currentUser.getId().equals(p.getAssignedFreelancerId()) || p.getStatus() == Project.Status.OPEN) {
                HBox item = new HBox(15);
                item.setAlignment(Pos.CENTER_LEFT);
                item.setPadding(new Insets(10));
                item.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 8;");

                Label dBadge = UIComponents.createBadge("📅 " + p.getDeadline(), UIComponents.COLOR_PRIMARY, "white");
                VBox info = new VBox(2);
                Label tLbl = new Label(p.getTitle());
                tLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                tLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label cLbl = new Label("Client: " + p.getClientName() + " | Category: " + p.getCategory());
                cLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                info.getChildren().addAll(tLbl, cLbl);

                item.getChildren().addAll(dBadge, info);
                grid.getChildren().add(item);
            }
        }

        card.getChildren().add(grid);
        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 6. Encrypted Chat & Google Meet
    private String selectedChatProjectId = null;

    private void showChat() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("💬 Encrypted Client Communication & Google Meet");

        // Meet bar
        HBox meetBar = new HBox(15);
        meetBar.setPadding(new Insets(12));
        meetBar.setAlignment(Pos.CENTER_LEFT);
        meetBar.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-background-radius: 8;");
        Label meetIcon = new Label("📹 Google Meet Integration:");
        meetIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        meetIcon.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
        Button btnLaunchMeet = UIComponents.createPrimaryButton("Launch Instant Virtual Meeting");
        btnLaunchMeet.setOnAction(e -> UIComponents.showAlert(Alert.AlertType.INFORMATION, "Google Meet Launcher", "Meeting Launched", "Google Meet Room Created: https://meet.google.com/sb-freelance-demo\nShared link with active client!"));
        meetBar.getChildren().addAll(meetIcon, btnLaunchMeet);

        // Main chat layout: project list on left, chat area on right
        HBox chatLayout = new HBox(12);
        chatLayout.setPrefHeight(420);

        // --- Left: Project / Conversation list ---
        VBox projectListPanel = new VBox(8);
        projectListPanel.setPrefWidth(240);
        projectListPanel.setPadding(new Insets(12));
        projectListPanel.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: " + UIComponents.COLOR_BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label convLabel = new Label("📂 Conversations");
        convLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        convLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
        projectListPanel.getChildren().add(convLabel);

        boolean hasConversations = false;
        for (Project p : db.getProjects().values()) {
            if (currentUser.getId().equals(p.getAssignedFreelancerId()) && p.getStatus() == Project.Status.IN_PROGRESS) {
                hasConversations = true;
                boolean isSelected = p.getId().equals(selectedChatProjectId);

                VBox projItem = new VBox(2);
                projItem.setPadding(new Insets(10));
                projItem.setStyle("-fx-background-color: " + (isSelected ? UIComponents.COLOR_PRIMARY : UIComponents.COLOR_BG_INPUT) + "; -fx-background-radius: 8; -fx-cursor: hand;");

                Label projTitle = new Label(p.getTitle());
                projTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                projTitle.setTextFill(Color.web(isSelected ? "white" : UIComponents.COLOR_TEXT_PRIMARY));
                projTitle.setWrapText(true);

                Label clientName = new Label("🏢 " + p.getClientName());
                clientName.setFont(Font.font("Segoe UI", 11));
                clientName.setTextFill(Color.web(isSelected ? "#E0E0FF" : UIComponents.COLOR_TEXT_MUTED));

                // Unread count hint
                long msgCount = db.getChatMessages().values().stream()
                        .filter(m -> m.getProjectId().equals(p.getId()))
                        .count();
                Label msgCountLbl = new Label(msgCount + " messages");
                msgCountLbl.setFont(Font.font("Segoe UI", 10));
                msgCountLbl.setTextFill(Color.web(isSelected ? "#C7D2FE" : UIComponents.COLOR_TEXT_MUTED));

                projItem.getChildren().addAll(projTitle, clientName, msgCountLbl);
                projItem.setOnMouseClicked(ev -> {
                    selectedChatProjectId = p.getId();
                    showChat();
                });

                projectListPanel.getChildren().add(projItem);
            }
        }

        if (!hasConversations) {
            Label noProj = new Label("No active projects.\nGet hired to start chatting!");
            noProj.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            noProj.setWrapText(true);
            projectListPanel.getChildren().add(noProj);
        }

        ScrollPane projectScroll = new ScrollPane(projectListPanel);
        projectScroll.setFitToWidth(true);
        projectScroll.setPrefWidth(250);
        projectScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // --- Right: Chat messages area ---
        VBox chatPanel = new VBox(10);
        HBox.setHgrow(chatPanel, Priority.ALWAYS);
        chatPanel.setPadding(new Insets(12));
        chatPanel.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-border-color: " + UIComponents.COLOR_BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        if (selectedChatProjectId == null) {
            // Auto-select first available project
            for (Project p : db.getProjects().values()) {
                if (currentUser.getId().equals(p.getAssignedFreelancerId()) && p.getStatus() == Project.Status.IN_PROGRESS) {
                    selectedChatProjectId = p.getId();
                    break;
                }
            }
        }

        if (selectedChatProjectId != null) {
            Project selProj = db.getProjects().get(selectedChatProjectId);
            if (selProj != null) {
                // Chat header
                HBox chatHeader = new HBox(10);
                chatHeader.setAlignment(Pos.CENTER_LEFT);
                chatHeader.setPadding(new Insets(0, 0, 8, 0));
                Label chatTitle = new Label("💬 " + selProj.getTitle());
                chatTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
                chatTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                Label chatWith = new Label("with 🏢 " + selProj.getClientName());
                chatWith.setFont(Font.font("Segoe UI", 12));
                chatWith.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                Label encBadge = UIComponents.createBadge("🔒 AES-256 Encrypted", UIComponents.COLOR_SUCCESS, "white");
                Region hsp = new Region();
                HBox.setHgrow(hsp, Priority.ALWAYS);
                chatHeader.getChildren().addAll(chatTitle, chatWith, hsp, encBadge);

                // Messages
                VBox msgContainer = new VBox(8);
                msgContainer.setPadding(new Insets(8));

                java.util.List<ChatMessage> projectMsgs = db.getChatMessages().values().stream()
                        .filter(m -> m.getProjectId().equals(selectedChatProjectId))
                        .sorted((a, b) -> a.getId().compareTo(b.getId()))
                        .collect(java.util.stream.Collectors.toList());

                if (projectMsgs.isEmpty()) {
                    Label noMsg = new Label("No messages yet. Start the conversation!");
                    noMsg.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                    noMsg.setFont(Font.font("Segoe UI", 13));
                    msgContainer.getChildren().add(noMsg);
                } else {
                    for (ChatMessage msg : projectMsgs) {
                        boolean isMine = msg.getSenderId().equals(currentUser.getId());

                        HBox msgRow = new HBox();
                        msgRow.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                        msgRow.setPadding(new Insets(2, 0, 2, 0));

                        VBox bubble = new VBox(2);
                        bubble.setMaxWidth(400);
                        bubble.setPadding(new Insets(8, 12, 8, 12));
                        bubble.setStyle("-fx-background-color: " + (isMine ? UIComponents.COLOR_PRIMARY : UIComponents.COLOR_BG_INPUT) + "; -fx-background-radius: 12;");

                        Label senderLbl = new Label(msg.getSenderName());
                        senderLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                        senderLbl.setTextFill(Color.web(isMine ? "#C7D2FE" : UIComponents.COLOR_TEXT_MUTED));

                        Label contentLbl = new Label(msg.getContent());
                        contentLbl.setWrapText(true);
                        contentLbl.setFont(Font.font("Segoe UI", 13));
                        contentLbl.setTextFill(Color.web(isMine ? "white" : UIComponents.COLOR_TEXT_PRIMARY));

                        Label timeLbl = new Label(msg.getTimestamp() + " 🔒");
                        timeLbl.setFont(Font.font("Segoe UI", 10));
                        timeLbl.setTextFill(Color.web(isMine ? "#A5B4FC" : UIComponents.COLOR_TEXT_MUTED));

                        bubble.getChildren().addAll(senderLbl, contentLbl, timeLbl);
                        msgRow.getChildren().add(bubble);
                        msgContainer.getChildren().add(msgRow);
                    }
                }

                ScrollPane msgScroll = new ScrollPane(msgContainer);
                msgScroll.setFitToWidth(true);
                VBox.setVgrow(msgScroll, Priority.ALWAYS);
                msgScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
                msgScroll.setVvalue(1.0); // scroll to bottom

                // Input bar
                HBox inputBar = new HBox(10);
                inputBar.setAlignment(Pos.CENTER_LEFT);
                inputBar.setPadding(new Insets(8, 0, 0, 0));
                TextField tfMsg = UIComponents.createTextField("Type encrypted message...");
                HBox.setHgrow(tfMsg, Priority.ALWAYS);
                Button btnSend = UIComponents.createPrimaryButton("Send 🔒");
                Button btnAttach = UIComponents.createSecondaryButton("📎");

                String receiverId = selProj.getClientId();
                final String chatProjId = selectedChatProjectId;

                btnSend.setOnAction(e -> {
                    if (!tfMsg.getText().trim().isEmpty()) {
                        String cmId = "msg_" + System.currentTimeMillis();
                        ChatMessage cm = new ChatMessage(cmId, chatProjId, currentUser.getId(), currentUser.getUsername(), receiverId,
                                tfMsg.getText().trim(), null, true, new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date()));
                        db.getChatMessages().put(cmId, cm);
                        db.saveData();
                        tfMsg.clear();
                        showChat();
                    }
                });

                tfMsg.setOnAction(e -> btnSend.fire());

                btnAttach.setOnAction(e -> {
                    String cmId = "msg_" + System.currentTimeMillis();
                    String attachName = "attachment_" + System.currentTimeMillis() + ".pdf";
                    ChatMessage cm = new ChatMessage(cmId, chatProjId, currentUser.getId(), currentUser.getUsername(), receiverId,
                            "📎 Sent an attachment: " + attachName, attachName, true, new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date()));
                    db.getChatMessages().put(cmId, cm);
                    db.saveData();
                    showChat();
                });

                inputBar.getChildren().addAll(tfMsg, btnAttach, btnSend);
                chatPanel.getChildren().addAll(chatHeader, new Separator(), msgScroll, new Separator(), inputBar);
            }
        } else {
            Label noChatLbl = new Label("Select a project conversation from the left panel to start chatting.");
            noChatLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            noChatLbl.setFont(Font.font("Segoe UI", 14));
            noChatLbl.setWrapText(true);
            chatPanel.getChildren().add(noChatLbl);
        }

        chatLayout.getChildren().addAll(projectScroll, chatPanel);
        VBox.setVgrow(chatLayout, Priority.ALWAYS);

        box.getChildren().addAll(title, meetBar, chatLayout);
        contentArea.getChildren().setAll(box);
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

    // 8. Notifications
    private void showNotifications() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("System Notifications & Announcements");

        List<Notification> notifs = notifService.getUserNotifications(currentUser.getId());
        VBox card = UIComponents.createCard();

        if (notifs.isEmpty()) {
            card.getChildren().add(new Label("No notifications at present."));
        } else {
            for (Notification n : notifs) {
                VBox row = new VBox(4);
                row.setPadding(new Insets(8));
                row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 6;");

                Label nTitle = new Label("🔔 " + n.getTitle() + " (" + n.getTimestamp() + ")");
                nTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                nTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label nMsg = new Label(n.getMessage());
                nMsg.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                row.getChildren().addAll(nTitle, nMsg);
                card.getChildren().add(row);
            }
        }

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(box);
    }
}
