package com.sparta.spartaoutsourcing.store.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.spartaoutsourcing.store.dto.store.StoreRequestDto;
import com.sparta.spartaoutsourcing.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
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
    private boolean isClose = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
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
        this.isClose = true;
    }

}
