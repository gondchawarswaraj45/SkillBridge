package com.freelancing.model.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Contract Milestone in SkillBridge.
 * Mapped to SQLite table 'milestones'.
 */
public class Milestone implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING, IN_PROGRESS, SUBMITTED, REVISION_REQUESTED, APPROVED, PAID
    }

    private String id;
    private String contractId;
    private String title;
    private String description;
    private double amount;
    private String deadline;
    private Status status;
    private int sequenceOrder = 1;
    private String createdAt;

    // Joined metadata & latest deliverable
    private String projectTitle;
    private String clientName;
    private String freelancerName;
    private List<Deliverable> deliverables = new ArrayList<>();
    private Deliverable latestDeliverable;

    // Backward compatibility fields
    private String projectId;
    private String deliverableFile;
    private String deliverableNotes;

    public Milestone() {
        this.status = Status.PENDING;
    }

    public Milestone(String id, String contractId, String title, String description, double amount, String deadline, Status status) {
        this.id = id;
        this.contractId = contractId;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.deadline = deadline;
        this.status = status != null ? status : Status.PENDING;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(int sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    public List<Deliverable> getDeliverables() { return deliverables; }
    public void setDeliverables(List<Deliverable> deliverables) { this.deliverables = deliverables != null ? deliverables : new ArrayList<>(); }

    public Deliverable getLatestDeliverable() { return latestDeliverable; }
    public void setLatestDeliverable(Deliverable latestDeliverable) { this.latestDeliverable = latestDeliverable; }

    // Backward compatibility aliases
    public String getProjectId() { return projectId != null ? projectId : contractId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getDueDate() { return deadline; }
    public void setDueDate(String dueDate) { this.deadline = dueDate; }

    public String getDeliverableFile() { return deliverableFile; }
    public void setDeliverableFile(String deliverableFile) { this.deliverableFile = deliverableFile; }

    public String getDeliverableNotes() { return deliverableNotes; }
    public void setDeliverableNotes(String deliverableNotes) { this.deliverableNotes = deliverableNotes; }
}
