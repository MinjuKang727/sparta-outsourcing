package com.sparta.spartaoutsourcing.order.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStateResponseDto {

    private final Long orderId;
    private final String storeName;
    private final String state;

}
