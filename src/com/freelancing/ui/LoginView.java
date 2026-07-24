package com.freelancing.ui;

import com.freelancing.model.User;
import com.freelancing.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class LoginView {
    private final Stage stage;
    private final AuthService authService = new AuthService();
    private Consumer<User> onLoginSuccess;

    public LoginView(Stage stage, Consumer<User> onLoginSuccess) {
        this.stage = stage;
        this.onLoginSuccess = onLoginSuccess;
    }

    public Scene createScene() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_DARK + ";");

        // Header Logo & Title
        Label logo = new Label("⚡ Freelancing.SB");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        logo.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));

        Label subtitle = new Label("Next-Generation Freelance Platform for Desktop");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));

        // Form Card
        VBox card = new VBox(15);
        card.setMaxWidth(420);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + ";" +
                      "-fx-border-color: " + UIComponents.COLOR_BORDER + ";" +
                      "-fx-border-radius: 12;" +
                      "-fx-background-radius: 12;");

        Label cardTitle = new Label("Welcome Back");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        cardTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

        Label inputLbl = new Label("Username / Email / Mobile Number:");
        inputLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        TextField tfIdentifier = UIComponents.createTextField("Enter username, email, or phone...");

        Label passLbl = new Label("Password:");
        passLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        PasswordField pfPassword = UIComponents.createPasswordField("Enter password...");

        Button btnLogin = UIComponents.createPrimaryButton("Sign In to Account");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        Hyperlink linkRegister = new Hyperlink("Don't have an account? Register here");
        linkRegister.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));
        linkRegister.setStyle("-fx-underline: true;");

        Label quickTitle = new Label("Quick Demo Logins:");
        quickTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        quickTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        HBox demoBar = new HBox(8);
        demoBar.setAlignment(Pos.CENTER);

        Button btnDemoAdmin = UIComponents.createSecondaryButton("Admin");
        btnDemoAdmin.setOnAction(e -> {
            tfIdentifier.setText("admin");
            pfPassword.setText("admin123");
        });

        Button btnDemoClient = UIComponents.createSecondaryButton("Client");
        btnDemoClient.setOnAction(e -> {
            tfIdentifier.setText("client@example.com");
            pfPassword.setText("client123");
        });

        Button btnDemoFree = UIComponents.createSecondaryButton("Freelancer");
        btnDemoFree.setOnAction(e -> {
            tfIdentifier.setText("freelancer@example.com");
            pfPassword.setText("free123");
        });

        demoBar.getChildren().addAll(btnDemoAdmin, btnDemoClient, btnDemoFree);

        btnLogin.setOnAction(e -> {
            String id = tfIdentifier.getText().trim();
            String pwd = pfPassword.getText().trim();

            if (id.isEmpty() || pwd.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Login Error", "Fields Empty", "Please enter your username/email/mobile and password.");
                return;
            }

            User user = authService.login(id, pwd);
            if (user != null) {
                if (onLoginSuccess != null) {
                    onLoginSuccess.accept(user);
                }
            } else {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Credentials", "No account found matching the given identifier and password.");
            }
        });

        linkRegister.setOnAction(e -> {
            RegisterView registerView = new RegisterView(stage, onLoginSuccess);
            stage.setScene(registerView.createScene());
        });

        card.getChildren().addAll(cardTitle, inputLbl, tfIdentifier, passLbl, pfPassword, btnLogin, linkRegister, new Separator(), quickTitle, demoBar);
        container.getChildren().addAll(logo, subtitle, card);

        return new Scene(container, 900, 650);
    }
}
