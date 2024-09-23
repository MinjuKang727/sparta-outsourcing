package com.sparta.spartaoutsourcing.menu.service;

import com.sparta.spartaoutsourcing.category.entity.Category;
import com.sparta.spartaoutsourcing.category.repository.CategoryRepository;
import com.sparta.spartaoutsourcing.menu.dto.request.MenuRequestDto;
import com.sparta.spartaoutsourcing.menu.dto.response.MenuResponseDto;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.menu.exception.AlreadyDeletedException;
import com.sparta.spartaoutsourcing.menu.exception.AlreadyExistsException;
import com.sparta.spartaoutsourcing.menu.exception.NotFoundException;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.option.entity.MenuOption;
import com.sparta.spartaoutsourcing.option.repository.MenuOptionRepository;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j(topic = "MenuService")
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final OptionGroupRepository optionGroupRepository;


    // 메뉴 생성
    public MenuResponseDto createMenu(Long store_id, MenuRequestDto menuRequestDto) {
        log.info("createMenu() 메서드 실행");

        // 가게 존재 유무 확인
        Store store = storeRepository.findById(store_id)
                .orElseThrow(() -> new NotFoundException("해당하는 가게가 없습니다."));

        // 카테고리 확인(없을 시 생성)
        Category category = categoryRepository.findByName(menuRequestDto.getCategory().getName())
                .orElseGet(() -> {
                    Category newCategory = new Category(menuRequestDto.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });

        // 카테고리가 비활성화 상태면 예외 발생
        if (category.getIsDeleted()) {
            throw new NotFoundException("해당 카테고리는 삭제되었습니다.");
        }

        // 메뉴 존재 유무 확인
        if (menuRepository.existsByMenuNameAndStoreId(menuRequestDto.getName(), store_id)) {
            throw new AlreadyExistsException("해당가게의 메뉴가 이미 존재합니다.");
        }
        
        // 메뉴 생성
        Menu menu = new Menu(store, category, menuRequestDto.getName(), menuRequestDto.getPrice());

        // 메뉴를 먼저 저장
        menuRepository.save(menu);


        // 옵션 그룹과 옵션 생성
        if (menuRequestDto.getOptionGroups() != null) {
            for (var groupDto : menuRequestDto.getOptionGroups()) {
                OptionGroup optionGroup = new OptionGroup(groupDto.getName(), menu); // 메뉴 포함
                optionGroupRepository.save(optionGroup); // 옵션 그룹 저장

                for (var optionDto : groupDto.getOptions()) {
                    MenuOption menuOption = new MenuOption(optionDto.getName(), optionDto.getPrice(), optionGroup, menu);
                    optionGroup.getMenuOptions().add(menuOption);
                    menuOptionRepository.save(menuOption); // 메뉴 옵션 저장
                }
                menu.getOptionGroups().add(optionGroup); // 메뉴에 옵션 그룹 추가
            }
        }

        // 메뉴 저장
        menuRepository.save(menu);
        return new MenuResponseDto(menu);

    }

    // 메뉴 수정
    public MenuResponseDto updateMenu(Long store_id, Long menu_id, MenuRequestDto menuRequestDto) {
        log.info("updateMenu() 메서드 실행");

        Menu menu = menuRepository.findById(menu_id)
                .orElseThrow(() -> new NotFoundException("해당하는 메뉴가 없습니다."));

        if (!menu.getStore().getId().equals(store_id)) {
            throw new NotFoundException("해당하는 메뉴가 등록된 가게가 아닙니다.");
        }

        // 카테고리 수정
        if (menuRequestDto.getCategory() != null && menuRequestDto.getCategory().getName() != null) {
            // 기존 카테고리 찾기
            Category newCategory = categoryRepository.findByName(menuRequestDto.getCategory().getName())
                    .orElseGet(() -> {
                        // 새 카테고리 생성
                        return categoryRepository.save(new Category(menuRequestDto.getCategory().getName()));
                    });

            // 카테고리가 비활성화 상태면 예외 발생
            if (newCategory.getIsDeleted()) {
                throw new NotFoundException("해당 카테고리는 삭제되었습니다.");
            }

            // 현재 메뉴의 카테고리와 새 카테고리가 다르면 변경
            if (!menu.getCategory().getName().equals(newCategory.getName())) {
                menu.setCategory(newCategory);
            }
        }

        // 메뉴 이름 및 가격 수정
        menu.update(menuRequestDto.getName(), menuRequestDto.getPrice());

        // 옵션 그룹 수정 또는 추가
        if (menuRequestDto.getOptionGroups() != null) {
            for (var groupDto : menuRequestDto.getOptionGroups()) {
                // 기존 옵션 그룹이 있는지 확인
                Optional<OptionGroup> existingGroupOpt = menu.getOptionGroups().stream()
                        .filter(group -> group.getName().equals(groupDto.getName()))
                        .findFirst();

                if (existingGroupOpt.isPresent()) {
                    // 기존 옵션 그룹이 있을 경우, 기존 옵션 업데이트
                    OptionGroup existingGroup = existingGroupOpt.get();
                    existingGroup.getMenuOptions().clear(); // 기존 옵션 지우기

                    for (var optionDto : groupDto.getOptions()) {
                        MenuOption menuOption = new MenuOption(optionDto.getName(), optionDto.getPrice(), existingGroup, menu);
                        existingGroup.getMenuOptions().add(menuOption);
                        menuOptionRepository.save(menuOption);
                    }
                } else {
                    // 새 옵션 그룹 추가
                    OptionGroup newGroup = new OptionGroup(groupDto.getName(), menu);
                    optionGroupRepository.save(newGroup);

                    // 새 옵션 추가
                    for (var optionDto : groupDto.getOptions()) {
                        MenuOption menuOption = new MenuOption(optionDto.getName(), optionDto.getPrice(), newGroup, menu);
                        newGroup.getMenuOptions().add(menuOption);
                        menuOptionRepository.save(menuOption);
                    }
                    menu.getOptionGroups().add(newGroup);
                }
            }
        }

        menuRepository.save(menu);
        return new MenuResponseDto(menu);

    }

    // 메뉴 삭제
    public void deleteMenu(Long store_id, Long menu_id) {
        log.info("deleteMenu() 메서드 실행");

        Menu menu = menuRepository.findById(menu_id)
                .orElseThrow(() -> new NotFoundException("해당하는 메뉴가 없습니다."));
        
        // 해당 메뉴가 해당 가게에 있는지 확인
        if (!menu.getStore().getId().equals(store_id)) {
            throw new NotFoundException("해당하는 메뉴가 등록된 가게가 아닙니다.");
        }

        // 해당 메뉴가 이미 삭제되었는지 확인
        if (menu.getIsDeleted()) {
            throw new AlreadyDeletedException("해당하는 메뉴는 이미 삭제되었습니다.");
        }

        // 해당 카테고리가 이미 삭제되었는지 확인
        if (menu.getCategory().getIsDeleted()) {
            throw new NotFoundException("해당 카테고리는 삭제되었습니다.");
        }

        // 메뉴 상태 변경(삭제상태 상태로)
        menu.setDeleted(true);
        menuRepository.save(menu);

        // 옵션 그룹과 옵션 상태 변경(삭제상태)
        for (var optionGroup : menu.getOptionGroups()) {
            optionGroup.setDeleted(true);
            optionGroupRepository.save(optionGroup); // 옵션 그룹 저장

            for (var menuOption : optionGroup.getMenuOptions()) {
                menuOption.setDeleted(true);
                menuOptionRepository.save(menuOption); // 메뉴 옵션 저장
            }
        }
    }

    // 메뉴 복원
    public void restoreMenu(Long store_id, Long menu_id) {
        log.info("updateMenu() 메서드 실행");

        Menu menu = menuRepository.findById(menu_id)
                .orElseThrow(() -> new NotFoundException("해당하는 메뉴가 없습니다."));

        if (!menu.getStore().getId().equals(store_id)) {
            throw new NotFoundException("해당하는 메뉴가 등록된 가게가 아닙니다.");
        }

        if (!menu.getIsDeleted()) {
            throw new AlreadyDeletedException("해당하는 메뉴는 이미 복원되었습니다.");
        }

        if (menu.getCategory().getIsDeleted()) {
            throw new NotFoundException("해당 카테고리는 삭제되었습니다.");
        }

        // 메뉴 상태 변경(복원 상태로)
        menu.setDeleted(false);
        menuRepository.save(menu);

        // 옵션 그룹과 옵션 복원
        for (var optionGroup : menu.getOptionGroups()) {
            optionGroup.setDeleted(false);
            optionGroupRepository.save(optionGroup); // 옵션 그룹 복원

            for (var menuOption : optionGroup.getMenuOptions()) {
                menuOption.setDeleted(false);
                menuOptionRepository.save(menuOption); // 메뉴 옵션 복원
            }
        }
    }
}
