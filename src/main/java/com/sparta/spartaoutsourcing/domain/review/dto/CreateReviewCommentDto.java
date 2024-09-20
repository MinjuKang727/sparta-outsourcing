package com.sparta.spartaoutsourcing.domain.review.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewCommentDto {
    @NotEmpty
    String content;
}
