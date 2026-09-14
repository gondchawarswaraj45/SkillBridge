package com.freelancing.service;

import com.freelancing.model.common.Notification;
import com.freelancing.model.common.User;
import com.freelancing.service.common.NotificationService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    private static NotificationService notificationService;
    private User testUser;

    @BeforeAll
    public static void initDatabase() {
        DatabaseInitializer.initialize();
        notificationService = new NotificationService();
    }

    @BeforeEach
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

    @Test
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

    @Test
    public void testUnreadCountAndMarkAsRead() {
        notificationService.sendNotification(testUser.getId(), "Alert 1", "Message 1");
        notificationService.sendNotification(testUser.getId(), "Alert 2", "Message 2");

        assertEquals(2, notificationService.getUnreadCount(testUser.getId()));

        List<Notification> notifs = notificationService.getUserNotifications(testUser.getId());
        assertTrue(notificationService.markAsRead(notifs.get(0).getId()));

        assertEquals(1, notificationService.getUnreadCount(testUser.getId()));
    }

    @Test
    public void testMarkAllAsRead() {
        notificationService.sendNotification(testUser.getId(), "Alert A", "Msg A");
        notificationService.sendNotification(testUser.getId(), "Alert B", "Msg B");
        notificationService.sendNotification(testUser.getId(), "Alert C", "Msg C");

        assertEquals(3, notificationService.getUnreadCount(testUser.getId()));

        assertTrue(notificationService.markAllAsRead(testUser.getId()));
        assertEquals(0, notificationService.getUnreadCount(testUser.getId()));
    }
}
