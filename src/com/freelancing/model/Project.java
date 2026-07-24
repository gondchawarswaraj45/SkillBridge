package com.freelancing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_PROGRESS, COMPLETED, CANCELLED, DISPUTED
    }

    private String id;
    private String clientId;
    private String clientName;
    private String title;
    private String category;
    private String description;
    private List<String> requiredSkills = new ArrayList<>();
    private double budget;
    private String deadline;
    private Status status;
    private String assignedFreelancerId;
    private String assignedFreelancerName;
    private String createdAt;

    public Project() {}

    public Project(String id, String clientId, String clientName, String title, String category, String description,
                   List<String> requiredSkills, double budget, String deadline, Status status,
                   String assignedFreelancerId, String assignedFreelancerName, String createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.title = title;
        this.category = category;
        this.description = description;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.budget = budget;
        this.deadline = deadline;
        this.status = status;
        this.assignedFreelancerId = assignedFreelancerId;
        this.assignedFreelancerName = assignedFreelancerName;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAssignedFreelancerId() { return assignedFreelancerId; }
    public void setAssignedFreelancerId(String assignedFreelancerId) { this.assignedFreelancerId = assignedFreelancerId; }

    public String getAssignedFreelancerName() { return assignedFreelancerName; }
    public void setAssignedFreelancerName(String assignedFreelancerName) { this.assignedFreelancerName = assignedFreelancerName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
