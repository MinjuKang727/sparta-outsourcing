package com.sparta.spartaoutsourcing.domain.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponseDto {
    private String message;
    private MenuResponseDto menu;
}
