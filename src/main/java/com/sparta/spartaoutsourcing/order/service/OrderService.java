package com.sparta.spartaoutsourcing.order.service;


import com.sparta.spartaoutsourcing.basket.entity.Basket;
import com.sparta.spartaoutsourcing.basket.repository.BasketRepository;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.order.dto.OrderRequestDto;
import com.sparta.spartaoutsourcing.order.dto.OrderResponseDto;
import com.sparta.spartaoutsourcing.order.dto.OrderStateRequestDto;
import com.sparta.spartaoutsourcing.order.dto.OrderStateResponseDto;
import com.sparta.spartaoutsourcing.order.entity.Order;
import com.sparta.spartaoutsourcing.order.entity.OrderState;
import com.sparta.spartaoutsourcing.order.repository.OrderRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final BasketRepository basketRepository;

    // 단건 주문
    public OrderResponseDto createOrder(User user, Long storeId, Long menuId, OrderRequestDto dto) {
        // 가게 조회
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new NullPointerException("가게가 존재하지 않습니다."));

        // 영업시간 확인
        if (dto.getOrderTime().isBefore(store.getOpenTime()) || dto.getOrderTime().isAfter(store.getCloseTime())) {
            throw new IllegalArgumentException("영업시간이 아닙니다.");
        }

        // 메뉴 조회
        Menu menu = menuRepository.findById(menuId).orElseThrow(() ->
                new NullPointerException("메뉴가 존재하지 않습니다."));

        // 가게에 메뉴 존재하는지 확인
        if (!ObjectUtils.nullSafeEquals(store.getId(), menu.getStore().getId())) {
            throw new IllegalArgumentException("주문하려는 가게에 메뉴가 없습니다.");
        }

        Order order = new Order(user, store, menu, dto.getQuantity(), OrderState.REQUEST_ORDER);
        Order savedOrder = orderRepository.save(order);
        Long totalPrice = (long) savedOrder.getQuantity() * menu.getPrice();
        if (totalPrice < Long.parseLong(store.getMinOrderPrice())) {
            throw new IllegalArgumentException("최소 주문 금액보다 낮습니다.");
        }
        return new OrderResponseDto(order);
    }


    // 장바구니 전부 주문
    public List<OrderResponseDto> orderBasket(User user) {

        List<Basket> basketList = basketRepository.findByUserId(user.getId());

//        LocalTime localTime = LocalTime.now();

//        // 영업시간 확인
//        if (localTime.isBefore(basketList.get(0).getStore().getOpenTime()) ||
//                localTime.isAfter(basketList.get(0).getStore().getCloseTime())) {
//            throw new IllegalArgumentException("영업시간이 아닙니다.");
//        }

        // 주문 금액 합
        Long totalPriceAll = 0L;
        for (Basket basket : basketList) {
            Long price = (long) basket.getQuantity() * basket.getMenu().getPrice();
            totalPriceAll += price;
        }
        if (totalPriceAll < Long.parseLong(basketList.get(0).getStore().getMinOrderPrice())) {
            throw new IllegalArgumentException("최소 주문 금액보다 낮습니다.");
        }

        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();

        for (Basket basket : basketList) {
            Order order = new Order(basket.getUser(), basket.getStore(), basket.getMenu(), basket.getQuantity(),
                    OrderState.REQUEST_ORDER);
            Order savedOrder = orderRepository.save(order);

                OrderResponseDto orderResponseDto = new OrderResponseDto(savedOrder);
                orderResponseDtoList.add(orderResponseDto);
        }
        basketRepository.deleteAll(basketList);
        return orderResponseDtoList;
    }

    // 주문상태 업데이트
    public OrderStateResponseDto updateOrder(Long storeId, Long orderId, User user, OrderStateRequestDto dto) {

        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new NullPointerException("가게가 존재하지 않습니다."));

        if (user.getRole() == UserRole.USER) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        if (!ObjectUtils.nullSafeEquals(store.getUsers().getId(), user.getId())) {
            throw new IllegalArgumentException("가게 주인이 아닙니다.");
        }
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NullPointerException("주문이 존재하지 않습니다."));
        if (!ObjectUtils.nullSafeEquals(store.getId(), order.getStore().getId())) {
            throw new IllegalArgumentException("해당 가게의 주문이 아닙니다.");
        }
        order.updateState(dto.getState());
        Order savedOrder = orderRepository.save(order);
        return new OrderStateResponseDto(
                savedOrder.getId(),
                savedOrder.getStore().getStoreName(),
                savedOrder.getState().getStateName()
        );
    }

    // 주문목록 조회
    public List<OrderResponseDto> getOrders(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("modifiedAt").descending());
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        if (orders.isEmpty()) {
            throw new NullPointerException("Not found.");
        }
        return orders.stream().map(OrderResponseDto::new).toList();

    }
}

