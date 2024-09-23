package com.sparta.spartaoutsourcing.optionGroup.dto.request;

import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OptionGroupRequestDto {
    private String name;
    private List<MenuOptionRequestDto> options;
}
