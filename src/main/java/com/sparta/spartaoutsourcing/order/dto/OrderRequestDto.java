package com.sparta.spartaoutsourcing.order.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private int quantity;
    private LocalTime orderTime;

}
