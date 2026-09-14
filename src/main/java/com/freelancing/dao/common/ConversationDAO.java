package com.freelancing.dao.common;

import com.freelancing.model.common.Conversation;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {

    public boolean create(Conversation conv) {
        String sql = "INSERT INTO conversations (id, user1_id, user2_id, project_id, last_message, last_message_time, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conv.getId());
            ps.setString(2, conv.getUser1Id());
            ps.setString(3, conv.getUser2Id());
            ps.setString(4, conv.getProjectId());
            ps.setString(5, conv.getLastMessage());
            ps.setString(6, conv.getLastMessageTime() != null ? conv.getLastMessageTime() : new Timestamp(System.currentTimeMillis()).toString());
            ps.setString(7, conv.getCreatedAt() != null ? conv.getCreatedAt() : new Timestamp(System.currentTimeMillis()).toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ConversationDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public Conversation findById(String id) {
        String sql = "SELECT c.*, p.title AS project_title, "
                   + "u1.username AS u1_username, u1.role AS u1_role, "
                   + "u2.username AS u2_username, u2.role AS u2_role "
                   + "FROM conversations c "
                   + "LEFT JOIN projects p ON c.project_id = p.id "
                   + "JOIN users u1 ON c.user1_id = u1.id "
                   + "JOIN users u2 ON c.user2_id = u2.id "
                   + "WHERE c.id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs, null);
                }
            }
        } catch (SQLException e) {
            System.err.println("ConversationDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    public Conversation findBetweenUsers(String userAId, String userBId, String projectId) {
        String sql;
        if (projectId != null && !projectId.trim().isEmpty()) {
            sql = "SELECT c.*, p.title AS project_title, "
                + "u1.username AS u1_username, u1.role AS u1_role, "
                + "u2.username AS u2_username, u2.role AS u2_role "
                + "FROM conversations c "
                + "LEFT JOIN projects p ON c.project_id = p.id "
                + "JOIN users u1 ON c.user1_id = u1.id "
                + "JOIN users u2 ON c.user2_id = u2.id "
                + "WHERE ((c.user1_id = ? AND c.user2_id = ?) OR (c.user1_id = ? AND c.user2_id = ?)) "
                + "AND c.project_id = ?;";
        } else {
            sql = "SELECT c.*, p.title AS project_title, "
                + "u1.username AS u1_username, u1.role AS u1_role, "
                + "u2.username AS u2_username, u2.role AS u2_role "
                + "FROM conversations c "
                + "LEFT JOIN projects p ON c.project_id = p.id "
                + "JOIN users u1 ON c.user1_id = u1.id "
                + "JOIN users u2 ON c.user2_id = u2.id "
                + "WHERE ((c.user1_id = ? AND c.user2_id = ?) OR (c.user1_id = ? AND c.user2_id = ?));";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userAId);
            ps.setString(2, userBId);
            ps.setString(3, userBId);
            ps.setString(4, userAId);
            if (projectId != null && !projectId.trim().isEmpty()) {
                ps.setString(5, projectId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs, userAId);
                }
            }
        } catch (SQLException e) {
            System.err.println("ConversationDAO.findBetweenUsers error: " + e.getMessage());
        }
        return null;
    }

    public List<Conversation> findByUserId(String userId) {
        List<Conversation> list = new ArrayList<>();
        String sql = "SELECT c.*, p.title AS project_title, "
                   + "u1.username AS u1_username, u1.role AS u1_role, "
                   + "u2.username AS u2_username, u2.role AS u2_role, "
                   + "(SELECT COUNT(*) FROM messages m WHERE m.conversation_id = c.id AND m.sender_id != ? AND m.is_read = 0) AS unread_count "
                   + "FROM conversations c "
                   + "LEFT JOIN projects p ON c.project_id = p.id "
                   + "JOIN users u1 ON c.user1_id = u1.id "
                   + "JOIN users u2 ON c.user2_id = u2.id "
                   + "WHERE c.user1_id = ? OR c.user2_id = ? "
                   + "ORDER BY c.last_message_time DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setString(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs, userId));
                }
            }
        } catch (SQLException e) {
            System.err.println("ConversationDAO.findByUserId error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateLastMessage(String conversationId, String lastMessage, String lastMessageTime) {
        String sql = "UPDATE conversations SET last_message = ?, last_message_time = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lastMessage);
            ps.setString(2, lastMessageTime);
            ps.setString(3, conversationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ConversationDAO.updateLastMessage error: " + e.getMessage());
            return false;
        }
    }

    private Conversation mapRow(ResultSet rs, String currentUserId) throws SQLException {
        Conversation c = new Conversation();
        c.setId(rs.getString("id"));
        c.setUser1Id(rs.getString("user1_id"));
        c.setUser2Id(rs.getString("user2_id"));
        c.setProjectId(rs.getString("project_id"));
        c.setLastMessage(rs.getString("last_message"));
        c.setLastMessageTime(rs.getString("last_message_time"));
        c.setCreatedAt(rs.getString("created_at"));

        try {
            c.setProjectTitle(rs.getString("project_title"));
        } catch (SQLException ignored) {}

        String u1Id = c.getUser1Id();
        String u1Name = rs.getString("u1_username");
        String u1Role = rs.getString("u1_role");

        String u2Id = c.getUser2Id();
        String u2Name = rs.getString("u2_username");
        String u2Role = rs.getString("u2_role");

        if (currentUserId != null && currentUserId.equals(u1Id)) {
            c.setOtherUserId(u2Id);
            c.setOtherUsername(u2Name);
            c.setOtherUserRole(u2Role);
        } else if (currentUserId != null && currentUserId.equals(u2Id)) {
            c.setOtherUserId(u1Id);
            c.setOtherUsername(u1Name);
            c.setOtherUserRole(u1Role);
        } else {
            // Default to user2
            c.setOtherUserId(u2Id);
            c.setOtherUsername(u2Name);
            c.setOtherUserRole(u2Role);
        }

        try {
            c.setUnreadCount(rs.getInt("unread_count"));
        } catch (SQLException ignored) {}

        return c;
    }
}
