package com.sparta.spartaoutsourcing.store.dto.favorites;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class FavoritesRequestDto {

    private String storeName;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String minOrderPrice;

    private boolean isFavorite;

    private LocalDateTime createdAt;
}
