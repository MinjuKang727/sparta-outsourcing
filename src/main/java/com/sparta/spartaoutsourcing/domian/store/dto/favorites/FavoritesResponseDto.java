package com.sparta.spartaoutsourcing.domian.store.dto.favorites;

import com.sparta.spartaoutsourcing.domian.store.entity.Favorites;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoritesResponseDto {

    private boolean isFavorite;
    private LocalDateTime createdAt;

    public FavoritesResponseDto(Favorites favorites) {
        this.isFavorite = favorites.isFavorite();
        this.createdAt = favorites.getCreatedAt();
    }
}
