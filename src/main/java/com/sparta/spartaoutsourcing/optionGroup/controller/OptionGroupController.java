package com.sparta.spartaoutsourcing.optionGroup.controller;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.menu.exception.NotFoundException;
import com.sparta.spartaoutsourcing.optionGroup.dto.request.OptionGroupRequestDto;
import com.sparta.spartaoutsourcing.optionGroup.dto.response.OptionGroupSimpleResponseDto;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.optionGroup.service.OptionGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;

@Slf4j(topic = "OptionGroupController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/optionGroup")
public class OptionGroupController {

    private final OptionGroupService optionGroupService;
    private final JwtUtil jwtUtil;

    // 옵션 그룹 생성
    @PostMapping("/{menu_id}")
    public ResponseEntity<?> createOptionGroup(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long menu_id, @RequestBody OptionGroupRequestDto requestDto) {
        log.info(":::옵션 그룹 생성:::");

        if (!checkOwnerAndStoreOwner(userDetails, menu_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(optionGroupService.addOptionGroup(menu_id, requestDto, userDetails.getUser()));
    }

    // 옵션 그룹 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOptionGroup(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long id, @RequestBody OptionGroupRequestDto requestDto) {
        log.info(":::옵션 그룹 수정:::");

        // 옵션 그룹의 메뉴 ID 조회
        Long menuId = optionGroupService.findMenuIdByGroupId(id);

        // 소유자 및 가게 소유자 확인
        if (!checkOwnerAndStoreOwner(userDetails, menuId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        return ResponseEntity.ok().body(optionGroupService.updateOptionGroup(id, requestDto, userDetails.getUser()));
    }

    // 옵션 그룹 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOptionGroup(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long id) {
        log.info(":::옵션 그룹 삭제:::");

        if (!checkOwnerAndGroupCreator(userDetails, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        optionGroupService.deleteOptionGroup(id);
        return ResponseEntity.ok().body("옵션 그룹 삭제 성공");
    }

    // 옵션 그룹 복원
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreOptionGroup(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @PathVariable Long id) {
        log.info(":::옵션 그룹 복원:::");

        if (!checkOwnerAndGroupCreator(userDetails, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        optionGroupService.restoreOptionGroup(id);
        return ResponseEntity.ok().body("옵션 그룹 복원 성공");
    }

    // 특정 사용자의 활성화된 옵션 그룹 조회
    @GetMapping("")
    public ResponseEntity<?> getAllOptionGroups(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(":::특정 사용자의 활성화된 옵션 그룹 조회:::");

        Long userId = userDetails.getUser().getId();

        List<OptionGroupSimpleResponseDto> optionGroup = optionGroupService.getAllOptionGroupsByUserId(userId);

        if (optionGroup.isEmpty()) {
            return ResponseEntity.ok("활성화된 옵션 그룹이 없습니다.");
        }
        return ResponseEntity.ok(optionGroup);
    }

    // 소유자 및 가게 소유자 확인
    private boolean checkOwnerAndStoreOwner(UserDetailsImpl userDetails, Long menuId) {
        // 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();

        // 권한 가져오기
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // 메뉴 소유자 확인
        Long menuOwnerId = optionGroupService.findMenuOwnerIdById(menuId);

        // 조건을 만족하는지 확인
        return role.equals("OWNER") && menuOwnerId.equals(userId);
    }

    // 그룹 소유자 확인
    private boolean checkOwnerAndGroupCreator(UserDetailsImpl userDetails, Long groupId) {
        // 사용자 ID 가져오기
        Long userId = userDetails.getUser().getId();

        // 권한 가져오기
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // 그룹 조회
        OptionGroup group = optionGroupService.findById(groupId); // 그룹 조회 로직

        // 그룹 소유자 확인
        Long groupOwnerId = group.getOwner().getId();

        // 조건을 만족하는지 확인
        return role.equals("OWNER") && groupOwnerId.equals(userId);
    }
}