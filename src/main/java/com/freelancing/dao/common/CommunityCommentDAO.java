package com.freelancing.dao.common;

import com.freelancing.model.common.CommunityComment;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for community_comments table in SQLite.
 */
public class CommunityCommentDAO {

    public CommunityComment create(CommunityComment comment) {
        if (comment.getId() == null || comment.getId().isEmpty()) {
            comment.setId("comm_" + UUID.randomUUID().toString().substring(0, 8));
        }

        String sql = "INSERT INTO community_comments (id, post_id, author_id, content, created_at) "
                + "VALUES (?, ?, ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment.getId());
            ps.setString(2, comment.getPostId());
            ps.setString(3, comment.getAuthorId());
            ps.setString(4, comment.getContent());
            ps.executeUpdate();
            return findById(comment.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating community comment: " + e.getMessage(), e);
        }
    }

    public CommunityComment findById(String id) {
        String sql = "SELECT c.*, u.username AS author_name, u.role AS author_role "
                + "FROM community_comments c "
                + "JOIN users u ON c.author_id = u.id "
                + "WHERE c.id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding comment: " + e.getMessage(), e);
        }
        return null;
    }

    public List<CommunityComment> findByPostId(String postId) {
        List<CommunityComment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.username AS author_name, u.role AS author_role "
                + "FROM community_comments c "
                + "JOIN users u ON c.author_id = u.id "
                + "WHERE c.post_id = ? "
                + "ORDER BY c.created_at ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching comments for post: " + e.getMessage(), e);
        }
        return list;
    }

    public int getCommentCountForPost(String postId) {
        String sql = "SELECT COUNT(*) FROM community_comments WHERE post_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    public void delete(String id) {
        String sql = "DELETE FROM community_comments WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comment: " + e.getMessage(), e);
        }
    }

    private CommunityComment mapResultSetToComment(ResultSet rs) throws SQLException {
        CommunityComment c = new CommunityComment();
        c.setId(rs.getString("id"));
        c.setPostId(rs.getString("post_id"));
        c.setAuthorId(rs.getString("author_id"));
        c.setContent(rs.getString("content"));
        c.setCreatedAt(rs.getString("created_at"));
        c.setAuthorName(rs.getString("author_name"));
        c.setAuthorRole(rs.getString("author_role"));
        return c;
    }
}
