package com.sparta.spartaoutsourcing.domain.menu.repository;

import com.sparta.spartaoutsourcing.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
