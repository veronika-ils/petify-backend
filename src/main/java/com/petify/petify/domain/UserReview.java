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

    public UserReview() {}

    public UserReview(Review review, Long targetUserId) {
        this.review = review;        // DO NOT set reviewId
        this.targetUserId = targetUserId;
    }

    // getters/setters

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
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}

