package com.sparta.spartaoutsourcing.optionGroup.repository;

import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionGroupRepository extends JpaRepository<OptionGroup, Long> {
    boolean existsByName(String name);

    List<OptionGroup> findByIsDeleted(boolean b);
}
