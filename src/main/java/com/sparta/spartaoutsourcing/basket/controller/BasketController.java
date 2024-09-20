package com.sparta.spartaoutsourcing.basket.controller;


import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.basket.dto.BasketRequestDto;
import com.sparta.spartaoutsourcing.basket.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    // 장바구니에 추가
    @PostMapping("/baskets/menus/{menuId}")
    public void addBasket(
            @RequestBody BasketRequestDto dto,
            @PathVariable Long menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        basketService.addBasket(userDetails.getUser(), menuId, dto);
    }

}
