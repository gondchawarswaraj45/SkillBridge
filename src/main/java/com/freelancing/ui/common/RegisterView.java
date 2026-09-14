package com.freelancing.ui.common;

import com.freelancing.model.common.User;
import com.freelancing.service.common.AuthService;

import com.freelancing.config.AppTheme;
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
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.InputStream;
import java.util.function.Consumer;

public class RegisterView {
    private final AuthService authService = new AuthService();
    private final Consumer<User> onLoginSuccess;

    public RegisterView() {
        this(u -> {});
    }

    public RegisterView(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    /** Compatibility constructor for legacy callers */
    public RegisterView(Object ignored, Consumer<User> onLoginSuccess) {
        this(onLoginSuccess);
    }

    public Parent createContent() {
        StackPane root = new StackPane();
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setStyle(AppTheme.getRootStyle());

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setMaxWidth(500);

        Button btnBackHome = UIComponents.createExportButton("← Back to SkillBridge Home");
        btnBackHome.setOnAction(e -> HomePage.showHomeView());

        Region topSp = new Region();
        HBox.setHgrow(topSp, Priority.ALWAYS);

        Button btnTheme = UIComponents.createThemeToggle(() -> {
            HomePage.showRegisterView();
        });

        topBar.getChildren().addAll(btnBackHome, topSp, btnTheme);

        Label logo = new Label("⚡ Join SkillBridge");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        logo.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        VBox card = new VBox(12);
        card.setMaxWidth(500);
        card.setPadding(new Insets(26));
        card.setStyle(AppTheme.getCardGlassStyle());

        // Thematic Visual Banner
        ImageView authBannerView = new ImageView();
        try {
            InputStream is = getClass().getResourceAsStream("/images/auth_banner.jpg");
            if (is != null) {
                authBannerView.setImage(new Image(is));
                authBannerView.setFitWidth(448);
                authBannerView.setFitHeight(125);
                authBannerView.setPreserveRatio(false);

                Rectangle clip = new Rectangle(448, 125);
                clip.setArcWidth(14);
                clip.setArcHeight(14);
                authBannerView.setClip(clip);
            }
        } catch (Exception ignored) {}

        Label cardTitle = new Label("Create New Account");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        cardTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

        // Account Type Choice with Thematic 3D Role Badge
        Label roleLbl = new Label("Account Type:");
        roleLbl.setTextFill(Color.web(AppTheme.getTextMuted()));
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll(
            "Freelancer (Offer Skills & Work on Projects)",
            "Client (Post Projects & Hire Talent)"
        );
        cbRole.getSelectionModel().select(0);
        cbRole.setMaxWidth(Double.MAX_VALUE);
        UIComponents.styleComboBox(cbRole);

        ImageView roleBadgeView = new ImageView();
        roleBadgeView.setFitWidth(56);
        roleBadgeView.setFitHeight(56);
        roleBadgeView.setPreserveRatio(true);
        Rectangle badgeClip = new Rectangle(56, 56);
        badgeClip.setArcWidth(56);
        badgeClip.setArcHeight(56);
        roleBadgeView.setClip(badgeClip);

        Runnable updateRoleBadge = () -> {
            boolean isFreelancer = cbRole.getSelectionModel().getSelectedIndex() == 0;
            String path = isFreelancer ? "/images/skillbridge_avatar.jpg" : "/images/skillbridge_client_badge.jpg";
            try {
                InputStream st = getClass().getResourceAsStream(path);
                if (st != null) {
                    roleBadgeView.setImage(new Image(st));
                }
            } catch (Exception ignored) {}
        };
        updateRoleBadge.run();
        cbRole.setOnAction(e -> updateRoleBadge.run());

        roleBadgeView.setStyle("-fx-cursor: hand;");
        roleBadgeView.setOnMouseClicked(e -> {
            int next = (cbRole.getSelectionModel().getSelectedIndex() + 1) % 2;
            cbRole.getSelectionModel().select(next);
            updateRoleBadge.run();
        });

        HBox roleRow = new HBox(12);
        roleRow.setAlignment(Pos.CENTER_LEFT);
        VBox roleBox = new VBox(4);
        HBox.setHgrow(roleBox, Priority.ALWAYS);
        roleBox.getChildren().addAll(roleLbl, cbRole);
        roleRow.getChildren().addAll(roleBadgeView, roleBox);

        // Fields
        TextField tfUsername = UIComponents.createTextField("Choose username...");
        TextField tfEmail = UIComponents.createTextField("Work email address...");
        TextField tfPhone = UIComponents.createTextField("Mobile number (with country code)...");

        // Password with double-check retype and dynamic real-time strength meter
        PasswordField pfPassword1 = UIComponents.createPasswordField("Create strong password...");
        PasswordField pfPassword2 = UIComponents.createPasswordField("Retype password to confirm...");

        ProgressBar pbStrength = new ProgressBar(0);
        pbStrength.setMaxWidth(Double.MAX_VALUE);
        pbStrength.setPrefHeight(6);

        Label lblStrength = new Label("Password Strength: None");
        lblStrength.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lblStrength.setTextFill(Color.web(AppTheme.getTextMuted()));

        pfPassword1.textProperty().addListener((obs, oldV, newV) -> {
            int score = 0;
            if (newV.length() >= 6) score++;
            if (newV.length() >= 10) score++;
            if (newV.matches(".*[A-Z].*")) score++;
            if (newV.matches(".*[0-9].*")) score++;
            if (newV.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;

            if (newV.isEmpty()) {
                pbStrength.setProgress(0);
                lblStrength.setText("Password Strength: None");
                lblStrength.setTextFill(Color.web(AppTheme.getTextMuted()));
            } else if (score <= 2) {
                pbStrength.setProgress(0.25);
                lblStrength.setText("Password Strength: Weak (add uppercase & digits)");
                lblStrength.setTextFill(Color.web(AppTheme.COLOR_DANGER));
            } else if (score == 3) {
                pbStrength.setProgress(0.60);
                lblStrength.setText("Password Strength: Fair");
                lblStrength.setTextFill(Color.web(AppTheme.COLOR_AMBER));
            } else if (score == 4) {
                pbStrength.setProgress(0.85);
                lblStrength.setText("Password Strength: Strong");
                lblStrength.setTextFill(Color.web(AppTheme.COLOR_SUCCESS));
            } else {
                pbStrength.setProgress(1.0);
                lblStrength.setText("Password Strength: Enterprise Grade 🛡️");
                lblStrength.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
            }
        });

        // Enter key listeners for dynamic seamless navigation
        tfUsername.setOnAction(e -> tfEmail.requestFocus());
        tfEmail.setOnAction(e -> tfPhone.requestFocus());
        tfPhone.setOnAction(e -> pfPassword1.requestFocus());
        pfPassword1.setOnAction(e -> pfPassword2.requestFocus());

        Button btnRegister = UIComponents.createSuccessButton("Verify via OTP & Create Account");
        btnRegister.setMaxWidth(Double.MAX_VALUE);

        pfPassword2.setOnAction(e -> btnRegister.fire());

        Hyperlink linkBack = new Hyperlink("Already have an account? Back to Login");
        linkBack.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));
        linkBack.setStyle("-fx-underline: true; -fx-font-size: 13px;");

        VBox alertBox = new VBox();
        alertBox.setAlignment(Pos.CENTER);

        Consumer<String> showError = msg -> {
            alertBox.getChildren().clear();
            HBox alert = UIComponents.createInlineAlert("Registration Error", msg, true, () -> alertBox.getChildren().clear());
            alertBox.getChildren().add(alert);
        };
        Consumer<String> showWarning = msg -> {
            alertBox.getChildren().clear();
            HBox alert = UIComponents.createInlineAlert("Missing Fields", msg, true, () -> alertBox.getChildren().clear());
            alertBox.getChildren().add(alert);
        };

        btnRegister.setOnAction(e -> {
            String uname = tfUsername.getText().trim();
            String email = tfEmail.getText().trim();
            String phone = tfPhone.getText().trim();
            String pwd1 = pfPassword1.getText().trim();
            String pwd2 = pfPassword2.getText().trim();

            if (uname.isEmpty() || email.isEmpty() || phone.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()) {
                showWarning.accept("Please complete all registration fields.");
                return;
            }

            if (!pwd1.equals(pwd2)) {
                showError.accept("Passwords do not match. Please retype your password.");
                return;
            }

            alertBox.getChildren().clear();
            btnRegister.setDisable(true);
            btnRegister.setText("Checking Availability...");

            com.freelancing.util.AnimationUtil.runAsync(
                () -> {
                    boolean uAvail = authService.isUsernameAvailable(uname);
                    boolean eAvail = authService.isEmailAvailable(email);
                    return new boolean[] { uAvail, eAvail };
                },
                results -> {
                    btnRegister.setDisable(false);
                    btnRegister.setText("Verify via OTP & Create Account");

                    if (!results[0]) {
                        showError.accept("Username '" + uname + "' is already registered.");
                        return;
                    }
                    if (!results[1]) {
                        showError.accept("Email address '" + email + "' is already registered.");
                        return;
                    }

                    // In-Scene OTP Verification Overlay (Zero Multi-Stage Windows)
                    String simulatedOtp = authService.generateSimulatedOtp();
                    showOtpModalOverlay(root, simulatedOtp, phone, email, () -> {
                        btnRegister.setDisable(true);
                        btnRegister.setText("Creating Account...");
                        User.Role role = cbRole.getSelectionModel().getSelectedIndex() == 0 ? User.Role.FREELANCER : User.Role.CLIENT;

                        com.freelancing.util.AnimationUtil.runAsync(
                            () -> authService.registerUser(uname, email, phone, pwd1, role),
                            newUser -> {
                                btnRegister.setDisable(false);
                                btnRegister.setText("Verify via OTP & Create Account");
                                com.freelancing.app.SessionManager.getInstance().login(newUser);
                                if (onLoginSuccess != null) {
                                    onLoginSuccess.accept(newUser);
                                } else {
                                    HomePage.showRoleView(newUser);
                                }
                            }
                        );
                    });
                }
            );
        });

        linkBack.setOnAction(e -> {
            HomePage.showLoginView();
        });

        Label lblUser = new Label("Username:");
        lblUser.setTextFill(Color.web(AppTheme.getTextMuted()));
        Label lblEmail = new Label("Email Address:");
        lblEmail.setTextFill(Color.web(AppTheme.getTextMuted()));
        Label lblPhone = new Label("Mobile Number:");
        lblPhone.setTextFill(Color.web(AppTheme.getTextMuted()));
        Label lblPass1 = new Label("Password:");
        lblPass1.setTextFill(Color.web(AppTheme.getTextMuted()));
        Label lblPass2 = new Label("Confirm Password:");
        lblPass2.setTextFill(Color.web(AppTheme.getTextMuted()));

        card.getChildren().addAll(authBannerView, cardTitle, alertBox, roleRow,
                lblUser, tfUsername,
                lblEmail, tfEmail,
                lblPhone, tfPhone,
                lblPass1, pfPassword1,
                pbStrength, lblStrength,
                lblPass2, pfPassword2,
                btnRegister, linkBack);

        com.freelancing.util.AnimationUtil.applyFadeZoom(card, 300);

        container.getChildren().addAll(topBar, logo, card);
        root.getChildren().add(container);
        return root;
    }

    private void showOtpModalOverlay(StackPane root, String simulatedOtp, String phone, String email, Runnable onSuccess) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        overlay.setPadding(new Insets(24));

        VBox card = new VBox(16);
        card.setMaxWidth(460);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: " + AppTheme.getBgCard() + ";"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: " + AppTheme.getBorderCard() + ";"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 1.5;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 28, 0, 0, 10);");

        Label lblIcon = new Label("🔐");
        lblIcon.setStyle("-fx-font-size: 28px; -fx-background-color: #3B82F6; -fx-background-radius: 28; "
                + "-fx-min-width: 52px; -fx-min-height: 52px; -fx-alignment: center; -fx-text-fill: white;");

        Label lblTitle = new Label("Mobile & Email OTP Verification");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        lblTitle.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Label lblSubtitle = new Label("Simulated SMS/Email OTP sent to " + phone + " & " + email);
        lblSubtitle.setFont(Font.font("Segoe UI", 12));
        lblSubtitle.setTextFill(Color.web(AppTheme.getTextMuted()));
        lblSubtitle.setWrapText(true);
        lblSubtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label lblDemoBadge = new Label("Demo OTP: " + simulatedOtp);
        lblDemoBadge.setStyle("-fx-background-color: rgba(59, 130, 246, 0.15); -fx-text-fill: #60A5FA; "
                + "-fx-font-family: monospace; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 6; -fx-border-color: rgba(96, 165, 250, 0.3); -fx-border-radius: 6;");

        TextField txtOtp = UIComponents.createTextField("Enter 6-digit OTP...");
        txtOtp.setStyle(txtOtp.getStyle() + "; -fx-alignment: center; -fx-font-size: 16px; -fx-font-weight: bold; -fx-letter-spacing: 4px;");
        txtOtp.setMaxWidth(240);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        lblError.setVisible(false);
        lblError.setManaged(false);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER);

        Button btnCancel = UIComponents.createSecondaryButton("Cancel");
        btnCancel.setOnAction(e -> root.getChildren().remove(overlay));

        Button btnVerify = UIComponents.createSuccessButton("Verify & Create Account");
        btnVerify.setOnAction(e -> {
            String entered = txtOtp.getText() != null ? txtOtp.getText().trim() : "";
            if (entered.equals(simulatedOtp)) {
                root.getChildren().remove(overlay);
                onSuccess.run();
            } else {
                lblError.setText("Incorrect OTP. Please enter " + simulatedOtp);
                lblError.setVisible(true);
                lblError.setManaged(true);
                com.freelancing.util.AnimationUtil.shakeNode(card);
            }
        });

        txtOtp.setOnAction(e -> btnVerify.fire());

        actions.getChildren().addAll(btnCancel, btnVerify);
        card.getChildren().addAll(lblIcon, lblTitle, lblSubtitle, lblDemoBadge, txtOtp, lblError, actions);
        overlay.getChildren().add(card);

        root.getChildren().add(overlay);
        com.freelancing.util.AnimationUtil.applyPopScale(card, 220);
        javafx.application.Platform.runLater(txtOtp::requestFocus);
    }

    public Scene createScene() {
        Parent root = createContent();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1000, bounds.getWidth() * 0.85);
        double sceneHeight = Math.min(780, bounds.getHeight() * 0.90);
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        AppTheme.applyAppStylesheet(scene);
        return scene;
    }

    public SubScene createSubScene(double width, double height) {
        Parent root = createContent();
        return new SubScene(root, width, height);
    }
}
