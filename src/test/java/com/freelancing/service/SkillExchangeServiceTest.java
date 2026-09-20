package com.freelancing.service;

import com.freelancing.model.common.Notification;
import com.freelancing.model.common.User;
import com.freelancing.model.freelancer.SkillExchange;
import com.freelancing.service.common.NotificationService;
import com.freelancing.service.freelancer.SkillExchangeService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SkillExchangeServiceTest {

    private static SkillExchangeService exchangeService;
    private static NotificationService notificationService;

    private User offererUser;
    private User requesterUser;

    public static void initDatabase() {
        DatabaseInitializer.initialize();
        exchangeService = new SkillExchangeService();
        notificationService = new NotificationService();
    }

    public void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        offererUser = new User();
        offererUser.setId("usr_off_" + suffix);
        offererUser.setUsername("offerer_" + suffix);
        offererUser.setEmail("offerer_" + suffix + "@test.com");
        offererUser.setPassword("password");
        offererUser.setRole(User.Role.FREELANCER);

        requesterUser = new User();
        requesterUser.setId("usr_req_" + suffix);
        requesterUser.setUsername("requester_" + suffix);
        requesterUser.setEmail("requester_" + suffix + "@test.com");
        requesterUser.setPassword("password");
        requesterUser.setRole(User.Role.FREELANCER);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertUser = "INSERT INTO users (id, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                ps.setString(1, offererUser.getId());
                ps.setString(2, offererUser.getUsername());
                ps.setString(3, offererUser.getEmail());
                ps.setString(4, "hash");
                ps.setString(5, offererUser.getRole().name());
                ps.addBatch();

                ps.setString(1, requesterUser.getId());
                ps.setString(2, requesterUser.getUsername());
                ps.setString(3, requesterUser.getEmail());
                ps.setString(4, "hash");
                ps.setString(5, requesterUser.getRole().name());
                ps.addBatch();

                ps.executeBatch();
            }
        }
    }

    public void testCreateOfferAndBrowse() {
        SkillExchange offer = exchangeService.createOffer(
                offererUser.getId(),
                "JavaFX & SQLite Architecture",
                "React & TypeScript",
                "Willing to mentor 3 hours a week on desktop UI architecture."
        );
        assertNotNull(offer.getId());
        assertEquals(SkillExchange.Status.OPEN, offer.getStatus());

        List<SkillExchange> openOffers = exchangeService.getOffers("OPEN", "JavaFX");
        assertTrue(openOffers.stream().anyMatch(o -> o.getId().equals(offer.getId())));

        List<SkillExchange> myOffers = exchangeService.getUserOffers(offererUser.getId());
        assertTrue(myOffers.stream().anyMatch(o -> o.getId().equals(offer.getId())));
    }

    public void testRequestExchangeWorkflow() {
        SkillExchange offer = exchangeService.createOffer(
                offererUser.getId(),
                "Spring Boot Microservices",
                "Flutter Mobile Dev",
                "Backend mentoring in exchange for mobile pairing."
        );

        boolean requested = exchangeService.requestExchange(
                offer.getId(),
                requesterUser.getId(),
                "Hi, I have 4 years of Flutter experience and want to learn Spring Boot!"
        );
        assertTrue(requested, "Requesting exchange should succeed");

        SkillExchange updated = exchangeService.getOfferById(offer.getId());
        assertEquals(SkillExchange.Status.REQUESTED, updated.getStatus());
        assertEquals(requesterUser.getId(), updated.getRequesterId());
        assertEquals("Hi, I have 4 years of Flutter experience and want to learn Spring Boot!", updated.getRequesterNote());

        // Offerer should receive in-app notification
        List<Notification> notifs = notificationService.getUserNotifications(offererUser.getId());
        assertTrue(notifs.stream().anyMatch(n -> "SKILL_EXCHANGE".equals(n.getType()) && n.getTitle().contains("Skill Barter Request")));
    }

    public void testAcceptExchangeWorkflow() {
        SkillExchange offer = exchangeService.createOffer(
                offererUser.getId(),
                "Python ML Inference",
                "DevOps Kubernetes",
                "AI barter session"
        );

        exchangeService.requestExchange(offer.getId(), requesterUser.getId(), "Let's connect on Kubernetes!");
        boolean accepted = exchangeService.acceptExchange(offer.getId(), offererUser.getId());
        assertTrue(accepted, "Accepting barter request should succeed");

        SkillExchange inProgress = exchangeService.getOfferById(offer.getId());
        assertEquals(SkillExchange.Status.IN_PROGRESS, inProgress.getStatus());

        // Requester should receive acceptance notification
        List<Notification> notifs = notificationService.getUserNotifications(requesterUser.getId());
        assertTrue(notifs.stream().anyMatch(n -> "SKILL_EXCHANGE".equals(n.getType()) && n.getTitle().contains("Accepted")));
    }

    public void testRejectExchangeWorkflow() {
        SkillExchange offer = exchangeService.createOffer(
                offererUser.getId(),
                "SQL Query Optimization",
                "UI Design Figma",
                "Database speed tuning for design feedback."
        );

        exchangeService.requestExchange(offer.getId(), requesterUser.getId(), "Can we swap?");
        boolean rejected = exchangeService.rejectExchange(offer.getId(), offererUser.getId());
        assertTrue(rejected, "Rejecting barter request should succeed");

        SkillExchange resetOffer = exchangeService.getOfferById(offer.getId());
        assertEquals(SkillExchange.Status.OPEN, resetOffer.getStatus());
        assertNull(resetOffer.getRequesterId());
        assertNull(resetOffer.getRequesterNote());

        // Requester should receive rejection notification
        List<Notification> notifs = notificationService.getUserNotifications(requesterUser.getId());
        assertTrue(notifs.stream().anyMatch(n -> "SKILL_EXCHANGE".equals(n.getType()) && n.getTitle().contains("Update")));
    }

    public void testCompleteExchangeWorkflow() {
        SkillExchange offer = exchangeService.createOffer(
                offererUser.getId(),
                "AWS Cloud Architecture",
                "Golang Concurrency",
                "Full architectural review."
        );

        exchangeService.requestExchange(offer.getId(), requesterUser.getId(), "Ready to swap!");
        exchangeService.acceptExchange(offer.getId(), offererUser.getId());

        boolean completed = exchangeService.completeExchange(offer.getId(), requesterUser.getId());
        assertTrue(completed, "Completing barter session should succeed");

        SkillExchange finishedOffer = exchangeService.getOfferById(offer.getId());
        assertEquals(SkillExchange.Status.COMPLETED, finishedOffer.getStatus());

        // Both users should receive completion notifications
        List<Notification> offNotifs = notificationService.getUserNotifications(offererUser.getId());
        List<Notification> reqNotifs = notificationService.getUserNotifications(requesterUser.getId());
        assertTrue(offNotifs.stream().anyMatch(n -> "SKILL_EXCHANGE".equals(n.getType()) && n.getTitle().contains("Completed")));
        assertTrue(reqNotifs.stream().anyMatch(n -> "SKILL_EXCHANGE".equals(n.getType()) && n.getTitle().contains("Completed")));
    }

    public void testSelfRequestDisallowed() {
        SkillExchange offer = exchangeService.createOffer(
                offererUser.getId(),
                "C++ Algorithms",
                "Rust Concurrency",
                "Low-level systems swap."
        );

        assertThrows(IllegalArgumentException.class, () -> {
            exchangeService.requestExchange(offer.getId(), offererUser.getId(), "Barter with myself");
        });
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running SkillExchangeServiceTest...");
        int passed = 0;
        int total = 6;
        try {
            initDatabase();
        } catch (Throwable t) {
            System.err.println("Setup failed for SkillExchangeServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            SkillExchangeServiceTest test = new SkillExchangeServiceTest();
            test.setUp();
            test.testCreateOfferAndBrowse();
            passed++;
            System.out.println("  [PASS] testCreateOfferAndBrowse");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCreateOfferAndBrowse: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            SkillExchangeServiceTest test = new SkillExchangeServiceTest();
            test.setUp();
            test.testRequestExchangeWorkflow();
            passed++;
            System.out.println("  [PASS] testRequestExchangeWorkflow");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testRequestExchangeWorkflow: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            SkillExchangeServiceTest test = new SkillExchangeServiceTest();
            test.setUp();
            test.testAcceptExchangeWorkflow();
            passed++;
            System.out.println("  [PASS] testAcceptExchangeWorkflow");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testAcceptExchangeWorkflow: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            SkillExchangeServiceTest test = new SkillExchangeServiceTest();
            test.setUp();
            test.testRejectExchangeWorkflow();
            passed++;
            System.out.println("  [PASS] testRejectExchangeWorkflow");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testRejectExchangeWorkflow: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            SkillExchangeServiceTest test = new SkillExchangeServiceTest();
            test.setUp();
            test.testCompleteExchangeWorkflow();
            passed++;
            System.out.println("  [PASS] testCompleteExchangeWorkflow");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCompleteExchangeWorkflow: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            SkillExchangeServiceTest test = new SkillExchangeServiceTest();
            test.setUp();
            test.testSelfRequestDisallowed();
            passed++;
            System.out.println("  [PASS] testSelfRequestDisallowed");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSelfRequestDisallowed: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("SkillExchangeServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in SkillExchangeServiceTest");
        }
    }
}
