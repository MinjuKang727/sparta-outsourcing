package com.sparta.spartaoutsourcing.review.repository;

import com.sparta.spartaoutsourcing.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrder_Id(Long order_id);
}
