package com.sparta.spartaoutsourcing.store.dto.statistics;

import lombok.Data;

@Data
public class StatisticsRequestDto {

    private Long dailyCustomerCount;

    private Long monthlyCustomerCount;

    private Long dailySales;

    private Long monthlySales;
}
