package com.sparta.spartaoutsourcing.category.service;

import com.sparta.spartaoutsourcing.category.dto.request.CategoryRequestDto;
import com.sparta.spartaoutsourcing.category.dto.response.CategoryResponseDto;
import com.sparta.spartaoutsourcing.category.entity.Category;
import com.sparta.spartaoutsourcing.category.repository.CategoryRepository;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.option.repository.MenuOptionRepository;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private OptionGroupRepository optionGroupRepository;

    @Test
    void createCategory() {
        // given
        CategoryRequestDto requestDto = new CategoryRequestDto("New Category");
        given(categoryRepository.existsByName(requestDto.getName())).willReturn(false);

        // when
        CategoryResponseDto response = categoryService.createCategory(requestDto);

        // then
        assertNotNull(response);
        assertEquals("New Category", response.getCategory());
    }

    @Test
    void updateCategory() {
        // given
        Category existingCategory = new Category("Old Category");
        existingCategory.setDeleted(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("New Category")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = new CategoryRequestDto("New Category");

        // when
        var response = categoryService.updateCategory(1L, dto);

        // then
        assertEquals("New Category", response.getCategory());
    }

    @Test
    void deleteCategory() {
        // given
        Category category = new Category("Category to Delete");
        category.setDeleted(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // when
        categoryService.deleteCategory(1L);

        // then
        assertTrue(category.getIsDeleted());
    }

    @Test
    void restoreCategory() {
        // given
        Category category = new Category("Category to Restore");
        category.setDeleted(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // when
        categoryService.restoreCategory(1L);

        // then
        assertFalse(category.getIsDeleted());
    }

    @Test
    void getAllActiveCategories() {
        // given
        when(categoryRepository.findByIsDeleted(false)).thenReturn(Collections.singletonList(new Category("Active Category")));

        // when
        var activeCategories = categoryService.getAllActiveCategories();

        // then
        assertEquals(1, activeCategories.size());
        assertEquals("Active Category", activeCategories.get(0).getCategory());
    }
}