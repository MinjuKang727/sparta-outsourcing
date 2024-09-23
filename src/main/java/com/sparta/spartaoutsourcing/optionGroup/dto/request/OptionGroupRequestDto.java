package com.sparta.spartaoutsourcing.optionGroup.dto.request;

import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class OptionGroupRequestDto {
    private String name;
    private List<MenuOptionRequestDto> options;
}
