package com.sparta.spartaoutsourcing.domain.category.dto.response;

import com.sparta.spartaoutsourcing.domain.category.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {
    private Long id;        // 카테고리 ID
    private String name;    // 카테고리 이름

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
