package com.sparta.spartaoutsourcing.domian.store.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sparta.spartaoutsourcing.store.dto.store.StoreRequestDto;
import com.sparta.spartaoutsourcing.store.dto.store.StoreResponseDto;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.exception.MaxStoreLimitException;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.store.service.StoreService;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storeService = new StoreService(storeRepository, userRepository);
    }


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
    void createStore_checkStores() {
        // given
        Long userId = 1L;
        StoreRequestDto requestDto = new StoreRequestDto();
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.countStoreByIsCloseFalse(user)).thenReturn(3L);

        // when & then
        MaxStoreLimitException exception = assertThrows(MaxStoreLimitException.class, ()
                -> storeService.createStore(userId, requestDto));
        assertEquals("가게는 3개까지 생성 할 수 있습니다", exception.getMessage());
    }

    //    가게 생성 성공 검증
    @Test
    void createStore_saveCheck() {
        // given
        Long userId = 1L;
        StoreRequestDto requestDto = new StoreRequestDto();
        User user = new User();
        user.setRole(UserRole.OWNER);
        requestDto.setStoreName("test");
        requestDto.setOpenTime(LocalTime.of(10, 0));
        requestDto.setCloseTime(LocalTime.of(20, 0));
        requestDto.setMinOrderPrice("10000");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.countStoreByIsCloseFalse(user)).thenReturn(2L);

        Store store = new Store();
        store.setId(1L);
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        // when
        storeService.createStore(userId, requestDto);
        // then
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    //    생성한 가게가 존재 하는지 검증
    @Test
    void saveStore_checkStore() {
        // given
        Long storeId = 1L;
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // when & then
        NullPointerException exception = assertThrows(NullPointerException.class, ()
                -> storeService.getStore(storeId));
        assertEquals("가게가 존재 하지 없습니다", exception.getMessage());

    }

//    폐점 검증
    @Test
    void Store_IsCloseCheck() {
        // given
        Long storeId = 1L;
        Store store = mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(store.isClose()).thenReturn(true);
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()
                -> storeService.getStore(storeId));
        assertEquals("폐업된 가게 입니다", exception.getMessage());

    }

//   가게 조회 검증
    @Test
    void Store_FindCheck() {
        // given
        Long storeId = 1L;
        Store store = mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(store.getStoreName()).thenReturn("testName");
        when((store.getOpenTime())).thenReturn(LocalTime.of(10, 0));
        when((store.getCloseTime())).thenReturn(LocalTime.of(20, 0));
        when((store.getMinOrderPrice())).thenReturn("10000");
        when((store.getOwnerContent())).thenReturn("testContent");
        when(store.isClose()).thenReturn(false);
        // when
        StoreResponseDto storeResponseDto = storeService.getStore(storeId);

        // then
        assertEquals(storeResponseDto.getStoreName(), store.getStoreName());
        assertEquals(storeResponseDto.getOpenTime(), store.getOpenTime());
        assertEquals(storeResponseDto.getCloseTime(), store.getCloseTime());
        assertEquals(storeResponseDto.getMinOrderPrice(), store.getMinOrderPrice());
        assertEquals(storeResponseDto.getOwnerContent(), store.getOwnerContent());
    }

}
