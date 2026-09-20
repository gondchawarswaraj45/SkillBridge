package com.freelancing.app;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import static org.junit.jupiter.api.Assertions.*;

public class NavigationManagerTest {

    private NavigationManager navManager;

    public static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX Platform already initialized
        }
    }

    public void setUp() {
        navManager = NavigationManager.getInstance();
        navManager.clearHistory();
    }

    public void testSingletonInstance() {
        assertNotNull(navManager, "NavigationManager instance must not be null");
        NavigationManager another = NavigationManager.getInstance();
        assertSame(navManager, another, "NavigationManager must return the exact same singleton instance");
    }

    public void testInitialState() {
        navManager.clearHistory();
        assertFalse(navManager.canGoBack(), "Initially, canGoBack should be false");
        assertEquals(0, navManager.getHistorySize(), "History size should initially be 0");
    }

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

    public void testNullSafety() {
        assertDoesNotThrow(() -> navManager.navigateTo(null), "navigateTo(null) should be a safe no-op");
        assertDoesNotThrow(() -> navManager.setScene(null), "setScene(null) should be a safe no-op");
    }

    public void testShowAdminLogin() {
        assertDoesNotThrow(() -> navManager.showAdminLogin(), "showAdminLogin should create and set admin login scene safely");
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running NavigationManagerTest...");
        int passed = 0;
        int total = 6;
        try {
            initJavaFX();
        } catch (Throwable t) {
            System.err.println("Setup failed for NavigationManagerTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            NavigationManagerTest test = new NavigationManagerTest();
            test.setUp();
            test.testSingletonInstance();
            passed++;
            System.out.println("  [PASS] testSingletonInstance");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSingletonInstance: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            NavigationManagerTest test = new NavigationManagerTest();
            test.setUp();
            test.testInitialState();
            passed++;
            System.out.println("  [PASS] testInitialState");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testInitialState: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            NavigationManagerTest test = new NavigationManagerTest();
            test.setUp();
            test.testNavigationHistoryStack();
            passed++;
            System.out.println("  [PASS] testNavigationHistoryStack");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testNavigationHistoryStack: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            NavigationManagerTest test = new NavigationManagerTest();
            test.setUp();
            test.testClearHistory();
            passed++;
            System.out.println("  [PASS] testClearHistory");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testClearHistory: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            NavigationManagerTest test = new NavigationManagerTest();
            test.setUp();
            test.testNullSafety();
            passed++;
            System.out.println("  [PASS] testNullSafety");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testNullSafety: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            NavigationManagerTest test = new NavigationManagerTest();
            test.setUp();
            test.testShowAdminLogin();
            passed++;
            System.out.println("  [PASS] testShowAdminLogin");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testShowAdminLogin: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("NavigationManagerTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in NavigationManagerTest");
        }
    }
}
