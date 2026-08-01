package com.freelancing.ui;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.*;
import com.freelancing.service.AiService;
import com.freelancing.service.NotificationService;
import com.freelancing.service.PaymentService;

import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientMainView {
    private final Stage stage;
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final AiService aiService = new AiService();
    private final PaymentService paymentService = new PaymentService();
    private final NotificationService notifService = new NotificationService();

    private BorderPane root;
    private StackPane contentArea;

    public ClientMainView(Stage stage, User user) {
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

        Label logo = new Label("⚡ SkillBridge  |  Client Portal");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("🏢 " + currentUser.getUsername() + " (Verified Client)");
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

        Button btnNavDash = createNavButton("📊 Client Dashboard", true);
        Button btnNavFeed = createNavButton("📰 Community Feed", false);
        Button btnNavPost = createNavButton("➕ Post New Project", false);
        Button btnNavBids = createNavButton("📋 Job Bids & Assign", false);
        Button btnNavProps = createNavButton("📩 Proposal Review", false);
        Button btnNavSearch = createNavButton("🔍 Find Freelancers & AI", false);
        Button btnNavProgress = createNavButton("💳 Milestones & Razorpay", false);
        Button btnNavChat = createNavButton("💬 Chat & Messaging", false);
        Button btnNavProfile = createNavButton("🏢 Company Profile", false);

        Button[] navBtns = {btnNavDash, btnNavFeed, btnNavPost, btnNavBids, btnNavProps, btnNavSearch, btnNavProgress, btnNavChat, btnNavProfile};

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        btnNavDash.setOnAction(e -> { selectNav(btnNavDash, navBtns); showDashboard(); });
        btnNavFeed.setOnAction(e -> { selectNav(btnNavFeed, navBtns); showFeed(); });
        btnNavPost.setOnAction(e -> { selectNav(btnNavPost, navBtns); showPostProject(); });
        btnNavBids.setOnAction(e -> { selectNav(btnNavBids, navBtns); showJobBidsAndAssign(); });
        btnNavProps.setOnAction(e -> { selectNav(btnNavProps, navBtns); showProposals(); });
        btnNavSearch.setOnAction(e -> { selectNav(btnNavSearch, navBtns); showFreelancerSearch(); });
        btnNavProgress.setOnAction(e -> { selectNav(btnNavProgress, navBtns); showMilestonesAndPayments(); });
        btnNavChat.setOnAction(e -> { selectNav(btnNavChat, navBtns); showChat(); });
        btnNavProfile.setOnAction(e -> { selectNav(btnNavProfile, navBtns); showCompanyProfile(); });

        sidebar.getChildren().addAll(btnNavDash, btnNavFeed, btnNavPost, btnNavBids, btnNavProps, btnNavSearch, btnNavProgress, btnNavChat, btnNavProfile);
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

    // 1. Dashboard View with Budget Distribution PieChart
    private void showDashboard() {
        VBox box = new VBox(20);
        Label title = UIComponents.createTitle("Client Dashboard");

        ClientProfile cp = db.getClientProfiles().get(currentUser.getId());
        double spent = cp != null ? cp.getTotalSpent() : 0.0;

        // Count total bids received across all job posts
        int totalBids = db.countBidsForClient(currentUser.getId());

        HBox statGrid = new HBox(15);
        VBox card1 = UIComponents.createStatCard("📢", "Projects Posted", String.valueOf(db.countProjectsByClient(currentUser.getId())), UIComponents.COLOR_PRIMARY);
        VBox card2 = UIComponents.createStatCard("🤝", "Active Contracts", String.valueOf(db.countActiveContractsByClient(currentUser.getId())), UIComponents.COLOR_SUCCESS);
        VBox card3 = UIComponents.createStatCard("💰", "Total Capital Spent", "$" + String.format("%.2f", spent), UIComponents.COLOR_AMBER);
        VBox card4 = UIComponents.createStatCard("🎯", "Bids Received", String.valueOf(totalBids), UIComponents.COLOR_PURPLE);
        statGrid.getChildren().addAll(card1, card2, card3, card4);

        // Category Budget Chart
        Map<String, Double> catMap = new HashMap<>();
        catMap.put("Mobile Dev", 4500.0);
        catMap.put("UI/UX Design", 2800.0);
        catMap.put("AI/ML", 3200.0);

        PieChart chart = UIComponents.createPieChart("📊 Project Capital Allocation by Category", catMap);
        VBox chartCard = UIComponents.createCard();
        chartCard.getChildren().addAll(UIComponents.createHeader("Capital Allocation Analysis"), chart);

        VBox projCard = UIComponents.createCard();
        projCard.getChildren().add(UIComponents.createHeader("📁 Your Posted Projects"));

        for (Project p : db.getProjectsByClient(currentUser.getId())) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 8;");

            VBox det = new VBox(4);
            Label pTitle = new Label(p.getTitle());
            pTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            pTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

            Label pMeta = new Label("Category: " + p.getCategory() + " | Budget: $" + p.getBudget() + " | Status: " + p.getStatus());
            pMeta.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            det.getChildren().addAll(pTitle, pMeta);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label badge = UIComponents.createBadge(p.getStatus().toString(), UIComponents.COLOR_PRIMARY, "white");
            row.getChildren().addAll(det, sp, badge);
            projCard.getChildren().add(row);
        }

        box.getChildren().addAll(title, statGrid, chartCard, projCard);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // Community Feed
    private void showFeed() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📰 Community Feed");

        // Create Post Card
        VBox createCard = UIComponents.createCard();
        createCard.getChildren().add(UIComponents.createHeader("✍️ Create a New Post"));

        TextField tfPostTitle = UIComponents.createTextField("Post title...");
        TextArea taPostContent = UIComponents.createTextArea("Share your thoughts, post hiring needs, or start a discussion...");
        taPostContent.setPrefRowCount(4);

        ComboBox<String> cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("Showcase", "Hiring", "Discussion", "Feedback");
        cbCategory.getSelectionModel().select(1);
        cbCategory.setMaxWidth(Double.MAX_VALUE);
        cbCategory.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: white;");

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

            FeedPost post = new FeedPost(postId, currentUser.getId(), currentUser.getUsername(), "CLIENT",
                    pTitle, pContent, cat, FeedPost.PostStatus.ACTIVE, ts);
            db.getFeedPosts().put(postId, post);
            db.logActivity("Client " + currentUser.getUsername() + " published feed post: " + pTitle);
            db.saveData();

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
        contentArea.getChildren().setAll(new ScrollPane(box));
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
                projInfoBar.setStyle("-fx-background-color: #1a2744; -fx-background-radius: 8; -fx-border-color: " + UIComponents.COLOR_PRIMARY + "; -fx-border-radius: 8; -fx-border-width: 1;");

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

        // Show bids count for JOB_POST
        if (post.getCategory() == FeedPost.PostCategory.JOB_POST) {
            Label bidCountBadge = UIComponents.createBadge("🎯 " + post.getBids().size() + " Bids", UIComponents.COLOR_AMBER, "white");
            actionBar.getChildren().add(bidCountBadge);
        }

        Button btnFeedback = UIComponents.createSuccessButton("⭐ Give Feedback");
        btnFeedback.setOnAction(e -> {
            if (post.getAuthorId().equals(currentUser.getId())) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Feedback", "Not Allowed", "You cannot give feedback on your own post.");
                return;
            }
            TextInputDialog feedbackDialog = new TextInputDialog();
            feedbackDialog.setTitle("Give Feedback");
            feedbackDialog.setHeaderText("Write feedback for " + post.getAuthorName());
            feedbackDialog.setContentText("Your feedback:");
            feedbackDialog.showAndWait().ifPresent(feedback -> {
                if (!feedback.trim().isEmpty()) {
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

            Project linkedProj = post.getLinkedProjectId() != null ? db.getProjects().get(post.getLinkedProjectId()) : null;

            for (FeedPost.FeedBid bid : post.getBids()) {
                VBox bidCard = new VBox(6);
                bidCard.setPadding(new Insets(10, 12, 10, 12));
                bidCard.setStyle("-fx-background-color: #1a2744; -fx-background-radius: 8; -fx-border-color: " + UIComponents.COLOR_BORDER + "; -fx-border-radius: 8;");

                // Bid header: name, amount, days, timestamp
                HBox bidTop = new HBox(10);
                bidTop.setAlignment(Pos.CENTER_LEFT);
                Label bidderName = new Label("👤 " + bid.getFreelancerName());
                bidderName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                bidderName.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label bidAmount = UIComponents.createBadge("$" + String.format("%.0f", bid.getBidAmount()), UIComponents.COLOR_SUCCESS, "white");
                Label bidDays = UIComponents.createBadge(bid.getEstimatedDays() + " days", UIComponents.COLOR_PRIMARY, "white");

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
                    int matchPct = linkedProj.getRequiredSkills().isEmpty() ? 0 : (int) ((matchCount * 100) / linkedProj.getRequiredSkills().size());
                    Label matchBadge = UIComponents.createBadge(matchPct + "% Skill Match", matchPct >= 50 ? UIComponents.COLOR_SUCCESS : UIComponents.COLOR_AMBER, "white");
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

    private int getPostedProjectCount() {
        return (int) db.getProjects().values().stream().filter(p -> p.getClientId().equals(currentUser.getId())).count();
    }

    private int getActiveContractCount() {
        return (int) db.getProjects().values().stream().filter(p -> p.getClientId().equals(currentUser.getId()) && p.getStatus() == Project.Status.IN_PROGRESS).count();
    }

    // 2. Post New Project & AI Generator (auto-posts to feed as JOB_POST)
    private void showPostProject() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Post New Project with AI Assistance");

        VBox card = UIComponents.createCard();

        TextField tfTitle = UIComponents.createTextField("Project Title (e.g. Android & Desktop Inventory App)...");
        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll("Software & Desktop App", "Mobile Development", "UI/UX Design", "AI & Machine Learning", "Web Development");
        cbCat.getSelectionModel().select(0);
        cbCat.setMaxWidth(Double.MAX_VALUE);

        TextArea taDesc = UIComponents.createTextArea("Detailed project requirements, deliverables, and scope...");
        tfTitle.setPrefHeight(35);
        taDesc.setPrefRowCount(7);

        Button btnAiGen = UIComponents.createSecondaryButton("🤖 AI Generate Detailed Project Description");
        btnAiGen.setOnAction(e -> {
            String pTitle = tfTitle.getText().trim();
            String cat = cbCat.getValue();
            if (pTitle.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "AI Generator", "Title Required", "Please enter a project title first to generate AI description.");
                return;
            }
            String generated = aiService.generateProjectDescription(pTitle, cat, taDesc.getText());
            taDesc.setText(generated);
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "AI Generator", "Description Generated", "AI successfully created structured requirements and deliverables!");
        });

        TextField tfSkills = UIComponents.createTextField("Required Skills (comma separated, e.g. Java, Swing, SQLite)");
        TextField tfBudget = UIComponents.createTextField("Project Budget ($)...");
        TextField tfDeadline = UIComponents.createTextField("Project Deadline (e.g. 2026-09-30)...");

        Button btnPost = UIComponents.createPrimaryButton("Post Project Live");
        btnPost.setOnAction(e -> {
            String pTitle = tfTitle.getText().trim();
            String desc = taDesc.getText().trim();
            String bStr = tfBudget.getText().trim();
            String dl = tfDeadline.getText().trim();

            if (pTitle.isEmpty() || desc.isEmpty() || bStr.isEmpty() || dl.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Validation", "Missing Fields", "Please complete all project fields.");
                return;
            }

            try {
                double budget = Double.parseDouble(bStr);
                String projId = "proj_" + System.currentTimeMillis();
                String[] skArr = tfSkills.getText().split(",");
                List<String> skList = java.util.Arrays.stream(skArr).map(String::trim).filter(s -> !s.isEmpty()).toList();

                String nowDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                String nowFull = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());

                Project project = new Project(projId, currentUser.getId(), currentUser.getUsername(), pTitle, cbCat.getValue(), desc,
                        skList, budget, dl, Project.Status.OPEN, null, null, nowDate);

                db.getProjects().put(projId, project);

                // Auto-create JOB_POST in feed
                String feedId = "feed_job_" + System.currentTimeMillis();
                StringBuilder jobContent = new StringBuilder();
                jobContent.append(desc);
                jobContent.append("\n\n💰 Budget: $").append(String.format("%.2f", budget));
                jobContent.append("\n📅 Deadline: ").append(dl);
                if (!skList.isEmpty()) {
                    jobContent.append("\n🛠 Required Skills: ").append(String.join(", ", skList));
                }
                jobContent.append("\n📂 Category: ").append(cbCat.getValue());

                FeedPost jobPost = new FeedPost(feedId, currentUser.getId(), currentUser.getUsername(), "CLIENT",
                        "💼 " + pTitle, jobContent.toString(),
                        FeedPost.PostCategory.JOB_POST, FeedPost.PostStatus.ACTIVE, nowFull);
                jobPost.setLinkedProjectId(projId);
                db.getFeedPosts().put(feedId, jobPost);

                db.logActivitySilent("Client " + currentUser.getUsername() + " posted new project: " + pTitle + " (auto-posted to feed)");
                db.invalidateCaches();
                db.saveData();

                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Project Posted",
                        "Your project has been posted to the marketplace AND added to the community feed as a job post!\n\nFreelancers can now see it, like, comment, and bid on it.");
                showDashboard();
            } catch (Exception ex) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Budget", "Please enter a valid numeric budget amount.");
            }
        });

        card.getChildren().addAll(
                new Label("Project Title:"), tfTitle,
                new Label("Category:"), cbCat,
                new Label("Description & Requirements:"), taDesc, btnAiGen,
                new Label("Required Skills:"), tfSkills,
                new Label("Budget ($):"), tfBudget,
                new Label("Deadline Date:"), tfDeadline,
                btnPost
        );

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 3. Job Bids & Assign — Dedicated view for reviewing bids and assigning freelancers
    private void showJobBidsAndAssign() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("📋 Job Bids & Freelancer Assignment");
        Label subtitle = new Label("Review all bids on your job posts, compare freelancer skills, and assign the best candidate.");
        subtitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        subtitle.setWrapText(true);
        subtitle.setFont(Font.font("Segoe UI", 13));

        VBox jobList = new VBox(15);

        // Get all JOB_POST feed posts by this client (indexed & cached)
        List<FeedPost> myJobPosts = db.getJobPostsByClient(currentUser.getId());

        if (myJobPosts.isEmpty()) {
            VBox emptyCard = UIComponents.createCard();
            Label emptyLbl = new Label("You haven't posted any jobs yet. Go to 'Post New Project' to create a job that freelancers can bid on!");
            emptyLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
            emptyLbl.setWrapText(true);
            emptyCard.getChildren().add(emptyLbl);
            jobList.getChildren().add(emptyCard);
        } else {
            for (FeedPost jobPost : myJobPosts) {
                Project linkedProj = jobPost.getLinkedProjectId() != null ? db.getProjects().get(jobPost.getLinkedProjectId()) : null;

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

                Label bidsCountBadge = UIComponents.createBadge("🎯 " + jobPost.getBids().size() + " Bids", UIComponents.COLOR_AMBER, "white");
                Label likesCountBadge = UIComponents.createBadge("👍 " + jobPost.getLikes() + " Likes", UIComponents.COLOR_BG_INPUT, UIComponents.COLOR_TEXT_PRIMARY);
                Label commentsCountBadge = UIComponents.createBadge("💬 " + jobPost.getComments().size(), UIComponents.COLOR_BG_INPUT, UIComponents.COLOR_TEXT_PRIMARY);

                jobHeader.getChildren().addAll(jobTitle, hsp, likesCountBadge, commentsCountBadge, bidsCountBadge, statusBadge);

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
                    Label noBids = new Label("No bids yet. Freelancers will see this job in their feed and can place bids.");
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
                        bidCard.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 8;");

                        // Top row: name + amount + days + timestamp
                        HBox bidTop = new HBox(10);
                        bidTop.setAlignment(Pos.CENTER_LEFT);

                        Label nameLabel = new Label("👤 " + bid.getFreelancerName());
                        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                        nameLabel.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                        Label amtBadge = UIComponents.createBadge("💰 $" + String.format("%.0f", bid.getBidAmount()), UIComponents.COLOR_SUCCESS, "white");
                        Label daysBadge = UIComponents.createBadge("📅 " + bid.getEstimatedDays() + " days", UIComponents.COLOR_PRIMARY, "white");

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
                            if (isMatch) matchedSkills++;
                            Label skillBadge = UIComponents.createBadge(
                                    (isMatch ? "✓ " : "") + skill,
                                    isMatch ? UIComponents.COLOR_SUCCESS : "#2a3a5c",
                                    "white");
                            skillsRow.getChildren().add(skillBadge);
                        }

                        // Match percentage
                        int matchPct = (linkedProj != null && !linkedProj.getRequiredSkills().isEmpty())
                                ? (matchedSkills * 100 / linkedProj.getRequiredSkills().size()) : 0;
                        Label matchLabel = UIComponents.createBadge(matchPct + "% Match",
                                matchPct >= 75 ? UIComponents.COLOR_SUCCESS : matchPct >= 50 ? UIComponents.COLOR_AMBER : UIComponents.COLOR_DANGER,
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
                            Button btnAssign = UIComponents.createSuccessButton("✅ Assign " + bid.getFreelancerName() + " to Project");
                            btnAssign.setOnAction(ev -> {
                                assignFreelancerFromBid(jobPost, bid, linkedProj);
                                showJobBidsAndAssign(); // refresh
                            });
                            bidCard.getChildren().add(btnAssign);
                        } else if (linkedProj != null && linkedProj.getAssignedFreelancerId() != null
                                && linkedProj.getAssignedFreelancerId().equals(bid.getFreelancerId())) {
                            Label assignedLabel = UIComponents.createBadge("✅ ASSIGNED TO THIS FREELANCER", UIComponents.COLOR_SUCCESS, "white");
                            bidCard.getChildren().add(assignedLabel);
                        }

                        jobCard.getChildren().add(bidCard);
                    }
                }

                jobList.getChildren().add(jobCard);
            }
        }

        box.getChildren().addAll(title, subtitle, jobList);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 4. Proposal Review & Hiring
    private void showProposals() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Review Proposals & Hire Freelancers");

        VBox list = new VBox(15);
        for (Proposal p : db.getProposals().values()) {
            Project proj = db.getProjects().get(p.getProjectId());
            if (proj != null && proj.getClientId().equals(currentUser.getId())) {
                VBox card = UIComponents.createCard();

                Label pHeader = UIComponents.createHeader("Proposal for: " + p.getProjectTitle());
                Label freeInfo = new Label("Freelancer: " + p.getFreelancerName() + " | Bid: $" + p.getBidAmount() + " | Est. Days: " + p.getEstimatedDays() + " | Status: " + p.getStatus());
                freeInfo.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Label cover = new Label("Cover Letter:\n" + p.getCoverLetter());
                cover.setWrapText(true);
                cover.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

                HBox actions = new HBox(10);
                if (p.getStatus() == Proposal.Status.PENDING) {
                    Button btnHire = UIComponents.createSuccessButton("Accept Proposal & Hire Freelancer");
                    btnHire.setOnAction(e -> {
                        p.setStatus(Proposal.Status.ACCEPTED);
                        proj.setStatus(Project.Status.IN_PROGRESS);
                        proj.setAssignedFreelancerId(p.getFreelancerId());
                        proj.setAssignedFreelancerName(p.getFreelancerName());

                        Milestone ms1 = new Milestone("ms_" + System.currentTimeMillis(), proj.getId(), "Project Deliverables & Source Code Handover", "Complete project build and documentation", p.getBidAmount(), proj.getDeadline(), Milestone.Status.IN_PROGRESS);
                        db.getMilestones().put(ms1.getId(), ms1);

                        db.getProposals().put(p.getId(), p);
                        db.getProjects().put(proj.getId(), proj);

                        notifService.sendNotification(p.getFreelancerId(), "Proposal Accepted!", "Your proposal for '" + proj.getTitle() + "' was accepted by " + currentUser.getUsername());
                        db.logActivity("Client " + currentUser.getUsername() + " hired freelancer " + p.getFreelancerName() + " for project " + proj.getTitle());
                        db.saveData();

                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Hire Success", "Freelancer Hired", "You have hired " + p.getFreelancerName() + "! Milestone contract initialized.");
                        showProposals();
                    });

                    Button btnReject = UIComponents.createDangerButton("Decline Proposal");
                    btnReject.setOnAction(e -> {
                        p.setStatus(Proposal.Status.REJECTED);
                        db.getProposals().put(p.getId(), p);
                        db.saveData();
                        showProposals();
                    });

                    actions.getChildren().addAll(btnHire, btnReject);
                } else {
                    actions.getChildren().add(UIComponents.createBadge("Status: " + p.getStatus(), UIComponents.COLOR_PRIMARY, "white"));
                }

                card.getChildren().addAll(pHeader, freeInfo, cover, actions);
                list.getChildren().add(card);
            }
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(new ScrollPane(box));
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

                Label skills = new Label("Skills: " + String.join(", ", fp.getSkills()) + " | Rating: ⭐ " + fp.getRating() + " / 5.0");
                skills.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

                Button btnInvite = UIComponents.createPrimaryButton("Invite to Post Project");
                btnInvite.setOnAction(e -> {
                    notifService.sendNotification(u.getId(), "Project Invitation", currentUser.getUsername() + " invited you to view their posted projects!");
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Invite Sent", "Freelancer Invited", "Sent project invitation notification to " + u.getUsername());
                });

                card.getChildren().addAll(name, bio, skills, btnInvite);
                list.getChildren().add(card);
            }
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 6. Milestones & Razorpay Payment Release with Export Receipt
    private void showMilestonesAndPayments() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Milestone Progress & Razorpay Payments");

        VBox list = new VBox(15);
        for (Project proj : db.getProjectsByClient(currentUser.getId())) {
            VBox card = UIComponents.createCard();
            Label pTitle = UIComponents.createHeader("Project: " + proj.getTitle());

            VBox msContainer = new VBox(8);
            for (Milestone m : db.getMilestonesByProject(proj.getId())) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-background-radius: 8;");

                Label mText = new Label(m.getTitle() + " - $" + m.getAmount() + " [" + m.getStatus() + "]");
                mText.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                if (m.getStatus() == Milestone.Status.SUBMITTED || m.getStatus() == Milestone.Status.APPROVED) {
                    Button btnPayRzp = UIComponents.createSuccessButton("💳 Release Payment via Razorpay");
                    btnPayRzp.setOnAction(e -> {
                        PaymentService.PaymentReceipt receipt = paymentService.processRazorpayPayment(m, proj);

                        ClientProfile cp = db.getClientProfiles().get(currentUser.getId());
                        if (cp != null) {
                            cp.setTotalSpent(cp.getTotalSpent() + m.getAmount());
                            db.getClientProfiles().put(currentUser.getId(), cp);
                        }

                        notifService.sendNotification(proj.getAssignedFreelancerId(), "Payment Released!", "Razorpay Payment of $" + m.getAmount() + " released for milestone '" + m.getTitle() + "'");

                        try (PrintWriter out = new PrintWriter(new File("Invoice_Receipt_" + receipt.transactionId + ".txt"))) {
                            out.println(receipt.toString());
                        } catch (Exception ex) {}

                        db.invalidateCaches();
                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "Razorpay Payment Released", "Payment Success & Invoice Saved", receipt.toString() + "\n\nInvoice exported to Invoice_Receipt_" + receipt.transactionId + ".txt");
                        showMilestonesAndPayments();
                    });
                    row.getChildren().addAll(mText, sp, btnPayRzp);
                } else {
                    Label statusBadge = UIComponents.createBadge("Status: " + m.getStatus(), UIComponents.COLOR_PRIMARY, "white");
                    row.getChildren().addAll(mText, sp, statusBadge);
                }
                msContainer.getChildren().add(row);
            }

            card.getChildren().addAll(pTitle, msContainer);
            list.getChildren().add(card);
        }

        box.getChildren().addAll(title, list);
        contentArea.getChildren().setAll(new ScrollPane(box));
    }

    // 7. Chat & Messaging
    private String selectedChatProjectId = null;

    private void showChat() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("💬 Chat & Messaging");

        // Meet bar
        HBox meetBar = new HBox(15);
        meetBar.setPadding(new Insets(12));
        meetBar.setAlignment(Pos.CENTER_LEFT);
        meetBar.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + "; -fx-background-radius: 8;");
        Label meetIcon = new Label("📹 Google Meet Integration:");
        meetIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        meetIcon.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));
        Button btnLaunchMeet = UIComponents.createPrimaryButton("Launch Virtual Meeting");
        btnLaunchMeet.setOnAction(e -> UIComponents.showAlert(Alert.AlertType.INFORMATION, "Google Meet Launcher", "Meeting Launched", "Google Meet Room Created: https://meet.google.com/sb-freelance-demo\nShared link with assigned freelancer!"));
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
        for (Project p : db.getProjectsByClient(currentUser.getId())) {
            if (p.getStatus() == Project.Status.IN_PROGRESS && p.getAssignedFreelancerId() != null) {
                hasConversations = true;
                boolean isSelected = p.getId().equals(selectedChatProjectId);

                VBox projItem = new VBox(2);
                projItem.setPadding(new Insets(10));
                projItem.setStyle("-fx-background-color: " + (isSelected ? UIComponents.COLOR_PRIMARY : UIComponents.COLOR_BG_INPUT) + "; -fx-background-radius: 8; -fx-cursor: hand;");

                Label projTitle = new Label(p.getTitle());
                projTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                projTitle.setTextFill(Color.web(isSelected ? "white" : UIComponents.COLOR_TEXT_PRIMARY));
                projTitle.setWrapText(true);

                Label freeName = new Label("👤 " + (p.getAssignedFreelancerName() != null ? p.getAssignedFreelancerName() : "Freelancer"));
                freeName.setFont(Font.font("Segoe UI", 11));
                freeName.setTextFill(Color.web(isSelected ? "#E0E0FF" : UIComponents.COLOR_TEXT_MUTED));

                long msgCount = db.getChatByProject(p.getId()).size();
                Label msgCountLbl = new Label(msgCount + " messages");
                msgCountLbl.setFont(Font.font("Segoe UI", 10));
                msgCountLbl.setTextFill(Color.web(isSelected ? "#C7D2FE" : UIComponents.COLOR_TEXT_MUTED));

                projItem.getChildren().addAll(projTitle, freeName, msgCountLbl);
                projItem.setOnMouseClicked(ev -> {
                    selectedChatProjectId = p.getId();
                    showChat();
                });

                projectListPanel.getChildren().add(projItem);
            }
        }

        if (!hasConversations) {
            Label noProj = new Label("No active projects with\nassigned freelancers yet.");
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
            for (Project p : db.getProjectsByClient(currentUser.getId())) {
                if (p.getStatus() == Project.Status.IN_PROGRESS && p.getAssignedFreelancerId() != null) {
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
                Label chatWith = new Label("with 👤 " + (selProj.getAssignedFreelancerName() != null ? selProj.getAssignedFreelancerName() : "Freelancer"));
                chatWith.setFont(Font.font("Segoe UI", 12));
                chatWith.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
                Label encBadge = UIComponents.createBadge("🔒 AES-256 Encrypted", UIComponents.COLOR_SUCCESS, "white");
                Region hsp = new Region();
                HBox.setHgrow(hsp, Priority.ALWAYS);
                chatHeader.getChildren().addAll(chatTitle, chatWith, hsp, encBadge);

                // Messages
                VBox msgContainer = new VBox(8);
                msgContainer.setPadding(new Insets(8));

                List<ChatMessage> projectMsgs = db.getChatByProject(selectedChatProjectId);

                if (projectMsgs.isEmpty()) {
                    Label noMsg = new Label("No messages yet. Start the conversation with your freelancer!");
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
                msgScroll.setVvalue(1.0);

                // Input bar
                HBox inputBar = new HBox(10);
                inputBar.setAlignment(Pos.CENTER_LEFT);
                inputBar.setPadding(new Insets(8, 0, 0, 0));
                TextField tfMsg = UIComponents.createTextField("Type encrypted message...");
                HBox.setHgrow(tfMsg, Priority.ALWAYS);
                Button btnSend = UIComponents.createPrimaryButton("Send 🔒");
                Button btnAttach = UIComponents.createSecondaryButton("📎");

                String receiverId = selProj.getAssignedFreelancerId();
                final String chatProjId = selectedChatProjectId;

                btnSend.setOnAction(e -> {
                    if (!tfMsg.getText().trim().isEmpty()) {
                        String cmId = "msg_" + System.currentTimeMillis();
                        ChatMessage cm = new ChatMessage(cmId, chatProjId, currentUser.getId(), currentUser.getUsername(), receiverId,
                                tfMsg.getText().trim(), null, true, new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date()));
                        db.getChatMessages().put(cmId, cm);
                        db.invalidateCaches();
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
                    db.invalidateCaches();
                    db.saveData();
                    showChat();
                });

                inputBar.getChildren().addAll(tfMsg, btnAttach, btnSend);
                chatPanel.getChildren().addAll(chatHeader, new Separator(), msgScroll, new Separator(), inputBar);
            }
        } else {
            Label noChatLbl = new Label("Select a project conversation from the left panel,\nor hire a freelancer to start chatting.");
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

    // 8. Company Profile
    private void showCompanyProfile() {
        VBox box = new VBox(15);
        Label title = UIComponents.createTitle("Company & Client Profile");

        ClientProfile cp = db.getClientProfiles().get(currentUser.getId());
        if (cp == null) cp = new ClientProfile();

        VBox card = UIComponents.createCard();

        TextField tfCompany = UIComponents.createTextField("Company / Client Name...");
        tfCompany.setText(cp.getCompanyName() != null ? cp.getCompanyName() : "");

        TextField tfIndustry = UIComponents.createTextField("Industry Sector...");
        tfIndustry.setText(cp.getIndustry() != null ? cp.getIndustry() : "");

        TextArea taDesc = UIComponents.createTextArea("Company Overview & Description...");
        taDesc.setText(cp.getDescription() != null ? cp.getDescription() : "");

        TextField tfWeb = UIComponents.createTextField("Website URL...");
        tfWeb.setText(cp.getWebsite() != null ? cp.getWebsite() : "");

        ClientProfile finalCp = cp;
        Button btnSave = UIComponents.createPrimaryButton("Save Company Details");
        btnSave.setOnAction(e -> {
            finalCp.setCompanyName(tfCompany.getText().trim());
            finalCp.setIndustry(tfIndustry.getText().trim());
            finalCp.setDescription(taDesc.getText().trim());
            finalCp.setWebsite(tfWeb.getText().trim());

            db.getClientProfiles().put(currentUser.getId(), finalCp);
            db.logActivity("Client updated company profile: " + currentUser.getUsername());
            db.saveData();

            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Saved", "Company details saved successfully!");
        });

        card.getChildren().addAll(
                new Label("Company Name:"), tfCompany,
                new Label("Industry Sector:"), tfIndustry,
                new Label("Description:"), taDesc,
                new Label("Website URL:"), tfWeb,
                btnSave
        );

        box.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(box);
    }
}
