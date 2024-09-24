package com.sparta.spartaoutsourcing.store.service;

import com.sparta.spartaoutsourcing.order.repository.OrderRepository;
import com.sparta.spartaoutsourcing.store.dto.statistics.StatisticsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class StoreStatisticsService {

    private final OrderRepository orderRepository;

    public StatisticsResponseDto getStoreStatistics(Long storeId) {
//        일간 고객수
        Long dailyCustomers = orderRepository.countDailyCustomers(storeId);
//        월간 고객수
        Long monthlyCustomers = orderRepository.countMonthlyCustomers(storeId);
//        일간 매출
        Long dailySales = orderRepository.sumDailySales(storeId);
//        월간 매출
        Long monthlySales = orderRepository.sumMonthlySales(storeId);

        return new StatisticsResponseDto(dailyCustomers, monthlyCustomers, dailySales, monthlySales);
    }
}
