package com.freelancing.dao.common;

import com.freelancing.model.common.ChatMessage;

import com.freelancing.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean create(ChatMessage msg) {
        String sql = "INSERT INTO messages (id, conversation_id, sender_id, content, attachment_path, is_read, sent_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, msg.getId());
            ps.setString(2, msg.getConversationId());
            ps.setString(3, msg.getSenderId());
            ps.setString(4, msg.getContent());
            ps.setString(5, msg.getAttachmentPath());
            ps.setBoolean(6, msg.isRead());
            ps.setString(7, msg.getSentAt() != null ? msg.getSentAt() : new Timestamp(System.currentTimeMillis()).toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("MessageDAO.create error: " + e.getMessage());
            return false;
        }
    }

    public List<ChatMessage> findByConversationId(String conversationId) {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT m.*, u.username AS sender_name, "
                   + "COALESCE(fp.avatar_path, cp.avatar_path) AS sender_avatar "
                   + "FROM messages m "
                   + "JOIN users u ON m.sender_id = u.id "
                   + "LEFT JOIN freelancer_profiles fp ON fp.user_id = u.id "
                   + "LEFT JOIN client_profiles cp ON cp.user_id = u.id "
                   + "WHERE m.conversation_id = ? "
                   + "ORDER BY m.sent_at ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conversationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("MessageDAO.findByConversationId error: " + e.getMessage());
        }
        return list;
    }

    public boolean markAsRead(String conversationId, String currentUserId) {
        String sql = "UPDATE messages SET is_read = 1 WHERE conversation_id = ? AND sender_id != ? AND is_read = 0;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conversationId);
            ps.setString(2, currentUserId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("MessageDAO.markAsRead error: " + e.getMessage());
            return false;
        }
    }

    public int getUnreadCount(String conversationId, String currentUserId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE conversation_id = ? AND sender_id != ? AND is_read = 0;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conversationId);
            ps.setString(2, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("MessageDAO.getUnreadCount error: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalUnreadCountForUser(String userId) {
        String sql = "SELECT COUNT(*) FROM messages m "
                   + "JOIN conversations c ON m.conversation_id = c.id "
                   + "WHERE (c.user1_id = ? OR c.user2_id = ?) AND m.sender_id != ? AND m.is_read = 0;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setString(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("MessageDAO.getTotalUnreadCountForUser error: " + e.getMessage());
        }
        return 0;
    }

    private ChatMessage mapRow(ResultSet rs) throws SQLException {
        ChatMessage msg = new ChatMessage();
        msg.setId(rs.getString("id"));
        msg.setConversationId(rs.getString("conversation_id"));
        msg.setSenderId(rs.getString("sender_id"));
        msg.setContent(rs.getString("content"));
        msg.setAttachmentPath(rs.getString("attachment_path"));
        msg.setRead(rs.getBoolean("is_read"));
        msg.setSentAt(rs.getString("sent_at"));
        msg.setTimestamp(msg.getSentAt());

        try {
            msg.setSenderName(rs.getString("sender_name"));
        } catch (SQLException ignored) {}
        try {
            msg.setSenderAvatar(rs.getString("sender_avatar"));
        } catch (SQLException ignored) {}

        return msg;
    }
}
