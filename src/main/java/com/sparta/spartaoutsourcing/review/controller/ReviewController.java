package com.sparta.spartaoutsourcing.review.controller;

import com.sparta.spartaoutsourcing.review.dto.CreateReviewCommentDto;
import com.sparta.spartaoutsourcing.review.dto.CreateReviewRequestDto;
import com.sparta.spartaoutsourcing.review.dto.ReviewResponseDto;
import com.sparta.spartaoutsourcing.review.entity.Review;
import com.sparta.spartaoutsourcing.review.entity.ReviewComment;
import com.sparta.spartaoutsourcing.review.service.ReviewCommentService;
import com.sparta.spartaoutsourcing.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {
    final ReviewService reviewService;
    final ReviewCommentService reviewCommentService;

    public ReviewController(ReviewService reviewService, ReviewCommentService reviewCommentService) {
        this.reviewService = reviewService;
        this.reviewCommentService = reviewCommentService;
    }

    @PostMapping("/orders/{orderId}/review")
    ResponseEntity<ReviewResponseDto> createReview(@PathVariable String orderId, @RequestBody @Valid CreateReviewRequestDto createReviewRequestDto) {
        Review review = reviewService.createReview(createReviewRequestDto.getRating(), createReviewRequestDto.getContent());
        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponseDto);
    }

    @PostMapping("/reviews/{reviewId}/comment")
    ResponseEntity createReviewComment(@PathVariable Long reviewId, @RequestBody @Valid CreateReviewCommentDto createReviewCommentDto) {
        ReviewComment reviewComment = reviewCommentService.createReviewComment(reviewId, createReviewCommentDto.getContent());

        return ResponseEntity.ok(null);
    }
}
