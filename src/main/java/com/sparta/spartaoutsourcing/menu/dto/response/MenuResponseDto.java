package com.sparta.spartaoutsourcing.menu.dto.response;

import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.optionGroup.dto.response.OptionGroupResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class MenuResponseDto {
    private Long id; // 메뉴 ID
    private String menu; // 메뉴 이름
    private int price; // 가격
    private String categoryName; // 카테고리 이름
    private List<OptionGroupResponseDto> optionGroups; // 옵션 그룹 리스트

    public MenuResponseDto(Menu menu) {
        this.id = menu.getId();
        this.menu = menu.getMenuName();
        this.price = menu.getPrice();
        this.categoryName = menu.getCategory().getName();
        this.optionGroups = menu.getOptionGroups().stream()
                .map(OptionGroupResponseDto::new)
                .collect(Collectors.toList());
    }
}