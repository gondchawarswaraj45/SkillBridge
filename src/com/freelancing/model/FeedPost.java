package com.freelancing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeedPost implements Serializable {
    private static final long serialVersionUID = 2L;

    public enum PostCategory {
        SHOWCASE, HIRING, DISCUSSION, FEEDBACK, ANNOUNCEMENT, JOB_POST
    }

    public enum PostStatus {
        ACTIVE, FLAGGED, REMOVED
    }

    private String id;
    private String authorId;
    private String authorName;
    private String authorRole; // "FREELANCER" or "CLIENT"
    private String title;
    private String content;
    private PostCategory category;
    private int likes;
    private Set<String> likedByUserIds = new HashSet<>();
    private List<FeedComment> comments = new ArrayList<>();
    private List<FeedBid> bids = new ArrayList<>();
    private Set<String> bidderIds = new HashSet<>();
    private PostStatus status;
    private String timestamp;
    private String linkedProjectId; // Links JOB_POST to a Project

    public FeedPost() {}

    public FeedPost(String id, String authorId, String authorName, String authorRole,
                    String title, String content, PostCategory category, PostStatus status, String timestamp) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.title = title;
        this.content = content;
        this.category = category;
        this.likes = 0;
        this.likedByUserIds = new HashSet<>();
        this.comments = new ArrayList<>();
        this.bids = new ArrayList<>();
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public PostCategory getCategory() { return category; }
    public void setCategory(PostCategory category) { this.category = category; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public Set<String> getLikedByUserIds() { return likedByUserIds; }
    public void setLikedByUserIds(Set<String> likedByUserIds) { this.likedByUserIds = likedByUserIds; }

    public List<FeedComment> getComments() { return comments; }
    public void setComments(List<FeedComment> comments) { this.comments = comments; }

    public List<FeedBid> getBids() { return bids; }
    public void setBids(List<FeedBid> bids) { this.bids = bids; }

    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getLinkedProjectId() { return linkedProjectId; }
    public void setLinkedProjectId(String linkedProjectId) { this.linkedProjectId = linkedProjectId; }

    public void toggleLike(String userId) {
        if (likedByUserIds.contains(userId)) {
            likedByUserIds.remove(userId);
            likes = Math.max(0, likes - 1);
        } else {
            likedByUserIds.add(userId);
            likes++;
        }
    }

    public void addComment(FeedComment comment) {
        comments.add(comment);
    }

    public void addBid(FeedBid bid) {
        bids.add(bid);
        bidderIds.add(bid.getFreelancerId());
    }

    /** O(1) check using HashSet instead of O(n) linear scan */
    public boolean hasUserBid(String userId) {
        return bidderIds.contains(userId);
    }

    // Inner class for comments
    public static class FeedComment implements Serializable {
        private static final long serialVersionUID = 1L;

        private String commentId;
        private String authorId;
        private String authorName;
        private String content;
        private String timestamp;

        public FeedComment() {}

        public FeedComment(String commentId, String authorId, String authorName, String content, String timestamp) {
            this.commentId = commentId;
            this.authorId = authorId;
            this.authorName = authorName;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getCommentId() { return commentId; }
        public void setCommentId(String commentId) { this.commentId = commentId; }

        public String getAuthorId() { return authorId; }
        public void setAuthorId(String authorId) { this.authorId = authorId; }

        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    // Inner class for bids on JOB_POST
    public static class FeedBid implements Serializable {
        private static final long serialVersionUID = 1L;

        private String bidId;
        private String freelancerId;
        private String freelancerName;
        private double bidAmount;
        private int estimatedDays;
        private String coverLetter;
        private List<String> freelancerSkills;
        private String timestamp; // Exact date+time when bid was placed

        public FeedBid() {}

        public FeedBid(String bidId, String freelancerId, String freelancerName,
                       double bidAmount, int estimatedDays, String coverLetter,
                       List<String> freelancerSkills, String timestamp) {
            this.bidId = bidId;
            this.freelancerId = freelancerId;
            this.freelancerName = freelancerName;
            this.bidAmount = bidAmount;
            this.estimatedDays = estimatedDays;
            this.coverLetter = coverLetter;
            this.freelancerSkills = freelancerSkills != null ? freelancerSkills : new ArrayList<>();
            this.timestamp = timestamp;
        }


        public String getBidId() { return bidId; }
        public void setBidId(String bidId) { this.bidId = bidId; }

        public String getFreelancerId() { return freelancerId; }
        public void setFreelancerId(String freelancerId) { this.freelancerId = freelancerId; }

        public String getFreelancerName() { return freelancerName; }
        public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

        public double getBidAmount() { return bidAmount; }
        public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }

        public int getEstimatedDays() { return estimatedDays; }
        public void setEstimatedDays(int estimatedDays) { this.estimatedDays = estimatedDays; }

        public String getCoverLetter() { return coverLetter; }
        public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

        public List<String> getFreelancerSkills() { return freelancerSkills; }
        public void setFreelancerSkills(List<String> freelancerSkills) { this.freelancerSkills = freelancerSkills; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

}
