package com.sparta.spartaoutsourcing.option.service;

import com.sparta.spartaoutsourcing.category.entity.Category;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import com.sparta.spartaoutsourcing.option.dto.response.MenuOptionResponseDto;
import com.sparta.spartaoutsourcing.option.entity.MenuOption;
import com.sparta.spartaoutsourcing.option.repository.MenuOptionRepository;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuOptionServiceTest {

    @InjectMocks
    private MenuOptionService menuOptionService;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private OptionGroupRepository optionGroupRepository;

    @Test
    void addMenuOption() {
        // Given
        Long groupId = 1L;
        MenuOptionRequestDto requestDto = new MenuOptionRequestDto("New Option", 1000L);
        OptionGroup optionGroup = new OptionGroup();

        given(optionGroupRepository.findById(groupId)).willReturn(Optional.of(optionGroup));
        given(menuOptionRepository.existsByNameAndOptionGroup_Id(requestDto.getName(), groupId)).willReturn(false);
        given(menuOptionRepository.save(any(MenuOption.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        MenuOptionResponseDto responseDto = menuOptionService.addMenuOption(groupId, requestDto);

        // Then
        assertNotNull(responseDto);
        assertEquals("New Option", responseDto.getOption());
        assertEquals(1000, responseDto.getPrice());
    }

    @Test
    void updateMenuOption() {
        // Given
        Long optionId = 1L;
        MenuOption existingOption = new MenuOption("Old Option", 1000L, new OptionGroup(), null);
        MenuOptionRequestDto requestDto = new MenuOptionRequestDto("Updated Option", 1500L);

        given(menuOptionRepository.findById(optionId)).willReturn(Optional.of(existingOption));
        given(menuOptionRepository.existsByNameAndOptionGroup_Id(requestDto.getName(), existingOption.getOptionGroup().getId())).willReturn(false);

        // When
        MenuOptionResponseDto responseDto = menuOptionService.updateMenuOption(optionId, requestDto);

        // Then
        assertEquals("Updated Option", responseDto.getOption());
        assertEquals(1500, responseDto.getPrice());
    }

    @Test
    void deleteMenuOption() {
        // Given
        Long optionId = 1L;
        MenuOption existingOption = new MenuOption("Option to Delete", 1000L, new OptionGroup(), null);

        given(menuOptionRepository.findById(optionId)).willReturn(Optional.of(existingOption));

        // When
        menuOptionService.deleteMenuOption(optionId);

        // Then
        assertTrue(existingOption.getIsDeleted());
    }

    @Test
    void restoreMenuOption() {
        // Given
        Long optionId = 1L;
        MenuOption existingOption = new MenuOption("Option to Restore", 1000L, new OptionGroup(), null);
        existingOption.setDeleted(true);

        given(menuOptionRepository.findById(optionId)).willReturn(Optional.of(existingOption));

        // When
        menuOptionService.restoreMenuOption(optionId);

        // Then
        assertFalse(existingOption.getIsDeleted());
    }

    @Test
    void getActiveMenuOptionsByGroup() {
        // Given
        Long groupId = 1L;
        OptionGroup optionGroup = new OptionGroup();
        MenuOption activeOption1 = new MenuOption("Active Option 1", 1000L, optionGroup, null);
        MenuOption activeOption2 = new MenuOption("Active Option 2", 1500L, optionGroup, null);

        given(menuOptionRepository.findByIsDeletedAndOptionGroup_Id(false, groupId))
                .willReturn(List.of(activeOption1, activeOption2));

        // When
        List<MenuOptionResponseDto> responseDtos = menuOptionService.getActiveMenuOptionsByGroup(groupId);

        // Then
        assertEquals(2, responseDtos.size());
        assertEquals("Active Option 1", responseDtos.get(0).getOption());
        assertEquals(1000, responseDtos.get(0).getPrice());
        assertEquals("Active Option 2", responseDtos.get(1).getOption());
        assertEquals(1500, responseDtos.get(1).getPrice());
    }

    @Test
    void getOptionGroupIdByOptionId() {
        // Given
        Long optionId = 1L;

        // User 객체 생성
        User user = new User();
        user.setId(1L); // User ID 설정

        // Store 및 Menu 객체 생성
        Store store = new Store();
        Menu menu = new Menu(store, new Category(), "Sample Menu", 1000); // Menu 생성

        // OptionGroup 객체 생성
        OptionGroup optionGroup = new OptionGroup("Sample Option Group", menu, user);

        // MenuOption 객체 생성
        MenuOption menuOption = new MenuOption("Sample Option", 1000L, optionGroup, null);

        // Mocking repository response
        given(menuOptionRepository.findById(optionId)).willReturn(Optional.of(menuOption));

        // When
        Long result = menuOptionService.getOptionGroupIdByOptionId(optionId);

        // Then
        assertEquals(optionGroup.getId(), result); // OptionGroup ID가 반환되어야 함
    }
}