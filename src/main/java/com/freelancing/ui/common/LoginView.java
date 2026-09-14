package com.freelancing.ui.common;

import com.freelancing.model.common.User;
import com.freelancing.service.common.AuthService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.freelancing.config.AppTheme;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.InputStream;
import java.util.function.Consumer;

public class LoginView {
    private final AuthService authService = new AuthService();
    private Consumer<User> onLoginSuccess;
    private boolean adminMode = false;

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
    }

    public boolean isAdminMode() {
        return adminMode;
    }

    public LoginView() {
        this(u -> {});
    }

    public LoginView(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    /** Compatibility constructor for legacy callers */
    public LoginView(Object ignored, Consumer<User> onLoginSuccess) {
        this(onLoginSuccess);
    }

    public Parent createContent() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setStyle(AppTheme.getRootStyle());

        // Top Back to Home action & Theme Toggle
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setMaxWidth(460);

        Button btnBackHome = UIComponents.createExportButton("← Back to SkillBridge Home");
        btnBackHome.setOnAction(e -> HomePage.showHomeView());

        Region topSp = new Region();
        HBox.setHgrow(topSp, Priority.ALWAYS);

        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showLoginView();
        });

        topBar.getChildren().addAll(btnBackHome, topSp, btnTheme);

        // Header Logo & Title
        Label logo = new Label("⚡ SkillBridge");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        logo.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        Label subtitle = new Label("Next-Generation Freelancing & Skill Exchange Platform");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web(AppTheme.getTextMuted()));

        // Thematic Visual Artwork Banner connecting Dark & Light themes
        ImageView bannerImgView = new ImageView();
        try {
            InputStream is = getClass().getResourceAsStream("/images/auth_banner.jpg");
            if (is != null) {
                bannerImgView.setImage(new Image(is));
                bannerImgView.setFitWidth(412);
                bannerImgView.setFitHeight(140);
                bannerImgView.setPreserveRatio(false);

                Rectangle clip = new Rectangle(412, 140);
                clip.setArcWidth(14);
                clip.setArcHeight(14);
                bannerImgView.setClip(clip);

                bannerImgView.setStyle("-fx-cursor: hand;");
                bannerImgView.setOnMouseClicked(e -> {
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "SkillBridge Security", "Enterprise Security Architecture",
                        "SkillBridge enforces zero-trust credential encryption, Argon2 password hashing, and multi-signature escrow protocol.");
                });
            }
        } catch (Exception ignored) {}

        // Form Card
        VBox card = new VBox(15);
        card.setMaxWidth(460);
        card.setPadding(new Insets(24));
        card.setStyle(AppTheme.getCardGlassStyle());

        Label cardTitle = new Label(adminMode ? "Administrator Login" : "Welcome Back");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        cardTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

        if (adminMode) {
            Label adminBadge = UIComponents.createBadge("🛡️ ADMIN SECURITY PORTAL", "rgba(239, 68, 68, 0.2)", "#EF4444");
            Label adminDesc = new Label("Authorized platform staff credentials required for audit & dispute governance.");
            adminDesc.setFont(Font.font("Segoe UI", 12));
            adminDesc.setTextFill(Color.web(AppTheme.getTextMuted()));
            adminDesc.setWrapText(true);
            card.getChildren().addAll(adminBadge, adminDesc);
        }

        VBox alertContainer = new VBox(4);
        alertContainer.setAlignment(Pos.CENTER);

        Label inputLbl = new Label("Username / Email / Mobile Number:");
        inputLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        TextField tfIdentifier = UIComponents.createTextField(adminMode ? "admin" : "Enter username, email, or phone...");

        Label passLbl = new Label("Password:");
        passLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        PasswordField pfPassword = UIComponents.createPasswordField(adminMode ? "admin123" : "Enter password...");

        if (adminMode) {
            tfIdentifier.setText("admin");
            pfPassword.setText("admin123");
        }

        Button btnLogin = UIComponents.createAccentButton(adminMode ? "Authenticate as Administrator" : "Sign In to Account");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        if (adminMode) {
            btnLogin.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        }

        // Enter key listeners for seamless dynamic submission
        tfIdentifier.setOnAction(e -> pfPassword.requestFocus());
        pfPassword.setOnAction(e -> btnLogin.fire());

        Hyperlink linkRegister = new Hyperlink("Don't have an account? Register here");
        linkRegister.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));
        linkRegister.setStyle("-fx-underline: true; -fx-font-size: 13px;");

        Label quickTitle = new Label("Quick Demo Credentials:");
        quickTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        quickTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        HBox demoBar = new HBox(8);
        demoBar.setAlignment(Pos.CENTER);

        Button btnDemoAdmin = UIComponents.createSecondaryButton("👑 Admin");
        btnDemoAdmin.setOnAction(e -> {
            alertContainer.getChildren().clear();
            tfIdentifier.setText("admin");
            pfPassword.setText("admin123");
        });

        Button btnDemoClient = UIComponents.createClientDemoButton("🏢 Client (Sarah)");
        btnDemoClient.setOnAction(e -> {
            alertContainer.getChildren().clear();
            tfIdentifier.setText("sarah_client");
            pfPassword.setText("client123");
        });

        Button btnDemoFree = UIComponents.createFreelancerDemoButton("⚡ Freelancer (Alex)");
        btnDemoFree.setOnAction(e -> {
            alertContainer.getChildren().clear();
            tfIdentifier.setText("alex_dev");
            pfPassword.setText("free123");
        });

        demoBar.getChildren().addAll(btnDemoAdmin, btnDemoClient, btnDemoFree);

        btnLogin.setOnAction(e -> {
            String id = tfIdentifier.getText().trim();
            String pwd = pfPassword.getText().trim();

            alertContainer.getChildren().clear();
            if (id.isEmpty() || pwd.isEmpty()) {
                HBox alert = UIComponents.createInlineAlert("Fields Empty", "Please enter your username/email/mobile and password.", true, () -> alertContainer.getChildren().clear());
                alertContainer.getChildren().add(alert);
                return;
            }

            btnLogin.setDisable(true);
            String originalBtnText = btnLogin.getText();
            btnLogin.setText("Authenticating...");

            com.freelancing.util.AnimationUtil.runAsync(
                () -> authService.login(id, pwd),
                user -> {
                    btnLogin.setDisable(false);
                    btnLogin.setText(originalBtnText);
                    if (user != null) {
                        com.freelancing.app.SessionManager.getInstance().login(user);
                        if (onLoginSuccess != null) {
                            onLoginSuccess.accept(user);
                        } else {
                            HomePage.showRoleView(user);
                        }
                    } else {
                        alertContainer.getChildren().clear();
                        HBox alert = UIComponents.createInlineAlert("Login Failed", "Invalid credentials. Please verify your username and password.", true, () -> alertContainer.getChildren().clear());
                        alertContainer.getChildren().add(alert);
                    }
                }
            );
        });

        linkRegister.setOnAction(e -> {
            HomePage.showRegisterView();
        });

        card.getChildren().addAll(bannerImgView, cardTitle, alertContainer, inputLbl, tfIdentifier, passLbl, pfPassword, btnLogin, linkRegister, new Separator(), quickTitle, demoBar);
        com.freelancing.util.AnimationUtil.applyFadeZoom(card, 300);
        container.getChildren().addAll(topBar, logo, subtitle, card);
        return container;
    }

    public Scene createScene() {
        Parent root = createContent();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1000, bounds.getWidth() * 0.85);
        double sceneHeight = Math.min(720, bounds.getHeight() * 0.88);
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        AppTheme.applyAppStylesheet(scene);
        return scene;
    }

    public SubScene createSubScene(double width, double height) {
        Parent root = createContent();
        return new SubScene(root, width, height);
    }
}
