package com.freelancing.model.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Freelancing Project in SkillBridge.
 * Mapped to SQLite table 'projects' and join table 'project_skills'.
 */
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_PROGRESS, AWARDED, COMPLETED, CANCELLED, DISPUTED
    }

    public enum BudgetType {
        FIXED, HOURLY
    }

    public enum ExperienceLevel {
        ENTRY, INTERMEDIATE, EXPERT
    }

    private String id;
    private String clientId;
    private String clientName;
    private String title;
    private String category;
    private String description;
    private List<String> requiredSkills = new ArrayList<>();
    private String budgetType = "FIXED";
    private double budgetMin = 0.0;
    private double budgetMax = 0.0;
    private double budget = 0.0;
    private String deadline;
    private String experienceLevel = "INTERMEDIATE";
    private Status status = Status.OPEN;
    private String awardedFreelancerId;
    private String awardedFreelancerName;
    private String createdAt;
    private int proposalsCount = 0;

    public Project() {}

    /** Full Constructor with SQLite schema attributes */
    public Project(String id, String clientId, String clientName, String title, String category, String description,
                   List<String> requiredSkills, String budgetType, double budgetMin, double budgetMax,
                   String deadline, String experienceLevel, Status status,
                   String awardedFreelancerId, String awardedFreelancerName, String createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.title = title;
        this.category = category;
        this.description = description;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.budgetType = budgetType != null ? budgetType.toUpperCase() : "FIXED";
        this.budgetMin = budgetMin;
        this.budgetMax = budgetMax;
        this.budget = budgetMax > 0 ? budgetMax : budgetMin;
        this.deadline = deadline;
        this.experienceLevel = experienceLevel != null ? experienceLevel.toUpperCase() : "INTERMEDIATE";
        this.status = status != null ? status : Status.OPEN;
        this.awardedFreelancerId = awardedFreelancerId;
        this.awardedFreelancerName = awardedFreelancerName;
        this.createdAt = createdAt;
    }

    /** Compatibility constructor for legacy code */
    public Project(String id, String clientId, String clientName, String title, String category, String description,
                   List<String> requiredSkills, double budget, String deadline, Status status,
                   String assignedFreelancerId, String assignedFreelancerName, String createdAt) {
        this(id, clientId, clientName, title, category, description, requiredSkills, "FIXED",
             budget > 0 ? budget * 0.8 : 0.0, budget, deadline, "INTERMEDIATE", status,
             assignedFreelancerId, assignedFreelancerName, createdAt);
    }

    // Getters and Setters
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
    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
    }

    public String getBudgetType() { return budgetType != null ? budgetType : "FIXED"; }
    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType != null ? budgetType.toUpperCase() : "FIXED";
    }

    public double getBudgetMin() { return budgetMin; }
    public void setBudgetMin(double budgetMin) { this.budgetMin = budgetMin; }

    public double getBudgetMax() { return budgetMax; }
    public void setBudgetMax(double budgetMax) {
        this.budgetMax = budgetMax;
        if (this.budget <= 0.0) this.budget = budgetMax;
    }

    public double getBudget() {
        if (budget > 0) return budget;
        if (budgetMax > 0) return budgetMax;
        return budgetMin;
    }
    public void setBudget(double budget) {
        this.budget = budget;
        if (this.budgetMax <= 0.0) this.budgetMax = budget;
    }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getExperienceLevel() { return experienceLevel != null ? experienceLevel : "INTERMEDIATE"; }
    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel != null ? experienceLevel.toUpperCase() : "INTERMEDIATE";
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public void setStatus(String statusStr) {
        try {
            this.status = Status.valueOf(statusStr.toUpperCase());
        } catch (Exception e) {
            this.status = Status.OPEN;
        }
    }

    public String getAwardedFreelancerId() { return awardedFreelancerId; }
    public void setAwardedFreelancerId(String awardedFreelancerId) { this.awardedFreelancerId = awardedFreelancerId; }

    public String getAwardedFreelancerName() { return awardedFreelancerName; }
    public void setAwardedFreelancerName(String awardedFreelancerName) { this.awardedFreelancerName = awardedFreelancerName; }

    // Aliases for assignedFreelancer
    public String getAssignedFreelancerId() { return awardedFreelancerId; }
    public void setAssignedFreelancerId(String assignedFreelancerId) { this.awardedFreelancerId = assignedFreelancerId; }

    public String getAssignedFreelancerName() { return awardedFreelancerName; }
    public void setAssignedFreelancerName(String assignedFreelancerName) { this.awardedFreelancerName = assignedFreelancerName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getProposalsCount() { return proposalsCount; }
    public void setProposalsCount(int proposalsCount) { this.proposalsCount = proposalsCount; }

    public String getBudgetDisplay() {
        if ("HOURLY".equalsIgnoreCase(budgetType)) {
            if (budgetMin > 0 && budgetMax > 0 && budgetMin != budgetMax) {
                return String.format("$%.0f - $%.0f/hr", budgetMin, budgetMax);
            } else {
                return String.format("$%.0f/hr", budgetMax > 0 ? budgetMax : (budgetMin > 0 ? budgetMin : budget));
            }
        } else {
            if (budgetMin > 0 && budgetMax > 0 && budgetMin != budgetMax) {
                return String.format("$%,.0f - $%,.0f (Fixed)", budgetMin, budgetMax);
            } else {
                return String.format("$%,.0f (Fixed)", budgetMax > 0 ? budgetMax : (budgetMin > 0 ? budgetMin : budget));
            }
        }
    }
}
