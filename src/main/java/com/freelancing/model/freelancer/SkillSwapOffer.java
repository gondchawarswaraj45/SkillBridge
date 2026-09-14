package com.freelancing.model.freelancer;

import java.io.Serializable;

/**
 * Model representing a peer-to-peer Skill Barter / Skill Exchange offer.
 */
public class SkillSwapOffer implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_PROGRESS, COMPLETED
    }

    private String id;
    private String authorId;
    private String authorName;
    private String authorTitle;
    private String offeredSkill;
    private String seekingSkill;
    private String experienceLevel;
    private int hoursPerWeek;
    private int creditsRequired;
    private Status status;
    private String description;
    private String createdDate;
    private double authorRating;

    public SkillSwapOffer() {
        this.status = Status.OPEN;
        this.creditsRequired = 2;
        this.authorRating = 4.9;
    }

    public SkillSwapOffer(String id, String authorId, String authorName, String authorTitle,
                          String offeredSkill, String seekingSkill, String experienceLevel,
                          int hoursPerWeek, int creditsRequired, Status status,
                          String description, String createdDate, double authorRating) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorTitle = authorTitle;
        this.offeredSkill = offeredSkill;
        this.seekingSkill = seekingSkill;
        this.experienceLevel = experienceLevel;
        this.hoursPerWeek = hoursPerWeek;
        this.creditsRequired = creditsRequired;
        this.status = status;
        this.description = description;
        this.createdDate = createdDate;
        this.authorRating = authorRating;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorTitle() { return authorTitle; }
    public void setAuthorTitle(String authorTitle) { this.authorTitle = authorTitle; }

    public String getOfferedSkill() { return offeredSkill; }
    public void setOfferedSkill(String offeredSkill) { this.offeredSkill = offeredSkill; }

    public String getSeekingSkill() { return seekingSkill; }
    public void setSeekingSkill(String seekingSkill) { this.seekingSkill = seekingSkill; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public int getCreditsRequired() { return creditsRequired; }
    public void setCreditsRequired(int creditsRequired) { this.creditsRequired = creditsRequired; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public double getAuthorRating() { return authorRating; }
    public void setAuthorRating(double authorRating) { this.authorRating = authorRating; }
}
