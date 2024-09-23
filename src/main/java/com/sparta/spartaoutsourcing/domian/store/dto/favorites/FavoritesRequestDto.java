package com.sparta.spartaoutsourcing.domian.store.dto.favorites;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoritesRequestDto {

    private boolean isFavorite;
    private LocalDateTime createdAt;
}
