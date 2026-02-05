package com.petify.petify.service;

import com.petify.petify.domain.Review;
import com.petify.petify.domain.User;
import com.petify.petify.domain.UserReview;
import com.petify.petify.dto.CreateReviewRequest;
import com.petify.petify.dto.ReviewDTO;
import com.petify.petify.repo.ReviewRepository;
import com.petify.petify.repo.UserReviewRepository;
import com.petify.petify.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final UserReviewRepository userReviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserReviewRepository userReviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userReviewRepository = userReviewRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create or update a review for a user
     * @param reviewerId the user ID of the reviewer
     * @param targetUserId the user ID being reviewed
     * @param request the review request with rating and comment
     * @return the created/updated review as DTO
     */
    @Transactional
    public ReviewDTO createOrUpdateReview(Long reviewerId, Long targetUserId, CreateReviewRequest request) {
        logger.info("Creating/updating review from user {} for user {}", reviewerId, targetUserId);

        // Validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            logger.error("Invalid rating: {}", request.getRating());
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Get reviewer
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> {
                    logger.error("Reviewer not found with ID: {}", reviewerId);
                    return new RuntimeException("Reviewer not found");
                });

        // Get target user
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    logger.error("Target user not found with ID: {}", targetUserId);
                    return new RuntimeException("Target user not found");
                });

        // Check if review already exists
        UserReview userReview = userReviewRepository.findByReviewReviewerUserIdAndTargetUserId(reviewerId, targetUserId)
                .orElse(null);

        Review review;
        if (userReview != null) {
            // Update existing review
            logger.info("Updating existing review for user {}", targetUserId);
            review = userReview.getReview();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setUpdatedAt(LocalDateTime.now());
        } else {
            // Create new review
            logger.info("Creating new review for user {}", targetUserId);
            review = new Review(reviewer, request.getRating(), request.getComment());
            review = reviewRepository.save(review);

            // Create user_reviews entry
            userReview = new UserReview(review, targetUserId);
            userReview = userReviewRepository.save(userReview);
            logger.info("UserReview entry created for review ID: {}, target user ID: {}", review.getReviewId(), targetUserId);
        }

        Review savedReview = reviewRepository.save(review);
        logger.info("Review saved with ID: {}", savedReview.getReviewId());

        return new ReviewDTO(savedReview);
    }

    /**
     * Get all reviews for a user
     * @param targetUserId the user ID being reviewed
     * @return list of reviews
     */
    public List<ReviewDTO> getReviewsByUser(Long targetUserId) {
        logger.info("Fetching reviews for user {}", targetUserId);

        userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userReviewRepository.findReviewsForTargetUser(targetUserId).stream()
                .map(ReviewDTO::new)
                .toList();
    }


    /**
     * Delete a review
     * @param reviewId the review ID
     * @param userId the user ID of the person deleting (must be reviewer)
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        logger.info("Deleting review {} by user {}", reviewId, userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review not found with ID: {}", reviewId);
                    return new RuntimeException("Review not found");
                });

        // Check if user is reviewer
        if (!review.getReviewer().getUserId().equals(userId)) {
            logger.error("User {} not authorized to delete review {}", userId, reviewId);
            throw new RuntimeException("You can only delete your own reviews");
        }

        // Delete user_review entry first (due to FK constraint)
        UserReview userReview = userReviewRepository.findById(reviewId).orElse(null);
        if (userReview != null) {
            userReviewRepository.delete(userReview);
        }

        // Then delete review
        reviewRepository.delete(review);
        logger.info("Review deleted successfully");
    }
}
