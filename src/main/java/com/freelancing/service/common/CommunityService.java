package com.freelancing.service.common;

import com.freelancing.dao.common.CommunityCommentDAO;
import com.freelancing.dao.common.CommunityPostDAO;
import com.freelancing.model.common.CommunityComment;
import com.freelancing.model.common.CommunityPost;

import com.freelancing.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Service orchestrating community posts, discussions, showcases, hiring posts,
 * comments, likes, and post moderation reports.
 */
public class CommunityService {
    private static final Logger LOGGER = Logger.getLogger(CommunityService.class.getName());

    private final CommunityPostDAO postDAO;
    private final CommunityCommentDAO commentDAO;
    private final NotificationService notifService;

    public CommunityService() {
        this.postDAO = new CommunityPostDAO();
        this.commentDAO = new CommunityCommentDAO();
        this.notifService = new NotificationService();
    }

    public CommunityService(CommunityPostDAO postDAO, CommunityCommentDAO commentDAO, NotificationService notifService) {
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.notifService = notifService;
    }

    public CommunityPost createPost(String authorId, String title, String content, String category) {
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID is required");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Post title is required");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content is required");
        }

        CommunityPost post = new CommunityPost();
        post.setAuthorId(authorId.trim());
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setCategory(category != null ? category.trim().toUpperCase() : CommunityPost.CAT_DISCUSSIONS);

        CommunityPost created = postDAO.create(post);
        LOGGER.info("Community post created: " + created.getId() + " by author: " + authorId);
        return created;
    }

    public List<CommunityPost> getPosts(String categoryFilter, String keyword, String currentUserId) {
        return postDAO.findAll(categoryFilter, keyword, currentUserId);
    }

    public CommunityPost getPostById(String postId, String currentUserId) {
        return postDAO.findById(postId, currentUserId);
    }

    public List<CommunityPost> getUserPosts(String authorId, String currentUserId) {
        return postDAO.findByAuthorId(authorId, currentUserId);
    }

    public boolean toggleLike(String postId, String userId) {
        if (postId == null || userId == null) return false;
        return postDAO.toggleLike(postId, userId);
    }

    public CommunityComment addComment(String postId, String authorId, String content) {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID is required");
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID is required");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        CommunityComment c = new CommunityComment();
        c.setPostId(postId.trim());
        c.setAuthorId(authorId.trim());
        c.setContent(content.trim());

        CommunityComment created = commentDAO.create(c);

        // Notify post author if commenter is someone else
        CommunityPost post = postDAO.findById(postId, authorId);
        if (post != null && post.getAuthorId() != null && !post.getAuthorId().equals(authorId)) {
            String commenterName = created.getAuthorName() != null ? created.getAuthorName() : "A community member";
            notifService.sendNotification(
                    post.getAuthorId(),
                    "💬 New Comment on Your Post",
                    commenterName + " commented on '" + post.getTitle() + "': " + (content.length() > 60 ? content.substring(0, 57) + "..." : content),
                    "COMMUNITY",
                    postId
            );
        }

        return created;
    }

    public List<CommunityComment> getComments(String postId) {
        return commentDAO.findByPostId(postId);
    }

    public void deletePost(String postId) {
        postDAO.delete(postId);
    }

    public void deleteComment(String commentId) {
        commentDAO.delete(commentId);
    }

    /**
     * Reports a post for moderation review, storing the record in the SQLite reports table.
     */
    public boolean reportPost(String postId, String reporterUserId, String reason) {
        if (postId == null || reporterUserId == null || reason == null || reason.trim().isEmpty()) {
            return false;
        }

        CommunityPost post = postDAO.findById(postId, reporterUserId);
        String postTitle = post != null ? post.getTitle() : "Post " + postId;

        String reportId = "rep_" + UUID.randomUUID().toString().substring(0, 8);
        String sql = "INSERT INTO reports (id, title, report_type, generated_by, content, created_at) "
                + "VALUES (?, ?, 'COMMUNITY_POST', ?, ?, datetime('now'));";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reportId);
            ps.setString(2, "Flagged Post: " + postTitle);
            ps.setString(3, reporterUserId);
            ps.setString(4, "Reason: " + reason.trim() + " | PostID: " + postId);
            ps.executeUpdate();

            notifService.sendNotification(
                    reporterUserId,
                    "🚩 Post Report Received",
                    "Thank you for reporting. Our moderation team has received your report regarding '" + postTitle + "'.",
                    "SYSTEM",
                    postId
            );
            return true;
        } catch (SQLException e) {
            LOGGER.severe("Failed to file post report: " + e.getMessage());
            return false;
        }
    }
}
