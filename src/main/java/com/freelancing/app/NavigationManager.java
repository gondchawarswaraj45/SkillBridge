package com.freelancing.app;

import com.freelancing.model.common.User;
import com.freelancing.ui.admin.AdminMainView;
import com.freelancing.ui.admin.AnalyticsDashboardView;
import com.freelancing.ui.common.CalendarView;
import com.freelancing.ui.common.CommunityForumView;
import com.freelancing.ui.common.HomePage;
import com.freelancing.ui.common.LoginView;
import com.freelancing.ui.common.RegisterView;
import com.freelancing.ui.common.SettingsView;
import com.freelancing.ui.company.ClientMainView;
import com.freelancing.ui.company.ContractsAndEscrowView;
import com.freelancing.ui.freelancer.FreelancerMainView;
import com.freelancing.ui.freelancer.SkillExchangeView;

import com.freelancing.config.AppTheme;
import com.freelancing.ui.common.AppTitleBar;
import com.freelancing.ui.common.WindowResizeHelper;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Centralized navigation manager for SkillBridge.
 * Manages single-stage lifecycle, SubScene embedding, in-scene modal overlays, and scene transitions.
 */
public class NavigationManager {
    private static NavigationManager instance;
    private Stage primaryStage;
    private Scene primaryScene;
    private final StackPane masterRoot = new StackPane();
    private final VBox windowContainer = new VBox();
    private AppTitleBar appTitleBar;
    private final StackPane contentHost = new StackPane();
    private final StackPane modalOverlayLayer = new StackPane();
    private final Deque<Parent> viewHistory = new ArrayDeque<>();
    private Parent currentRoot;

    private NavigationManager() {
        VBox.setVgrow(contentHost, Priority.ALWAYS);
        windowContainer.getChildren().add(contentHost);
        windowContainer.setStyle("-fx-background-color: #0B0F19;");
        contentHost.setStyle("-fx-background-color: #0B0F19;");

        masterRoot.getChildren().addAll(windowContainer, modalOverlayLayer);
        masterRoot.setStyle("-fx-background-color: #0B0F19; -fx-border-color: #2563EB; -fx-border-width: 1px;");
        modalOverlayLayer.setPickOnBounds(false);
        modalOverlayLayer.setVisible(false);
    }

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        if (stage != null) {
            if (!stage.isShowing()) {
                try {
                    stage.initStyle(StageStyle.UNDECORATED);
                } catch (Exception ignored) {}
            }
            if (appTitleBar == null) {
                appTitleBar = new AppTitleBar(stage, "SkillBridge - Freelancing & Skill Exchange Platform");
                windowContainer.getChildren().add(0, appTitleBar);
            }
            WindowResizeHelper.attach(stage);
        }
        ensurePrimaryScene();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryScene(Scene scene) {
        if (scene != null) {
            setScene(scene);
        }
    }

    public Scene getPrimaryScene() {
        return primaryScene;
    }

    public StackPane getMasterRoot() {
        return masterRoot;
    }

    public StackPane getContentHost() {
        return contentHost;
    }

    public StackPane getModalOverlayLayer() {
        return modalOverlayLayer;
    }

    public AppTitleBar getAppTitleBar() {
        return appTitleBar;
    }

    public void setWindowTitle(String title) {
        if (appTitleBar != null) {
            appTitleBar.setTitle(title);
        }
        if (primaryStage != null) {
            primaryStage.setTitle(title);
        }
    }

    private void ensurePrimaryScene() {
        if (primaryScene == null || primaryScene.getRoot() != masterRoot) {
            double w = primaryStage != null && primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 1280;
            double h = primaryStage != null && primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 800;
            primaryScene = new Scene(masterRoot, w, h);
            primaryScene.setFill(javafx.scene.paint.Color.web("#0B0F19"));
            AppTheme.applyAppStylesheet(primaryScene);
        }
        if (primaryStage != null && primaryStage.getScene() != primaryScene) {
            primaryStage.setScene(primaryScene);
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        }
    }

    /**
     * Renders a root view directly inside contentHost on the single primary Stage.
     */
    private void renderView(Parent root, boolean applyFade, boolean isForward, boolean isBack) {
        if (root == null) return;
        this.currentRoot = root;

        // 1. Detach from any external Scene if root was previously mounted in a standalone Scene
        if (root.getScene() != null && root.getScene() != primaryScene && root.getScene().getRoot() == root) {
            root.getScene().setRoot(new javafx.scene.Group());
        }

        // 2. Detach from any parent container
        if (root.getParent() instanceof javafx.scene.layout.Pane) {
            ((javafx.scene.layout.Pane) root.getParent()).getChildren().remove(root);
        } else if (root.getParent() instanceof javafx.scene.Group) {
            ((javafx.scene.Group) root.getParent()).getChildren().remove(root);
        }

        // Guarantee root view is 100% visible with normal scale and position
        root.setOpacity(1.0);
        root.setScaleX(1.0);
        root.setScaleY(1.0);
        root.setTranslateX(0.0);
        root.setTranslateY(0.0);

        contentHost.getChildren().setAll(root);
        ensurePrimaryScene();
        AppTheme.applyAppStylesheet(root);
        root.applyCss();

        if (isForward) {
            com.freelancing.util.AnimationUtil.applySlideInRight(root, 280);
        } else if (isBack) {
            com.freelancing.util.AnimationUtil.applySlideInLeft(root, 280);
        }
    }

    /**
     * Updates the active view inside contentHost on the primary stage.
     */
    public void setScene(Scene scene) {
        if (scene == null) return;
        Parent root = scene.getRoot();
        System.out.println("[SkillBridge-Nav] Setting active view on primary stage");
        renderView(root, true, false, false);
    }

    /**
     * Navigate to a new root view, pushing current root to history stack.
     */
    public void navigateTo(Parent root) {
        if (root == null) return;

        System.out.println("[SkillBridge-Nav] Navigating forward to: " + root.getClass().getSimpleName() + " (History depth: " + viewHistory.size() + ")");
        if (currentRoot != null) {
            viewHistory.push(currentRoot);
        }
        renderView(root, false, true, false);
    }

    /**
     * Go back to previous view in history stack.
     */
    public boolean goBack() {
        if (!viewHistory.isEmpty()) {
            Parent prev = viewHistory.pop();
            System.out.println("[SkillBridge-Nav] Navigating back to: " + prev.getClass().getSimpleName() + " (Remaining in history: " + viewHistory.size() + ")");
            renderView(prev, false, false, true);
            return true;
        }
        return false;
    }

    /** Check if back navigation is available */
    public boolean canGoBack() {
        return !viewHistory.isEmpty();
    }

    /** Clear navigation history and reset current root */
    public void clearHistory() {
        viewHistory.clear();
        currentRoot = null;
    }

    /** Get the current view root */
    public Parent getCurrentView() {
        return currentRoot;
    }

    /** Get history depth */
    public int getHistorySize() {
        return viewHistory.size();
    }

    /** Show in-scene modal overlay */
    public void showModal(javafx.scene.Node modalContent) {
        if (!javafx.application.Platform.isFxApplicationThread()) {
            try {
                javafx.application.Platform.runLater(() -> showModal(modalContent));
            } catch (Exception ignored) {}
            return;
        }
        ensurePrimaryScene();
        modalOverlayLayer.getChildren().setAll(modalContent);
        modalOverlayLayer.setVisible(true);
        modalOverlayLayer.setPickOnBounds(true);
        com.freelancing.util.AnimationUtil.applyPopScale(modalContent, 200);
    }

    /** Dismiss active in-scene modal overlay */
    public void closeModal() {
        if (!javafx.application.Platform.isFxApplicationThread()) {
            try {
                javafx.application.Platform.runLater(this::closeModal);
            } catch (Exception ignored) {}
            return;
        }
        modalOverlayLayer.getChildren().clear();
        modalOverlayLayer.setVisible(false);
        modalOverlayLayer.setPickOnBounds(false);
    }

    // =========================================================================
    // HIGH-LEVEL ROUTE DISPATCHERS
    // =========================================================================

    public void showHome() {
        HomePage hp = HomePage.getInstance() != null ? HomePage.getInstance() : new HomePage();
        renderView(hp.createHomeView(), false, false, false);
    }

    public void showLogin() {
        LoginView loginView = new LoginView(this::showRoleDashboard);
        renderView(loginView.createContent(), false, false, false);
    }

    public void showRegister() {
        RegisterView registerView = new RegisterView(this::showRoleDashboard);
        renderView(registerView.createContent(), false, false, false);
    }

    public void showAdminLogin() {
        LoginView loginView = new LoginView(this::showRoleDashboard);
        loginView.setAdminMode(true);
        renderView(loginView.createContent(), false, false, false);
    }

    public void showRoleDashboard(User user) {
        if (user == null) {
            showLogin();
            return;
        }
        SessionManager.getInstance().login(user);
        if (user.isFreelancer()) {
            showFreelancerDashboard(user);
        } else if (user.isClient()) {
            showClientDashboard(user);
        } else {
            showAdminDashboard(user);
        }
    }

    public void showFreelancerDashboard(User user) {
        FreelancerMainView view = new FreelancerMainView(user);
        renderView(view.createContent(), false, false, false);
    }

    public void showClientDashboard(User user) {
        ClientMainView view = new ClientMainView(user);
        renderView(view.createContent(), false, false, false);
    }

    public void showAdminDashboard(User user) {
        AdminMainView view = new AdminMainView(user);
        renderView(view.createContent(), false, false, false);
    }

    public void showCommunityForum() {
        CommunityForumView view = new CommunityForumView();
        renderView(view.createContent(), false, false, false);
    }

    public void showSkillExchange() {
        SkillExchangeView view = new SkillExchangeView();
        renderView(view.createContent(), false, false, false);
    }

    public void showContractsAndEscrow() {
        ContractsAndEscrowView view = new ContractsAndEscrowView();
        renderView(view.createContent(), false, false, false);
    }

    public void showAnalyticsDashboard() {
        AnalyticsDashboardView view = new AnalyticsDashboardView();
        renderView(view.createContent(), false, false, false);
    }

    public void showCalendar(User user) {
        CalendarView view = new CalendarView(user);
        renderView(view, false, false, false);
    }

    public void showSettings(User user) {
        SettingsView view = new SettingsView(user, this::showHome);
        renderView(view, false, false, false);
    }
}

