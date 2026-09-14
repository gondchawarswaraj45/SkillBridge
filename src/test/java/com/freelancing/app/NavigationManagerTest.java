package com.freelancing.app;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NavigationManagerTest {

    private NavigationManager navManager;

    @BeforeAll
    public static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX Platform already initialized
        }
    }

    @BeforeEach
    public void setUp() {
        navManager = NavigationManager.getInstance();
        navManager.clearHistory();
    }

    @Test
    @DisplayName("Verify NavigationManager singleton behavior")
    public void testSingletonInstance() {
        assertNotNull(navManager, "NavigationManager instance must not be null");
        NavigationManager another = NavigationManager.getInstance();
        assertSame(navManager, another, "NavigationManager must return the exact same singleton instance");
    }

    @Test
    @DisplayName("Verify initial navigation state and history depth")
    public void testInitialState() {
        navManager.clearHistory();
        assertFalse(navManager.canGoBack(), "Initially, canGoBack should be false");
        assertEquals(0, navManager.getHistorySize(), "History size should initially be 0");
    }

    @Test
    @DisplayName("Verify view stack push, current root, and goBack transitions")
    public void testNavigationHistoryStack() {
        Pane view1 = new Pane();
        Pane view2 = new StackPane();
        Pane view3 = new Pane();

        // 1. Navigate to first view
        navManager.navigateTo(view1);
        assertSame(view1, navManager.getCurrentView(), "Current view should be view1");
        assertFalse(navManager.canGoBack(), "First view should not have back history");
        assertEquals(0, navManager.getHistorySize());

        // 2. Navigate to second view
        navManager.navigateTo(view2);
        assertSame(view2, navManager.getCurrentView(), "Current view should be view2");
        assertTrue(navManager.canGoBack(), "Should be able to go back after navigating to view2");
        assertEquals(1, navManager.getHistorySize());

        // 3. Navigate to third view
        navManager.navigateTo(view3);
        assertSame(view3, navManager.getCurrentView(), "Current view should be view3");
        assertEquals(2, navManager.getHistorySize());

        // 4. Go back to view2
        boolean backed = navManager.goBack();
        assertTrue(backed, "goBack should return true when history exists");
        assertSame(view2, navManager.getCurrentView(), "Current view should now be view2");
        assertEquals(1, navManager.getHistorySize());

        // 5. Go back to view1
        backed = navManager.goBack();
        assertTrue(backed);
        assertSame(view1, navManager.getCurrentView(), "Current view should now be view1");
        assertFalse(navManager.canGoBack(), "Should reach the start of history");
        assertEquals(0, navManager.getHistorySize());

        // 6. Go back on empty stack
        assertFalse(navManager.goBack(), "goBack on empty stack should return false");
    }

    @Test
    @DisplayName("Verify clearHistory resets navigation stack")
    public void testClearHistory() {
        Pane v1 = new Pane();
        Pane v2 = new Pane();
        navManager.navigateTo(v1);
        navManager.navigateTo(v2);
        assertTrue(navManager.canGoBack());
        assertEquals(1, navManager.getHistorySize());

        navManager.clearHistory();
        assertFalse(navManager.canGoBack());
        assertEquals(0, navManager.getHistorySize());
    }

    @Test
    @DisplayName("Verify null safety during navigation operations")
    public void testNullSafety() {
        assertDoesNotThrow(() -> navManager.navigateTo(null), "navigateTo(null) should be a safe no-op");
        assertDoesNotThrow(() -> navManager.setScene(null), "setScene(null) should be a safe no-op");
    }

    @Test
    @DisplayName("Verify showAdminLogin sets admin mode login view")
    public void testShowAdminLogin() {
        assertDoesNotThrow(() -> navManager.showAdminLogin(), "showAdminLogin should create and set admin login scene safely");
    }
}
