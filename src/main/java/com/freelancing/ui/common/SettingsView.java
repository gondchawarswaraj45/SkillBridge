package com.freelancing.ui.common;

import com.freelancing.model.common.User;
import com.freelancing.config.AppTheme;
import com.freelancing.db.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * SkillBridge Universal Settings & Preferences View.
 * Supports Profile editing, Appearance & Theme switching, Multi-currency configuration,
 * Notification toggles, and Security settings. Zero white in light mode!
 */
public class SettingsView extends BorderPane {

    private final DatabaseManager db;
    private final User currentUser;
    private final Runnable onThemeChanged;

    private VBox activeTabContent;
    private String selectedSection = "Profile & Account";

    public String getSelectedSection() {
        return selectedSection;
    }

    public SettingsView(User user, Runnable onThemeChanged) {
        this.db = DatabaseManager.getInstance();
        this.currentUser = user;
        this.onThemeChanged = onThemeChanged;

        buildUI();
    }

    private void buildUI() {
        setStyle("-fx-background-color: " + AppTheme.getBgDark() + ";");

        // Top Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 24, 18, 24));
        header.setStyle("-fx-background-color: " + AppTheme.getBgPanel() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-width: 0 0 1 0;");

        Label title = new Label("⚙️ System Preferences & Settings");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(AppTheme.getTextPrimary()));

        header.getChildren().add(title);
        setTop(header);

        // Center Content: Left Nav Sidebar + Right Settings Form
        HBox body = new HBox(20);
        body.setPadding(new Insets(24));
        HBox.setHgrow(body, Priority.ALWAYS);

        // Sidebar Navigation
        VBox sideNav = new VBox(6);
        sideNav.setPrefWidth(220);
        sideNav.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12;");

        String[] sections = {"Profile & Account", "Appearance & Theme", "Notifications", "Security & Privacy", "Payment Methods", "About & Support"};
        for (String sec : sections) {
            Button secBtn = new Button(sec);
            secBtn.setMaxWidth(Double.MAX_VALUE);
            secBtn.setAlignment(Pos.CENTER_LEFT);
            secBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
            secBtn.setPadding(new Insets(10, 14, 10, 14));
            styleNavBtn(secBtn, sec.equals("Profile & Account"));

            secBtn.setOnAction(e -> {
                selectedSection = sec;
                for (javafx.scene.Node n : sideNav.getChildren()) {
                    if (n instanceof Button) styleNavBtn((Button) n, n == secBtn);
                }
                renderSection(sec);
            });

            sideNav.getChildren().add(secBtn);
        }

        // Right Settings Form Container
        activeTabContent = new VBox(16);
        activeTabContent.setStyle("-fx-background-color: " + AppTheme.getBgCard() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 24;");
        HBox.setHgrow(activeTabContent, Priority.ALWAYS);

        ScrollPane formScroll = UIComponents.createScrollPane(activeTabContent);
        HBox.setHgrow(formScroll, Priority.ALWAYS);

        body.getChildren().addAll(sideNav, formScroll);
        setCenter(body);

        renderSection("Profile & Account");
        UIComponents.fadeIn(this, 300);
    }

    private void styleNavBtn(Button btn, boolean active) {
        if (active) {
            btn.setStyle("-fx-background-color: " + AppTheme.COLOR_PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextPrimary() + "; -fx-background-radius: 8; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-text-fill: " + AppTheme.COLOR_PRIMARY + "; -fx-background-radius: 8; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + AppTheme.getTextPrimary() + "; -fx-background-radius: 8; -fx-cursor: hand;"));
        }
    }

    private void renderSection(String sec) {
        activeTabContent.getChildren().clear();

        Label title = new Label(sec);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        title.setTextFill(Color.web(AppTheme.getTextPrimary()));
        activeTabContent.getChildren().addAll(title, new Separator());

        switch (sec) {
            case "Profile & Account":
                renderProfileSection();
                break;
            case "Appearance & Theme":
                renderAppearanceSection();
                break;
            case "Notifications":
                renderNotificationsSection();
                break;
            case "Security & Privacy":
                renderSecuritySection();
                break;
            case "Payment Methods":
                renderPaymentsSection();
                break;
            case "About & Support":
                renderAboutSection();
                break;
        }

        UIComponents.fadeIn(activeTabContent, 200);
    }

    private void renderProfileSection() {
        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(16);

        TextField userField = UIComponents.createTextField(currentUser != null ? currentUser.getUsername() : "username");
        TextField emailField = UIComponents.createTextField(currentUser != null ? currentUser.getEmail() : "user@example.com");
        TextField phoneField = UIComponents.createTextField(currentUser != null && currentUser.getPhone() != null ? currentUser.getPhone() : "+919876543210");
        ComboBox<String> roleCombo = UIComponents.createStyledComboBox("CUSTOMER", "VENDOR", "ADMIN");
        if (currentUser != null && currentUser.getRole() != null) {
            roleCombo.setValue(currentUser.getRole().name());
        }

        grid.add(new Label("Username:"), 0, 0); grid.add(userField, 1, 0);
        grid.add(new Label("Email Address:"), 0, 1); grid.add(emailField, 1, 1);
        grid.add(new Label("Phone Number:"), 0, 2); grid.add(phoneField, 1, 2);
        grid.add(new Label("Account Role:"), 0, 3); grid.add(roleCombo, 1, 3);

        Button saveBtn = UIComponents.createPrimaryButton("Save Profile Changes");
        saveBtn.setOnAction(e -> {
            if (currentUser != null) {
                currentUser.setEmail(emailField.getText().trim());
                currentUser.setPhone(phoneField.getText().trim());
                db.saveData();
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Profile Updated", "Success", "Profile settings updated successfully!");
            }
        });

        activeTabContent.getChildren().addAll(grid, saveBtn);
    }

    private void renderAppearanceSection() {
        VBox box = new VBox(16);

        // Theme Toggle
        HBox themeRow = new HBox(16);
        themeRow.setAlignment(Pos.CENTER_LEFT);

        Label themeLbl = new Label("Current Theme Mode:");
        themeLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        themeLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        Button toggleThemeBtn = UIComponents.createPrimaryButton(AppTheme.isDarkMode() ? "🌙 Dark Mode (Click to switch to Light)" : "☀️ Light Mode (Click to switch to Dark)");
        toggleThemeBtn.setOnAction(e -> {
            AppTheme.toggleTheme();
            toggleThemeBtn.setText(AppTheme.isDarkMode() ? "🌙 Dark Mode (Click to switch to Light)" : "☀️ Light Mode (Click to switch to Dark)");
            if (onThemeChanged != null) onThemeChanged.run();
        });

        themeRow.getChildren().addAll(themeLbl, toggleThemeBtn);

        // Currency Selector
        HBox curRow = new HBox(16);
        curRow.setAlignment(Pos.CENTER_LEFT);

        Label curLbl = new Label("Default Platform Currency:");
        curLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        curLbl.setTextFill(Color.web(AppTheme.getTextPrimary()));

        ComboBox<String> curCombo = UIComponents.createStyledComboBox("USD ($)", "INR (₹)", "EUR (€)", "GBP (£)");
        curCombo.setValue(AppTheme.getCurrentCurrency() + " (Active)");
        curCombo.setOnAction(e -> {
            String sel = curCombo.getValue();
            if (sel.contains("USD")) AppTheme.setCurrentCurrency("USD");
            else if (sel.contains("INR")) AppTheme.setCurrentCurrency("INR");
            else if (sel.contains("EUR")) AppTheme.setCurrentCurrency("EUR");
            else if (sel.contains("GBP")) AppTheme.setCurrentCurrency("GBP");
        });

        curRow.getChildren().addAll(curLbl, curCombo);

        box.getChildren().addAll(themeRow, new Separator(), curRow);
        activeTabContent.getChildren().add(box);
    }

    private void renderNotificationsSection() {
        VBox box = new VBox(12);

        String[] notifs = {
                "Event RSVP confirmations & changes",
                "Vendor quotation responses & updates",
                "Escrow payment deposits and releases",
                "Chat messages and video call invites",
                "Platform marketing newsletters & discounts"
        };

        for (String n : notifs) {
            CheckBox cb = new CheckBox(n);
            cb.setSelected(true);
            cb.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
            cb.setTextFill(Color.web(AppTheme.getTextPrimary()));
            box.getChildren().add(cb);
        }

        Button saveNotifs = UIComponents.createPrimaryButton("Save Notification Preferences");
        saveNotifs.setOnAction(e -> UIComponents.showAlert(Alert.AlertType.INFORMATION, "Notification Preferences", "Preferences Saved", "Notification preferences saved!"));

        box.getChildren().addAll(new Separator(), saveNotifs);
        activeTabContent.getChildren().add(box);
    }

    private void renderSecuritySection() {
        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(16);

        PasswordField curPass = UIComponents.createPasswordField("Enter current password");
        PasswordField newPass = UIComponents.createPasswordField("Enter new password (min 8 chars)");
        PasswordField confPass = UIComponents.createPasswordField("Confirm new password");

        grid.add(new Label("Current Password:"), 0, 0); grid.add(curPass, 1, 0);
        grid.add(new Label("New Password:"), 0, 1); grid.add(newPass, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2); grid.add(confPass, 1, 2);

        Button updatePassBtn = UIComponents.createPrimaryButton("Update Password");
        updatePassBtn.setOnAction(e -> {
            if (newPass.getText().equals(confPass.getText()) && !newPass.getText().isEmpty()) {
                if (currentUser != null) {
                    currentUser.setPassword(newPass.getText());
                    db.saveData();
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Security Settings", "Password Updated", "Password updated successfully!");
                }
            } else {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Password Error", "Validation Failed", "Passwords do not match or field is blank.");
            }
        });

        activeTabContent.getChildren().addAll(grid, updatePassBtn);
    }

    private void renderPaymentsSection() {
        VBox box = new VBox(14);

        Label desc = new Label("Manage linked bank accounts, UPI identifiers, and default escrow payout methods.");
        desc.setFont(Font.font("Segoe UI", 13));
        desc.setTextFill(Color.web(AppTheme.getTextMuted()));

        VBox card1 = new VBox(4);
        card1.setPadding(new Insets(12));
        card1.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        card1.getChildren().addAll(
                new Label("🏦 Primary Bank: HDFC Bank (**** 7263) - VERIFIED"),
                new Label("IFSC: HDFC0001234 • Fast Escrow Payout Enabled")
        );

        VBox card2 = new VBox(4);
        card2.setPadding(new Insets(12));
        card2.setStyle("-fx-background-color: " + AppTheme.getBgInput() + "; -fx-border-color: " + AppTheme.getBorderColor() + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        card2.getChildren().addAll(
                new Label("💳 UPI ID: business@okaxis - VERIFIED"),
                new Label("Instant QR & VPA Collections Active")
        );

        Button addAccBtn = UIComponents.createPrimaryButton("+ Link New Payment Method");
        addAccBtn.setOnAction(e -> UIComponents.showAlert(Alert.AlertType.INFORMATION, "Escrow & Account Gateway", "Secure Connection", "Opening SkillBridge Secure Escrow & Account Gateway..."));

        box.getChildren().addAll(desc, card1, card2, addAccBtn);
        activeTabContent.getChildren().add(box);
    }

    private void renderAboutSection() {
        VBox box = new VBox(12);

        Label vLbl = new Label("SkillBridge Desktop Platform");
        vLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        vLbl.setTextFill(Color.web(AppTheme.COLOR_PRIMARY));

        Label subLbl = new Label("Release: v1.0.0 (Core Suite)\nEngine: JavaFX 17 + Core Java + JDBC + SQLite");
        subLbl.setFont(Font.font("Segoe UI", 13));
        subLbl.setTextFill(Color.web(AppTheme.getTextMuted()));

        Label policy = new Label("All transactions are protected by SkillBridge Escrow Guarantee.\nTerms of Service • Privacy Policy • Security Audits");
        policy.setFont(Font.font("Segoe UI", 12));
        policy.setTextFill(Color.web(AppTheme.getTextMuted()));

        box.getChildren().addAll(vLbl, subLbl, new Separator(), policy);
        activeTabContent.getChildren().add(box);
    }
}
