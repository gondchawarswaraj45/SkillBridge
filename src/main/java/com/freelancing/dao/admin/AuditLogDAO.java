package com.freelancing.dao.admin;

import com.freelancing.model.admin.AuditLog;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO for audit_logs table in SQLite.
 * Manages immutable audit trails for administrative, security, and dispute events.
 */
public class AuditLogDAO {

    public AuditLog log(String userId, String action, String details, String ipAddress) {
        String id = "audit_" + UUID.randomUUID().toString().substring(0, 8);
        String sql = "INSERT INTO audit_logs (id, user_id, action, details, ip_address, timestamp) "
                + "VALUES (?, ?, ?, ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            ps.setString(3, action);
            ps.setString(4, details);
            ps.setString(5, ipAddress != null ? ipAddress : "127.0.0.1");
            ps.executeUpdate();
            return findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error writing audit log: " + e.getMessage(), e);
        }
    }

    public AuditLog findById(String id) {
        String sql = "SELECT a.*, u.username FROM audit_logs a "
                + "LEFT JOIN users u ON a.user_id = u.id "
                + "WHERE a.id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAuditLog(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding audit log: " + e.getMessage(), e);
        }
        return null;
    }

    public List<AuditLog> findAll(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.*, u.username FROM audit_logs a "
                + "LEFT JOIN users u ON a.user_id = u.id "
                + "ORDER BY a.timestamp DESC LIMIT ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 100);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying audit logs: " + e.getMessage(), e);
        }
        return list;
    }

    public List<AuditLog> findByUserId(String userId) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.*, u.username FROM audit_logs a "
                + "LEFT JOIN users u ON a.user_id = u.id "
                + "WHERE a.user_id = ? "
                + "ORDER BY a.timestamp DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying user audit logs: " + e.getMessage(), e);
        }
        return list;
    }

    private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getString("id"));
        log.setUserId(rs.getString("user_id"));
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setDetails(rs.getString("details"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setTimestamp(rs.getString("timestamp"));
        return log;
    }
}
