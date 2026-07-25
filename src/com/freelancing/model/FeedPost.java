package com.freelancing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeedPost implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum PostCategory {
        SHOWCASE, HIRING, DISCUSSION, FEEDBACK, ANNOUNCEMENT
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
    private PostStatus status;
    private String timestamp;

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

    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

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
}
