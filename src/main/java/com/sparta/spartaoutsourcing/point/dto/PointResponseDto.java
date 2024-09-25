package com.sparta.spartaoutsourcing.point.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PointResponseDto {
    Long id;
    LocalDateTime createAt;
    Integer amount;

    @Builder
    private PointResponseDto(Long id, LocalDateTime createAt, Integer amount) {
        this.id = id;
        this.createAt = createAt;
        this.amount = amount;
    }
}
