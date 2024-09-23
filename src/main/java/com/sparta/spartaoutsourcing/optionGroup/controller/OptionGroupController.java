package com.sparta.spartaoutsourcing.optionGroup.controller;

import com.sparta.spartaoutsourcing.optionGroup.dto.request.OptionGroupRequestDto;
import com.sparta.spartaoutsourcing.optionGroup.dto.response.OptionGroupSimpleResponseDto;
import com.sparta.spartaoutsourcing.optionGroup.service.OptionGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "OptionGroupController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/optionGroup")
public class OptionGroupController {

    private final OptionGroupService optionGroupService;

    // 옵션 그룹 생성
    @PostMapping("/{menu_id}")
    public ResponseEntity<?> createOptionGroup(@PathVariable Long menu_id, @RequestBody OptionGroupRequestDto requestDto) {
        log.info(":::옵션 그룹 생성:::");

        return ResponseEntity.status(HttpStatus.CREATED).body(optionGroupService.addOptionGroup(menu_id, requestDto));
    }

    // 옵션 그룹 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOptionGroup(@PathVariable Long id, @RequestBody OptionGroupRequestDto requestDto) {
        log.info(":::옵션 그룹 수정:::");

        return ResponseEntity.ok().body(optionGroupService.updateOptionGroup(id, requestDto));
    }

    // 옵션 그룹 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOptionGroup(@PathVariable Long id) {
        log.info(":::옵션 그룹 삭제:::");

        optionGroupService.deleteOptionGroup(id);
        return ResponseEntity.ok().body("옵션 그룹 삭제 성공");
    }

    // 옵션 그룹 복원
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreOptionGroup(@PathVariable Long id) {
        log.info(":::옵션 그룹 복원:::");

        optionGroupService.restoreOptionGroup(id);
        return ResponseEntity.ok().body("옵션 그룹 복원 성공");
    }

    // 활성화된 모든 옵션 그룹 조회
    @GetMapping("")
    public ResponseEntity<List<OptionGroupSimpleResponseDto>> getAllOptionGroups() {
        log.info(":::활성화된 모든 옵션 그룹 조회:::");

        List<OptionGroupSimpleResponseDto> optionGroup = optionGroupService.getAllOptionGroups();

        if (optionGroup.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(optionGroup);
    }
}