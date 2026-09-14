package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Model representing a simulated WebRTC video call session.
 */
public class VideoCall implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        INITIATED, ACTIVE, CONNECTED, ENDED, REJECTED, CANCELLED
    }

    private String id;
    private String conversationId;
    private String initiatorId;
    private String initiatorName;
    private String receiverId;
    private String receiverName;
    private String startTime;
    private String endTime;
    private long durationSeconds;
    private Status status = Status.INITIATED;
    private boolean cameraEnabled = true;
    private boolean micEnabled = true;
    private boolean screenShareEnabled = false;

    public VideoCall() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getInitiatorId() { return initiatorId; }
    public void setInitiatorId(String initiatorId) { this.initiatorId = initiatorId; }

    public String getInitiatorName() { return initiatorName; }
    public void setInitiatorName(String initiatorName) { this.initiatorName = initiatorName; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isCameraEnabled() { return cameraEnabled; }
    public void setCameraEnabled(boolean cameraEnabled) { this.cameraEnabled = cameraEnabled; }

    public boolean isMicEnabled() { return micEnabled; }
    public void setMicEnabled(boolean micEnabled) { this.micEnabled = micEnabled; }

    public boolean isScreenShareEnabled() { return screenShareEnabled; }
    public void setScreenShareEnabled(boolean screenShareEnabled) { this.screenShareEnabled = screenShareEnabled; }
}
