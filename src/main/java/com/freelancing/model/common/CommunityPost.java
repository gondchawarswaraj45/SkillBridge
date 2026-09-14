package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Model representing a Community Forum / Discussion post in SkillBridge.
 * Mapped to SQLite table 'community_posts'.
 */
public class CommunityPost implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String CAT_DISCUSSIONS = "DISCUSSIONS";
    public static final String CAT_QUESTIONS = "QUESTIONS";
    public static final String CAT_SHOWCASE = "SHOWCASE";
    public static final String CAT_HIRING = "HIRING";
    public static final String CAT_FEEDBACK = "FEEDBACK";
    public static final String CAT_GENERAL = "GENERAL";

    private String id;
    private String authorId;
    private String title;
    private String content;
    private String category;
    private int likesCount;
    private String createdAt;

    // Joined & computed display fields
    private String authorName;
    private String authorRole;
    private int commentsCount;
    private boolean likedByCurrentUser;

    public CommunityPost() {
        this.category = CAT_DISCUSSIONS;
        this.likesCount = 0;
        this.commentsCount = 0;
        this.likedByCurrentUser = false;
    }

    public CommunityPost(String id, String authorId, String title, String content, String category) {
        this();
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.category = category != null ? category.toUpperCase() : CAT_DISCUSSIONS;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getLikes() { return likesCount; }
    public void setLikes(int likes) { this.likesCount = likes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}
