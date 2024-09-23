package com.sparta.spartaoutsourcing.domian.store.dto.store;

import lombok.Data;

import java.time.LocalTime;

@Data
public class StoreRequestDto {

    private String storeName;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String minOrderPrice;

}
