package com.sparta.spartaoutsourcing.option.repository;

import com.sparta.spartaoutsourcing.option.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
    boolean existsByNameAndOptionGroup_Id(String name, Long group_id);

    List<MenuOption> findByIsDeletedAndOptionGroup_Id(boolean isDeleted, Long group_id);

    boolean existsByOptionGroup_Id(Long id);
}
