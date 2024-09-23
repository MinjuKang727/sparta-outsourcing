package com.sparta.spartaoutsourcing.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewCommentResponseDto {
    Long id;
    String content;

    @Builder
    private ReviewCommentResponseDto(Long id, Integer rating, String content) {
        this.id = id;
        this.content = content;
    }
}
