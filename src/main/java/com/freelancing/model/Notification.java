package com.freelancing.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String title;
    private String message;
    private boolean read;
    private String timestamp;

    public Notification() {}

    public Notification(String id, String userId, String title, String message, boolean read, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.read = read;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
