package com.sparta.spartaoutsourcing.optionGroup.dto.request;

import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OptionGroupRequestDto {
    private String name;
    private List<MenuOptionRequestDto> options;
}
