package com.sparta.spartaoutsourcing.optionGroup.dto.response;

import com.sparta.spartaoutsourcing.option.dto.response.MenuOptionResponseDto;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class OptionGroupResponseDto {
    private Long id;
    private String name;
    private List<MenuOptionResponseDto> options;

    public OptionGroupResponseDto(OptionGroup optionGroup) {
        this.id = optionGroup.getId();
        this.name = optionGroup.getName();
        this.options = optionGroup.getMenuOptions().stream()
                .map(MenuOptionResponseDto::new)
                .collect(Collectors.toList());
    }

    public OptionGroupResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
