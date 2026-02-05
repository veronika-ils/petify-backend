package com.petify.petify.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "user_reviews")
public class UserReview {

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_user_id", insertable = false, updatable = false)
    private User targetUser;

    // Constructors
    public UserReview() {
    }

    public UserReview(Review review, Long targetUserId) {
        this.review = review;
        this.reviewId = review.getReviewId();
        this.targetUserId = targetUserId;
    }

    // Getters and Setters
    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
        if (review != null) {
            this.reviewId = review.getReviewId();
        }
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }
}
