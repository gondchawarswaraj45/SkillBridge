package com.freelancing.ui.common;

import com.freelancing.model.common.CommunityComment;
import com.freelancing.model.common.CommunityPost;
import com.freelancing.model.common.FeedPost;
import com.freelancing.service.common.CommunityService;

import com.freelancing.app.SessionManager;
import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;
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

/**
 * Developer Community Forum, Engineering Showcases, and Knowledge Sharing Hub.
 * Strictly adheres to Single Stage Rule (swaps Scene on HomePage.primaryStage).
 * 100% Inline CSS driven by AppTheme (non-white pastel light mode).
 */
public class CommunityForumView {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final CommunityService communityService = new CommunityService();
    private StackPane rootStack;
    private VBox feedContainer;
    private String selectedCategory = "All";
    private TextField searchField;

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

        VBox content = new VBox(24);
        content.setPadding(new Insets(24, 40, 40, 40));
        content.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = UIComponents.createScrollPane(content);

        // Community Banner
        HBox banner = createBanner();

        // Filter Bar & Search
        HBox filterBar = createFilterBar();

        // Feed Container
        feedContainer = new VBox(16);
        refreshFeed();

        content.getChildren().addAll(banner, filterBar, feedContainer);
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
        Button linkAnalytics = createNavLink("📊 Analytics", false, () -> HomePage.showAnalyticsDashboardView());
        Button linkCommunity = createNavLink("💬 Community", true, () -> {});

        navLinks.getChildren().addAll(linkMarket, linkSwap, linkEscrow, linkAnalytics, linkCommunity);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showCommunityForumView();
        });

        Button btnNewPost = UIComponents.createAccentButton("＋ New Discussion");
        btnNewPost.setOnAction(e -> openNewPostModal());

        Button btnBackHome = UIComponents.createTitleBarButton("← Back to Home", () -> HomePage.showHomeView());

        nav.getChildren().addAll(logo, navLinks, sp, btnTheme, btnNewPost, btnBackHome);
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
            if (action != null) action.run();
        });
        return btn;
    }

    // =========================================================================
    // COMMUNITY HERO BANNER
    // =========================================================================

    private HBox createBanner() {
        HBox banner = new HBox(20);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(20, 26, 20, 26));
        banner.setStyle(AppTheme.getHeroBannerStyle());

        VBox left = new VBox(8);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label badge = new Label("💬 Engineering Knowledge & Tech Discussions");
        badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        badge.setStyle("-fx-background-color: rgba(99, 102, 241, 0.15); -fx-text-fill: " + AppTheme.COLOR_PRIMARY + "; -fx-padding: 4 12; -fx-background-radius: 20; -fx-cursor: hand;");
        badge.setOnMouseClicked(e -> openCommunityGuidelinesModal());

        Label heading = new Label("Connect With Elite Developers Worldwide");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label sub = new Label("Share architectural lessons, request code reviews, discuss tech stacks, and showcase production deployments.");
        sub.setFont(Font.font("Segoe UI", 12));
        sub.setTextFill(Color.web(AppTheme.getTextMuted()));
        sub.setMaxWidth(650);

        left.getChildren().addAll(badge, heading, sub);

        Button btnStartPost = UIComponents.createAccentButton("＋ Share Experience");
        btnStartPost.setOnAction(e -> openNewPostModal());

        banner.getChildren().addAll(left, btnStartPost);
        return banner;
    }

    // =========================================================================
    // FILTER BAR & SEARCH
    // =========================================================================

    private HBox createFilterBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        searchField = UIComponents.createTextField("Search discussions, topics, technologies...");
        searchField.setPrefWidth(320);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshFeed());

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox categoryChips = new HBox(6);
        String[] cats = {"All", "Discussions", "Questions", "Showcase", "Hiring", "Feedback"};
        for (String c : cats) {
            Button chip = new Button(c);
            chip.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
            applyChipStyle(chip, c.equals(selectedCategory));
            chip.setOnAction(e -> {
                selectedCategory = c;
                for (Node n : categoryChips.getChildren()) {
                    if (n instanceof Button) {
                        applyChipStyle((Button) n, ((Button) n).getText().equals(selectedCategory));
                    }
                }
                refreshFeed();
            });
            categoryChips.getChildren().add(chip);
        }

        bar.getChildren().addAll(new Label("🔍"), searchField, sp, categoryChips);
        return bar;
    }

    private void applyChipStyle(Button chip, boolean active) {
        if (active) {
            chip.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 5 14; -fx-cursor: hand;");
        } else {
            chip.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #1E293B; -fx-text-fill: #CBD5E1; -fx-border-color: #334155; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14; -fx-cursor: hand;"
                : "-fx-background-color: #DCE6F5; -fx-text-fill: #1E293B; -fx-border-color: #BACADB; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14; -fx-cursor: hand;");
        }
    }

    // =========================================================================
    // POSTS FEED (SQLITE BACKED + FALLBACK)
    // =========================================================================

    private void refreshFeed() {
        if (feedContainer == null) return;
        feedContainer.getChildren().clear();

        final String query = searchField != null ? searchField.getText().trim() : "";
        final String catFilter = "All".equalsIgnoreCase(selectedCategory) ? null : selectedCategory;
        final String currentUserId = getEffectiveUserId();

        List<CommunityPost> posts;
        try {
            posts = communityService.getPosts(catFilter, query, currentUserId);
        } catch (Exception e) {
            posts = Collections.emptyList();
        }

        if (posts.isEmpty()) {
            // Check legacy feed posts as fallback
            List<FeedPost> legacy = new ArrayList<>(db.getFeedPosts().values());
            if (legacy.isEmpty()) {
                VBox empty = new VBox(8);
                empty.setPadding(new Insets(30));
                empty.setAlignment(Pos.CENTER);
                Label emptyLbl = new Label("No community discussions match your current filter.");
                emptyLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
                empty.getChildren().add(emptyLbl);
                feedContainer.getChildren().add(empty);
                return;
            } else {
                for (FeedPost fp : legacy) {
                    feedContainer.getChildren().add(createLegacyPostCard(fp));
                }
                return;
            }
        }

        for (CommunityPost post : posts) {
            feedContainer.getChildren().add(createPostCard(post));
        }
    }

    private VBox createPostCard(CommunityPost post) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);");

        // Top Author Row
        HBox authorRow = new HBox(10);
        authorRow.setAlignment(Pos.CENTER_LEFT);

        Node avatar = createThematicAvatar(36);

        VBox meta = new VBox(2);
        Label name = new Label(post.getAuthorName() != null ? post.getAuthorName() : "Community Member");
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        name.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label date = new Label(post.getCreatedAt() != null ? post.getCreatedAt() : "Recently");
        date.setFont(Font.font("Segoe UI", 11));
        date.setTextFill(Color.web(AppTheme.getTextMuted()));
        meta.getChildren().addAll(name, date);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label catBadge = UIComponents.createBadge(post.getCategory(), "rgba(16, 185, 129, 0.2)", AppTheme.COLOR_SUCCESS);

        Button btnReport = new Button("🚩 Report");
        btnReport.setFont(Font.font("Segoe UI", 10));
        btnReport.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-cursor: hand; -fx-padding: 2 6;");
        btnReport.setOnAction(e -> openReportModal(post.getId(), post.getTitle()));

        authorRow.getChildren().addAll(avatar, meta, sp, catBadge, btnReport);

        // Title & Content
        Label title = new Label(post.getTitle());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setTextFill(Color.web(AppTheme.getTextPrimary()));
        title.setWrapText(true);

        Label content = new Label(post.getContent());
        content.setFont(Font.font("Segoe UI", 13));
        content.setTextFill(Color.web(AppTheme.getTextMuted()));
        content.setWrapText(true);

        // Interaction Action Bar (Like Counter + Comments)
        HBox actionRow = new HBox(14);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        final String currentUserId = getEffectiveUserId();
        Button btnLike = new Button((post.isLikedByCurrentUser() ? "❤️ " : "🤍 ") + post.getLikes() + " Likes");
        btnLike.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        updateLikeButtonStyle(btnLike, post.isLikedByCurrentUser());

        btnLike.setOnAction(e -> {
            boolean nowLiked = communityService.toggleLike(post.getId(), currentUserId);
            post.setLikedByCurrentUser(nowLiked);
            post.setLikes(post.getLikes() + (nowLiked ? 1 : -1));
            btnLike.setText((nowLiked ? "❤️ " : "🤍 ") + post.getLikes() + " Likes");
            updateLikeButtonStyle(btnLike, nowLiked);
        });

        Button btnComments = new Button("💬 " + post.getCommentsCount() + " Comments");
        btnComments.setFont(Font.font("Segoe UI", 11));
        btnComments.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #0F172A; -fx-text-fill: #94A3B8; -fx-background-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;"
            : "-fx-background-color: #CAD8EA; -fx-text-fill: #1E293B; -fx-border-color: #A6BCD6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;");

        actionRow.getChildren().addAll(btnLike, btnComments);

        // Comments Drawer (collapsible)
        VBox commentsBox = new VBox(8);
        commentsBox.setVisible(false);
        commentsBox.setManaged(false);
        commentsBox.setPadding(new Insets(10));
        commentsBox.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #0F172A; -fx-background-radius: 8;"
            : "-fx-background-color: #CFDAEC; -fx-background-radius: 8;");

        VBox commentsList = new VBox(6);
        commentsBox.getChildren().add(commentsList);

        HBox addCmtRow = new HBox(8);
        TextField tfNewCmt = UIComponents.createTextField("Write a reply...");
        HBox.setHgrow(tfNewCmt, Priority.ALWAYS);
        Button btnAddCmt = UIComponents.createPrimaryButton("Reply");
        btnAddCmt.setOnAction(e -> {
            String text = tfNewCmt.getText().trim();
            if (!text.isEmpty()) {
                CommunityComment created = communityService.addComment(post.getId(), currentUserId, text);
                tfNewCmt.clear();
                post.setCommentsCount(post.getCommentsCount() + 1);
                btnComments.setText("💬 " + post.getCommentsCount() + " Comments");

                HBox cRow = buildCommentRow(created.getAuthorName() != null ? created.getAuthorName() : "You", text, created.getCreatedAt());
                commentsList.getChildren().add(cRow);
            }
        });
        addCmtRow.getChildren().addAll(tfNewCmt, btnAddCmt);
        commentsBox.getChildren().add(addCmtRow);

        btnComments.setOnAction(e -> {
            boolean show = !commentsBox.isVisible();
            commentsBox.setVisible(show);
            commentsBox.setManaged(show);
            if (show) {
                // Populate comments from SQLite
                commentsList.getChildren().clear();
                List<CommunityComment> loaded = communityService.getComments(post.getId());
                for (CommunityComment cmt : loaded) {
                    commentsList.getChildren().add(buildCommentRow(cmt.getAuthorName() != null ? cmt.getAuthorName() : "User", cmt.getContent(), cmt.getCreatedAt()));
                }
            }
        });

        card.getChildren().addAll(authorRow, title, content, actionRow, commentsBox);
        return card;
    }

    private void updateLikeButtonStyle(Button btnLike, boolean liked) {
        if (liked) {
            btnLike.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: rgba(239, 68, 68, 0.25); -fx-text-fill: #F87171; -fx-background-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;"
                : "-fx-background-color: #FFE4E6; -fx-text-fill: #9F1239; -fx-border-color: #FDA4AF; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;");
        } else {
            btnLike.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #0F172A; -fx-text-fill: #94A3B8; -fx-background-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;"
                : "-fx-background-color: #CAD8EA; -fx-text-fill: #1E293B; -fx-border-color: #A6BCD6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;");
        }
    }

    private HBox buildCommentRow(String author, String text, String time) {
        HBox cRow = new HBox(8);
        cRow.setAlignment(Pos.CENTER_LEFT);
        Label cUser = new Label(author + ":");
        cUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        cUser.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
        Label cTxt = new Label(text);
        cTxt.setFont(Font.font("Segoe UI", 11));
        cTxt.setTextFill(Color.web(AppTheme.getTextPrimary()));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label cTime = new Label(time != null ? time : "");
        cTime.setFont(Font.font("Segoe UI", 9));
        cTime.setTextFill(Color.web(AppTheme.getTextMuted()));
        cRow.getChildren().addAll(cUser, cTxt, sp, cTime);
        return cRow;
    }

    private VBox createLegacyPostCard(FeedPost post) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle(AppTheme.isDarkMode()
            ? "-fx-background-color: #1E293B; -fx-border-color: rgba(99, 102, 241, 0.25); -fx-border-radius: 12; -fx-background-radius: 12;"
            : "-fx-background-color: #DCE6F5; -fx-border-color: #93C5FD; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox authorRow = new HBox(10);
        authorRow.setAlignment(Pos.CENTER_LEFT);
        Node avatar = createThematicAvatar(36);
        VBox meta = new VBox(2);
        Label name = new Label(post.getAuthorName());
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        name.setTextFill(Color.web(AppTheme.getTextPrimary()));
        meta.getChildren().add(name);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label catBadge = UIComponents.createBadge(post.getCategory().name(), "rgba(16, 185, 129, 0.2)", AppTheme.COLOR_SUCCESS);
        authorRow.getChildren().addAll(avatar, meta, sp, catBadge);

        Label title = new Label(post.getTitle());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label content = new Label(post.getContent());
        content.setFont(Font.font("Segoe UI", 13));
        content.setTextFill(Color.web(AppTheme.getTextMuted()));
        content.setWrapText(true);

        card.getChildren().addAll(authorRow, title, content);
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

    private void openReportModal(String postId, String postTitle) {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(480);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("🚩 Report Post");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        Label targetLbl = new Label("Flagging: " + postTitle);
        targetLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        targetLbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        ComboBox<String> cbReason = new ComboBox<>();
        cbReason.getItems().addAll(
                "Spam or Advertisement",
                "Harassment or Inappropriate Content",
                "Intellectual Property / NDA Violation",
                "Off-Topic / Low Quality",
                "Other Policy Violation"
        );
        cbReason.setValue("Spam or Advertisement");
        UIComponents.styleComboBox(cbReason);

        TextArea taDetails = UIComponents.createTextArea("Additional notes for moderation team (optional)...");
        taDetails.setPrefRowCount(3);

        Button btnSubmitReport = UIComponents.createDangerButton("Submit Moderation Report");
        btnSubmitReport.setOnAction(e -> {
            String fullReason = cbReason.getValue() + (taDetails.getText().trim().isEmpty() ? "" : " - " + taDetails.getText().trim());
            boolean success = communityService.reportPost(postId, getEffectiveUserId(), fullReason);
            closeModal();
            if (success) {
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Report Filed", "Thank you", "Our moderation team has received your report.");
            } else {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Report Error", "Failed", "Could not submit report. Please try again.");
            }
        });

        modal.getChildren().addAll(header, targetLbl, new Label("Reason for reporting:"), cbReason, new Label("Details:"), taDetails, btnSubmitReport);
        showModal(modal);
    }

    // =========================================================================
    // NEW POST MODAL
    // =========================================================================

    private void openNewPostModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(520);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("💬 Share With Developer Community");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        TextField tfTitle = UIComponents.createTextField("Post Title (e.g. Scaling Spring Boot Microservices with Redis)...");
        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll("Discussions", "Questions", "Showcase", "Hiring", "Feedback");
        cbCat.setValue("Discussions");
        UIComponents.styleComboBox(cbCat);

        TextArea taContent = UIComponents.createTextArea("Write your technical insights, architectural decisions, or discussion topic...");
        taContent.setPrefRowCount(5);

        Button btnSubmit = UIComponents.createAccentButton("Publish to SkillBridge Community");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> {
            String pTitle = tfTitle.getText().trim();
            String pContent = taContent.getText().trim();
            if (pTitle.isEmpty() || pContent.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Missing Fields", "Incomplete Post", "Please provide a title and content.");
                return;
            }

            String currentUserId = getEffectiveUserId();
            try {
                communityService.createPost(currentUserId, pTitle, pContent, cbCat.getValue());
            } catch (Exception ex) {
                // Fallback / log
            }

            // Also keep legacy feed updated
            try {
                FeedPost.PostCategory catEnum = FeedPost.PostCategory.SHOWCASE;
                if ("Discussions".equalsIgnoreCase(cbCat.getValue())) catEnum = FeedPost.PostCategory.DISCUSSION;
                else if ("Feedback".equalsIgnoreCase(cbCat.getValue())) catEnum = FeedPost.PostCategory.FEEDBACK;
                else if ("Hiring".equalsIgnoreCase(cbCat.getValue())) catEnum = FeedPost.PostCategory.JOB_POST;

                FeedPost newPost = new FeedPost(
                    "feed_" + UUID.randomUUID().toString().substring(0, 8),
                    currentUserId, SessionManager.getInstance().getCurrentUsername(), "MEMBER",
                    pTitle, pContent, catEnum, FeedPost.PostStatus.ACTIVE, "Just now"
                );
                db.getFeedPosts().put(newPost.getId(), newPost);
                db.saveData();
            } catch (Exception ignored) {}

            closeModal();
            refreshFeed();
            UIComponents.showAlert(Alert.AlertType.INFORMATION, "Published", "Post Created", "Your post is now live on the community forum!");
        });

        modal.getChildren().addAll(header, new Label("Post Title:"), tfTitle, new Label("Category:"), cbCat, new Label("Content:"), taContent, btnSubmit);
        showModal(modal);
    }

    private void openCommunityGuidelinesModal() {
        VBox modal = UIComponents.createGlassCard();
        modal.setMaxWidth(560);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = UIComponents.createHeader("📜 SkillBridge Community Guidelines");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextMuted() + "; -fx-font-size: 16; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeModal());
        header.getChildren().addAll(title, sp, btnClose);

        VBox rules = new VBox(10);
        rules.setPadding(new Insets(12, 0, 12, 0));

        String[][] guidelines = {
            {"🤝 Respect & Constructive Feedback", "Focus feedback on technical decisions, design patterns, and code clarity."},
            {"🔒 Non-Disclosure & IP Integrity", "Do not post proprietary client source code, credentials, or private API keys."},
            {"⚡ Open Knowledge Sharing", "Share reusable benchmarks, open-source libraries, and architectural lessons."},
            {"🛡️ Zero Spam & Self-Promotion Policy", "Job postings belong exclusively in the Job Post category with transparent budgets."}
        };

        for (String[] g : guidelines) {
            VBox item = new VBox(2);
            item.setPadding(new Insets(8, 12, 8, 12));
            item.setStyle(AppTheme.isDarkMode()
                ? "-fx-background-color: #0F172A; -fx-background-radius: 8;"
                : "-fx-background-color: #CAD8EA; -fx-background-radius: 8;");
            Label h = new Label(g[0]);
            h.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            h.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
            Label b = new Label(g[1]);
            b.setFont(Font.font("Segoe UI", 11));
            b.setTextFill(Color.web(AppTheme.getTextPrimary()));
            b.setWrapText(true);
            item.getChildren().addAll(h, b);
            rules.getChildren().add(item);
        }

        Button btnDismiss = UIComponents.createPrimaryButton("I Agree & Understand");
        btnDismiss.setMaxWidth(Double.MAX_VALUE);
        btnDismiss.setOnAction(e -> closeModal());

        modal.getChildren().addAll(header, rules, btnDismiss);
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
}
