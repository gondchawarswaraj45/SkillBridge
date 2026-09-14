package com.freelancing.dao.common;

import com.freelancing.model.common.CommunityPost;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for community_posts table in SQLite.
 */
public class CommunityPostDAO {

    public CommunityPost create(CommunityPost post) {
        if (post.getId() == null || post.getId().isEmpty()) {
            post.setId("post_" + UUID.randomUUID().toString().substring(0, 8));
        }

        String sql = "INSERT INTO community_posts (id, author_id, title, content, category, likes_count, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getId());
            ps.setString(2, post.getAuthorId());
            ps.setString(3, post.getTitle());
            ps.setString(4, post.getContent());
            ps.setString(5, post.getCategory() != null ? post.getCategory() : CommunityPost.CAT_DISCUSSIONS);
            ps.setInt(6, post.getLikesCount());
            ps.executeUpdate();
            return findById(post.getId(), post.getAuthorId());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating community post: " + e.getMessage(), e);
        }
    }

    public CommunityPost findById(String id, String currentUserId) {
        String sql = "SELECT p.*, u.username AS author_name, u.role AS author_role, "
                + "(SELECT COUNT(*) FROM community_comments c WHERE c.post_id = p.id) AS comments_count, "
                + "EXISTS(SELECT 1 FROM favorites f WHERE f.user_id = ? AND f.item_type = 'POST_LIKE' AND f.item_id = p.id) AS is_liked "
                + "FROM community_posts p "
                + "JOIN users u ON p.author_id = u.id "
                + "WHERE p.id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentUserId != null ? currentUserId : "");
            ps.setString(2, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPost(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching community post: " + e.getMessage(), e);
        }
        return null;
    }

    public List<CommunityPost> findAll(String categoryFilter, String keyword, String currentUserId) {
        List<CommunityPost> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, u.username AS author_name, u.role AS author_role, "
                + "(SELECT COUNT(*) FROM community_comments c WHERE c.post_id = p.id) AS comments_count, "
                + "EXISTS(SELECT 1 FROM favorites f WHERE f.user_id = ? AND f.item_type = 'POST_LIKE' AND f.item_id = p.id) AS is_liked "
                + "FROM community_posts p "
                + "JOIN users u ON p.author_id = u.id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        params.add(currentUserId != null ? currentUserId : "");

        if (categoryFilter != null && !categoryFilter.isEmpty() && !"ALL".equalsIgnoreCase(categoryFilter)) {
            sql.append("AND UPPER(p.category) = ? ");
            params.add(categoryFilter.toUpperCase());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.title LIKE ? OR p.content LIKE ? OR u.username LIKE ?) ");
            String term = "%" + keyword.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        sql.append("ORDER BY p.created_at DESC;");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying community posts: " + e.getMessage(), e);
        }
        return list;
    }

    public List<CommunityPost> findByAuthorId(String authorId, String currentUserId) {
        List<CommunityPost> list = new ArrayList<>();
        String sql = "SELECT p.*, u.username AS author_name, u.role AS author_role, "
                + "(SELECT COUNT(*) FROM community_comments c WHERE c.post_id = p.id) AS comments_count, "
                + "EXISTS(SELECT 1 FROM favorites f WHERE f.user_id = ? AND f.item_type = 'POST_LIKE' AND f.item_id = p.id) AS is_liked "
                + "FROM community_posts p "
                + "JOIN users u ON p.author_id = u.id "
                + "WHERE p.author_id = ? "
                + "ORDER BY p.created_at DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentUserId != null ? currentUserId : "");
            ps.setString(2, authorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying user posts: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Toggles like state for a post by a user.
     * @return true if now liked, false if unliked
     */
    public boolean toggleLike(String postId, String userId) {
        String checkSql = "SELECT id FROM favorites WHERE user_id = ? AND item_type = 'POST_LIKE' AND item_id = ?;";
        String deleteFav = "DELETE FROM favorites WHERE user_id = ? AND item_type = 'POST_LIKE' AND item_id = ?;";
        String insertFav = "INSERT INTO favorites (id, user_id, item_type, item_id, created_at) VALUES (?, ?, 'POST_LIKE', ?, datetime('now'));";
        String decCount = "UPDATE community_posts SET likes_count = MAX(0, likes_count - 1) WHERE id = ?;";
        String incCount = "UPDATE community_posts SET likes_count = likes_count + 1 WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean isLiked = false;
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, userId);
                psCheck.setString(2, postId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    isLiked = rs.next();
                }
            }

            if (isLiked) {
                try (PreparedStatement psDel = conn.prepareStatement(deleteFav);
                     PreparedStatement psDec = conn.prepareStatement(decCount)) {
                    psDel.setString(1, userId);
                    psDel.setString(2, postId);
                    psDel.executeUpdate();

                    psDec.setString(1, postId);
                    psDec.executeUpdate();
                }
                return false;
            } else {
                try (PreparedStatement psIns = conn.prepareStatement(insertFav);
                     PreparedStatement psInc = conn.prepareStatement(incCount)) {
                    psIns.setString(1, "fav_" + UUID.randomUUID().toString().substring(0, 8));
                    psIns.setString(2, userId);
                    psIns.setString(3, postId);
                    psIns.executeUpdate();

                    psInc.setString(1, postId);
                    psInc.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error toggling like on post: " + e.getMessage(), e);
        }
    }

    public boolean isLikedByUser(String postId, String userId) {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND item_type = 'POST_LIKE' AND item_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, postId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void update(CommunityPost post) {
        String sql = "UPDATE community_posts SET title = ?, content = ?, category = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getCategory());
            ps.setString(4, post.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating community post: " + e.getMessage(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM community_posts WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting community post: " + e.getMessage(), e);
        }
    }

    private CommunityPost mapResultSetToPost(ResultSet rs) throws SQLException {
        CommunityPost post = new CommunityPost();
        post.setId(rs.getString("id"));
        post.setAuthorId(rs.getString("author_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setCategory(rs.getString("category"));
        post.setLikesCount(rs.getInt("likes_count"));
        post.setCreatedAt(rs.getString("created_at"));
        post.setAuthorName(rs.getString("author_name"));
        post.setAuthorRole(rs.getString("author_role"));
        post.setCommentsCount(rs.getInt("comments_count"));
        post.setLikedByCurrentUser(rs.getInt("is_liked") > 0);
        return post;
    }
}
