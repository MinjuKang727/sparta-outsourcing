package com.sparta.spartaoutsourcing.review.repository;

import com.sparta.spartaoutsourcing.review.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    boolean existsByReview_Id(Long review_id);
}
