package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Model representing a financial transaction in SkillBridge.
 * Mapped to SQLite table 'transactions'.
 * Types: PAYMENT, EARNING, WITHDRAWAL, REFUND, ESCROW_DEPOSIT
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        PAYMENT, EARNING, WITHDRAWAL, REFUND, ESCROW_DEPOSIT
    }

    public enum Status {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    private String id;
    private String contractId;
    private String milestoneId;
    private String senderId;
    private String receiverId;
    private double amount;
    private Type type;
    private Status status;
    private String reference;
    private String createdAt;

    // Joined display metadata
    private String senderUsername;
    private String receiverUsername;
    private String projectTitle;
    private String milestoneTitle;

    public Transaction() {
        this.status = Status.COMPLETED;
    }

    public Transaction(String id, String contractId, String milestoneId, String senderId,
                       String receiverId, double amount, Type type, Status status,
                       String reference, String createdAt) {
        this.id = id;
        this.contractId = contractId;
        this.milestoneId = milestoneId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.type = type;
        this.status = status != null ? status : Status.COMPLETED;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getMilestoneId() { return milestoneId; }
    public void setMilestoneId(String milestoneId) { this.milestoneId = milestoneId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getMilestoneTitle() { return milestoneTitle; }
    public void setMilestoneTitle(String milestoneTitle) { this.milestoneTitle = milestoneTitle; }

    public boolean isPayment() { return type == Type.PAYMENT; }
    public boolean isEarning() { return type == Type.EARNING; }
    public boolean isWithdrawal() { return type == Type.WITHDRAWAL; }
}
