package com.sparta.spartaoutsourcing.domain.menu.dto.response;

import com.sparta.spartaoutsourcing.domain.menu.entity.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuResponseDto {
    private Long id; // 메뉴 ID 추가
    private String name; // 메뉴 이름
    private Long price; // 가격

    public MenuResponseDto(Menu menu) {
        this.id = menu.getId(); // 메뉴 ID
        this.name = menu.getName(); // 메뉴 이름
        this.price = menu.getPrice(); // 가격
    }
}