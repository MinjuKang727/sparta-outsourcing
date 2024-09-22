package com.sparta.spartaoutsourcing.menu.dto.request;

import com.sparta.spartaoutsourcing.category.dto.request.CategoryRequestDto;
import com.sparta.spartaoutsourcing.optionGroup.dto.request.OptionGroupRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class MenuRequestDto {
    private String name;
    private Long price;
    private CategoryRequestDto category;
    private List<OptionGroupRequestDto> optionGroups;
}