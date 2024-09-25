package com.sparta.spartaoutsourcing.store.dto.statistics;

import lombok.Data;

@Data
public class StatisticsResponseDto {

    private Long dailyCustomerCount;

    private Long monthlyCustomerCount;

    private Long dailySales;

    private Long monthlySales;

    public StatisticsResponseDto(Long dailyCustomers, Long monthlyCustomers, Long dailySales, Long monthlySales) {
        this.dailyCustomerCount = dailyCustomers;
        this.monthlyCustomerCount = monthlyCustomers;
        this.dailySales = dailySales;
        this.monthlySales = monthlySales;
    }

}
