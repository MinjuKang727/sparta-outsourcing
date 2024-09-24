package com.sparta.spartaoutsourcing.category.controller;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.category.dto.response.CategoryResponseDto;
import com.sparta.spartaoutsourcing.category.service.CategoryService;
import com.sparta.spartaoutsourcing.category.dto.request.CategoryRequestDto;
import com.sparta.spartaoutsourcing.menu.exception.NotFoundException;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "CategoryController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtUtil jwtUtil;

    // 카테고리 생성
    @PostMapping("")
    public ResponseEntity<?> createCategory(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info(":::카테고리 생성:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails);
        if (response != null) return response;

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequestDto));
    }

    // 카테고리 수정
    @PutMapping("/{category_id}")
    public ResponseEntity<?> updateCategory(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable Long category_id, @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info(":::카테고리 수정:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails);
        if (response != null) return response;

        return ResponseEntity.ok().body(categoryService.updateCategory(category_id, categoryRequestDto));
    }

    // 카테고리 삭제
    @DeleteMapping("/{category_id}")
    public ResponseEntity<?> deleteCategory(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable Long category_id) {
        log.info(":::카테고리 삭제:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails);
        if (response != null) return response;

        categoryService.deleteCategory(category_id);
        return ResponseEntity.ok().body("카테고리 삭제 성공");
    }

    // 카테고리 복원
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreCategory(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable Long id) {
        log.info(":::카테고리 복원:::");

        ResponseEntity<?> response = checkStoreOwner(userDetails);
        if (response != null) return response;

        categoryService.restoreCategory(id);

        return ResponseEntity.ok().body("카테고리 복원 성공");
    }

    // 활성화된 모든 카테고리 조회
    @GetMapping("")
    public ResponseEntity<List<CategoryResponseDto>> getAllActiveCategories() {
        log.info(":::활성화된 모든 카테고리 조회:::");

        List<CategoryResponseDto> categories = categoryService.getAllActiveCategories();

        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    // OWNER 소유자 확인 메서드
    private ResponseEntity<?> checkStoreOwner(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        String auth = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // 소유자 확인
        if (!auth.equals("OWNER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }
        return null; // 권한이 있을 경우 null 반환
    }

}
