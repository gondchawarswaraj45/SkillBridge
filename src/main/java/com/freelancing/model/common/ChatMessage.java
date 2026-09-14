package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Represents a chat message within a conversation.
 * Mapped to SQLite `messages` table.
 */
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        TEXT, IMAGE, DOCUMENT, BOOKING_INFO, QUOTE, CONTRACT
    }

    private String id;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private String receiverId;
    private String projectId;
    private String content;
    private String attachmentPath;
    private boolean isRead;
    private boolean isEncrypted;
    private String timestamp;
    private MessageType messageType = MessageType.TEXT;
    private String imageUrl;

    public ChatMessage() {
    }

    public ChatMessage(String id, String projectId, String senderId, String senderName, String receiverId,
            String content, String attachmentName, boolean isEncrypted, String timestamp) {
        this.id = id;
        this.projectId = projectId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
        this.attachmentPath = attachmentName;
        this.isEncrypted = isEncrypted;
        this.timestamp = timestamp;
        this.messageType = MessageType.TEXT;
    }

    public ChatMessage(String id, String projectId, String senderId, String senderName, String receiverId,
            String content, String attachmentName, boolean isEncrypted, String timestamp,
            MessageType messageType, String imageUrl, String conversationId) {
        this.id = id;
        this.projectId = projectId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
        this.attachmentPath = attachmentName;
        this.isEncrypted = isEncrypted;
        this.timestamp = timestamp;
        this.messageType = messageType != null ? messageType : MessageType.TEXT;
        this.imageUrl = imageUrl;
        this.conversationId = conversationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getAttachmentName() {
        return attachmentPath;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentPath = attachmentName;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSentAt() {
        return timestamp;
    }

    public void setSentAt(String sentAt) {
        this.timestamp = sentAt;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

        
    

    
        
    
        
    

    
        
    
        
    

    
        
    
        
    

    
        
    
        
    

    
        
    
