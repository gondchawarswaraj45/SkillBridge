package com.freelancing.service;

import com.freelancing.model.common.Notification;
import com.freelancing.model.common.User;
import com.freelancing.service.common.NotificationService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    private static NotificationService notificationService;
    private User testUser;

    public static void initDatabase() {
        DatabaseInitializer.initialize();
        notificationService = new NotificationService();
    }

    public void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        testUser = new User();
        testUser.setId("usr_notif_" + suffix);
        testUser.setUsername("notif_user_" + suffix);
        testUser.setEmail("notif_" + suffix + "@test.com");
        testUser.setPassword("hashedpassword");
        testUser.setRole(User.Role.FREELANCER);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (id, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?);")) {
            ps.setString(1, testUser.getId());
            ps.setString(2, testUser.getUsername());
            ps.setString(3, testUser.getEmail());
            ps.setString(4, testUser.getPassword());
            ps.setString(5, testUser.getRole().name());
            ps.executeUpdate();
        }
    }

    public void testSendAndRetrieveNotifications() {
        notificationService.sendNotification(testUser.getId(), "Proposal Accepted", "Your proposal for Project X was accepted!", "PROPOSAL", "ref_123");

        List<Notification> notifs = notificationService.getUserNotifications(testUser.getId());
        assertEquals(1, notifs.size());

        Notification n = notifs.get(0);
        assertEquals("Proposal Accepted", n.getTitle());
        assertEquals("Your proposal for Project X was accepted!", n.getMessage());
        assertEquals("PROPOSAL", n.getType());
        assertEquals("ref_123", n.getReferenceId());
        assertFalse(n.isRead());
    }

    public void testUnreadCountAndMarkAsRead() {
        notificationService.sendNotification(testUser.getId(), "Alert 1", "Message 1");
        notificationService.sendNotification(testUser.getId(), "Alert 2", "Message 2");

        assertEquals(2, notificationService.getUnreadCount(testUser.getId()));

        List<Notification> notifs = notificationService.getUserNotifications(testUser.getId());
        assertTrue(notificationService.markAsRead(notifs.get(0).getId()));

        assertEquals(1, notificationService.getUnreadCount(testUser.getId()));
    }

    public void testMarkAllAsRead() {
        notificationService.sendNotification(testUser.getId(), "Alert A", "Msg A");
        notificationService.sendNotification(testUser.getId(), "Alert B", "Msg B");
        notificationService.sendNotification(testUser.getId(), "Alert C", "Msg C");

        assertEquals(3, notificationService.getUnreadCount(testUser.getId()));

        assertTrue(notificationService.markAllAsRead(testUser.getId()));
        assertEquals(0, notificationService.getUnreadCount(testUser.getId()));
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running NotificationServiceTest...");
        int passed = 0;
        int total = 3;
        try {
            initDatabase();
        } catch (Throwable t) {
            System.err.println("Setup failed for NotificationServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            NotificationServiceTest test = new NotificationServiceTest();
            test.setUp();
            test.testSendAndRetrieveNotifications();
            passed++;
            System.out.println("  [PASS] testSendAndRetrieveNotifications");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSendAndRetrieveNotifications: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            NotificationServiceTest test = new NotificationServiceTest();
            test.setUp();
            test.testUnreadCountAndMarkAsRead();
            passed++;
            System.out.println("  [PASS] testUnreadCountAndMarkAsRead");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testUnreadCountAndMarkAsRead: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            NotificationServiceTest test = new NotificationServiceTest();
            test.setUp();
            test.testMarkAllAsRead();
            passed++;
            System.out.println("  [PASS] testMarkAllAsRead");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testMarkAllAsRead: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("NotificationServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in NotificationServiceTest");
        }
    }
}
