package com.sparta.spartaoutsourcing.basket.service;


import com.sparta.spartaoutsourcing.basket.dto.BasketRequestDto;
import com.sparta.spartaoutsourcing.basket.dto.BasketResponseDto;
import com.sparta.spartaoutsourcing.basket.entity.Basket;
import com.sparta.spartaoutsourcing.basket.repository.BasketRepository;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasketService {

    private final BasketRepository basketRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;


    public void addBasket(User user, Long menuId, BasketRequestDto dto) {

        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new NullPointerException("해당 메뉴가 존재하지 않습니다."));
        Store store = menu.getStore();

        List<Basket> basketList = basketRepository.findByUserId(user.getId());

        // 장바구니에 있는 메뉴가 동일한 가게인지 확인
        if (!basketList.isEmpty()) {
            if (!basketList.get(0).getStore().getId().equals(menu.getStore().getId())) {
                throw new IllegalArgumentException("장바구니에는 한 가게의 메뉴만 담을 수 있습니다.");
            }
            // 이미 들어있는 메뉴면 개수만 추가
            for (Basket basket : basketList) {
                if (basket.getMenu() == menu) {
                    int quantity = basket.getQuantity() + dto.getQuantity();
                    basket.update(quantity);
                    basketRepository.save(basket);
                    return;
                }
            }
        }
        Basket basketSaved = new Basket(user, menu, store, dto.getQuantity());
        basketRepository.save(basketSaved);

    }

    @Transactional
    public void updateBasket(User user, Long menuId, BasketRequestDto dto) {

        basketRepository.deleteByUserId(user.getId());

        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new NullPointerException("해당 메뉴가 존재하지 않습니다."));
        Store store = menu.getStore();

        Basket basketSaved = new Basket(user, menu, store, dto.getQuantity());
        basketRepository.save(basketSaved);

    }

    public List<BasketResponseDto> getBasket(User user) {
        List<Basket> basketList = basketRepository.findByUserId(user.getId());

        List<BasketResponseDto> basketResponseDtoList = new ArrayList<>();

        for (Basket basket : basketList) {
            BasketResponseDto basketResponseDto = new BasketResponseDto(basket);
            basketResponseDtoList.add(basketResponseDto);
        }
        return basketResponseDtoList;
    }

    public void deleteBasket(User user) {
        basketRepository.deleteByUserId(user.getId());
    }
}

