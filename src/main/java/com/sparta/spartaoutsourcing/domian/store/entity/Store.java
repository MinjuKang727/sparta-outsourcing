package com.sparta.spartaoutsourcing.domian.store.entity;

import com.sparta.spartaoutsourcing.domian.store.dto.StoreRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(name = "store_name",nullable = false)
    private String storeName;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time" ,nullable = false)
    private LocalTime closeTime;

    @Column(name = "min_order_price", nullable = false)
    private String minOrderPrice;

    @Column(name = "is_close",nullable = false)
    private boolean isClose = true;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;


    public Store(StoreRequestDto storeRequestDto) {

        this.storeName = storeRequestDto.getStoreName();
        this.openTime = storeRequestDto.getOpenTime();
        this.closeTime = storeRequestDto.getCloseTime();
        this.minOrderPrice = storeRequestDto.getMinOrderPrice();
    }
    public void update(String storeName,LocalTime openTime,LocalTime closeTime,String minOrderPrice) {
        this.storeName = storeName;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.minOrderPrice = minOrderPrice;
    }

    public void activateStore(){
        this.isClose = false;
    }

}
