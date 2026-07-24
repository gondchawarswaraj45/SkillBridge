package com.freelancing.model;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String projectId;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String content;
    private String attachmentName;
    private boolean isEncrypted;
    private String timestamp;

    public ChatMessage() {}

    public ChatMessage(String id, String projectId, String senderId, String senderName, String receiverId,
                       String content, String attachmentName, boolean isEncrypted, String timestamp) {
        this.id = id;
        this.projectId = projectId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
        this.attachmentName = attachmentName;
        this.isEncrypted = isEncrypted;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }

    public boolean isEncrypted() { return isEncrypted; }
    public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
