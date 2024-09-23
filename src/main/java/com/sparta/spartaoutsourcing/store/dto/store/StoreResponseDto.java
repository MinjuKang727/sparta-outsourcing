package com.sparta.spartaoutsourcing.store.dto.store;

import com.sparta.spartaoutsourcing.menu.dto.response.MenuResponseDto;
import com.sparta.spartaoutsourcing.store.entity.Store;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class StoreResponseDto {

    private Long id;

    private String storeName;

    private LocalTime openTime;

    private LocalTime closeTime;

    private boolean isFavorite;

    private String minOrderPrice;

    private List<MenuResponseDto> menus;




    public StoreResponseDto(Store store) {
        this.id = store.getId();
        this.storeName = store.getStoreName();
        this.openTime = store.getOpenTime();
        this.closeTime = store.getCloseTime();
        this.minOrderPrice = store.getMinOrderPrice();
        this.menus = store.getMenus().stream().map(MenuResponseDto::new).collect(Collectors.toList());
    }
}
