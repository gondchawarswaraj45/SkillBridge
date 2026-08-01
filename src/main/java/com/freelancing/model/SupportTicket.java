package com.freelancing.model;

import java.io.Serializable;

public class SupportTicket implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_PROGRESS, CLOSED
    }

    private String id;
    private String userId;
    private String userName;
    private String subject;
    private String message;
    private Status status;
    private String adminReply;
    private String createdAt;

    public SupportTicket() {}

    public SupportTicket(String id, String userId, String userName, String subject, String message, Status status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
