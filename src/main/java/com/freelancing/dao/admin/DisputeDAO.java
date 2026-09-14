package com.freelancing.dao.admin;

import com.freelancing.model.admin.Dispute;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO for disputes table in SQLite.
 */
public class DisputeDAO {

    public Dispute create(Dispute dispute) {
        if (dispute.getId() == null || dispute.getId().isEmpty()) {
            dispute.setId("disp_" + UUID.randomUUID().toString().substring(0, 8));
        }

        String desc = dispute.getDescription() != null ? dispute.getDescription() : (dispute.getReason() != null ? dispute.getReason() : "");
        String sql = "INSERT INTO disputes (id, contract_id, raised_by, reason, details, description, status, resolution, resolved_by, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dispute.getId());
            ps.setString(2, dispute.getContractId());
            ps.setString(3, dispute.getRaisedById());
            ps.setString(4, dispute.getReason());
            ps.setString(5, desc);
            ps.setString(6, desc);
            ps.setString(7, dispute.getStatus() != null ? dispute.getStatus().name() : Dispute.Status.OPEN.name());
            ps.setString(8, dispute.getResolution());
            ps.setString(9, dispute.getResolvedBy());
            ps.executeUpdate();
            return findById(dispute.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating dispute: " + e.getMessage(), e);
        }
    }

    public Dispute findById(String id) {
        String sql = "SELECT d.*, p.title AS contract_title, u1.username AS raised_by_name, u2.username AS resolved_by_name "
                + "FROM disputes d "
                + "JOIN contracts c ON d.contract_id = c.id "
                + "LEFT JOIN projects p ON c.project_id = p.id "
                + "JOIN users u1 ON d.raised_by = u1.id "
                + "LEFT JOIN users u2 ON d.resolved_by = u2.id "
                + "WHERE d.id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDispute(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding dispute: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Dispute> findAll(String statusFilter) {
        List<Dispute> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT d.*, p.title AS contract_title, u1.username AS raised_by_name, u2.username AS resolved_by_name "
                + "FROM disputes d "
                + "JOIN contracts c ON d.contract_id = c.id "
                + "LEFT JOIN projects p ON c.project_id = p.id "
                + "JOIN users u1 ON d.raised_by = u1.id "
                + "LEFT JOIN users u2 ON d.resolved_by = u2.id "
                + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND d.status = ? ");
            params.add(statusFilter.toUpperCase());
        }

        sql.append("ORDER BY d.created_at DESC;");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDispute(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying disputes: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Dispute> findByContractId(String contractId) {
        List<Dispute> list = new ArrayList<>();
        String sql = "SELECT d.*, p.title AS contract_title, u1.username AS raised_by_name, u2.username AS resolved_by_name "
                + "FROM disputes d "
                + "JOIN contracts c ON d.contract_id = c.id "
                + "LEFT JOIN projects p ON c.project_id = p.id "
                + "JOIN users u1 ON d.raised_by = u1.id "
                + "LEFT JOIN users u2 ON d.resolved_by = u2.id "
                + "WHERE d.contract_id = ? "
                + "ORDER BY d.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDispute(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying contract disputes: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean resolveDispute(String disputeId, String resolution, String resolvedBy, String newStatus) {
        String sql = "UPDATE disputes SET resolution = ?, resolved_by = ?, status = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resolution);
            ps.setString(2, resolvedBy);
            ps.setString(3, newStatus != null ? newStatus.toUpperCase() : Dispute.Status.RESOLVED.name());
            ps.setString(4, disputeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error resolving dispute: " + e.getMessage(), e);
        }
    }

    private Dispute mapResultSetToDispute(ResultSet rs) throws SQLException {
        Dispute d = new Dispute();
        d.setId(rs.getString("id"));
        d.setContractId(rs.getString("contract_id"));
        d.setContractTitle(rs.getString("contract_title"));
        d.setRaisedById(rs.getString("raised_by"));
        d.setRaisedByName(rs.getString("raised_by_name"));
        d.setReason(rs.getString("reason"));
        String desc = null;
        try {
            desc = rs.getString("description");
        } catch (SQLException ignored) {}
        if (desc == null || desc.isEmpty()) {
            try {
                desc = rs.getString("details");
            } catch (SQLException ignored) {}
        }
        d.setDescription(desc);

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                d.setStatus(Dispute.Status.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                d.setStatus(Dispute.Status.OPEN);
            }
        }

        d.setResolution(rs.getString("resolution"));
        d.setResolvedBy(rs.getString("resolved_by"));
        d.setResolvedByName(rs.getString("resolved_by_name"));
        d.setCreatedAt(rs.getString("created_at"));
        return d;
    }
}
