package com.sparta.spartaoutsourcing.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    Long id;
    Integer rating;
    String content;

    @Builder
    private ReviewResponseDto(Long id, Integer rating, String content) {
        this.id = id;
        this.rating= rating;
        this.content = content;
    }
}
