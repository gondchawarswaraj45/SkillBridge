package com.freelancing.dao.company;

import com.freelancing.model.company.Review;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the SQLite reviews table.
 */
public class ReviewDAO {

    public boolean create(Review review) {
        String sql = "INSERT INTO reviews (id, project_id, reviewer_id, reviewee_id, rating, feedback) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, review.getId());
            ps.setString(2, review.getProjectId());
            ps.setString(3, review.getReviewerId());
            ps.setString(4, review.getRevieweeId());
            ps.setDouble(5, review.getRating());
            ps.setString(6, review.getFeedback());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ReviewDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public Review findById(String id) {
        String sql = "SELECT r.*, u1.username AS reviewer_name, u2.username AS reviewee_name, p.title AS project_title "
                + "FROM reviews r "
                + "LEFT JOIN users u1 ON r.reviewer_id = u1.id "
                + "LEFT JOIN users u2 ON r.reviewee_id = u2.id "
                + "LEFT JOIN projects p ON r.project_id = p.id "
                + "WHERE r.id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public List<Review> findByRevieweeId(String revieweeId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u1.username AS reviewer_name, u2.username AS reviewee_name, p.title AS project_title "
                + "FROM reviews r "
                + "LEFT JOIN users u1 ON r.reviewer_id = u1.id "
                + "LEFT JOIN users u2 ON r.reviewee_id = u2.id "
                + "LEFT JOIN projects p ON r.project_id = p.id "
                + "WHERE r.reviewee_id = ? "
                + "ORDER BY r.created_at DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, revieweeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReview(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.findByRevieweeId error: " + e.getMessage());
        }
        return list;
    }

    public List<Review> findByProjectId(String projectId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u1.username AS reviewer_name, u2.username AS reviewee_name, p.title AS project_title "
                + "FROM reviews r "
                + "LEFT JOIN users u1 ON r.reviewer_id = u1.id "
                + "LEFT JOIN users u2 ON r.reviewee_id = u2.id "
                + "LEFT JOIN projects p ON r.project_id = p.id "
                + "WHERE r.project_id = ? "
                + "ORDER BY r.created_at DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReview(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.findByProjectId error: " + e.getMessage());
        }
        return list;
    }

    public double getAverageRatingForUser(String userId) {
        String sql = "SELECT AVG(rating) FROM reviews WHERE reviewee_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.getAverageRatingForUser error: " + e.getMessage());
        }
        return 5.0;
    }

    public int getReviewCountForUser(String userId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE reviewee_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.getReviewCountForUser error: " + e.getMessage());
        }
        return 0;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM reviews WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ReviewDAO.delete error: " + e.getMessage());
            return false;
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getString("id"));
        review.setProjectId(rs.getString("project_id"));
        review.setReviewerId(rs.getString("reviewer_id"));
        review.setRevieweeId(rs.getString("reviewee_id"));
        review.setRating(rs.getDouble("rating"));
        review.setFeedback(rs.getString("feedback"));
        review.setCreatedAt(rs.getString("created_at"));

        try {
            review.setReviewerName(rs.getString("reviewer_name"));
            review.setRevieweeName(rs.getString("reviewee_name"));
            review.setProjectTitle(rs.getString("project_title"));
        } catch (SQLException ignored) {}

        return review;
    }
}
