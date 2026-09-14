package com.freelancing.service.company;

import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ReviewDAO;
import com.freelancing.dao.freelancer.FreelancerProfileDAO;
import com.freelancing.model.common.User;
import com.freelancing.model.company.Rating;
import com.freelancing.model.company.Review;
import com.freelancing.model.freelancer.FreelancerProfile;
import com.freelancing.service.common.NotificationService;

import com.freelancing.db.DatabaseManager;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing reviews, client-freelancer ratings, and reputation scores in SQLite.
 */
public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final FreelancerProfileDAO freelancerProfileDAO;
    private final UserDAO userDAO;
    private final NotificationService notificationService;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
        this.freelancerProfileDAO = new FreelancerProfileDAO();
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
    }

    public ReviewService(ReviewDAO reviewDAO, FreelancerProfileDAO freelancerProfileDAO, UserDAO userDAO, NotificationService notificationService) {
        this.reviewDAO = reviewDAO;
        this.freelancerProfileDAO = freelancerProfileDAO;
        this.userDAO = userDAO;
        this.notificationService = notificationService;
    }

    /**
     * Submits a rating and review for a user, updating SQLite reviews table and profile aggregate metrics.
     */
    public Review submitReview(String reviewerUserId, String revieweeUserId, String projectId, double rating, String feedback) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0 stars.");
        }
        if (feedback == null || feedback.trim().isEmpty()) {
            throw new IllegalArgumentException("Review feedback text cannot be empty.");
        }
        if (reviewerUserId.equals(revieweeUserId)) {
            throw new IllegalArgumentException("Users cannot submit a review for themselves.");
        }

        String reviewId = "rev_" + UUID.randomUUID().toString().substring(0, 8);
        Review review = new Review();
        review.setId(reviewId);
        review.setProjectId(projectId);
        review.setReviewerId(reviewerUserId);
        review.setRevieweeId(revieweeUserId);
        review.setRating(rating);
        review.setFeedback(feedback.trim());

        boolean created = reviewDAO.create(review);
        if (!created) {
            throw new RuntimeException("Failed to persist review to SQLite database.");
        }

        // Fetch populated review with joins
        Review populated = reviewDAO.findById(reviewId);
        if (populated == null) populated = review;

        // If reviewee is a freelancer, update their profile rating aggregate
        FreelancerProfile fp = freelancerProfileDAO.findByUserId(revieweeUserId);
        if (fp != null) {
            double avgRating = reviewDAO.getAverageRatingForUser(revieweeUserId);
            int reviewCount = reviewDAO.getReviewCountForUser(revieweeUserId);
            freelancerProfileDAO.updateStats(fp.getId(), avgRating, reviewCount, fp.getCompletedProjects());
            fp.setRating(avgRating);
            fp.setTotalReviews(reviewCount);
            DatabaseManager.getInstance().getFreelancerProfiles().put(revieweeUserId, fp);
        }

        // Send notification to reviewee
        User reviewer = userDAO.findById(reviewerUserId);
        String reviewerName = reviewer != null ? reviewer.getUsername() : "A user";
        notificationService.sendNotification(revieweeUserId, "⭐ New Review Received!",
                reviewerName + " gave you " + String.format("%.1f", rating) + " stars: \"" + feedback.trim() + "\"");

        // Sync legacy Rating map in DatabaseManager
        Rating legacyRating = new Rating(reviewId, projectId, reviewerUserId, reviewerName, revieweeUserId, rating, feedback.trim(), populated.getCreatedAt());
        DatabaseManager.getInstance().getRatings().put(reviewId, legacyRating);
        DatabaseManager.getInstance().logActivity(reviewerName + " submitted a " + rating + "-star review for " + revieweeUserId);

        return populated;
    }

    public List<Review> getReviewsForUser(String userId) {
        return reviewDAO.findByRevieweeId(userId);
    }

    public List<Review> getReviewsForProject(String projectId) {
        return reviewDAO.findByProjectId(projectId);
    }

    public double getAverageRating(String userId) {
        return reviewDAO.getAverageRatingForUser(userId);
    }

    public int getReviewCount(String userId) {
        return reviewDAO.getReviewCountForUser(userId);
    }
}
