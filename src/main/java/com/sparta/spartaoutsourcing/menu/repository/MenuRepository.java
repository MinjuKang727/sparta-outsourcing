package com.sparta.spartaoutsourcing.menu.repository;

import com.sparta.spartaoutsourcing.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    boolean existsByMenuNameAndStoreId(String name, Long store_id);
}
