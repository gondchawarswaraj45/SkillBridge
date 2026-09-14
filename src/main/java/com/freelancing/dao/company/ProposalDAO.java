package com.freelancing.dao.company;

import com.freelancing.model.company.Proposal;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Proposals persistence in SQLite.
 * Manages proposal submissions, status transitions, and queries with rich join metadata.
 */
public class ProposalDAO {

    private static final String BASE_SELECT =
            "SELECT pr.id, pr.project_id, pr.freelancer_id, pr.bid_amount, pr.delivery_days, "
            + "pr.cover_letter, pr.status, pr.ai_match_score, pr.created_at, "
            + "p.title AS project_title, p.budget_max AS project_budget, p.budget_type AS project_budget_type, p.deadline AS project_deadline, "
            + "cp.id AS client_profile_id, cu.username AS client_username, "
            + "fu.id AS freelancer_user_id, fu.username AS freelancer_username, "
            + "fp.title AS freelancer_profile_title, fp.rating AS freelancer_rating, fp.avatar_path AS freelancer_avatar, "
            + "fp.total_reviews AS freelancer_total_reviews, fp.completed_projects AS freelancer_completed_projects "
            + "FROM proposals pr "
            + "JOIN projects p ON pr.project_id = p.id "
            + "JOIN client_profiles cp ON p.client_id = cp.id "
            + "JOIN users cu ON cp.user_id = cu.id "
            + "JOIN freelancer_profiles fp ON pr.freelancer_id = fp.id "
            + "JOIN users fu ON fp.user_id = fu.id ";

    /**
     * Inserts a new proposal into SQLite.
     * Throws IllegalStateException if duplicate proposal constraint is violated.
     */
    public boolean create(Proposal proposal) {
        String sql = "INSERT INTO proposals ("
                   + "id, project_id, freelancer_id, bid_amount, delivery_days, "
                   + "cover_letter, status, ai_match_score, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
                   + (proposal.getCreatedAt() != null ? "?" : "CURRENT_TIMESTAMP") + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, proposal.getId());
            ps.setString(2, proposal.getProjectId());
            ps.setString(3, proposal.getFreelancerId());
            ps.setDouble(4, proposal.getBidAmount());
            ps.setInt(5, proposal.getDeliveryDays());
            ps.setString(6, proposal.getCoverLetter());
            ps.setString(7, proposal.getStatus() != null ? proposal.getStatus().name() : "SUBMITTED");
            ps.setDouble(8, proposal.getAiMatchScore());
            if (proposal.getCreatedAt() != null) {
                ps.setString(9, proposal.getCreatedAt());
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                throw new IllegalStateException("You have already submitted a proposal for this project.");
            }
            System.err.println("ProposalDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Proposal proposal) {
        String sql = "UPDATE proposals SET "
                   + "bid_amount = ?, delivery_days = ?, cover_letter = ?, "
                   + "status = ?, ai_match_score = ? "
                   + "WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, proposal.getBidAmount());
            ps.setInt(2, proposal.getDeliveryDays());
            ps.setString(3, proposal.getCoverLetter());
            ps.setString(4, proposal.getStatus() != null ? proposal.getStatus().name() : "SUBMITTED");
            ps.setDouble(5, proposal.getAiMatchScore());
            ps.setString(6, proposal.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProposalDAO.update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(Connection conn, String proposalId, Proposal.Status status) throws SQLException {
        String sql = "UPDATE proposals SET status = ? WHERE id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status != null ? status.name() : "SUBMITTED");
            ps.setString(2, proposalId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String proposalId, Proposal.Status status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return updateStatus(conn, proposalId, status);
        } catch (SQLException e) {
            System.err.println("ProposalDAO.updateStatus error: " + e.getMessage());
            return false;
        }
    }

    public Proposal findById(Connection conn, String id) throws SQLException {
        String sql = BASE_SELECT + "WHERE pr.id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProposal(rs);
                }
            }
        }
        return null;
    }

    public Proposal findById(String id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(conn, id);
        } catch (SQLException e) {
            System.err.println("ProposalDAO.findById error: " + e.getMessage());
            return null;
        }
    }

    public Proposal findByProjectAndFreelancer(String projectId, String freelancerProfileId) {
        String sql = BASE_SELECT + "WHERE pr.project_id = ? AND pr.freelancer_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, freelancerProfileId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProposal(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("ProposalDAO.findByProjectAndFreelancer error: " + e.getMessage());
        }
        return null;
    }

    public List<Proposal> findByProjectId(String projectId) {
        String sql = BASE_SELECT + "WHERE pr.project_id = ? ORDER BY pr.ai_match_score DESC, pr.created_at DESC;";
        List<Proposal> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProposal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProposalDAO.findByProjectId error: " + e.getMessage());
        }
        return list;
    }

    public List<Proposal> findByFreelancerProfileId(String freelancerProfileId) {
        String sql = BASE_SELECT + "WHERE pr.freelancer_id = ? ORDER BY pr.created_at DESC;";
        List<Proposal> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, freelancerProfileId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProposal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProposalDAO.findByFreelancerProfileId error: " + e.getMessage());
        }
        return list;
    }

    public List<Proposal> findByClientProfileId(String clientProfileId) {
        String sql = BASE_SELECT + "WHERE cp.id = ? ORDER BY pr.created_at DESC;";
        List<Proposal> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clientProfileId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProposal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ProposalDAO.findByClientProfileId error: " + e.getMessage());
        }
        return list;
    }

    public int countByProjectId(String projectId) {
        String sql = "SELECT COUNT(*) FROM proposals WHERE project_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("ProposalDAO.countByProjectId error: " + e.getMessage());
        }
        return 0;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM proposals WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProposalDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private Proposal mapResultSetToProposal(ResultSet rs) throws SQLException {
        Proposal p = new Proposal();
        p.setId(rs.getString("id"));
        p.setProjectId(rs.getString("project_id"));
        p.setFreelancerId(rs.getString("freelancer_id"));
        p.setBidAmount(rs.getDouble("bid_amount"));
        p.setDeliveryDays(rs.getInt("delivery_days"));
        p.setCoverLetter(rs.getString("cover_letter"));

        String statusStr = rs.getString("status");
        try {
            p.setStatus(Proposal.Status.valueOf(statusStr.toUpperCase()));
        } catch (Exception e) {
            p.setStatus(Proposal.Status.SUBMITTED);
        }

        p.setAiMatchScore(rs.getDouble("ai_match_score"));
        p.setCreatedAt(rs.getString("created_at"));

        // Enriched Joined Fields
        p.setProjectTitle(rs.getString("project_title"));
        p.setProjectBudget(rs.getDouble("project_budget"));
        p.setProjectBudgetType(rs.getString("project_budget_type"));
        p.setProjectDeadline(rs.getString("project_deadline"));

        p.setClientId(rs.getString("client_profile_id"));
        p.setClientName(rs.getString("client_username"));

        p.setFreelancerUserId(rs.getString("freelancer_user_id"));
        p.setFreelancerName(rs.getString("freelancer_username"));
        p.setFreelancerTitle(rs.getString("freelancer_profile_title"));
        p.setFreelancerRating(rs.getDouble("freelancer_rating"));
        p.setFreelancerAvatar(rs.getString("freelancer_avatar"));
        p.setFreelancerTotalReviews(rs.getInt("freelancer_total_reviews"));
        p.setFreelancerCompletedProjects(rs.getInt("freelancer_completed_projects"));

        return p;
    }
}
