package com.sparta.spartaoutsourcing.domian.store.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sparta.spartaoutsourcing.store.dto.store.StoreRequestDto;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.store.service.StoreService;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
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

    // 가게 생성 테스트
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

}
