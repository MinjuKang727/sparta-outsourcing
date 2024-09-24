package com.sparta.spartaoutsourcing.order.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BasketOrderRequestDto {
    @NotNull
    @Min(0)
    Integer usedPoint;
}
