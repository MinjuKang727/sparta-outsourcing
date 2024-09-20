package com.sparta.spartaoutsourcing.domain.menu.controller;

import com.sparta.spartaoutsourcing.domain.menu.dto.request.MenuRequestDto;
import com.sparta.spartaoutsourcing.domain.menu.dto.response.MenuResponseDto;
import com.sparta.spartaoutsourcing.domain.menu.dto.response.SuccessResponseDto;
import com.sparta.spartaoutsourcing.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "MenuController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class MenuController {

    private final MenuService menuService;

    // 메뉴 생성
    @PostMapping("/{store_id}/menus")
    public ResponseEntity<?> createMenu(@PathVariable Long store_id, @RequestBody MenuRequestDto menuRequestDto) {
        log.info(":::메뉴 생성:::");

        MenuResponseDto createdMenu = menuService.createMenu(store_id, menuRequestDto);
        SuccessResponseDto response = new SuccessResponseDto("메뉴 생성 완료", createdMenu);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 메뉴 수정
    @PutMapping("/{store_id}/menus/{menu_id}")
    public ResponseEntity<?> updateMenu(@PathVariable Long store_id, @PathVariable Long menu_id, @RequestBody MenuRequestDto menuRequestDto) {
        log.info(":::메뉴 수정:::");
        
        MenuResponseDto updateMenu = menuService.updateMenu(store_id, menu_id, menuRequestDto);
        SuccessResponseDto response = new SuccessResponseDto("메뉴 수정 완료", updateMenu);

        return ResponseEntity.ok().body(response);
    }

    // 메뉴 삭제
    @DeleteMapping("/{store_id}/menus/{menu_id}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long store_id, @PathVariable Long menu_id) {
        log.info(":::메뉴 삭제:::");
        
        menuService.deleteMenu(store_id, menu_id); // 예외가 발생하지 않으면 성공

        return ResponseEntity.ok().body("메뉴 삭제 성공");
    }
}
