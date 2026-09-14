package com.freelancing.model.company;

import java.io.Serializable;

/**
 * Model representing a milestone deliverable submission in SkillBridge.
 * Mapped to SQLite table 'deliverables'.
 */
public class Deliverable implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        SUBMITTED, REVISION_REQUESTED, APPROVED
    }

    private String id;
    private String milestoneId;
    private String freelancerId;
    private String title;
    private String description;
    private String filePath;
    private Status status;
    private String submittedAt;

    // Joined display metadata
    private String milestoneTitle;
    private String freelancerName;

    public Deliverable() {
        this.status = Status.SUBMITTED;
    }

    public Deliverable(String id, String milestoneId, String freelancerId, String title, String description, String filePath) {
        this.id = id;
        this.milestoneId = milestoneId;
        this.freelancerId = freelancerId;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.status = Status.SUBMITTED;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMilestoneId() { return milestoneId; }
    public void setMilestoneId(String milestoneId) { this.milestoneId = milestoneId; }

    public String getFreelancerId() { return freelancerId; }
    public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public String getMilestoneTitle() { return milestoneTitle; }
    public void setMilestoneTitle(String milestoneTitle) { this.milestoneTitle = milestoneTitle; }

    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }
}
