package com.sparta.spartaoutsourcing.store.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.store.dto.store.StoreRequestDto;
import com.sparta.spartaoutsourcing.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

//    사장님 공지사항
    @Column(name = "owner_content")
    private String ownerContent = "";

    @Column(name = "is_close",nullable = false)
    private boolean isClose = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User users;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Menu> menus = new ArrayList<>();


    public Store(StoreRequestDto storeRequestDto) {
        this.storeName = storeRequestDto.getStoreName();
        this.openTime = storeRequestDto.getOpenTime();
        this.closeTime = storeRequestDto.getCloseTime();
        this.minOrderPrice = storeRequestDto.getMinOrderPrice();
        this.ownerContent = storeRequestDto.getOwnerContent();
    }



    public void update(String storeName,LocalTime openTime,LocalTime closeTime,String minOrderPrice,String ownerContent) {
        this.storeName = storeName;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.minOrderPrice = minOrderPrice;
        this.ownerContent = ownerContent;
    }

    public void activateStore(){
        this.isClose = true;
    }

}
