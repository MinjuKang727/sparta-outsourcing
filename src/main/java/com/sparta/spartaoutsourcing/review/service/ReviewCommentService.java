package com.sparta.spartaoutsourcing.review.service;

import com.sparta.spartaoutsourcing.review.entity.Review;
import com.sparta.spartaoutsourcing.review.entity.ReviewComment;
import com.sparta.spartaoutsourcing.review.repository.ReviewCommentRepository;
import com.sparta.spartaoutsourcing.review.repository.ReviewRepository;
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
    public ReviewComment createReviewComment(Long reviewId, Long userId, String content) {
        if (reviewCommentRepository.existsByReview_Id(reviewId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 댓글을 작성하였습니다.");

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "해당하는 리뷰가 존재하지 않습니다."));

        if (!review.getOrder().getStore().getUsers().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "가게 주인만 댓글을 달 수 있습니다.");
        }

        ReviewComment reviewComment = ReviewComment.builder().review(review).content(content).build();

        ReviewComment savedReviewComment = reviewCommentRepository.save(reviewComment);
        reviewRepository.save(review);

        return savedReviewComment;
    }
}
