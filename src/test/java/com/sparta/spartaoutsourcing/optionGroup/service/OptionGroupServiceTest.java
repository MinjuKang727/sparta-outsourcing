package com.sparta.spartaoutsourcing.optionGroup.service;

import com.sparta.spartaoutsourcing.category.entity.Category;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import com.sparta.spartaoutsourcing.option.entity.MenuOption;
import com.sparta.spartaoutsourcing.option.repository.MenuOptionRepository;
import com.sparta.spartaoutsourcing.optionGroup.dto.request.OptionGroupRequestDto;
import com.sparta.spartaoutsourcing.optionGroup.dto.response.OptionGroupSimpleResponseDto;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OptionGroupServiceTest {

    @InjectMocks
    private OptionGroupService optionGroupService;

    @Mock
    private OptionGroupRepository optionGroupRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private MenuRepository menuRepository;

    @Test
    void addOptionGroup() {
        // Given
        Long menuId = 1L;
        Menu menu = new Menu();
        OptionGroupRequestDto requestDto = new OptionGroupRequestDto("New Group", Arrays.asList(
                new MenuOptionRequestDto("Option 1", 1000L),
                new MenuOptionRequestDto("Option 2", 1500L)
        ));

        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        given(optionGroupRepository.existsByName(requestDto.getName())).willReturn(false);

        // When
        OptionGroupSimpleResponseDto response = optionGroupService.addOptionGroup(menuId, requestDto, new User());

        // Then
        assertEquals("New Group", response.getOptionGroup());
    }

    @Test
    void updateOptionGroup() {
        // Given
        Long optionGroupId = 1L;
        Menu menu = new Menu();
        User owner = new User("owner@example.com", "owner123", "owner", UserRole.OWNER);
        OptionGroup optionGroup = new OptionGroup("Old Group", menu, owner);
        optionGroup.setDeleted(true); // 기본값으로 삭제 상태 설정

        OptionGroupRequestDto requestDto = new OptionGroupRequestDto("Updated Group", null);
        given(optionGroupRepository.findById(optionGroupId)).willReturn(Optional.of(optionGroup));
        given(optionGroupRepository.existsByName(requestDto.getName())).willReturn(false);

        // When
        OptionGroupSimpleResponseDto response = optionGroupService.updateOptionGroup(optionGroupId, requestDto, owner);

        // Then
        assertEquals("Updated Group", response.getOptionGroup());
        assertEquals("Updated Group", optionGroup.getName());
    }

    @Test
    void deleteOptionGroup() {
        // Given
        Long optionGroupId = 1L;
        Menu menu = new Menu();
        OptionGroup optionGroup = new OptionGroup("Group", menu, new User());
        optionGroup.setDeleted(false); // 삭제되지 않은 상태로 초기화

        MenuOption menuOption = new MenuOption("Option 1", 1000L, optionGroup, menu);
        optionGroup.getMenuOptions().add(menuOption);

        given(optionGroupRepository.findById(optionGroupId)).willReturn(Optional.of(optionGroup));

        // When
        optionGroupService.deleteOptionGroup(optionGroupId);

        // Then
        assertTrue(optionGroup.getIsDeleted());
        assertTrue(menuOption.getIsDeleted());
    }

    @Test
    void restoreOptionGroup() {
        // Given
        Long optionGroupId = 1L;
        OptionGroup optionGroup = new OptionGroup("Group", new Menu(), new User());
        optionGroup.setDeleted(true); // 삭제된 상태로 초기화

        given(optionGroupRepository.findById(optionGroupId)).willReturn(Optional.of(optionGroup));

        // When
        optionGroupService.restoreOptionGroup(optionGroupId);

        // Then
        assertFalse(optionGroup.getIsDeleted());
    }

    @Test
    void getAllOptionGroupsByUserId() {
        // Given
        Long userId = 1L;
        Menu menu1 = new Menu();
        Menu menu2 = new Menu();
        OptionGroup optionGroup1 = new OptionGroup("Group 1", menu1, new User());
        OptionGroup optionGroup2 = new OptionGroup("Group 2", menu2, new User());
        List<OptionGroup> optionGroups = Arrays.asList(optionGroup1, optionGroup2);

        given(optionGroupRepository.findByIsDeletedFalseAndOwnerId(userId)).willReturn(optionGroups);

        // When
        List<OptionGroupSimpleResponseDto> response = optionGroupService.getAllOptionGroupsByUserId(userId);

        // Then
        assertEquals(2, response.size());
        assertEquals("Group 1", response.get(0).getOptionGroup());
        assertEquals("Group 2", response.get(1).getOptionGroup());
    }

    @Test
    void findById() {
        // Given
        Long groupId = 1L;
        User user = new User();
        user.setId(1L); // User ID 설정

        // Store 및 Menu 객체 생성
        Store store = new Store();
        Menu menu = new Menu(store, new Category(), "Sample Menu", 1000); // Menu 생성

        // OptionGroup 객체 생성
        OptionGroup optionGroup = new OptionGroup("Sample Option Group", menu, user);

        // Mocking repository response
        given(optionGroupRepository.findById(groupId)).willReturn(Optional.of(optionGroup));

        // When
        OptionGroup result = optionGroupService.findById(groupId);

        // Then
        assertEquals(optionGroup, result); // 반환된 OptionGroup이 일치해야 함
    }

    @Test
    void findMenuOwnerIdById() {
        // Given
        Long menuId = 1L;

        // User 객체 생성 및 ID 설정
        User user = new User();
        user.setId(2L);

        // Store 객체 생성 및 User 설정
        Store store = new Store();
        store.setUser(user); // Store에 User 설정

        // Category 객체 생성
        Category category = new Category();

        // Menu 객체 생성 (Store와 Category 포함)
        Menu menu = new Menu(store, category, "Sample Menu", 1000);

        // MenuRepository가 menuId에 대해 Menu 객체를 반환하도록 설정
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

        // When
        Long ownerId = optionGroupService.findMenuOwnerIdById(menuId);

        // Then
        assertEquals(2L, ownerId);
    }

    @Test
    void findMenuIdByGroupId() {
        // Given
        Long groupId = 1L;

        // User 객체 생성
        User user = new User();
        user.setId(1L); // User ID 설정

        // Store 및 Menu 객체 생성
        Store store = new Store();
        Menu menu = new Menu(store, new Category(), "Sample Menu", 1000); // Menu 생성

        // OptionGroup 객체 생성
        OptionGroup optionGroup = new OptionGroup("Sample Option Group", menu, user);

        // Mocking repository response
        given(optionGroupRepository.findById(groupId)).willReturn(Optional.of(optionGroup));

        // When
        Long result = optionGroupService.findMenuIdByGroupId(groupId);

        // Then
        assertEquals(menu.getId(), result); // Menu ID가 반환되어야 함
    }
}