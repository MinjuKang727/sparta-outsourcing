package com.sparta.spartaoutsourcing.category.dto.response;

import com.sparta.spartaoutsourcing.category.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {
    private Long id;        // 카테고리 ID
    private String category;    // 카테고리 이름

    public CategoryResponseDto(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.category = category.getName();
    }
}
