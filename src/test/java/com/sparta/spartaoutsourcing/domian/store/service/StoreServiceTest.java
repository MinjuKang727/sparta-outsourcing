package com.sparta.spartaoutsourcing.domian.store.service;

import com.sparta.spartaoutsourcing.domian.store.dto.StoreRequestDto;
import com.sparta.spartaoutsourcing.domian.store.dto.StoreResponseDto;
import com.sparta.spartaoutsourcing.domian.store.entity.Store;
import com.sparta.spartaoutsourcing.domian.store.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

class StoreServiceTest {

    @Mock
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Test
    void createStore(StoreRequestDto storeRequestDto) {
        Store store = new Store(storeRequestDto);
        StoreResponseDto storeResponseDto = new StoreResponseDto(store);
        storeResponseDto.setId(1L);
        storeResponseDto.setStoreName("김치찌개");
        storeResponseDto.setOpenTime(LocalTime.parse("10:00"));
        storeResponseDto.setCloseTime(LocalTime.parse("18:00"));
        storeResponseDto.setMinOrderPrice("10000");
        storeService.createStore(storeRequestDto);

    }

    @Test
    void getStore() {
    }

    @Test
    void findAllStores() {
    }

    @Test
    void updateStore() {
    }

    @Test
    void storeDelete() {
    }
}