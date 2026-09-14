package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Represents an in-app notification.
 * Mapped to SQLite `notifications` table.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum NotificationType {
        MESSAGE, PROPOSAL, CONTRACT, MILESTONE, PAYMENT, MEETING, SYSTEM, SECURITY
    }

    private String id;
    private String userId;
    private String title;
    private String message;
    private String type = "INFO";
    private String referenceId;
    private boolean isRead;
    private String createdAt;
    private String actionUrl;

    public Notification() {
    }

    public Notification(String id, String userId, String title, String message, boolean isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.type = "INFO";
    }

    public Notification(String id, String userId, String title, String message, String type, String referenceId,
            boolean isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : "INFO";
        this.referenceId = referenceId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setType(NotificationType type) {
        this.type = type != null ? type.name() : "INFO";
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTimestamp() {
        return createdAt;
    }

    public void setTimestamp(String timestamp) {
        this.createdAt = timestamp;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

}

        
    

    
        
    
        
    

    
        
    
        
    

    
        
    
        
    

    
        
    
        
    

    
        
    
        
    

    
        
    
