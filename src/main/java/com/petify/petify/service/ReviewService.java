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
     * Create a new review for a user
     * @param reviewerId the user ID of the reviewer
     * @param targetUserId the user ID being reviewed
     * @param request the review request with rating and comment
     * @return the created review as DTO
     */
    @Transactional
    public ReviewDTO createReview(Long reviewerId, Long targetUserId, CreateReviewRequest request) {
        logger.info("Reviewer ID: {}", reviewerId);
        logger.info("Target User ID: {}", targetUserId);
        logger.info("Rating: {}", request.getRating());
        logger.info("Comment: {}", request.getComment());

        // Validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            logger.error(" VALIDATION FAILED: Invalid rating: {}", request.getRating());
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        logger.info(" Rating validation passed");

        // Check if reviewer exists
        logger.info("Fetching reviewer with ID: {}", reviewerId);
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> {
                    logger.error(" Reviewer not found with ID: {}", reviewerId);
                    return new RuntimeException("Reviewer not found");
                });
        logger.info(" Reviewer found: {} ({})", reviewer.getUsername(), reviewer.getUserId());

        // Check if target user exists
        logger.info("Fetching target user with ID: {}", targetUserId);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    logger.error(" Target user not found with ID: {}", targetUserId);
                    return new RuntimeException("Target user not found");
                });
        logger.info(" Target user found: {} ({})", targetUser.getUsername(), targetUser.getUserId());

        // Check if review already exists
        logger.info("Checking if reviewer {} has already reviewed user {}", reviewerId, targetUserId);
        userReviewRepository.findByReviewReviewerUserIdAndTargetUserId(reviewerId, targetUserId)
                .ifPresent(ur -> {
                    logger.error(" User {} has already reviewed user {}", reviewerId, targetUserId);
                    throw new RuntimeException("You have already reviewed this user");
                });
        logger.info(" No existing review found - safe to create new review");

        // Create Review entity
        logger.info("Creating Review entity...");
        Review review = new Review(reviewer, request.getRating(), request.getComment());
        logger.info("Review entity created (not yet persisted)");
        logger.info("Review: rating={}, comment={}", review.getRating(), review.getComment());

        // Save Review to database with flush
        logger.info("Saving Review to database with saveAndFlush...");
        review = reviewRepository.saveAndFlush(review);
        logger.info(" Review saved successfully");
        logger.info("Review ID after save: {}", review.getReviewId());

        if (review.getReviewId() == null) {
            logger.error(" CRITICAL: Review ID is NULL after save!");
            throw new RuntimeException("Failed to save review - ID is null");
        }
        logger.info(" Review ID is valid: {}", review.getReviewId());

        // Create UserReview entry
        //logger.info("Creating UserReview entity...");
        UserReview userReview = new UserReview();
        logger.info("UserReview created (empty)");

        logger.info("Setting Review on UserReview (will copy ID via @MapsId)...");
        userReview.setReview(review);
       // logger.info(" Review set on UserReview");
        logger.info("UserReview reviewId after setReview: {}", userReview.getReviewId());

        logger.info("Setting targetUserId to {}", targetUserId);
        userReview.setTargetUserId(targetUserId);
        //logger.info(" Target user ID set");

        // Save UserReview to database with flush
        //logger.info("Saving UserReview to database with saveAndFlush...");
        userReview = userReviewRepository.saveAndFlush(userReview);
        logger.info(" UserReview saved successfully");
        logger.info("UserReview reviewId: {}, targetUserId: {}", userReview.getReviewId(), userReview.getTargetUserId());

        // Create and return DTO
       // logger.info("Creating ReviewDTO from Review...");
        ReviewDTO reviewDTO = new ReviewDTO(review);
        logger.info(" ReviewDTO created successfully");
        logger.info("ReviewDTO: id={}, reviewer={}, rating={}", reviewDTO.getReviewId(), reviewDTO.getReviewerId(), reviewDTO.getRating());

        return reviewDTO;
    }


    /**
     * Get all reviews for a user
     * @param targetUserId the user ID being reviewed
     * @return list of reviews sorted by date (newest first)
     */
    public List<ReviewDTO> getReviewsByUser(Long targetUserId) {
        logger.info("=== START getReviewsByUser ===");
        logger.info("Target User ID: {}", targetUserId);

        // Verify user exists
        logger.info("Checking if user {} exists...", targetUserId);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    logger.error("❌ User not found with ID: {}", targetUserId);
                    return new RuntimeException("User not found");
                });
        logger.info("✅ User found: {} ({})", targetUser.getUsername(), targetUser.getUserId());

        // Fetch reviews using optimized query
        logger.info("Fetching all reviews for user {} using optimized query...", targetUserId);
        List<Review> reviews = userReviewRepository.findReviewsForTargetUser(targetUserId);
        logger.info("✅ Found {} reviews for user {}", reviews.size(), targetUserId);

        // Convert to DTOs
        logger.info("Converting {} reviews to ReviewDTOs...", reviews.size());
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(r -> {
                    logger.debug("Converting review ID {} from reviewer {}", r.getReviewId(), r.getReviewer().getUsername());
                    return new ReviewDTO(r);
                })
                .collect(Collectors.toList());
        logger.info("✅ Successfully converted {} reviews to DTOs", reviewDTOs.size());

        logger.info("=== END getReviewsByUser - SUCCESS ===");
        return reviewDTOs;
    }

    /**
     * Delete a review
     * @param reviewId the review ID
     * @param userId the user ID of the person deleting (must be reviewer)
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        logger.info("=== START deleteReview ===");
        logger.info("Review ID: {}", reviewId);
        logger.info("User ID: {}", userId);

        // Fetch review
        logger.info("Fetching review with ID: {}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("❌ Review not found with ID: {}", reviewId);
                    return new RuntimeException("Review not found");
                });
        logger.info("✅ Review found: reviewer={}, rating={}", review.getReviewer().getUsername(), review.getRating());

        // Check authorization
        logger.info("Checking if user {} is the reviewer of review {}", userId, reviewId);
        if (!review.getReviewer().getUserId().equals(userId)) {
            logger.error("❌ User {} is not authorized to delete review {}. Reviewer is {}", userId, reviewId, review.getReviewer().getUserId());
            throw new RuntimeException("You can only delete your own reviews");
        }
        logger.info("✅ User {} is authorized to delete this review", userId);

        // Delete user_review entry first (due to FK constraint)
        logger.info("Deleting UserReview entry for review ID: {}", reviewId);
        UserReview userReview = userReviewRepository.findById(reviewId).orElse(null);
        if (userReview != null) {
            logger.info("✅ Found UserReview entry, deleting...");
            userReviewRepository.delete(userReview);
            logger.info("✅ UserReview deleted");
        } else {
            logger.warn("⚠️  No UserReview entry found for review ID: {}", reviewId);
        }

        // Delete review
        logger.info("Deleting Review with ID: {}", reviewId);
        reviewRepository.delete(review);
        logger.info("✅ Review deleted successfully");

        logger.info("=== END deleteReview - SUCCESS ===");
    }
}
