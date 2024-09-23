package com.sparta.spartaoutsourcing.review.controller;

import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.review.dto.*;
import com.sparta.spartaoutsourcing.review.entity.Review;
import com.sparta.spartaoutsourcing.review.entity.ReviewComment;
import com.sparta.spartaoutsourcing.review.service.ReviewCommentService;
import com.sparta.spartaoutsourcing.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
    final ReviewService reviewService;
    final ReviewCommentService reviewCommentService;

    public ReviewController(ReviewService reviewService, ReviewCommentService reviewCommentService) {
        this.reviewService = reviewService;
        this.reviewCommentService = reviewCommentService;
    }

    @PostMapping("/orders/{orderId}/review")
    ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long orderId, @RequestBody @Valid CreateReviewRequestDto createReviewRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Review review = reviewService.createReview(orderId, userDetails.getUser().getId(), createReviewRequestDto.getRating(), createReviewRequestDto.getContent());
        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponseDto);
    }

    @PostMapping("/reviews/{reviewId}/comment")
    ResponseEntity<ReviewCommentResponseDto> createReviewComment(@PathVariable Long reviewId, @RequestBody @Valid CreateReviewCommentDto createReviewCommentDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ReviewComment reviewComment = reviewCommentService.createReviewComment(reviewId, userDetails.getUser().getId(), createReviewCommentDto.getContent());
        ReviewCommentResponseDto reviewCommentResponseDto = ReviewCommentResponseDto.builder()
                .id(reviewComment.getId())
                .content(reviewComment.getContent())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewCommentResponseDto);
    }

    @GetMapping("/stores/{storeId}/review")
    ResponseEntity<List<ReviewWithCommentResponseDto>> getStoreReviews(@PathVariable Long storeId, @RequestParam(required = false, name = "min") Integer _min, @RequestParam(required = false, name = "max") Integer _max) {
        Integer min = _min == null ? Integer.MIN_VALUE : _min;
        Integer max = _max == null ? Integer.MAX_VALUE : _max;
        List<Review> reviews = reviewService.getStoreReviews(storeId, min, max);
        List<ReviewWithCommentResponseDto> reviewWithCommentResponseDtos = reviews.stream().map(v -> ReviewWithCommentResponseDto.builder()
                .id(v.getId())
                .rating(v.getRating())
                .content(v.getContent())
                .reviewComment(v.getReviewComment() != null ? ReviewCommentResponseDto.builder()
                        .id(v.getReviewComment().getId())
                        .content(v.getContent()).build() : null
                ).build()
        ).toList();
        return ResponseEntity.ok(reviewWithCommentResponseDtos);
    }
}
