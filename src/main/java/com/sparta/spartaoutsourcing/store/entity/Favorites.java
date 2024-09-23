package com.sparta.spartaoutsourcing.store.entity;

import com.sparta.spartaoutsourcing.store.dto.favorites.FavoritesRequestDto;
import com.sparta.spartaoutsourcing.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorites_id")
    private Long id;

    @Column(name = "is_favorite")
    private boolean isFavorite;

    @Column(name ="created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store stores;


    public Favorites(User user, Store store, FavoritesRequestDto favoritesRequestDto) {
        this.users = user;
        this.stores = store;
        this.isFavorite = favoritesRequestDto.isFavorite();
        this.createdAt = LocalDateTime.now();
    }

    public void activateFavorites() {
        this.isFavorite = false;
    }
}
