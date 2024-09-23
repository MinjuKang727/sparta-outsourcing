package com.sparta.spartaoutsourcing.order.repository;

import com.sparta.spartaoutsourcing.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"review", "review.reviewComment"})
    List<Order> findByStore_IdAndReviewIsNotNullAndReview_RatingBetween(Long store_id, Integer min, Integer max);
}
