package com.freelancing.model.admin;

import java.io.Serializable;

/**
 * Immutable security and administrative audit log entry mapped to SQLite audit_logs table.
 */
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String username;
    private String action;
    private String details;
    private String ipAddress;
    private String timestamp;

    public AuditLog() {}

    public AuditLog(String id, String userId, String action, String details, String ipAddress, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
