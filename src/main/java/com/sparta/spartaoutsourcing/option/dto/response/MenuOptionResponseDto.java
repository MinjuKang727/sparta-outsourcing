package com.sparta.spartaoutsourcing.option.dto.response;

import com.sparta.spartaoutsourcing.option.entity.MenuOption;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuOptionResponseDto {
    private Long id;
    private String option;
    private Long price;

    public MenuOptionResponseDto(MenuOption menuOption) {
        this.id = menuOption.getId();
        this.option = menuOption.getName();
        this.price = menuOption.getPrice();
    }
}
