package com.sparta.spartaoutsourcing.domain.review.service;

import com.sparta.spartaoutsourcing.domain.review.entity.Review;
import com.sparta.spartaoutsourcing.domain.review.entity.ReviewComment;
import com.sparta.spartaoutsourcing.domain.review.repository.ReviewCommentRepository;
import com.sparta.spartaoutsourcing.domain.review.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewCommentService {
    final ReviewCommentRepository reviewCommentRepository;
    final ReviewRepository reviewRepository;

    public ReviewCommentService(ReviewCommentRepository reviewCommentRepository, ReviewRepository reviewRepository) {
        this.reviewCommentRepository = reviewCommentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public ReviewComment createReviewComment(Long reviewId, String content) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "해당하는 리뷰가 존재하지 않습니다."));
        ReviewComment reviewComment = ReviewComment.builder().review(review).content(content).build();

        ReviewComment savedReviewComment = reviewCommentRepository.save(reviewComment);
        reviewRepository.save(review);

        return savedReviewComment;
    }
}
