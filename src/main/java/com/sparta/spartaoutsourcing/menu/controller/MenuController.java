package com.sparta.spartaoutsourcing.menu.controller;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.menu.dto.request.MenuRequestDto;
import com.sparta.spartaoutsourcing.menu.exception.NotFoundException;
import com.sparta.spartaoutsourcing.menu.service.MenuService;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "MenuController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class MenuController {

    private final MenuService menuService;
    private final StoreRepository storeRepository;
    private final JwtUtil jwtUtil;

    // 메뉴 생성
    @PostMapping("/{store_id}/menus")
    public ResponseEntity<?> createMenu(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable Long store_id,
                                        @RequestBody MenuRequestDto menuRequestDto) {
        log.info(":::메뉴 생성:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails, store_id);
        if (response != null) return response;

        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(store_id, menuRequestDto, userDetails.getUser()));
    }

    // 메뉴 수정
    @PutMapping("/{store_id}/menus/{menu_id}")
    public ResponseEntity<?> updateMenu(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable Long store_id, @PathVariable Long menu_id,
                                        @RequestBody MenuRequestDto menuRequestDto) {
        log.info(":::메뉴 수정:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails, store_id);
        if (response != null) return response;

        return ResponseEntity.ok().body(menuService.updateMenu(store_id, menu_id, menuRequestDto, userDetails.getUser()));
    }

    // 메뉴 삭제
    @DeleteMapping("/{store_id}/menus/{menu_id}")
    public ResponseEntity<?> deleteMenu(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable Long store_id, @PathVariable Long menu_id) {
        log.info(":::메뉴 삭제:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails, store_id);
        if (response != null) return response;
        
        menuService.deleteMenu(store_id, menu_id); // 예외가 발생하지 않으면 성공

        return ResponseEntity.ok().body("메뉴 삭제 성공");
    }

    // 메뉴 복원
    @PutMapping("/{store_id}/menus/{menu_id}/restore")
    public ResponseEntity<?> restoreMenu(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable Long store_id, @PathVariable Long menu_id) {
        log.info(":::메뉴 복원:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails, store_id);
        if (response != null) return response;

        menuService.restoreMenu(store_id, menu_id);

        return ResponseEntity.ok().body("메뉴 복원 성공");
    }

    // 가게 소유자 확인 메서드
    private ResponseEntity<?> checkStoreOwner(UserDetailsImpl userDetails, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("해당 가게를 찾을 수 없습니다."));

        Long userId = userDetails.getUser().getId();
        String auth = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // 소유자 확인
        if (!store.getUser().getId().equals(userId) || !auth.equals("OWNER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }
        return null; // 권한이 있을 경우 null 반환
    }
}
