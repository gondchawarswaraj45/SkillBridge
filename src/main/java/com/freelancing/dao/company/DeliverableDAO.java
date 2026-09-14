package com.freelancing.dao.company;

import com.freelancing.model.company.Deliverable;

import com.freelancing.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Deliverable submissions.
 * Supports deliverable tracking, latest artifact queries, and transaction participation.
 */
public class DeliverableDAO {

    private static final String SELECT_BASE =
            "SELECT d.*, m.title AS milestone_title, u.username AS freelancer_name " +
            "FROM deliverables d " +
            "JOIN milestones m ON d.milestone_id = m.id " +
            "JOIN freelancer_profiles fp ON d.freelancer_id = fp.id " +
            "JOIN users u ON fp.user_id = u.id ";

    public void create(Connection conn, Deliverable deliverable) throws SQLException {
        String sql = "INSERT INTO deliverables (id, milestone_id, freelancer_id, title, description, " +
                     "file_path, status, submitted_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deliverable.getId());
            stmt.setString(2, deliverable.getMilestoneId());
            stmt.setString(3, deliverable.getFreelancerId());
            stmt.setString(4, deliverable.getTitle());
            stmt.setString(5, deliverable.getDescription());
            stmt.setString(6, deliverable.getFilePath());
            stmt.setString(7, deliverable.getStatus() != null ? deliverable.getStatus().name() : Deliverable.Status.SUBMITTED.name());
            stmt.setString(8, deliverable.getSubmittedAt());
            stmt.executeUpdate();
        }
    }

    public void create(Deliverable deliverable) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            create(conn, deliverable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create deliverable: " + e.getMessage(), e);
        }
    }

    public Deliverable findById(String id) {
        String sql = SELECT_BASE + "WHERE d.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDeliverable(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find deliverable by id: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Deliverable> findByMilestoneId(String milestoneId) {
        String sql = SELECT_BASE + "WHERE d.milestone_id = ? ORDER BY d.submitted_at DESC";
        return queryList(sql, milestoneId);
    }

    public Deliverable findLatestByMilestoneId(String milestoneId) {
        String sql = SELECT_BASE + "WHERE d.milestone_id = ? ORDER BY d.submitted_at DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, milestoneId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDeliverable(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find latest deliverable for milestone: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Deliverable> findByFreelancerId(String freelancerId) {
        String sql = SELECT_BASE + "WHERE d.freelancer_id = ? ORDER BY d.submitted_at DESC";
        return queryList(sql, freelancerId);
    }

    public boolean updateStatus(Connection conn, String deliverableId, Deliverable.Status status) throws SQLException {
        String sql = "UPDATE deliverables SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, deliverableId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String deliverableId, Deliverable.Status status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStatus(conn, deliverableId, status);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update deliverable status: " + e.getMessage(), e);
        }
    }

    private List<Deliverable> queryList(String sql, String param) {
        List<Deliverable> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDeliverable(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query deliverables: " + e.getMessage(), e);
        }
        return list;
    }

    private Deliverable mapResultSetToDeliverable(ResultSet rs) throws SQLException {
        Deliverable d = new Deliverable();
        d.setId(rs.getString("id"));
        d.setMilestoneId(rs.getString("milestone_id"));
        d.setFreelancerId(rs.getString("freelancer_id"));
        d.setTitle(rs.getString("title"));
        d.setDescription(rs.getString("description"));
        d.setFilePath(rs.getString("file_path"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                d.setStatus(Deliverable.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                d.setStatus(Deliverable.Status.SUBMITTED);
            }
        }

        d.setSubmittedAt(rs.getString("submitted_at"));
        d.setMilestoneTitle(rs.getString("milestone_title"));
        d.setFreelancerName(rs.getString("freelancer_name"));

        return d;
    }
}
