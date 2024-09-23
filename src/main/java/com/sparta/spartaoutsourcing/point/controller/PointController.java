package com.sparta.spartaoutsourcing.point.controller;

import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.point.dto.GetPointResponseDto;
import com.sparta.spartaoutsourcing.point.dto.PointResponseDto;
import com.sparta.spartaoutsourcing.point.entity.Point;
import com.sparta.spartaoutsourcing.point.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PointController {
    final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/users/points")
    ResponseEntity<GetPointResponseDto> getPoints(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Point> pointList = pointService.getPoints(userDetails.getUser().getId());
        List<PointResponseDto> pointResponseDtos = pointList.stream().map(v -> PointResponseDto.builder()
                .id(v.getId())
                .createAt(v.getCreatedAt())
                .amount(v.getAmount()).build()
        ).toList();
        GetPointResponseDto getPointResponseDto = GetPointResponseDto.builder()
                .sum(pointList.stream().mapToInt(Point::getAmount).sum())
                .pointHistory(pointResponseDtos).build();
        return ResponseEntity.ok(getPointResponseDto);
    }
}
