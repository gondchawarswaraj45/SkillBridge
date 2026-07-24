package com.freelancing.model;

import java.io.Serializable;

public class Dispute implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_REVIEW, RESOLVED_CLIENT, RESOLVED_FREELANCER, CLOSED
    }

    private String id;
    private String projectId;
    private String projectTitle;
    private String raisedById;
    private String raisedByName;
    private String reason;
    private Status status;
    private String resolutionNotes;
    private String createdAt;

    public Dispute() {}

    public Dispute(String id, String projectId, String projectTitle, String raisedById, String raisedByName, String reason, Status status, String createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.raisedById = raisedById;
        this.raisedByName = raisedByName;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getRaisedById() { return raisedById; }
    public void setRaisedById(String raisedById) { this.raisedById = raisedById; }

    public String getRaisedByName() { return raisedByName; }
    public void setRaisedByName(String raisedByName) { this.raisedByName = raisedByName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
