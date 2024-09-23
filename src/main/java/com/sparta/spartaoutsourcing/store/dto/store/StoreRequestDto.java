package com.sparta.spartaoutsourcing.store.dto.store;

import lombok.Data;

import java.time.LocalTime;

@Data
public class StoreRequestDto {

    private String storeName;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String minOrderPrice;

}
