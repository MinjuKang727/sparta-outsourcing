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
        this.storeName = basket.getStore().getName();
        this.menuName = basket.getMenu().getMenuName();
        this.quantity = basket.getQuantity();
        this.totalPrice = basket.getQuantity()*basket.getMenu().getPrice();
    }

}


