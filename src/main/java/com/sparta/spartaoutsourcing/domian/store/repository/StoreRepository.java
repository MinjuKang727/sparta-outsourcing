package com.sparta.spartaoutsourcing.domian.store.repository;

import com.sparta.spartaoutsourcing.domian.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findByOpenTimeAndCloseTime(LocalTime openTime, LocalTime closeTime);

    boolean findByMinOrderPrice(String minOrderPrice);

    Optional<Store> findByStoreName(String storeName);
}
