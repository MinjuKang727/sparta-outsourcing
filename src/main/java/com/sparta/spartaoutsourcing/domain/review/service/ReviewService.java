package com.sparta.spartaoutsourcing.domain.review.service;

import com.sparta.spartaoutsourcing.domain.review.entity.Review;
import com.sparta.spartaoutsourcing.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review createReview(Integer rating, String content) {
        Review review = Review.builder().rating(rating).content(content).build();
        return reviewRepository.save(review);
    }
}
