package com.sparta.spartaoutsourcing.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewWithCommentResponseDto {
    Long id;
    Integer rating;
    String content;
    ReviewCommentResponseDto reviewComment;

    @Builder
    private ReviewWithCommentResponseDto(Long id, Integer rating, String content, ReviewCommentResponseDto reviewComment) {
        this.id = id;
        this.rating = rating;
        this.content = content;
        this.reviewComment = reviewComment;
    }
}
