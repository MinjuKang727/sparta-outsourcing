package com.sparta.spartaoutsourcing.basket.controller;


import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.basket.dto.BasketRequestDto;
import com.sparta.spartaoutsourcing.basket.dto.BasketResponseDto;
import com.sparta.spartaoutsourcing.basket.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    // 장바구니에 추가
    @PostMapping("/baskets/menus/{menuId}")
    public void addBasket(
            @RequestBody BasketRequestDto dto,
            @PathVariable("menuId") Long menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        basketService.addBasket(userDetails.getUser(), menuId, dto);
    }

    // 장바구니 조회
    @GetMapping("/baskets")
    public ResponseEntity<List<BasketResponseDto>> getBasket(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<BasketResponseDto> responseDtos = basketService.getBasket(userDetails.getUser());
        return ResponseEntity.ok(responseDtos);
    }

    //장바구니 업데이트
    @PutMapping("/baskets/menus/{menuId}")
    public void updateBasket(
            @RequestBody BasketRequestDto dto,
            @PathVariable("menuId") Long menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        basketService.updateBasket(userDetails.getUser(), menuId, dto);
    }

    // 장바구니 삭제
    @DeleteMapping("/baskets")
    public void deleteBasket(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        basketService.deleteBasket(userDetails.getUser());
    }

}
