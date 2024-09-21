package com.sparta.spartaoutsourcing.domain.menu.dto.request;

import com.sparta.spartaoutsourcing.domain.category.dto.request.CategoryRequestDto;
import com.sparta.spartaoutsourcing.domain.optionGroup.dto.request.OptionGroupRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class MenuRequestDto {
    private String name;
    private Long price;
    private CategoryRequestDto category;
    private List<OptionGroupRequestDto> optionGroups;
}