package com.sparta.spartaoutsourcing.store.dto.favorites;

import com.sparta.spartaoutsourcing.store.entity.Favorites;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class FavoritesResponseDto {
    private Long id;

    private String storeName;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String minOrderPrice;

    private boolean isFavorite;

    private LocalDateTime createdAt;

    public FavoritesResponseDto(Favorites favorites) {
        this.id = favorites.getId();
        this.storeName = favorites.getStores().getStoreName();
        this.openTime = favorites.getStores().getOpenTime();
        this.closeTime = favorites.getStores().getCloseTime();
        this.minOrderPrice = favorites.getStores().getMinOrderPrice();
        this.isFavorite = favorites.isFavorite();
        this.createdAt = favorites.getCreatedAt();
    }
}
