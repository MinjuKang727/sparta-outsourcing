package com.sparta.spartaoutsourcing.domain.optionGroup.dto.request;

import com.sparta.spartaoutsourcing.domain.option.dto.request.MenuOptionRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class OptionGroupRequestDto {
    private String name;
    private List<MenuOptionRequestDto> options;
}
