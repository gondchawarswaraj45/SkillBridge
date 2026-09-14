package com.freelancing.model.common;

import java.io.Serializable;

/**
 * Model representing a comment on a CommunityPost.
 * Mapped to SQLite table 'community_comments'.
 */
public class CommunityComment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String postId;
    private String authorId;
    private String content;
    private String createdAt;

    // Joined display fields
    private String authorName;
    private String authorRole;

    public CommunityComment() {}

    public CommunityComment(String id, String postId, String authorId, String content) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }
}
