package com.freelancing.model.company;

import java.io.Serializable;

/**
 * Model representing a Proposal submitted by a Freelancer for a Project.
 * Matches the SQLite 'proposals' schema with enriched display metadata.
 */
public class Proposal implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        SUBMITTED,
        PENDING,       // Backwards-compatible alias for SUBMITTED
        SHORTLISTED,
        ACCEPTED,
        REJECTED,
        WITHDRAWN
    }

    private String id;
    private String projectId;
    private String freelancerId; // References freelancer_profiles.id
    private double bidAmount;
    private int deliveryDays;
    private String coverLetter;
    private Status status = Status.SUBMITTED;
    private double aiMatchScore; // 0.0 - 100.0
    private String createdAt;

    // Joined / UI Display fields
    private String projectTitle;
    private double projectBudget;
    private String projectBudgetType;
    private String projectDeadline;
    private String clientId;
    private String clientName;
    private String freelancerUserId;
    private String freelancerName;
    private String freelancerTitle;
    private double freelancerRating = 5.0;
    private String freelancerAvatar;
    private int freelancerTotalReviews;
    private int freelancerCompletedProjects;

    public Proposal() {}

    public Proposal(String id, String projectId, String projectTitle, String freelancerId, String freelancerName,
                    String coverLetter, double bidAmount, int deliveryDays, Status status, String createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.freelancerId = freelancerId;
        this.freelancerName = freelancerName;
        this.coverLetter = coverLetter;
        this.bidAmount = bidAmount;
        this.deliveryDays = deliveryDays;
        this.status = status != null ? status : Status.SUBMITTED;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getFreelancerId() { return freelancerId; }
    public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }

    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }

    public int getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(int deliveryDays) { this.deliveryDays = deliveryDays; }

    // Alias for backwards compatibility
    public int getEstimatedDays() { return deliveryDays; }
    public void setEstimatedDays(int estimatedDays) { this.deliveryDays = estimatedDays; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getAiMatchScore() { return aiMatchScore; }
    public void setAiMatchScore(double aiMatchScore) { this.aiMatchScore = aiMatchScore; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Display fields
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public double getProjectBudget() { return projectBudget; }
    public void setProjectBudget(double projectBudget) { this.projectBudget = projectBudget; }

    public String getProjectBudgetType() { return projectBudgetType; }
    public void setProjectBudgetType(String projectBudgetType) { this.projectBudgetType = projectBudgetType; }

    public String getProjectDeadline() { return projectDeadline; }
    public void setProjectDeadline(String projectDeadline) { this.projectDeadline = projectDeadline; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getFreelancerUserId() { return freelancerUserId; }
    public void setFreelancerUserId(String freelancerUserId) { this.freelancerUserId = freelancerUserId; }

    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    public String getFreelancerTitle() { return freelancerTitle; }
    public void setFreelancerTitle(String freelancerTitle) { this.freelancerTitle = freelancerTitle; }

    public double getFreelancerRating() { return freelancerRating; }
    public void setFreelancerRating(double freelancerRating) { this.freelancerRating = freelancerRating; }

    public String getFreelancerAvatar() { return freelancerAvatar; }
    public void setFreelancerAvatar(String freelancerAvatar) { this.freelancerAvatar = freelancerAvatar; }

    public int getFreelancerTotalReviews() { return freelancerTotalReviews; }
    public void setFreelancerTotalReviews(int freelancerTotalReviews) { this.freelancerTotalReviews = freelancerTotalReviews; }

    public int getFreelancerCompletedProjects() { return freelancerCompletedProjects; }
    public void setFreelancerCompletedProjects(int freelancerCompletedProjects) { this.freelancerCompletedProjects = freelancerCompletedProjects; }

    public String getStatusDisplayName() {
        if (status == null) return "SUBMITTED";
        switch (status) {
            case PENDING:
            case SUBMITTED: return "Submitted";
            case SHORTLISTED: return "Shortlisted";
            case ACCEPTED: return "Accepted";
            case REJECTED: return "Declined";
            case WITHDRAWN: return "Withdrawn";
            default: return status.name();
        }
    }
}
