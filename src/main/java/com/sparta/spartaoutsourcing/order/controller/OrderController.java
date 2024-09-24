package com.sparta.spartaoutsourcing.order.controller;


import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.order.dto.*;
import com.sparta.spartaoutsourcing.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    // 단건 주문
    @PostMapping("/stores/{storeId}/menus/{menuId}/orders")
    public ResponseEntity<OrderResponseDto> createOrder(
            @PathVariable Long storeId, @PathVariable Long menuId,
            @RequestBody @Valid OrderRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        OrderResponseDto responseDto = orderService.createOrder(userDetails.getUser(), storeId, menuId, dto);
        return ResponseEntity.ok(responseDto);
    }

    // 장바구니 주문
    @PostMapping("/baskets/orders")
    public ResponseEntity<List<OrderResponseDto>> orderBasket(
            @RequestBody @Valid BasketOrderRequestDto basketOrderRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<OrderResponseDto> responseDtos = orderService.orderBasket(userDetails.getUser(), basketOrderRequestDto.getUsedPoint());
        return ResponseEntity.ok(responseDtos);
    }

    // 주문상태 업데이트
    @PutMapping("/stores/{storeId}/orders/{orderId}")
    public ResponseEntity<OrderStateResponseDto> updateOrder(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestBody OrderStateRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        OrderStateResponseDto responseDto = orderService.updateOrder(storeId, orderId, userDetails.getUser(), dto);
        return ResponseEntity.ok(responseDto);
    }

    // 주문자 주문목록 조회
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<OrderResponseDto> responseDtos = orderService.getOrders(userDetails.getUser().getId(), pageNo, pageSize);
        return ResponseEntity.ok(responseDtos);
    }


}
