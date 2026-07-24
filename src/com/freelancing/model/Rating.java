package com.freelancing.model;

import java.io.Serializable;

public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String projectId;
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private double score;
    private String reviewText;
    private String timestamp;

    public Rating() {}

    public Rating(String id, String projectId, String fromUserId, String fromUserName, String toUserId, double score, String reviewText, String timestamp) {
        this.id = id;
        this.projectId = projectId;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.toUserId = toUserId;
        this.score = score;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
