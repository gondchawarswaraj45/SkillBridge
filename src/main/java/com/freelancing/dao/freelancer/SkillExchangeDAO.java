package com.freelancing.dao.freelancer;

import com.freelancing.model.freelancer.SkillExchange;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for skill_exchange table in SQLite.
 */
public class SkillExchangeDAO {

    public SkillExchange create(SkillExchange exchange) {
        if (exchange.getId() == null || exchange.getId().isEmpty()) {
            exchange.setId("swap_" + UUID.randomUUID().toString().substring(0, 8));
        }

        String sql = "INSERT INTO skill_exchange (id, offerer_id, offered_skill, requested_skill, description, status, requester_id, requester_note, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, exchange.getId());
            ps.setString(2, exchange.getOffererId());
            ps.setString(3, exchange.getOfferedSkill());
            ps.setString(4, exchange.getRequestedSkill());
            ps.setString(5, exchange.getDescription());
            ps.setString(6, exchange.getStatus() != null ? exchange.getStatus().name() : SkillExchange.Status.OPEN.name());
            ps.setString(7, exchange.getRequesterId());
            ps.setString(8, exchange.getRequesterNote());
            ps.executeUpdate();
            return findById(exchange.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating skill exchange offer: " + e.getMessage(), e);
        }
    }

    public SkillExchange findById(String id) {
        String sql = "SELECT s.*, u1.username AS offerer_name, u2.username AS requester_name "
                + "FROM skill_exchange s "
                + "JOIN users u1 ON s.offerer_id = u1.id "
                + "LEFT JOIN users u2 ON s.requester_id = u2.id "
                + "WHERE s.id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExchange(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding skill exchange offer: " + e.getMessage(), e);
        }
        return null;
    }

    public List<SkillExchange> findAll(String statusFilter, String skillKeyword) {
        List<SkillExchange> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.*, u1.username AS offerer_name, u2.username AS requester_name "
                + "FROM skill_exchange s "
                + "JOIN users u1 ON s.offerer_id = u1.id "
                + "LEFT JOIN users u2 ON s.requester_id = u2.id "
                + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND s.status = ? ");
            params.add(statusFilter.toUpperCase());
        }

        if (skillKeyword != null && !skillKeyword.trim().isEmpty()) {
            sql.append("AND (s.offered_skill LIKE ? OR s.requested_skill LIKE ? OR s.description LIKE ? OR u1.username LIKE ?) ");
            String term = "%" + skillKeyword.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }

        sql.append("ORDER BY s.created_at DESC;");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExchange(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying skill exchanges: " + e.getMessage(), e);
        }
        return list;
    }

    public List<SkillExchange> findByOffererId(String offererId) {
        List<SkillExchange> list = new ArrayList<>();
        String sql = "SELECT s.*, u1.username AS offerer_name, u2.username AS requester_name "
                + "FROM skill_exchange s "
                + "JOIN users u1 ON s.offerer_id = u1.id "
                + "LEFT JOIN users u2 ON s.requester_id = u2.id "
                + "WHERE s.offerer_id = ? "
                + "ORDER BY s.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, offererId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExchange(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying offerer skill exchanges: " + e.getMessage(), e);
        }
        return list;
    }

    public List<SkillExchange> findByParticipantId(String userId) {
        List<SkillExchange> list = new ArrayList<>();
        String sql = "SELECT s.*, u1.username AS offerer_name, u2.username AS requester_name "
                + "FROM skill_exchange s "
                + "JOIN users u1 ON s.offerer_id = u1.id "
                + "LEFT JOIN users u2 ON s.requester_id = u2.id "
                + "WHERE s.offerer_id = ? OR s.requester_id = ? "
                + "ORDER BY s.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExchange(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying participant skill exchanges: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean requestExchange(String exchangeId, String requesterId, String note) {
        String sql = "UPDATE skill_exchange SET status = 'REQUESTED', requester_id = ?, requester_note = ? "
                + "WHERE id = ? AND status = 'OPEN' AND offerer_id != ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requesterId);
            ps.setString(2, note);
            ps.setString(3, exchangeId);
            ps.setString(4, requesterId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error requesting skill exchange: " + e.getMessage(), e);
        }
    }

    public boolean acceptExchange(String exchangeId, String offererId) {
        String sql = "UPDATE skill_exchange SET status = 'IN_PROGRESS' "
                + "WHERE id = ? AND offerer_id = ? AND status = 'REQUESTED';";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, exchangeId);
            ps.setString(2, offererId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error accepting skill exchange: " + e.getMessage(), e);
        }
    }

    public boolean rejectExchange(String exchangeId, String offererId) {
        String sql = "UPDATE skill_exchange SET status = 'OPEN', requester_id = NULL, requester_note = NULL "
                + "WHERE id = ? AND offerer_id = ? AND status = 'REQUESTED';";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, exchangeId);
            ps.setString(2, offererId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error rejecting skill exchange: " + e.getMessage(), e);
        }
    }

    public boolean completeExchange(String exchangeId, String userId) {
        String sql = "UPDATE skill_exchange SET status = 'COMPLETED' "
                + "WHERE id = ? AND status = 'IN_PROGRESS' AND (offerer_id = ? OR requester_id = ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, exchangeId);
            ps.setString(2, userId);
            ps.setString(3, userId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error completing skill exchange: " + e.getMessage(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM skill_exchange WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting skill exchange: " + e.getMessage(), e);
        }
    }

    private SkillExchange mapResultSetToExchange(ResultSet rs) throws SQLException {
        SkillExchange s = new SkillExchange();
        s.setId(rs.getString("id"));
        s.setOffererId(rs.getString("offerer_id"));
        s.setOfferedSkill(rs.getString("offered_skill"));
        s.setRequestedSkill(rs.getString("requested_skill"));
        s.setDescription(rs.getString("description"));

        String statusStr = rs.getString("status");
        try {
            s.setStatus(SkillExchange.Status.valueOf(statusStr));
        } catch (Exception ex) {
            s.setStatus(SkillExchange.Status.OPEN);
        }

        s.setRequesterId(rs.getString("requester_id"));
        s.setRequesterNote(rs.getString("requester_note"));
        s.setCreatedAt(rs.getString("created_at"));
        s.setOffererName(rs.getString("offerer_name"));
        s.setRequesterName(rs.getString("requester_name"));
        return s;
    }
}
