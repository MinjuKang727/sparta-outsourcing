package com.sparta.spartaoutsourcing.store.controller;

import com.sparta.spartaoutsourcing.store.dto.statistics.StatisticsResponseDto;
import com.sparta.spartaoutsourcing.store.service.StoreStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class StoreStatisticsController {

    private final StoreStatisticsService statisticsService;

    @GetMapping("/{storeId}/statistics")
    public ResponseEntity<StatisticsResponseDto> getDailyStoreStatistics(@PathVariable("storeId") Long storeId) {
        StatisticsResponseDto statistics = statisticsService.getStoreStatistics(storeId);
        return ResponseEntity.ok(statistics);
    }
}
