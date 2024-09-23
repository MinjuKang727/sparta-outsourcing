package com.sparta.spartaoutsourcing.review.service;

import com.sparta.spartaoutsourcing.order.entity.Order;
import com.sparta.spartaoutsourcing.order.entity.OrderState;
import com.sparta.spartaoutsourcing.order.repository.OrderRepository;
import com.sparta.spartaoutsourcing.review.entity.Review;
import com.sparta.spartaoutsourcing.review.repository.ReviewRepository;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {
    final ReviewRepository reviewRepository;
    final OrderRepository orderRepository;
    final StoreRepository storeRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository, StoreRepository storeRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public Review createReview(Long orderId, Long userId, Integer rating, String content) {
        if (reviewRepository.existsByOrder_Id(orderId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 리뷰를 작성하였습니다.");

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "해당하는 주문이 없습니다."));

        if (!order.getUser().getId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 한 주문에만 리뷰를 달 수 있습니다.");

        if (!order.getState().equals(OrderState.DELIVERED))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "배달이 완료되어야지 리뷰를 작성할 수 있습니다.");

        Review review = Review.builder().order(order).rating(rating).content(content).build();
        return reviewRepository.save(review);
    }

    public List<Review> getStoreReviews(Long storeId, Integer min, Integer max) {
        if (storeRepository.existsById(storeId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 가게가 존재하지 않습니다.");

        return orderRepository.findByStore_Id(storeId).stream().map(Order::getReview).toList();
    }
}
