package com.sparta.spartaoutsourcing.review.service;

import com.sparta.spartaoutsourcing.order.entity.Order;
import com.sparta.spartaoutsourcing.order.repository.OrderRepository;
import com.sparta.spartaoutsourcing.review.entity.Review;
import com.sparta.spartaoutsourcing.review.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {
    final ReviewRepository reviewRepository;
    final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Review createReview(Long orderId, Long userId, Integer rating, String content) {
        if (reviewRepository.existsByOrder_Id(orderId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 리뷰를 작성하였습니다.");

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "해당하는 주문이 없습니다."));
        if (!order.getUser().getId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 한 주문에만 리뷰를 달 수 있습니다.");

        Review review = Review.builder().order(order).rating(rating).content(content).build();
        return reviewRepository.save(review);
    }
}
