package com.freelancing.dao.company;

import com.freelancing.model.company.Milestone;

import com.freelancing.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Milestone entities.
 * Supports milestone lifecycle queries and atomic transaction participation.
 */
public class MilestoneDAO {

    public void create(Connection conn, Milestone milestone) throws SQLException {
        String sql = "INSERT INTO milestones (id, contract_id, title, description, amount, " +
                     "deadline, status, sequence_order, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, milestone.getId());
            stmt.setString(2, milestone.getContractId());
            stmt.setString(3, milestone.getTitle());
            stmt.setString(4, milestone.getDescription());
            stmt.setDouble(5, milestone.getAmount());
            stmt.setString(6, milestone.getDeadline());
            stmt.setString(7, milestone.getStatus() != null ? milestone.getStatus().name() : Milestone.Status.PENDING.name());
            stmt.setInt(8, milestone.getSequenceOrder());
            stmt.setString(9, milestone.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    public void create(Milestone milestone) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            create(conn, milestone);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create milestone: " + e.getMessage(), e);
        }
    }

    public Milestone findById(String id) {
        String sql = "SELECT m.*, c.project_id FROM milestones m " +
                     "JOIN contracts c ON m.contract_id = c.id " +
                     "WHERE m.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMilestone(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find milestone by id: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Milestone> findByContractId(Connection conn, String contractId) throws SQLException {
        String sql = "SELECT m.*, c.project_id FROM milestones m " +
                     "JOIN contracts c ON m.contract_id = c.id " +
                     "WHERE m.contract_id = ? ORDER BY m.sequence_order ASC, m.created_at ASC";
        return queryList(conn, sql, contractId);
    }

    public List<Milestone> findByContractId(String contractId) {
        String sql = "SELECT m.*, c.project_id FROM milestones m " +
                     "JOIN contracts c ON m.contract_id = c.id " +
                     "WHERE m.contract_id = ? ORDER BY m.sequence_order ASC, m.created_at ASC";
        return queryList(sql, contractId);
    }

    public List<Milestone> findByProjectId(String projectId) {
        String sql = "SELECT m.*, c.project_id FROM milestones m " +
                     "JOIN contracts c ON m.contract_id = c.id " +
                     "WHERE c.project_id = ? ORDER BY m.sequence_order ASC, m.created_at ASC";
        return queryList(sql, projectId);
    }

    public boolean update(Connection conn, Milestone milestone) throws SQLException {
        String sql = "UPDATE milestones SET title = ?, description = ?, amount = ?, deadline = ?, " +
                     "status = ?, sequence_order = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, milestone.getTitle());
            stmt.setString(2, milestone.getDescription());
            stmt.setDouble(3, milestone.getAmount());
            stmt.setString(4, milestone.getDeadline());
            stmt.setString(5, milestone.getStatus().name());
            stmt.setInt(6, milestone.getSequenceOrder());
            stmt.setString(7, milestone.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(Milestone milestone) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return update(conn, milestone);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update milestone: " + e.getMessage(), e);
        }
    }

    public boolean updateStatus(Connection conn, String milestoneId, Milestone.Status status) throws SQLException {
        String sql = "UPDATE milestones SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, milestoneId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String milestoneId, Milestone.Status status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStatus(conn, milestoneId, status);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update milestone status: " + e.getMessage(), e);
        }
    }

    public boolean delete(String milestoneId) {
        String sql = "DELETE FROM milestones WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, milestoneId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete milestone: " + e.getMessage(), e);
        }
    }

    public List<Milestone> queryList(Connection conn, String sql, String param) throws SQLException {
        List<Milestone> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMilestone(rs));
                }
            }
        }
        return list;
    }

    private List<Milestone> queryList(String sql, String param) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return queryList(conn, sql, param);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query milestones: " + e.getMessage(), e);
        }
    }

    private Milestone mapResultSetToMilestone(ResultSet rs) throws SQLException {
        Milestone m = new Milestone();
        m.setId(rs.getString("id"));
        m.setContractId(rs.getString("contract_id"));
        m.setTitle(rs.getString("title"));
        m.setDescription(rs.getString("description"));
        m.setAmount(rs.getDouble("amount"));
        m.setDeadline(rs.getString("deadline"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                m.setStatus(Milestone.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                m.setStatus(Milestone.Status.PENDING);
            }
        }

        m.setSequenceOrder(rs.getInt("sequence_order"));
        m.setCreatedAt(rs.getString("created_at"));

        try {
            m.setProjectId(rs.getString("project_id"));
        } catch (SQLException ignored) {}

        return m;
    }
}
