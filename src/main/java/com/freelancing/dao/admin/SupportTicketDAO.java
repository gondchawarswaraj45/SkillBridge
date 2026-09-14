package com.freelancing.dao.admin;

import com.freelancing.model.admin.SupportTicket;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO for support_tickets table in SQLite.
 */
public class SupportTicketDAO {

    public SupportTicket create(SupportTicket ticket) {
        if (ticket.getId() == null || ticket.getId().isEmpty()) {
            ticket.setId("tkt_" + UUID.randomUUID().toString().substring(0, 8));
        }

        String sql = "INSERT INTO support_tickets (id, user_id, subject, description, priority, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticket.getId());
            ps.setString(2, ticket.getUserId());
            ps.setString(3, ticket.getSubject());
            ps.setString(4, ticket.getDescription());
            ps.setString(5, ticket.getPriority() != null ? ticket.getPriority().name() : SupportTicket.Priority.MEDIUM.name());
            ps.setString(6, ticket.getStatus() != null ? ticket.getStatus().name() : SupportTicket.Status.OPEN.name());
            ps.executeUpdate();
            return findById(ticket.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating support ticket: " + e.getMessage(), e);
        }
    }

    public SupportTicket findById(String id) {
        String sql = "SELECT t.*, u.username, u.email "
                + "FROM support_tickets t "
                + "JOIN users u ON t.user_id = u.id "
                + "WHERE t.id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding support ticket: " + e.getMessage(), e);
        }
        return null;
    }

    public List<SupportTicket> findAll(String statusFilter) {
        List<SupportTicket> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, u.username, u.email "
                + "FROM support_tickets t "
                + "JOIN users u ON t.user_id = u.id "
                + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND t.status = ? ");
            params.add(statusFilter.toUpperCase());
        }

        sql.append("ORDER BY t.created_at DESC;");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying support tickets: " + e.getMessage(), e);
        }
        return list;
    }

    public List<SupportTicket> findByUserId(String userId) {
        List<SupportTicket> list = new ArrayList<>();
        String sql = "SELECT t.*, u.username, u.email "
                + "FROM support_tickets t "
                + "JOIN users u ON t.user_id = u.id "
                + "WHERE t.user_id = ? "
                + "ORDER BY t.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying user support tickets: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean updateStatus(String ticketId, String newStatus) {
        String sql = "UPDATE support_tickets SET status = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus != null ? newStatus.toUpperCase() : SupportTicket.Status.RESOLVED.name());
            ps.setString(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating support ticket status: " + e.getMessage(), e);
        }
    }

    private SupportTicket mapResultSetToTicket(ResultSet rs) throws SQLException {
        SupportTicket t = new SupportTicket();
        t.setId(rs.getString("id"));
        t.setUserId(rs.getString("user_id"));
        t.setUserName(rs.getString("username"));
        t.setUserEmail(rs.getString("email"));
        t.setSubject(rs.getString("subject"));
        t.setDescription(rs.getString("description"));

        String prioStr = rs.getString("priority");
        if (prioStr != null) {
            try {
                t.setPriority(SupportTicket.Priority.valueOf(prioStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                t.setPriority(SupportTicket.Priority.MEDIUM);
            }
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                t.setStatus(SupportTicket.Status.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                t.setStatus(SupportTicket.Status.OPEN);
            }
        }

        t.setCreatedAt(rs.getString("created_at"));
        return t;
    }
}
