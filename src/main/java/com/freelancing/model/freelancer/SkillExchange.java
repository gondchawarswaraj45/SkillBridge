package com.freelancing.model.freelancer;


import java.io.Serializable;

/**
 * Model representing a peer-to-peer Skill Barter / Exchange offer.
 * Mapped to SQLite table 'skill_exchange'.
 */
public class SkillExchange implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, REQUESTED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    private String id;
    private String offererId;
    private String offeredSkill;
    private String requestedSkill;
    private String description;
    private Status status = Status.OPEN;
    private String requesterId;
    private String requesterNote;
    private int hoursPerWeek = 3;
    private String createdAt;

    // Joined display fields
    private String offererName;
    private String requesterName;

    public SkillExchange() {
        this.status = Status.OPEN;
    }

    public SkillExchange(String id, String offererId, String offeredSkill, String requestedSkill, String description) {
        this();
        this.id = id;
        this.offererId = offererId;
        this.offeredSkill = offeredSkill;
        this.requestedSkill = requestedSkill;
        this.description = description;
    }

    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOffererId() { return offererId; }
    public void setOffererId(String offererId) { this.offererId = offererId; }

    public String getOfferedSkill() { return offeredSkill; }
    public void setOfferedSkill(String offeredSkill) { this.offeredSkill = offeredSkill; }

    public String getRequestedSkill() { return requestedSkill; }
    public void setRequestedSkill(String requestedSkill) { this.requestedSkill = requestedSkill; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status != null ? status : Status.OPEN; }

    public String getRequesterId() { return requesterId; }
    public void setRequesterId(String requesterId) { this.requesterId = requesterId; }

    public String getRequesterNote() { return requesterNote; }
    public void setRequesterNote(String requesterNote) { this.requesterNote = requesterNote; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getOffererName() { return offererName; }
    public void setOffererName(String offererName) { this.offererName = offererName; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
}
