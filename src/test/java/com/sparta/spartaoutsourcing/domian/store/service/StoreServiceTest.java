package com.sparta.spartaoutsourcing.domian.store.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sparta.spartaoutsourcing.store.dto.store.StoreRequestDto;
import com.sparta.spartaoutsourcing.store.exception.MaxStoreLimitException;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.store.service.StoreService;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreService storeService;


    // 사장님 검증
    @Test
    void createStore_checkUser() {
        // given
        Long userId = 1L;
        StoreRequestDto requestDto = new StoreRequestDto();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        NullPointerException exception = assertThrows(NullPointerException.class, ()
                -> storeService.createStore(userId, requestDto));
        assertEquals("회원이 아닙니다", exception.getMessage());
    }

//    생성 가게 개수 검증
    @Test
    void createStore_checkStore() {
        // given
        Long userId = 1L;
        StoreRequestDto requestDto = new StoreRequestDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        when(storeRepository.countStoreByIsCloseFalse(userId)).thenReturn(3L);

        // when & then
        MaxStoreLimitException exception = assertThrows(MaxStoreLimitException.class, ()
                -> storeService.createStore(userId, requestDto));
        assertEquals("가게는 3개까지 생성할 수 있습니다", exception.getMessage());
    }
}
