package com.freelancing.model;

import java.io.Serializable;

public class Proposal implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING, ACCEPTED, REJECTED, WITHDRAWN
    }

    private String id;
    private String projectId;
    private String projectTitle;
    private String freelancerId;
    private String freelancerName;
    private String coverLetter;
    private double bidAmount;
    private int estimatedDays;
    private Status status;
    private String createdAt;

    public Proposal() {}

    public Proposal(String id, String projectId, String projectTitle, String freelancerId, String freelancerName,
                    String coverLetter, double bidAmount, int estimatedDays, Status status, String createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.freelancerId = freelancerId;
        this.freelancerName = freelancerName;
        this.coverLetter = coverLetter;
        this.bidAmount = bidAmount;
        this.estimatedDays = estimatedDays;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getFreelancerId() { return freelancerId; }
    public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }

    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }

    public int getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(int estimatedDays) { this.estimatedDays = estimatedDays; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
