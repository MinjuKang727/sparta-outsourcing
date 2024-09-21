package com.sparta.spartaoutsourcing.domain.category.controller;

import com.sparta.spartaoutsourcing.domain.category.dto.request.CategoryRequestDto;
import com.sparta.spartaoutsourcing.domain.category.dto.response.CategoryResponseDto;
import com.sparta.spartaoutsourcing.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "CategoryController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping("")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        log.info(":::카테고리 생성:::");

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequestDto));
    }

    // 카테고리 수정
    @PutMapping("/{category_id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long category_id, @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info(":::카테고리 수정:::");

        return ResponseEntity.ok().body(categoryService.updateCategory(category_id, categoryRequestDto));
    }

    // 카테고리 삭제
    @DeleteMapping("/{category_id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long category_id) {
        log.info(":::카테고리 삭제:::");

        categoryService.deleteCategory(category_id);
        return ResponseEntity.ok().body("카테고리 삭제 성공");
    }

    // 카테고리 복원
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreCategory(@PathVariable Long id) {
        log.info(":::카테고리 복원:::");

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

}
