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

import java.util.Optional;
import java.util.function.Consumer;

public class RegisterView {
    private final Stage stage;
    private final AuthService authService = new AuthService();
    private final Consumer<User> onLoginSuccess;

    public RegisterView(Stage stage, Consumer<User> onLoginSuccess) {
        this.stage = stage;
        this.onLoginSuccess = onLoginSuccess;
    }

    public Scene createScene() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_DARK + ";");

        Label logo = new Label("⚡ Join Freelancing.SB");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));

        VBox card = new VBox(12);
        card.setMaxWidth(480);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_CARD + ";" +
                      "-fx-border-color: " + UIComponents.COLOR_BORDER + ";" +
                      "-fx-border-radius: 12;" +
                      "-fx-background-radius: 12;");

        Label cardTitle = new Label("Create New Account");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        cardTitle.setTextFill(Color.web(UIComponents.COLOR_TEXT_PRIMARY));

        // Account Type Choice
        Label roleLbl = new Label("Account Type:");
        roleLbl.setTextFill(Color.web(UIComponents.COLOR_TEXT_MUTED));
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("Freelancer (Work & Earn)", "Client (Hire & Post Projects)");
        cbRole.getSelectionModel().select(0);
        cbRole.setMaxWidth(Double.MAX_VALUE);
        cbRole.setStyle("-fx-background-color: " + UIComponents.COLOR_BG_INPUT + "; -fx-text-fill: white;");

        // Fields
        TextField tfUsername = UIComponents.createTextField("Username...");
        TextField tfEmail = UIComponents.createTextField("Email Address...");
        TextField tfPhone = UIComponents.createTextField("Mobile Number (with country code)...");

        // Password with double-check retype
        PasswordField pfPassword1 = UIComponents.createPasswordField("Create Password...");
        PasswordField pfPassword2 = UIComponents.createPasswordField("Retype Password to Verify...");

        Button btnRegister = UIComponents.createSuccessButton("Verify via OTP & Create Account");
        btnRegister.setMaxWidth(Double.MAX_VALUE);

        Hyperlink linkBack = new Hyperlink("Already have an account? Back to Login");
        linkBack.setTextFill(Color.web(UIComponents.COLOR_PRIMARY));

        btnRegister.setOnAction(e -> {
            String uname = tfUsername.getText().trim();
            String email = tfEmail.getText().trim();
            String phone = tfPhone.getText().trim();
            String pwd1 = pfPassword1.getText().trim();
            String pwd2 = pfPassword2.getText().trim();

            if (uname.isEmpty() || email.isEmpty() || phone.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Registration", "Missing Fields", "Please complete all registration fields.");
                return;
            }

            if (!pwd1.equals(pwd2)) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords Do Not Match", "Please retype your password correctly in both fields.");
                return;
            }

            if (!authService.isUsernameAvailable(uname)) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Registration", "Username Taken", "This username is already registered.");
                return;
            }

            if (!authService.isEmailAvailable(email)) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Registration", "Email Taken", "This email address is already registered.");
                return;
            }

            // OTP Verification Dialog Simulation
            String simulatedOtp = authService.generateSimulatedOtp();
            TextInputDialog otpDialog = new TextInputDialog();
            otpDialog.setTitle("Mobile & Email OTP Verification");
            otpDialog.setHeaderText("Simulated SMS/Email OTP Sent to " + phone + " & " + email);
            otpDialog.setContentText("Enter 6-digit OTP (Demo OTP: " + simulatedOtp + "):");

            Optional<String> otpResult = otpDialog.showAndWait();
            if (otpResult.isPresent() && otpResult.get().trim().equals(simulatedOtp)) {
                User.Role role = cbRole.getSelectionModel().getSelectedIndex() == 0 ? User.Role.FREELANCER : User.Role.CLIENT;
                User newUser = authService.registerUser(uname, email, phone, pwd1, role);

                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Success", "Account Verified", "Your account has been successfully created and verified via OTP!");
                if (onLoginSuccess != null) {
                    onLoginSuccess.accept(newUser);
                }
            } else {
                UIComponents.showAlert(Alert.AlertType.ERROR, "OTP Error", "Verification Failed", "Incorrect OTP entered. Account registration cancelled.");
            }
        });

        linkBack.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, onLoginSuccess);
            stage.setScene(loginView.createScene());
        });

        card.getChildren().addAll(cardTitle, roleLbl, cbRole,
                new Label("Username:"), tfUsername,
                new Label("Email Address:"), tfEmail,
                new Label("Mobile Number:"), tfPhone,
                new Label("Password:"), pfPassword1,
                new Label("Confirm Password:"), pfPassword2,
                btnRegister, linkBack);

        container.getChildren().addAll(logo, card);
        return new Scene(container, 900, 720);
    }
}
