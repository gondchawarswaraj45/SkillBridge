package com.freelancing.service;

import com.freelancing.model.common.CommunityComment;
import com.freelancing.model.common.CommunityPost;
import com.freelancing.model.common.Notification;
import com.freelancing.model.common.User;
import com.freelancing.service.common.CommunityService;
import com.freelancing.service.common.NotificationService;

import com.freelancing.db.DatabaseConnection;
import com.freelancing.db.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CommunityServiceTest {

    private static CommunityService communityService;
    private static NotificationService notificationService;

    private User authorUser;
    private User commenterUser;

    @BeforeAll
    public static void initDatabase() {
        DatabaseInitializer.initialize();
        communityService = new CommunityService();
        notificationService = new NotificationService();
    }

    @BeforeEach
    public void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        authorUser = new User();
        authorUser.setId("usr_auth_" + suffix);
        authorUser.setUsername("author_" + suffix);
        authorUser.setEmail("author_" + suffix + "@test.com");
        authorUser.setPassword("password");
        authorUser.setRole(User.Role.FREELANCER);

        commenterUser = new User();
        commenterUser.setId("usr_comm_" + suffix);
        commenterUser.setUsername("comm_" + suffix);
        commenterUser.setEmail("comm_" + suffix + "@test.com");
        commenterUser.setPassword("password");
        commenterUser.setRole(User.Role.CLIENT);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertUser = "INSERT INTO users (id, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                ps.setString(1, authorUser.getId());
                ps.setString(2, authorUser.getUsername());
                ps.setString(3, authorUser.getEmail());
                ps.setString(4, "hash");
                ps.setString(5, authorUser.getRole().name());
                ps.addBatch();

                ps.setString(1, commenterUser.getId());
                ps.setString(2, commenterUser.getUsername());
                ps.setString(3, commenterUser.getEmail());
                ps.setString(4, "hash");
                ps.setString(5, commenterUser.getRole().name());
                ps.addBatch();

                ps.executeBatch();
            }
        }
    }

    @Test
    public void testCreatePostAndQueryByCategory() {
        CommunityPost post1 = communityService.createPost(
                authorUser.getId(),
                "Architecture Tips for JavaFX Desktop Apps",
                "Keep UI logic cleanly separated from background workers and use reactive models.",
                CommunityPost.CAT_DISCUSSIONS
        );
        assertNotNull(post1.getId());
        assertEquals("Architecture Tips for JavaFX Desktop Apps", post1.getTitle());

        CommunityPost post2 = communityService.createPost(
                authorUser.getId(),
                "Showcasing Dark Theme Charting Suite",
                "Check out our new dark mode charts built with CSS and Canvas.",
                CommunityPost.CAT_SHOWCASE
        );
        assertNotNull(post2.getId());

        List<CommunityPost> discussions = communityService.getPosts(CommunityPost.CAT_DISCUSSIONS, null, authorUser.getId());
        assertTrue(discussions.stream().anyMatch(p -> p.getId().equals(post1.getId())));
        assertFalse(discussions.stream().anyMatch(p -> p.getId().equals(post2.getId())));

        List<CommunityPost> allPosts = communityService.getPosts("ALL", null, authorUser.getId());
        assertTrue(allPosts.stream().anyMatch(p -> p.getId().equals(post1.getId())));
        assertTrue(allPosts.stream().anyMatch(p -> p.getId().equals(post2.getId())));
    }

    @Test
    public void testToggleLikePost() {
        CommunityPost post = communityService.createPost(
                authorUser.getId(),
                "High Performance SQLite",
                "Discussion on WAL mode and PreparedStatements.",
                CommunityPost.CAT_DISCUSSIONS
        );

        // 1. First like by commenterUser
        boolean liked = communityService.toggleLike(post.getId(), commenterUser.getId());
        assertTrue(liked, "First toggle should like the post");

        CommunityPost fetched = communityService.getPostById(post.getId(), commenterUser.getId());
        assertEquals(1, fetched.getLikesCount());
        assertTrue(fetched.isLikedByCurrentUser());

        // 2. Second toggle by same user unlikes
        boolean unliked = communityService.toggleLike(post.getId(), commenterUser.getId());
        assertFalse(unliked, "Second toggle should unlike the post");

        fetched = communityService.getPostById(post.getId(), commenterUser.getId());
        assertEquals(0, fetched.getLikesCount());
        assertFalse(fetched.isLikedByCurrentUser());
    }

    @Test
    public void testAddCommentAndNotification() {
        CommunityPost post = communityService.createPost(
                authorUser.getId(),
                "Pair Programming on JavaFX",
                "Looking to pair on UI components this weekend.",
                CommunityPost.CAT_DISCUSSIONS
        );

        // Commenter adds a comment
        CommunityComment comment = communityService.addComment(
                post.getId(),
                commenterUser.getId(),
                "I'd love to join! Count me in for Saturday."
        );
        assertNotNull(comment.getId());
        assertEquals("I'd love to join! Count me in for Saturday.", comment.getContent());

        List<CommunityComment> comments = communityService.getComments(post.getId());
        assertEquals(1, comments.size());
        assertEquals(commenterUser.getUsername(), comments.get(0).getAuthorName());

        // Author of post should have received a notification
        List<Notification> notifs = notificationService.getUserNotifications(authorUser.getId());
        assertTrue(notifs.stream().anyMatch(n -> "COMMUNITY".equals(n.getType()) && n.getMessage().contains(commenterUser.getUsername())));
    }

    @Test
    public void testReportPost() {
        CommunityPost post = communityService.createPost(
                authorUser.getId(),
                "Sample Post to Report",
                "Content needing review.",
                CommunityPost.CAT_FEEDBACK
        );

        boolean reported = communityService.reportPost(post.getId(), commenterUser.getId(), "Inappropriate content or spam");
        assertTrue(reported, "Report submission should succeed");

        // Reporter receives confirmation notification
        List<Notification> notifs = notificationService.getUserNotifications(commenterUser.getId());
        assertTrue(notifs.stream().anyMatch(n -> n.getTitle().contains("Report Received")));
    }

    @Test
    public void testKeywordSearch() {
        String uniqueTag = "Tag_" + UUID.randomUUID().toString().substring(0, 6);
        communityService.createPost(authorUser.getId(), "Post with " + uniqueTag, "Description here", CommunityPost.CAT_GENERAL);
        communityService.createPost(authorUser.getId(), "Another post", "Body containing " + uniqueTag, CommunityPost.CAT_GENERAL);

        List<CommunityPost> results = communityService.getPosts("ALL", uniqueTag, authorUser.getId());
        assertEquals(2, results.size());
    }
}
