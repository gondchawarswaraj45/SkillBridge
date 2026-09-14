package com.freelancing.model.admin;

import java.io.Serializable;

public class Dispute implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_REVIEW, UNDER_REVIEW, RESOLVED, RESOLVED_CLIENT, RESOLVED_FREELANCER, DISMISSED, CLOSED
    }

    private String id;
    private String contractId;
    private String contractTitle;
    private String projectId; // alias for contractId
    private String projectTitle; // alias for contractTitle
    private String raisedById;
    private String raisedByName;
    private String reason;
    private String description;
    private Status status = Status.OPEN;
    private String resolution;
    private String resolutionNotes; // alias for resolution
    private String resolvedBy;
    private String resolvedByName;
    private String createdAt;

    public Dispute() {}

    public Dispute(String id, String contractId, String contractTitle, String raisedById, String raisedByName, String reason, Status status, String createdAt) {
        this.id = id;
        this.contractId = contractId;
        this.projectId = contractId;
        this.contractTitle = contractTitle;
        this.projectTitle = contractTitle;
        this.raisedById = raisedById;
        this.raisedByName = raisedByName;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContractId() { return contractId != null ? contractId : projectId; }
    public void setContractId(String contractId) {
        this.contractId = contractId;
        this.projectId = contractId;
    }

    public String getContractTitle() { return contractTitle != null ? contractTitle : projectTitle; }
    public void setContractTitle(String contractTitle) {
        this.contractTitle = contractTitle;
        this.projectTitle = contractTitle;
    }

    public String getProjectId() { return getContractId(); }
    public void setProjectId(String projectId) { setContractId(projectId); }

    public String getProjectTitle() { return getContractTitle(); }
    public void setProjectTitle(String projectTitle) { setContractTitle(projectTitle); }

    public String getRaisedById() { return raisedById; }
    public void setRaisedById(String raisedById) { this.raisedById = raisedById; }

    public String getRaisedByName() { return raisedByName; }
    public void setRaisedByName(String raisedByName) { this.raisedByName = raisedByName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDetails() { return description; }
    public void setDetails(String details) { this.description = details; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getResolution() { return resolution != null ? resolution : resolutionNotes; }
    public void setResolution(String resolution) {
        this.resolution = resolution;
        this.resolutionNotes = resolution;
    }

    public String getResolutionNotes() { return getResolution(); }
    public void setResolutionNotes(String resolutionNotes) { setResolution(resolutionNotes); }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

    public String getResolvedByName() { return resolvedByName; }
    public void setResolvedByName(String resolvedByName) { this.resolvedByName = resolvedByName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
