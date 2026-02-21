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
        logger.info("==== START createReview ====");
        // Validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            logger.error(" VALIDATION FAILED: Invalid rating: {}", request.getRating());
            throw new RuntimeException("Rating must be between 1 and 5");
        }


        // Check if reviewer exists
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> {
                    logger.error(" Reviewer not found with ID: {}", reviewerId);
                    return new RuntimeException("Reviewer not found");
                });

        // Check if target user exists
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    logger.error(" Target user not found with ID: {}", targetUserId);
                    return new RuntimeException("Target user not found");
                });

        // Check if review already exists and is not deleted
        logger.info("Checking if reviewer {} has already reviewed user {}", reviewerId, targetUserId);
        var existingReview = userReviewRepository.findTopByReviewReviewerUserIdAndTargetUserIdAndReviewIsDeletedFalseOrderByReviewCreatedAtDesc(reviewerId, targetUserId);

        if (existingReview.isPresent()) {
            Review existingReviewEntity = existingReview.get().getReview();
            logger.info("Found existing review with ID: {}", existingReviewEntity.getReviewId());
            logger.info("Existing review isDeleted status: {}", existingReviewEntity.getIsDeleted());

            if (!existingReviewEntity.getIsDeleted()) {
                logger.error(" User {} has already reviewed user {} and review is NOT deleted", reviewerId, targetUserId);
                throw new RuntimeException("You have already reviewed this user");
            } else {
                logger.info(" User {} has a deleted review for user {} - can create a new one", reviewerId, targetUserId);
            }
        } else {
            logger.info(" No existing review found - safe to create new review");
        }

        // Create Review entity
        Review review = new Review(reviewer, request.getRating(), request.getComment());


        // Save Review to database with flush
        review = reviewRepository.saveAndFlush(review);
        logger.info("Review ID after save: {}", review.getReviewId());

        if (review.getReviewId() == null) {
            logger.error(" CRITICAL: Review ID is NULL after save!");
            throw new RuntimeException("Failed to save review - ID is null");
        }

        // Create UserReview entry
        UserReview userReview = new UserReview();
        logger.info("Setting Review on UserReview (will copy ID via @MapsId)...");
        userReview.setReview(review);
        logger.info("UserReview reviewId after setReview: {}", userReview.getReviewId());

        userReview.setTargetUserId(targetUserId);

        // Save UserReview to database with flush
        userReview = userReviewRepository.saveAndFlush(userReview);
        logger.info(" UserReview saved successfully");

        // Create and return DTO
        ReviewDTO reviewDTO = new ReviewDTO(review);
        logger.info(" ReviewDTO created successfully");

        return reviewDTO;
    }


    /**
     * Get all reviews for a user
     * @param targetUserId the user ID being reviewed
     * @return list of reviews sorted by date (newest first)
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByUser(Long targetUserId) {
        logger.info("=== START getReviewsByUser ===");


        // Verify user exists
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    logger.error(" User not found with ID: {}", targetUserId);
                    return new RuntimeException("User not found");
                });

        // Fetch reviews using optimized query
        logger.info("Fetching all reviews for user {} using optimized query...", targetUserId);
        List<Review> reviews = userReviewRepository.findReviewsForTargetUser(targetUserId);


        // Convert to DTOs
        logger.info("Converting {} reviews to ReviewDTOs...", reviews.size());
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(r -> {
                    logger.debug("Converting review ID {} from reviewer {}", r.getReviewId(), r.getReviewer().getUsername());
                    return new ReviewDTO(r);
                })
                .collect(Collectors.toList());

        logger.info("=== END getReviewsByUser - SUCCESS ===");
        return reviewDTOs;
    }

    /**
     * Delete a review (soft delete - marks as deleted but keeps the record)
     * @param reviewId the review ID
     * @param userId the user ID of the person deleting (must be reviewer)
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        logger.info("=== START deleteReview (SOFT DELETE) ===");


        // Fetch review
        logger.info("Fetching review with ID: {}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error(" Review not found with ID: {}", reviewId);
                    return new RuntimeException("Review not found");
                });
        logger.info(" Review found: reviewer={}, rating={}", review.getReviewer().getUsername(), review.getRating());

        // Check authorization
        if (!review.getReviewer().getUserId().equals(userId)) {
            logger.error(" User {} is not authorized to delete review {}. Reviewer is {}", userId, reviewId, review.getReviewer().getUserId());
            throw new RuntimeException("You can only delete your own reviews");
        }

        // Soft delete: mark as deleted instead of physically removing
        review.setIsDeleted(true);
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);

        logger.info("=== END deleteReview - SUCCESS ===");
    }
}
