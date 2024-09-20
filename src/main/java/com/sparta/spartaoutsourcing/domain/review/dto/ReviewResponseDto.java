package com.sparta.spartaoutsourcing.domain.review.dto;

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
