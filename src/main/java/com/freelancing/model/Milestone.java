package com.freelancing.model;

import java.io.Serializable;

public class Milestone implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING, IN_PROGRESS, SUBMITTED, APPROVED, PAID
    }

    private String id;
    private String projectId;
    private String title;
    private String description;
    private double amount;
    private String dueDate;
    private Status status;
    private String deliverableFile;
    private String deliverableNotes;

    public Milestone() {}

    public Milestone(String id, String projectId, String title, String description, double amount, String dueDate, Status status) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDeliverableFile() { return deliverableFile; }
    public void setDeliverableFile(String deliverableFile) { this.deliverableFile = deliverableFile; }

    public String getDeliverableNotes() { return deliverableNotes; }
    public void setDeliverableNotes(String deliverableNotes) { this.deliverableNotes = deliverableNotes; }
}
