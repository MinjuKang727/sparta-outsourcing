package com.sparta.spartaoutsourcing.domain.review.controller;

import com.sparta.spartaoutsourcing.domain.review.dto.CreateReviewRequestDto;
import com.sparta.spartaoutsourcing.domain.review.entity.Review;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {
    @PostMapping("/orders/{orderId}/review")
    ResponseEntity createReview(@PathVariable String orderId, @RequestBody @Valid CreateReviewRequestDto createReviewRequestDto) {
        return ResponseEntity.ok(null);
    }


}
