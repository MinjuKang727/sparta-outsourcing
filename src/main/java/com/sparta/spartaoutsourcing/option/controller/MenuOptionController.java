package com.sparta.spartaoutsourcing.option.controller;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.menu.exception.NotFoundException;
import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import com.sparta.spartaoutsourcing.option.dto.response.MenuOptionResponseDto;
import com.sparta.spartaoutsourcing.option.service.MenuOptionService;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "MenuOptionController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/options")
public class MenuOptionController {

    private final MenuOptionService menuOptionService;
    private final StoreRepository storeRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final JwtUtil jwtUtil;

    // 옵션 생성
    @PostMapping("/{group_id}")
    public ResponseEntity<?> createMenuOption(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @PathVariable Long group_id, @RequestBody MenuOptionRequestDto requestDto) {
        log.info(":::옵션 생성:::");

        if (!checkOwnerAndStoreOwner(userDetails, group_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(menuOptionService.addMenuOption(group_id, requestDto));
    }

    // 옵션 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuOption(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @PathVariable Long id, @RequestBody MenuOptionRequestDto requestDto) {
        log.info(":::옵션 수정:::");

        Long groupId = menuOptionService.getOptionGroupIdByOptionId(id); // 그룹 ID 가져오기

        if (!checkOwnerAndStoreOwner(userDetails, groupId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        return ResponseEntity.ok().body(menuOptionService.updateMenuOption(id, requestDto));
    }

    // 옵션 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuOption(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @PathVariable Long id) {
        log.info(":::옵션 삭제:::");

        Long groupId = menuOptionService.getOptionGroupIdByOptionId(id);

        if (!checkOwnerAndStoreOwner(userDetails, groupId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        menuOptionService.deleteMenuOption(id);
        return ResponseEntity.ok().body("옵션 삭제 성공");
    }

    // 옵션 복원
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreMenuOption(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long id) {
        log.info(":::옵션 복원:::");

        Long groupId = menuOptionService.getOptionGroupIdByOptionId(id);

        if (!checkOwnerAndStoreOwner(userDetails, groupId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        menuOptionService.restoreMenuOption(id);
        return ResponseEntity.ok().body("옵션 복원 성공");
    }

    // 특정 옵션 그룹의 활성화된 모든 옵션 조회
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getActiveMenuOptionsByGroup(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long groupId) {
        log.info(":::특정 옵션 그룹의 활성화된 모든 옵션 조회:::");

        if (!checkOwnerAndStoreOwner(userDetails, groupId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        List<MenuOptionResponseDto> options = menuOptionService.getActiveMenuOptionsByGroup(groupId);

        if (options.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(options);
    }

    // 소유자 및 가게 소유자 확인
    private boolean checkOwnerAndStoreOwner(UserDetailsImpl userDetails, Long groupId) {
        // 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();

        // 권한 가져오기
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // 그룹 소유자 확인
        OptionGroup group = optionGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("해당 그룹을 찾을 수 없습니다."));

        // 그룹 소유자 ID 확인
        Long groupOwnerId = group.getOwner().getId();

        // 가게 소유자 확인
        Store store = storeRepository.findById(group.getMenu().getStore().getId())
                .orElseThrow(() -> new NotFoundException("해당 가게를 찾을 수 없습니다."));

        // 조건을 만족하는지 확인
        return role.equals("OWNER") && groupOwnerId.equals(userId);
    }
}