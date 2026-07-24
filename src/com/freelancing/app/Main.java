package com.freelancing.app;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.User;
import com.freelancing.ui.AdminMainView;
import com.freelancing.ui.ClientMainView;
import com.freelancing.ui.FreelancerMainView;
import com.freelancing.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Freelancing.SB - Desktop Platform");

        // Initialize Database Manager and Seed Records
        DatabaseManager.getInstance();

        // Launch Login View
        LoginView loginView = new LoginView(primaryStage, user -> navigateToRoleView(primaryStage, user));
        Scene loginScene = loginView.createScene();

        primaryStage.setScene(loginScene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    private void navigateToRoleView(Stage stage, User user) {
        if (user.getRole() == User.Role.FREELANCER) {
            FreelancerMainView freelancerView = new FreelancerMainView(stage, user);
            stage.setScene(freelancerView.createScene());
        } else if (user.getRole() == User.Role.CLIENT) {
            ClientMainView clientView = new ClientMainView(stage, user);
            stage.setScene(clientView.createScene());
        } else if (user.getRole() == User.Role.ADMIN) {
            AdminMainView adminView = new AdminMainView(stage, user);
            stage.setScene(adminView.createScene());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
