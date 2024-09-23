package com.sparta.spartaoutsourcing.basket.dto;


import com.sparta.spartaoutsourcing.basket.entity.Basket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasketResponseDto {

    private final String storeName;
    private final String menuName;
    private final int quantity;
    private final int totalPrice;

    public BasketResponseDto(Basket basket) {
        this.storeName = basket.getStore().getStoreName();
        this.menuName = basket.getMenu().getName();
        this.quantity = basket.getQuantity();
        this.totalPrice = (int) (basket.getQuantity()*basket.getMenu().getPrice());
    }

}


