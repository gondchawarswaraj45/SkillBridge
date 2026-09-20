package com.freelancing.service;

import com.freelancing.model.admin.AuditLog;
import com.freelancing.model.admin.Dispute;
import com.freelancing.model.admin.SupportTicket;
import com.freelancing.model.common.User;
import com.freelancing.service.admin.AdminService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    private static AdminService adminService;

    public static void setupDatabase() throws Exception {
        DatabaseInitializer.initialize();
        adminService = new AdminService();

        // Ensure a test contract exists for dispute tests
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR IGNORE INTO contracts (id, project_id, client_id, freelancer_id, total_amount, status) "
                     + "VALUES ('ctr_adm_test', 'proj_1', 'cp_sarah', 'fp_alex', 2000.0, 'ACTIVE');")) {
            ps.executeUpdate();
        }
    }

    public void testPlatformKpis() {
        Map<String, Object> kpis = adminService.getPlatformKpis();
        assertNotNull(kpis);
        assertTrue(((Number) kpis.get("totalUsers")).intValue() >= 1);
        assertTrue(((Number) kpis.get("totalProjects")).intValue() >= 0);
        assertTrue(((Number) kpis.get("totalContracts")).intValue() >= 0);
        assertNotNull(kpis.get("projectsByCategory"));
    }

    public void testUserSearchAndStatusToggleWithAuditLog() {
        String testUserId = "usr_test_" + UUID.randomUUID().toString().substring(0, 8);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (id, username, email, password_hash, role, status) VALUES (?, ?, ?, 'hash', 'FREELANCER', 'ACTIVE');")) {
            ps.setString(1, testUserId);
            ps.setString(2, "adm_user_" + UUID.randomUUID().toString().substring(0, 6));
            ps.setString(3, testUserId + "@test.com");
            ps.executeUpdate();
        } catch (Exception e) {
            fail("Failed to seed user: " + e.getMessage());
        }

        List<User> users = adminService.getUsers("adm_user_", "ALL", "ALL");
        assertFalse(users.isEmpty());

        // Suspend user
        boolean suspended = adminService.setUserStatus(testUserId, "SUSPENDED", "usr_admin", "Test violation");
        assertTrue(suspended);

        List<User> checkSuspended = adminService.getUsers(testUserId, "ALL", "SUSPENDED");
        assertFalse(checkSuspended.isEmpty());
        assertEquals(User.Status.SUSPENDED, checkSuspended.get(0).getStatus());

        // Verify audit log
        List<AuditLog> logs = adminService.getAuditLogs(20);
        assertFalse(logs.isEmpty());
        boolean foundAction = logs.stream().anyMatch(l -> "USER_STATUS_CHANGE".equals(l.getAction()) && l.getDetails().contains(testUserId));
        assertTrue(foundAction);

        // Restore user
        boolean restored = adminService.setUserStatus(testUserId, "ACTIVE", "usr_admin", "Reactivation");
        assertTrue(restored);
    }

    public void testDisputeLifecycleAndResolution() {
        Dispute created = adminService.raiseDispute("ctr_adm_test", "usr_client1", "Milestone scope disagreement", "Details of disagreement");
        assertNotNull(created);
        assertEquals("ctr_adm_test", created.getContractId());
        assertEquals(Dispute.Status.OPEN, created.getStatus());

        // Resolve dispute
        boolean resolved = adminService.resolveDispute(created.getId(), "Arbitration settlement released 50%", "usr_admin", "RESOLVED");
        assertTrue(resolved);

        Dispute found = adminService.getDisputeById(created.getId());
        assertNotNull(found);
        assertEquals(Dispute.Status.RESOLVED, found.getStatus());
        assertTrue(found.getResolution().contains("Arbitration settlement"));
    }

    public void testSupportTicketLifecycle() {
        SupportTicket ticket = adminService.createSupportTicket("usr_free1", "Payout Delay Issue", "I requested a payout 3 days ago", "HIGH");
        assertNotNull(ticket);
        assertEquals("Payout Delay Issue", ticket.getSubject());
        assertEquals(SupportTicket.Priority.HIGH, ticket.getPriority());
        assertEquals(SupportTicket.Status.OPEN, ticket.getStatus());

        // Update ticket status
        boolean updated = adminService.updateTicketStatus(ticket.getId(), "IN_PROGRESS", "usr_admin");
        assertTrue(updated);

        List<SupportTicket> openTickets = adminService.getSupportTickets("IN_PROGRESS");
        assertTrue(openTickets.stream().anyMatch(t -> t.getId().equals(ticket.getId())));
    }

    public void testDatabaseBackup() throws IOException {
        File tempDir = Files.createTempDirectory("skillbridge_backup_test").toFile();
        tempDir.deleteOnExit();

        File backupFile = adminService.backupDatabase(tempDir);
        assertNotNull(backupFile);
        assertTrue(backupFile.exists());
        assertTrue(backupFile.length() > 0);
        assertTrue(backupFile.getName().endsWith(".db"));

        backupFile.delete();
        tempDir.delete();
    }

    public void testCsvDataExport() throws Exception {
        File tempDir = Files.createTempDirectory("skillbridge_csv_test").toFile();
        tempDir.deleteOnExit();

        File exported = adminService.exportTableToCsv("users", tempDir);
        assertNotNull(exported);
        assertTrue(exported.exists());
        assertTrue(exported.length() > 0);
        assertTrue(exported.getName().endsWith(".csv"));

        List<String> lines = Files.readAllLines(exported.toPath());
        assertFalse(lines.isEmpty());
        // Header line must contain id, username, email
        assertTrue(lines.get(0).toLowerCase().contains("username"));

        exported.delete();
        tempDir.delete();
    }

    public void testAuditLogCustomEvent() {
        AuditLog logged = adminService.logAuditEvent("usr_admin", "CONFIG_CHANGE", "Updated platform commission rate to 10%", "192.168.1.1");
        assertNotNull(logged);
        assertEquals("CONFIG_CHANGE", logged.getAction());

        List<AuditLog> logs = adminService.getAuditLogs(10);
        assertTrue(logs.stream().anyMatch(l -> l.getId().equals(logged.getId())));
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        System.out.println("Running AdminServiceTest...");
        int passed = 0;
        int total = 7;
        try {
            setupDatabase();
        } catch (Throwable t) {
            System.err.println("Setup failed for AdminServiceTest: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        try {
            AdminServiceTest test = new AdminServiceTest();
            test.testPlatformKpis();
            passed++;
            System.out.println("  [PASS] testPlatformKpis");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testPlatformKpis: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            AdminServiceTest test = new AdminServiceTest();
            test.testUserSearchAndStatusToggleWithAuditLog();
            passed++;
            System.out.println("  [PASS] testUserSearchAndStatusToggleWithAuditLog");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testUserSearchAndStatusToggleWithAuditLog: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            AdminServiceTest test = new AdminServiceTest();
            test.testDisputeLifecycleAndResolution();
            passed++;
            System.out.println("  [PASS] testDisputeLifecycleAndResolution");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testDisputeLifecycleAndResolution: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            AdminServiceTest test = new AdminServiceTest();
            test.testSupportTicketLifecycle();
            passed++;
            System.out.println("  [PASS] testSupportTicketLifecycle");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testSupportTicketLifecycle: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            AdminServiceTest test = new AdminServiceTest();
            test.testDatabaseBackup();
            passed++;
            System.out.println("  [PASS] testDatabaseBackup");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testDatabaseBackup: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            AdminServiceTest test = new AdminServiceTest();
            test.testCsvDataExport();
            passed++;
            System.out.println("  [PASS] testCsvDataExport");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testCsvDataExport: " + t.getMessage());
            t.printStackTrace();
        }
        try {
            AdminServiceTest test = new AdminServiceTest();
            test.testAuditLogCustomEvent();
            passed++;
            System.out.println("  [PASS] testAuditLogCustomEvent");
        } catch (Throwable t) {
            System.err.println("  [FAIL] testAuditLogCustomEvent: " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("AdminServiceTest: " + passed + "/" + total + " tests passed.\n");
        if (passed != total) {
            throw new RuntimeException("Tests failed in AdminServiceTest");
        }
    }
}
