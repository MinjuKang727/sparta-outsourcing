package com.sparta.spartaoutsourcing.order.service;


import com.sparta.spartaoutsourcing.basket.entity.Basket;
import com.sparta.spartaoutsourcing.basket.repository.BasketRepository;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.order.dto.OrderRequestDto;
import com.sparta.spartaoutsourcing.order.dto.OrderResponseDto;
import com.sparta.spartaoutsourcing.order.entity.Order;
import com.sparta.spartaoutsourcing.order.repository.OrderRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.spartaoutsourcing.order.entity.OrderState.REQUEST_ORDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private BasketRepository basketRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void 주문_정상_동작() {
        // given
        long userId = 1L;
        long storeId = 1L;
        long menuId = 1L;
        long orderId = 1L;

        User user = new User("jitaek", "l3259120", "jitaek@naver.com", UserRole.USER, 1L);
        ReflectionTestUtils.setField(user, "id", userId);
        Store store = new Store(user, "jitaek_store", LocalTime.of(15, 30), LocalTime.of(21, 30), "15000", false);
        ReflectionTestUtils.setField(store, "id", storeId);
        Menu menu = new Menu(store, "햄버거", 8000);
        ReflectionTestUtils.setField(menu, "id", menuId);
        Order order = new Order(user, store, menu, 3, REQUEST_ORDER);
        ReflectionTestUtils.setField(order, "id", orderId);
        OrderRequestDto orderRequestDto = new OrderRequestDto(5, LocalTime.of(15, 15, 30), 0);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
        given(orderRepository.save(any())).willReturn(order);

        // when
        OrderResponseDto orderResponseDto = orderService.createOrder(user, storeId, menuId, orderRequestDto);

        // then
        assertNotNull(orderResponseDto);
        assertEquals(1, menu.getId());
    }


    @Test
    public void 장바구니_주문_정상동작() {
        // given
        long userId = 1L;
        long storeId = 1L;
        long menuId = 1L;
        User user = new User("jitaek", "l3259120", "jitaek@naver.com", UserRole.USER, 1L);
        ReflectionTestUtils.setField(user, "id", userId);
        Store store = new Store(user, "jitaek_store", LocalTime.of(15, 30), LocalTime.of(21, 30), "15000", false);
        ReflectionTestUtils.setField(store, "id", storeId);
        Menu menu = new Menu(store, "햄버거", 8000);
        ReflectionTestUtils.setField(menu, "id", menuId);
        Menu menu2 = new Menu(store, "치킨", 8000);
        Menu menu3 = new Menu(store, "피자", 8000);

        List<Basket> basketList = new ArrayList<>();

        Basket basket1 = new Basket(user, store, menu, 1);
        Basket basket2 = new Basket(user, store, menu2, 2);
        Basket basket3 = new Basket(user, store, menu3, 3);
        basketList.add(basket1);
        basketList.add(basket2);
        basketList.add(basket3);

        given(basketRepository.findByUserId(anyLong())).willReturn(basketList);

        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", (long) (Math.random() * 1000)); // 임의의 ID 설정
            return order;
        });

        // when
        List<OrderResponseDto> orderResponseDtos = orderService.orderBasket(user, 0);

        // then
        assertNotNull(orderResponseDtos); // 주문이 null이 아닌지 확인

        OrderResponseDto order1 = orderResponseDtos.get(1);
        assertEquals(menu2.getMenuName(), order1.getMenuName()); // 메뉴 이름이 일치하는지 확인
        assertEquals(2, order1.getQuantity()); // 수량이 일치하는지 확인


    }


}
