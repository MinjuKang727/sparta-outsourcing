package com.sparta.spartaoutsourcing.menu.service;

import com.sparta.spartaoutsourcing.category.dto.request.CategoryRequestDto;
import com.sparta.spartaoutsourcing.category.entity.Category;
import com.sparta.spartaoutsourcing.category.repository.CategoryRepository;
import com.sparta.spartaoutsourcing.menu.dto.request.MenuRequestDto;
import com.sparta.spartaoutsourcing.menu.dto.response.MenuResponseDto;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.option.repository.MenuOptionRepository;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
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
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private OptionGroupRepository optionGroupRepository;

    @Test
    void createMenu() {
        // given
        Long storeId = 1L;
        User owner = new User(); // User 객체 설정
        owner.setId(1L); // User ID 설정
        CategoryRequestDto categoryDto = new CategoryRequestDto("New Category");
        MenuRequestDto requestDto = new MenuRequestDto("Sample Menu", 1000, categoryDto, Collections.emptyList());
        Store store = new Store(); // Store 객체 설정

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(categoryRepository.findByName(requestDto.getCategory().getName())).willReturn(Optional.empty());
        given(categoryRepository.save(any(Category.class))).willReturn(new Category("New Category"));
        given(menuRepository.existsByMenuNameAndStoreId(any(), any())).willReturn(false);

        // 메뉴 저장을 위한 Mocking
        Menu savedMenu = new Menu(store, new Category("New Category"), requestDto.getName(), requestDto.getPrice());
        given(menuRepository.save(any(Menu.class))).willReturn(savedMenu);

        // when
        MenuResponseDto responseDto = menuService.createMenu(storeId, requestDto, owner);

        // then
        assertNotNull(responseDto);
        assertEquals("Sample Menu", responseDto.getMenu());
        assertEquals(1000, responseDto.getPrice());
    }

    @Test
    void updateMenu() {
        // Given
        Long storeId = 1L;
        Long menuId = 1L;
        Store store = new Store();
        store.setId(storeId);

        Category existingCategory = new Category("Existing Category");
        Menu existingMenu = new Menu(store, existingCategory, "Old Menu", 10000);

        CategoryRequestDto newCategoryDto = new CategoryRequestDto("New Category");
        MenuRequestDto menuRequestDto = new MenuRequestDto("Updated Menu", 15000, newCategoryDto, null);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingMenu));
        when(categoryRepository.findByName(newCategoryDto.getName())).thenReturn(Optional.of(new Category(newCategoryDto.getName())));

        // When
        MenuResponseDto response = menuService.updateMenu(storeId, menuId, menuRequestDto, new User());

        // Then
        assertEquals("Updated Menu", response.getMenu());
        assertEquals(15000, response.getPrice());
        assertEquals("New Category", response.getCategoryName());
    }

    @Test
    void deleteMenu() {
        // Given
        Long storeId = 1L;
        Long menuId = 1L;
        Store store = new Store();
        store.setId(storeId);

        Menu existingMenu = new Menu(store, new Category("Existing Category"), "Old Menu", 10000);
        existingMenu.setDeleted(false);
        existingMenu.getCategory().setDeleted(false);

        given(menuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));

        // When
        menuService.deleteMenu(storeId, menuId);

        // Then
        assertTrue(existingMenu.getIsDeleted());
    }

    @Test
    void restoreMenu() {
        // Given
        Long storeId = 1L;
        Long menuId = 1L;
        Store store = new Store();
        store.setId(storeId);

        Menu existingMenu = new Menu(store, new Category("Existing Category"), "Old Menu", 10000);
        existingMenu.setDeleted(true);
        existingMenu.getCategory().setDeleted(false);

        given(menuRepository.findById(menuId)).willReturn(Optional.of(existingMenu));

        // When
        menuService.restoreMenu(storeId, menuId);

        // Then
        assertFalse(existingMenu.getIsDeleted());
    }
}