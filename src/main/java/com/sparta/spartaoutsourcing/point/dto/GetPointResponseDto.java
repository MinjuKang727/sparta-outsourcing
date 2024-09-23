package com.sparta.spartaoutsourcing.point.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetPointResponseDto {
    Integer sum;


    List<PointResponseDto> pointHistory;

    @Builder
    private GetPointResponseDto(Integer sum, List<PointResponseDto> pointHistory) {
        this.sum = sum;
        this.pointHistory = pointHistory;
    }
}
