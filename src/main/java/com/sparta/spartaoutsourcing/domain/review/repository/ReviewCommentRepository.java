package com.sparta.spartaoutsourcing.domain.review.repository;

import com.sparta.spartaoutsourcing.domain.review.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
