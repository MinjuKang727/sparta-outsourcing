package com.sparta.spartaoutsourcing.order.dto;


import com.sparta.spartaoutsourcing.order.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStateRequestDto {

    private OrderState state;

}
