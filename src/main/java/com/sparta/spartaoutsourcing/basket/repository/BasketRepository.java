package com.sparta.spartaoutsourcing.basket.repository;

import com.sparta.spartaoutsourcing.basket.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    List<Basket> findByUserId(Long userId);
}


