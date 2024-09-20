package com.sparta.spartaoutsourcing.domain.review.repository;

import com.sparta.spartaoutsourcing.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
