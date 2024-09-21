package com.sparta.spartaoutsourcing.domain.optionGroup.dto.response;

import com.sparta.spartaoutsourcing.domain.optionGroup.entity.OptionGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptionGroupSimpleResponseDto {
    private Long id;
    private String name;

    public OptionGroupSimpleResponseDto(OptionGroup optionGroup) {
        this.id = optionGroup.getId();
        this.name = optionGroup.getName();
    }
}
