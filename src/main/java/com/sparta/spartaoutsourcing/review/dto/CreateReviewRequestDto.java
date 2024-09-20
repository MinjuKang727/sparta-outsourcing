package com.sparta.spartaoutsourcing.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequestDto {
    @Min(0)
    @Max(5)
    @NotNull
    Integer rating;

    @NotEmpty
    String content;
}
