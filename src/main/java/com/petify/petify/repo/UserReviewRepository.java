package com.petify.petify.repo;

import com.petify.petify.domain.Review;
import com.petify.petify.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {


    List<UserReview> findByTargetUserId(Long targetUserId);

    Optional<UserReview> findByReviewReviewerUserIdAndTargetUserId(Long reviewerId, Long targetUserId);

    @Query("""
        select r
        from UserReview ur
        join ur.review r
        join fetch r.reviewer
        where ur.targetUserId = :targetUserId
        and r.isDeleted = false
        order by r.createdAt desc
    """)
    List<Review> findReviewsForTargetUser(@Param("targetUserId") Long targetUserId);
    Optional<UserReview> findTopByReviewReviewerUserIdAndTargetUserIdAndReviewIsDeletedFalseOrderByReviewCreatedAtDesc(
            Long reviewerId, Long targetUserId
    );

}
