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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
    private final StackPane contentHost = new StackPane();
    private final StackPane modalOverlayLayer = new StackPane();
    private final Deque<Parent> viewHistory = new ArrayDeque<>();
    private Parent currentRoot;
    private SubScene activeSubScene;

    private NavigationManager() {
        masterRoot.getChildren().addAll(contentHost, modalOverlayLayer);
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
        ensurePrimaryScene();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryScene(Scene scene) {
        this.primaryScene = scene;
        if (scene != null) {
            this.currentRoot = scene.getRoot();
            AppTheme.applyAppStylesheet(scene);
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

    private void ensurePrimaryScene() {
        if (primaryScene == null) {
            double w = primaryStage != null && primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 1280;
            double h = primaryStage != null && primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 800;
            primaryScene = new Scene(masterRoot, w, h);
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
     * Renders a root view encapsulated within a responsive SubScene on the single primary Stage.
     */
    private void renderView(Parent root, boolean applyFade, boolean isForward, boolean isBack) {
        if (root == null) return;
        ensurePrimaryScene();
        this.currentRoot = root;
        AppTheme.applyAppStylesheet(root);

        // 1. Detach from any existing Scene if root was created inside a standalone Scene
        if (root.getScene() != null && root.getScene().getRoot() == root) {
            root.getScene().setRoot(new javafx.scene.Group());
        }

        // 2. Detach from any parent container
        if (root.getParent() instanceof javafx.scene.layout.Pane) {
            ((javafx.scene.layout.Pane) root.getParent()).getChildren().remove(root);
        } else if (root.getParent() instanceof javafx.scene.Group) {
            ((javafx.scene.Group) root.getParent()).getChildren().remove(root);
        }

        double w = masterRoot.getWidth() > 0 ? masterRoot.getWidth() : (primaryScene != null ? primaryScene.getWidth() : 1280);
        double h = masterRoot.getHeight() > 0 ? masterRoot.getHeight() : (primaryScene != null ? primaryScene.getHeight() : 800);

        if (activeSubScene == null) {
            activeSubScene = new SubScene(root, w, h);
            activeSubScene.widthProperty().bind(masterRoot.widthProperty());
            activeSubScene.heightProperty().bind(masterRoot.heightProperty());
            contentHost.getChildren().setAll(activeSubScene);
        } else {
            if (activeSubScene.getRoot() != root) {
                activeSubScene.setRoot(new javafx.scene.Group());
                activeSubScene.setRoot(root);
            }
            if (!contentHost.getChildren().contains(activeSubScene)) {
                contentHost.getChildren().setAll(activeSubScene);
            }
        }

        if (primaryStage != null && primaryStage.getScene() != primaryScene) {
            primaryStage.setScene(primaryScene);
        }

        if (isForward) {
            com.freelancing.util.AnimationUtil.applySlideInRight(root, 280);
        } else if (isBack) {
            com.freelancing.util.AnimationUtil.applySlideInLeft(root, 280);
        } else if (applyFade) {
            com.freelancing.util.AnimationUtil.applyFadeZoom(root, 280);
        }
    }

    /**
     * Updates the active view inside a SubScene on the primary stage.
     */
    public void setScene(Scene scene) {
        if (scene == null) return;
        Parent root = scene.getRoot();
        System.out.println("[SkillBridge-Nav] Setting active SubScene on primary stage (" + scene.getWidth() + "x" + scene.getHeight() + ")");
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
        if (activeSubScene != null) {
            activeSubScene.setRoot(new javafx.scene.Group());
        }
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
        setScene(hp.createHomeScene());
    }

    public void showLogin() {
        LoginView loginView = new LoginView(this::showRoleDashboard);
        setScene(loginView.createScene());
    }

    public void showRegister() {
        RegisterView registerView = new RegisterView(this::showRoleDashboard);
        setScene(registerView.createScene());
    }

    public void showAdminLogin() {
        LoginView loginView = new LoginView(this::showRoleDashboard);
        loginView.setAdminMode(true);
        setScene(loginView.createScene());
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
        setScene(view.createScene());
    }

    public void showClientDashboard(User user) {
        ClientMainView view = new ClientMainView(user);
        setScene(view.createScene());
    }

    public void showAdminDashboard(User user) {
        AdminMainView view = new AdminMainView(user);
        setScene(view.createScene());
    }

    public void showCommunityForum() {
        CommunityForumView view = new CommunityForumView();
        setScene(view.createScene());
    }

    public void showSkillExchange() {
        SkillExchangeView view = new SkillExchangeView();
        setScene(view.createScene());
    }

    public void showContractsAndEscrow() {
        ContractsAndEscrowView view = new ContractsAndEscrowView();
        setScene(view.createScene());
    }

    public void showAnalyticsDashboard() {
        AnalyticsDashboardView view = new AnalyticsDashboardView();
        setScene(view.createScene());
    }

    public void showCalendar(User user) {
        CalendarView view = new CalendarView(user);
        double w = primaryStage != null ? primaryStage.getWidth() : 1280;
        double h = primaryStage != null ? primaryStage.getHeight() : 800;
        Scene scene = new Scene(view, w, h);
        setScene(scene);
    }

    public void showSettings(User user) {
        SettingsView view = new SettingsView(user, this::showHome);
        double w = primaryStage != null ? primaryStage.getWidth() : 1280;
        double h = primaryStage != null ? primaryStage.getHeight() : 800;
        Scene scene = new Scene(view, w, h);
        setScene(scene);
    }
}

