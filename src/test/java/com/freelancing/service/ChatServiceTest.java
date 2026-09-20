package com.freelancing.service;

import com.freelancing.model.common.ChatMessage;
import com.freelancing.model.common.Conversation;
import com.freelancing.model.common.Notification;
import com.freelancing.model.common.User;
import com.freelancing.service.common.ChatService;
import com.freelancing.service.common.NotificationService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServiceTest {

    private static ChatService chatService;
    private static NotificationService notificationService;

    private User clientUser;
    private User freelancerUser;
    private String projectId;

    public static void initDatabase() {
        DatabaseInitializer.initialize();
        chatService = new ChatService();
        notificationService = new NotificationService();
    }

    public void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        clientUser = new User();
        clientUser.setId("usr_c_" + suffix);
        clientUser.setUsername("client_" + suffix);
        clientUser.setEmail("client_" + suffix + "@test.com");
        clientUser.setPassword("hashedpassword");
        clientUser.setRole(User.Role.CLIENT);

        freelancerUser = new User();
        freelancerUser.setId("usr_f_" + suffix);
        freelancerUser.setUsername("freelancer_" + suffix);
        freelancerUser.setEmail("freelancer_" + suffix + "@test.com");
        freelancerUser.setPassword("hashedpassword");
        freelancerUser.setRole(User.Role.FREELANCER);

        projectId = "proj_" + suffix;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertUser = "INSERT INTO users (id, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                ps.setString(1, clientUser.getId());
                ps.setString(2, clientUser.getUsername());
                ps.setString(3, clientUser.getEmail());
                ps.setString(4, clientUser.getPassword());
                ps.setString(5, clientUser.getRole().name());
                ps.executeUpdate();

                ps.setString(1, freelancerUser.getId());
                ps.setString(2, freelancerUser.getUsername());
                ps.setString(3, freelancerUser.getEmail());
                ps.setString(4, freelancerUser.getPassword());
                ps.setString(5, freelancerUser.getRole().name());
                ps.executeUpdate();
            }

            String insertClient = "INSERT INTO client_profiles (id, user_id, company_name) VALUES (?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertClient)) {
                ps.setString(1, "cp_" + suffix);
                ps.setString(2, clientUser.getId());
                ps.setString(3, "Test Client Corp");
                ps.executeUpdate();
            }

            String insertFreelancer = "INSERT INTO freelancer_profiles (id, user_id, title) VALUES (?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertFreelancer)) {
                ps.setString(1, "fp_" + suffix);
                ps.setString(2, freelancerUser.getId());
                ps.setString(3, "Expert Engineer");
                ps.executeUpdate();
            }

            String insertProject = "INSERT INTO projects (id, client_id, title, description, category) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertProject)) {
                ps.setString(1, projectId);
                ps.setString(2, "cp_" + suffix);
                ps.setString(3, "Chat Test Project");
                ps.setString(4, "Project description");
                ps.setString(5, "Software");
                ps.executeUpdate();
            }
        }
    }

    public void testGetOrCreateConversationIdempotent() {
        Conversation conv1 = chatService.getOrCreateConversation(clientUser.getId(), freelancerUser.getId(), projectId);
        assertNotNull(conv1);
        assertNotNull(conv1.getId());

        // Second call with same users must return existing conversation
        Conversation conv2 = chatService.getOrCreateConversation(freelancerUser.getId(), clientUser.getId(), projectId);
        assertNotNull(conv2);
        assertEquals(conv1.getId(), conv2.getId(), "Must reuse existing conversation");

        List<Conversation> clientConvs = chatService.getUserConversations(clientUser.getId());
        assertFalse(clientConvs.isEmpty());
        assertEquals(conv1.getId(), clientConvs.get(0).getId());
        assertEquals(freelancerUser.getUsername(), clientConvs.get(0).getOtherUsername());
    }

    public void testSendMessageAndNotificationDelivery() {
        Conversation conv = chatService.getOrCreateConversation(clientUser.getId(), freelancerUser.getId(), projectId);

        ChatMessage msg = chatService.sendMessage(conv.getId(), clientUser.getId(), "Hello, can we review sprint 1?", null);
        assertNotNull(msg);
        assertNotNull(msg.getId());
        assertEquals("Hello, can we review sprint 1?", msg.getContent());

        // Verify conversation last message updated
        Conversation updatedConv = chatService.getConversation(conv.getId());
        assertEquals("Hello, can we review sprint 1?", updatedConv.getLastMessage());

        // Verify recipient received in-app notification
        List<Notification> freeNotifs = notificationService.getUserNotifications(freelancerUser.getId());
        assertFalse(freeNotifs.isEmpty());
        assertTrue(freeNotifs.get(0).getTitle().contains("New message from " + clientUser.getUsername()));
        assertEquals("MESSAGE", freeNotifs.get(0).getType());
    }

    public void testMarkAsReadAndUnreadCounts() {
        Conversation conv = chatService.getOrCreateConversation(clientUser.getId(), freelancerUser.getId(), projectId);

        chatService.sendMessage(conv.getId(), clientUser.getId(), "Message 1", null);
        chatService.sendMessage(conv.getId(), clientUser.getId(), "Message 2", null);

        // Freelancer should have 2 unread messages
        int unread = chatService.getTotalUnreadCount(freelancerUser.getId());
        assertEquals(2, unread);

        // Sender (client) should have 0 unread messages
        int senderUnread = chatService.getTotalUnreadCount(clientUser.getId());
        assertEquals(0, senderUnread);

        // When freelancer opens the conversation, messages are marked as read
        List<ChatMessage> messages = chatService.getConversationMessages(conv.getId(), freelancerUser.getId());
        assertEquals(2, messages.size());

        int unreadAfterRead = chatService.getTotalUnreadCount(freelancerUser.getId());
        assertEquals(0, unreadAfterRead);
    }

    public void testCannotCreateConversationWithSelf() {
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.getOrCreateConversation(clientUser.getId(), clientUser.getId(), projectId);
        });
    }

    public void testSendMessageValidations() {
        Conversation conv = chatService.getOrCreateConversation(clientUser.getId(), freelancerUser.getId(), projectId);

        assertThrows(IllegalArgumentException.class, () -> {
            chatService.sendMessage(conv.getId(), clientUser.getId(), "", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            chatService.sendMessage("non_existent_conv", clientUser.getId(), "Content", null);
        });
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running ChatServiceTest...");
        int passed = 0;
        int total = 5;
        try {
            initDatabase();
        } catch (Throwable t) {
            System.err.println("Setup failed for ChatServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            ChatServiceTest test = new ChatServiceTest();
            test.setUp();
            test.testGetOrCreateConversationIdempotent();
            passed++;
            System.out.println("  [PASS] testGetOrCreateConversationIdempotent");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testGetOrCreateConversationIdempotent: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ChatServiceTest test = new ChatServiceTest();
            test.setUp();
            test.testSendMessageAndNotificationDelivery();
            passed++;
            System.out.println("  [PASS] testSendMessageAndNotificationDelivery");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSendMessageAndNotificationDelivery: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ChatServiceTest test = new ChatServiceTest();
            test.setUp();
            test.testMarkAsReadAndUnreadCounts();
            passed++;
            System.out.println("  [PASS] testMarkAsReadAndUnreadCounts");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testMarkAsReadAndUnreadCounts: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ChatServiceTest test = new ChatServiceTest();
            test.setUp();
            test.testCannotCreateConversationWithSelf();
            passed++;
            System.out.println("  [PASS] testCannotCreateConversationWithSelf");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCannotCreateConversationWithSelf: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            ChatServiceTest test = new ChatServiceTest();
            test.setUp();
            test.testSendMessageValidations();
            passed++;
            System.out.println("  [PASS] testSendMessageValidations");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSendMessageValidations: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("ChatServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in ChatServiceTest");
        }
    }
}
