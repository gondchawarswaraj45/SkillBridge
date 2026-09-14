package com.freelancing.service.admin;

import com.freelancing.dao.admin.AuditLogDAO;
import com.freelancing.dao.admin.DisputeDAO;
import com.freelancing.dao.admin.ReportDAO;
import com.freelancing.dao.admin.SupportTicketDAO;
import com.freelancing.dao.common.UserDAO;
import com.freelancing.model.admin.AuditLog;
import com.freelancing.model.admin.Dispute;
import com.freelancing.model.admin.Report;
import com.freelancing.model.admin.SupportTicket;
import com.freelancing.model.common.User;
import com.freelancing.service.common.NotificationService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.util.CsvUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Service orchestrating platform administration, analytics KPIs,
 * user governance, dispute arbitration, support ticketing,
 * audit logging, database backups, and CSV data export.
 */
public class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    private final UserDAO userDAO;
    private final DisputeDAO disputeDAO;
    private final SupportTicketDAO ticketDAO;
    private final ReportDAO reportDAO;
    private final AuditLogDAO auditLogDAO;
    private final NotificationService notifService;

    public AdminService() {
        this.userDAO = new UserDAO();
        this.disputeDAO = new DisputeDAO();
        this.ticketDAO = new SupportTicketDAO();
        this.reportDAO = new ReportDAO();
        this.auditLogDAO = new AuditLogDAO();
        this.notifService = new NotificationService();
    }

    public AdminService(UserDAO userDAO, DisputeDAO disputeDAO, SupportTicketDAO ticketDAO,
            ReportDAO reportDAO, AuditLogDAO auditLogDAO, NotificationService notifService) {
        this.userDAO = userDAO;
        this.disputeDAO = disputeDAO;
        this.ticketDAO = ticketDAO;
        this.reportDAO = reportDAO;
        this.auditLogDAO = auditLogDAO;
        this.notifService = notifService;
    }

    // =========================================================================
    // 1. PLATFORM ANALYTICS & KPIS
    // =========================================================================

    public Map<String, Object> getPlatformKpis() {
        Map<String, Object> kpis = new LinkedHashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total Users
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users;")) {
                kpis.put("totalUsers", rs.next() ? rs.getInt(1) : 0);
            }

            // Total Projects & Active Projects
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM projects;")) {
                kpis.put("totalProjects", rs.next() ? rs.getInt(1) : 0);
            }
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM projects WHERE status = 'IN_PROGRESS';")) {
                kpis.put("activeProjects", rs.next() ? rs.getInt(1) : 0);
            }

            // Total Contracts & Active Contracts
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM contracts;")) {
                kpis.put("totalContracts", rs.next() ? rs.getInt(1) : 0);
            }
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM contracts WHERE status = 'ACTIVE';")) {
                kpis.put("activeContracts", rs.next() ? rs.getInt(1) : 0);
            }

            // Total Platform Volume / Paid Transactions
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'PAYMENT';")) {
                kpis.put("totalRevenue", rs.next() ? rs.getDouble(1) : 0.0);
            }

            // Active Escrow Committed
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT COALESCE(SUM(amount), 0) FROM milestones WHERE status IN ('IN_PROGRESS', 'SUBMITTED', 'APPROVED');")) {
                kpis.put("activeEscrow", rs.next() ? rs.getDouble(1) : 0.0);
            }

            // Open Disputes
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT COUNT(*) FROM disputes WHERE status IN ('OPEN', 'IN_REVIEW', 'UNDER_REVIEW');")) {
                kpis.put("openDisputes", rs.next() ? rs.getInt(1) : 0);
            }

            // Open Support Tickets
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM support_tickets WHERE status = 'OPEN';")) {
                kpis.put("openTickets", rs.next() ? rs.getInt(1) : 0);
            }

            // Category Distribution for Charts
            Map<String, Double> catData = new LinkedHashMap<>();
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT category, COUNT(*) FROM projects GROUP BY category;")) {
                while (rs.next()) {
                    String cat = rs.getString(1);
                    double count = rs.getDouble(2);
                    catData.put(cat != null ? cat : "Other", count);
                }
            }
            kpis.put("projectsByCategory", catData);

        } catch (SQLException e) {
            LOGGER.severe("Error computing platform KPIs: " + e.getMessage());
            kpis.put("totalUsers", 0);
            kpis.put("totalProjects", 0);
            kpis.put("totalContracts", 0);
            kpis.put("totalRevenue", 0.0);
            kpis.put("activeEscrow", 0.0);
            kpis.put("openDisputes", 0);
            kpis.put("openTickets", 0);
            kpis.put("projectsByCategory", Collections.emptyMap());
        }

        return kpis;
    }

    // =========================================================================
    // 2. USER GOVERNANCE
    // =========================================================================

    public List<User> getUsers(String searchKeyword, String roleFilter, String statusFilter) {
        return userDAO.searchUsers(searchKeyword, roleFilter, statusFilter);
    }

    public boolean setUserStatus(String targetUserId, String newStatus, String adminUserId, String reason) {
        if (targetUserId == null || newStatus == null)
            return false;

        boolean updated = userDAO.updateStatus(targetUserId, newStatus);
        if (updated) {
            String details = "User " + targetUserId + " status changed to " + newStatus
                    + (reason != null && !reason.trim().isEmpty() ? ". Reason: " + reason.trim() : "");
            auditLogDAO.log(adminUserId, "USER_STATUS_CHANGE", details, "127.0.0.1");

            notifService.sendNotification(
                    targetUserId,
                    "Account Status Update",
                    "Your SkillBridge account status has been updated to: " + newStatus,
                    "SYSTEM",
                    null);
        }
        return updated;
    }

    public boolean setUserStatus(String adminUserId, String targetUserId, User.Status status) {
        return setUserStatus(targetUserId, status != null ? status.name() : "ACTIVE", adminUserId, "Updated by admin");
    }

    public boolean setUserStatus(String adminUserId, String targetUserId, String newStatus) {
        return setUserStatus(targetUserId, newStatus, adminUserId, "Updated by admin");
    }

    // =========================================================================
    // 3. DISPUTE ARBITRATION
    // =========================================================================

    public Dispute raiseDispute(String contractId, String raisedBy, String reason, String description) {
        if (contractId == null || raisedBy == null || reason == null) {
            throw new IllegalArgumentException("Contract ID, Raiser ID, and Reason are required");
        }

        Dispute d = new Dispute();
        d.setContractId(contractId);
        d.setRaisedById(raisedBy);
        d.setReason(reason);
        d.setDescription(description);
        d.setStatus(Dispute.Status.OPEN);

        Dispute created = disputeDAO.create(d);
        auditLogDAO.log(raisedBy, "DISPUTE_RAISED",
                "Dispute " + created.getId() + " opened on contract " + contractId + ": " + reason, "127.0.0.1");

        // Notify admins / platform
        notifService.sendNotification(
                raisedBy,
                "Dispute Registered",
                "Your dispute regarding '" + reason + "' has been logged and assigned to administration for review.",
                "DISPUTE",
                created.getId());

        return created;
    }

    public boolean resolveDispute(String disputeId, String resolution, String resolvedBy, String resolutionStatus) {
        if (disputeId == null || resolution == null)
            return false;

        boolean ok = disputeDAO.resolveDispute(disputeId, resolution, resolvedBy, resolutionStatus);
        if (ok) {
            Dispute dispute = disputeDAO.findById(disputeId);
            String contractId = dispute != null ? dispute.getContractId() : "";

            auditLogDAO.log(resolvedBy, "DISPUTE_RESOLVED",
                    "Dispute " + disputeId + " resolved (" + resolutionStatus + "): " + resolution, "127.0.0.1");

            if (dispute != null && dispute.getRaisedById() != null) {
                notifService.sendNotification(
                        dispute.getRaisedById(),
                        "⚖️ Dispute Resolved",
                        "Your dispute on contract " + contractId + " has been resolved: " + resolution,
                        "DISPUTE",
                        disputeId);
            }
        }
        return ok;
    }

    public boolean resolveDispute(String resolvedBy, String disputeId, String resolution,
            Dispute.Status resolutionStatus) {
        return resolveDispute(disputeId, resolution, resolvedBy,
                resolutionStatus != null ? resolutionStatus.name() : "RESOLVED");
    }

    

    public List<Dispute> getDisputes(String statusFilter) {
        return disputeDAO.findAll(statusFilter);
    }

    public Dispute getDisputeById(String id) {
        return disputeDAO.findById(id);
    }

    // =========================================================================
    // 4. SUPPORT TICKETS
    // =========================================================================

    public SupportTicket createSupportTicket(String userId, String subject, String description, String priority) {
        if (userId == null || subject == null || description == null) {
            throw new IllegalArgumentException("User ID, Subject, and Description are required");
        }

        SupportTicket t = new SupportTicket();
        t.setUserId(userId);
        t.setSubject(subject);
        t.setDescription(description);
        if (priority != null) {
            try {
                t.setPriority(SupportTicket.Priority.valueOf(priority.toUpperCase()));
            } catch (IllegalArgumentException e) {
                t.setPriority(SupportTicket.Priority.MEDIUM);
            }
        }
        t.setStatus(SupportTicket.Status.OPEN);

        SupportTicket created = ticketDAO.create(t);
        auditLogDAO.log(userId, "TICKET_CREATED", "Ticket " + created.getId() + ": " + subject, "127.0.0.1");
        return created;
    }

    public boolean updateTicketStatus(String ticketId, String newStatus, String adminUserId) {
        boolean ok = ticketDAO.updateStatus(ticketId, newStatus);
        if (ok) {
            auditLogDAO.log(adminUserId, "TICKET_STATUS_CHANGE", "Ticket " + ticketId + " updated to " + newStatus,
                    "127.0.0.1");
            SupportTicket t = ticketDAO.findById(ticketId);
            if (t != null && t.getUserId() != null) {
                notifService.sendNotification(
                        t.getUserId(),
                        "Support Ticket Update",
                        "Your support ticket '" + t.getSubject() + "' status is now: " + newStatus,
                        "SUPPORT",
                        ticketId);
            }
        }
        return ok;
    }

    public boolean updateTicketStatus(String adminUserId, String ticketId,
            SupportTicket.Status status) {
        return updateTicketStatus(ticketId, status != null ? status.name() : "OPEN", adminUserId);
    }

    

    public List<SupportTicket> getSupportTickets(String statusFilter) {
        return ticketDAO.findAll(statusFilter);
    }

    // =========================================================================
    // 5. AUDIT LOGS
    // =========================================================================

    public List<AuditLog> getAuditLogs(int limit) {
        return auditLogDAO.findAll(limit > 0 ? limit : 100);
    }

    public AuditLog logAuditEvent(String userId, String action, String details, String ipAddress) {
        return auditLogDAO.log(userId, action, details, ipAddress);
    }

    // =========================================================================
    // 6. REPORTS & COMMUNITY MODERATION
    // =========================================================================

    public List<Report> getReports() {
        return reportDAO.findAll();
    }

    public Report createReport(String title, String reportType, String generatedBy,
            String content, String filePath) {
        Report report = new Report();
        report.setTitle(title);
        report.setReportType(reportType);
        report.setGeneratedBy(generatedBy);
        report.setContent(content);
        report.setFilePath(filePath);
        return reportDAO.create(report);
    }

    public boolean deleteReport(String reportId) {
        return reportDAO.delete(reportId);
    }

    // =========================================================================
    // 7. DATABASE BACKUP & CSV EXPORTS
    // =========================================================================

    public File backupDatabase(String destDirPath) throws IOException {
        return backupDatabase(destDirPath != null ? new File(destDirPath) : null);
    }

    /**
     * Creates a timestamped snapshot of data/skillbridge.db into destination
     * folder.
     */
    public File backupDatabase(File destDir) throws IOException {
        File sourceDb = new File("data", "skillbridge.db");
        if (!sourceDb.exists()) {
            throw new IOException("Source SQLite database does not exist: " + sourceDb.getAbsolutePath());
        }

        File targetDir = destDir != null ? destDir : new File("backups");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File backupFile = new File(targetDir, "skillbridge_" + timestamp + ".db");

        Files.copy(sourceDb.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        auditLogDAO.log("usr_admin", "DB_BACKUP",
                "Snapshot created: " + backupFile.getName() + " (" + backupFile.length() + " bytes)", "127.0.0.1");

        LOGGER.info("SkillBridge SQLite database snapshot created: " + backupFile.getAbsolutePath());
        return backupFile;
    }

    public File exportTableToCsv(String tableName, String destDirPath) {
        try {
            return exportTableToCsv(tableName, destDirPath != null ? new File(destDirPath) : null);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting table " + tableName + " to CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Exports any given SQLite table into a standard CSV file using CsvUtil.
     */
    public File exportTableToCsv(String tableName, File destDir) throws IOException, SQLException {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name is required for CSV export");
        }

        // Validate table name against SQL injection
        String cleanTable = tableName.trim().replaceAll("[^a-zA-Z0-9_]", "");

        File targetDir = destDir != null ? destDir : new File("exports");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File csvFile = new File(targetDir, cleanTable + "_export.csv");

        String sql = "SELECT * FROM " + cleanTable + ";";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            String[] headers = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                headers[i] = meta.getColumnName(i + 1);
            }

            List<String[]> rows = new ArrayList<>();
            while (rs.next()) {
                String[] row = new String[colCount];
                for (int i = 0; i < colCount; i++) {
                    Object val = rs.getObject(i + 1);
                    row[i] = val != null ? val.toString() : "";
                }
                rows.add(row);
            }

            CsvUtil.exportCsv(headers, rows, csvFile);
        }

        auditLogDAO.log("usr_admin", "DATA_EXPORT",
                "Table '" + cleanTable + "' exported to CSV (" + csvFile.getName() + ")", "127.0.0.1");
        return csvFile;
    }
}
