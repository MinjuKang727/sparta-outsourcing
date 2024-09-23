package com.sparta.spartaoutsourcing.option.controller;

import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import com.sparta.spartaoutsourcing.option.dto.response.MenuOptionResponseDto;
import com.sparta.spartaoutsourcing.option.service.MenuOptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "MenuOptionController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/options")
public class MenuOptionController {

    private final MenuOptionService menuOptionService;

    // 옵션 생성
    @PostMapping("/{group_id}")
    public ResponseEntity<?> createMenuOption(@PathVariable Long group_id, @RequestBody MenuOptionRequestDto requestDto) {
        log.info(":::옵션 생성:::");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(menuOptionService.addMenuOption(group_id, requestDto));
    }

    // 옵션 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuOption(@PathVariable Long id, @RequestBody MenuOptionRequestDto requestDto) {
        log.info(":::옵션 수정:::");
        
        return ResponseEntity.ok().body(menuOptionService.updateMenuOption(id, requestDto));
    }

    // 옵션 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuOption(@PathVariable Long id) {
        log.info(":::옵션 삭제:::");

        menuOptionService.deleteMenuOption(id);
        return ResponseEntity.ok().body("옵션 삭제 성공");
    }

    // 옵션 복원
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreMenuOption(@PathVariable Long id) {
        log.info(":::옵션 복원:::");

        menuOptionService.restoreMenuOption(id);

        return ResponseEntity.ok().body("옵션 복원 성공");
    }

    // 특정 옵션 그룹의 활성화된 모든 옵션 조회
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<MenuOptionResponseDto>> getActiveMenuOptionsByGroup(@PathVariable Long groupId) {
        log.info(":::특정 옵션 그룹의 활성화된 모든 옵션 조회:::");

        List<MenuOptionResponseDto> options = menuOptionService.getActiveMenuOptionsByGroup(groupId);

        if (options.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(options);
    }
}