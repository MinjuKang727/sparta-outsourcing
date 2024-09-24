package com.sparta.spartaoutsourcing.store.controller;

import com.sparta.spartaoutsourcing.store.dto.favorites.FavoritesResponseDto;
import com.sparta.spartaoutsourcing.store.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    @PostMapping("/{userId}/{storeId}")
    public ResponseEntity<Void> addFavorite(@PathVariable("userId") Long userId, @PathVariable("storeId") Long storeId) {
        favoritesService.addFavorite(userId, storeId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoritesResponseDto>> getListFavorites(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(favoritesService.getListFavorites(userId));
    }
}
