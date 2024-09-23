package com.sparta.spartaoutsourcing.review.controller;

import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.review.dto.CreateReviewCommentDto;
import com.sparta.spartaoutsourcing.review.dto.CreateReviewRequestDto;
import com.sparta.spartaoutsourcing.review.dto.ReviewCommentResponseDto;
import com.sparta.spartaoutsourcing.review.dto.ReviewResponseDto;
import com.sparta.spartaoutsourcing.review.entity.Review;
import com.sparta.spartaoutsourcing.review.entity.ReviewComment;
import com.sparta.spartaoutsourcing.review.service.ReviewCommentService;
import com.sparta.spartaoutsourcing.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
