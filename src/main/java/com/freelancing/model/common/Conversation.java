package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Represents a direct messaging conversation between two users with optional project context.
 * Mapped to SQLite `conversations` table.
 */
public class Conversation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String user1Id;
    private String user2Id;
    private String projectId;
    private String lastMessage;
    private String lastMessageTime;
    private String createdAt;

    // Joined display fields
    private String otherUserId;
    private String otherUsername;
    private String otherUserRole;
    private String otherUserAvatar;
    private String projectTitle;
    private int unreadCount;

    public Conversation() {}

    public Conversation(String id, String user1Id, String user2Id, String projectId, String lastMessage, String lastMessageTime, String createdAt) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.projectId = projectId;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUser1Id() { return user1Id; }
    public void setUser1Id(String user1Id) { this.user1Id = user1Id; }

    public String getUser2Id() { return user2Id; }
    public void setUser2Id(String user2Id) { this.user2Id = user2Id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getOtherUserId() { return otherUserId; }
    public void setOtherUserId(String otherUserId) { this.otherUserId = otherUserId; }

    public String getOtherUsername() { return otherUsername; }
    public void setOtherUsername(String otherUsername) { this.otherUsername = otherUsername; }

    public String getOtherUserRole() { return otherUserRole; }
    public void setOtherUserRole(String otherUserRole) { this.otherUserRole = otherUserRole; }

    public String getOtherUserAvatar() { return otherUserAvatar; }
    public void setOtherUserAvatar(String otherUserAvatar) { this.otherUserAvatar = otherUserAvatar; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
