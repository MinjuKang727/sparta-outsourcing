package com.sparta.spartaoutsourcing.order.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    @NotNull
    @Min(0)
    Integer quantity;

    private LocalTime orderTime;

    @NotNull
    @Min(0)
    Integer usedPoint;
}
