package com.sparta.spartaoutsourcing.option.service;

import com.sparta.spartaoutsourcing.menu.exception.AlreadyDeletedException;
import com.sparta.spartaoutsourcing.menu.exception.AlreadyExistsException;
import com.sparta.spartaoutsourcing.menu.exception.NotFoundException;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.option.dto.request.MenuOptionRequestDto;
import com.sparta.spartaoutsourcing.option.dto.response.MenuOptionResponseDto;
import com.sparta.spartaoutsourcing.option.entity.MenuOption;
import com.sparta.spartaoutsourcing.option.repository.MenuOptionRepository;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "MenuOptionService")
@Service
@RequiredArgsConstructor
public class MenuOptionService {

    private final MenuOptionRepository menuOptionRepository;
    private final MenuRepository menuRepository;
    private final OptionGroupRepository optionGroupRepository;

    // 옵션 생성
    @Transactional
    public MenuOptionResponseDto addMenuOption(Long group_id, MenuOptionRequestDto requestDto) {
        log.info("addMenuOption() 메서드 실행");

        OptionGroup optionGroup = optionGroupRepository.findById(group_id)
                .orElseThrow(() -> new NotFoundException("해당하는 옵션 그룹이 없습니다."));

        // 옵션 그룹에 해당 옵션이 있는지 확인
        boolean exists = menuOptionRepository.existsByNameAndOptionGroup_Id(requestDto.getName(), group_id);
        if (exists) {
            throw new AlreadyExistsException("해당 옵션 그룹에 이미 같은 이름의 옵션이 존재합니다.");
        }

        MenuOption menuOption = new MenuOption(requestDto.getName(), requestDto.getPrice(), optionGroup, optionGroup.getMenu());
        menuOptionRepository.save(menuOption);

        return new MenuOptionResponseDto(menuOption);
    }

    // 옵션 수정
    @Transactional
    public MenuOptionResponseDto updateMenuOption(Long id, MenuOptionRequestDto requestDto) {
        log.info("updateMenuOption() 메서드 실행");

        MenuOption menuOption = menuOptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 메뉴 옵션이 없습니다."));

        // 이름과 가격이 모두 동일한지 확인
        if (menuOption.getName().equals(requestDto.getName()) && menuOption.getPrice().equals(requestDto.getPrice())) {
            throw new AlreadyExistsException("수정하려는 옵션이 이미 존재합니다."); // 에러 메시지 수정
        }

        // 이름이 변경되었는지 확인
        boolean isNameChanged = !menuOption.getName().equals(requestDto.getName());

        // 중복 여부 체크
        if (isNameChanged) {
            boolean exists = menuOptionRepository.existsByNameAndOptionGroup_Id(requestDto.getName(), menuOption.getOptionGroup().getId());
            if (exists) {
                throw new AlreadyExistsException("해당 옵션 그룹에 이미 같은 이름의 옵션이 존재합니다.");
            }
        }

        menuOption.update(requestDto.getName(), requestDto.getPrice());
        menuOptionRepository.save(menuOption);

        return new MenuOptionResponseDto(menuOption);
    }

    // 옵션 삭제
    @Transactional
    public void deleteMenuOption(Long id) {
        log.info("deleteMenuOption() 메서드 실행");

        MenuOption menuOption = menuOptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 메뉴 옵션이 없습니다."));

        menuOption.setDeleted(true);
        menuOptionRepository.save(menuOption);
    }

    // 옵션 복원
    @Transactional
    public void restoreMenuOption(Long id) {
        log.info("restoreMenuOption() 메서드 실행");

        MenuOption menuOption = menuOptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 메뉴 옵션이 없습니다."));

        if (!menuOption.getIsDeleted()) {
            throw new AlreadyDeletedException("해당하는 옵션은 이미 복원되었습니다.");
        }


        menuOption.setDeleted(false);
        menuOptionRepository.save(menuOption);
    }


    // 특정 옵션 그룹의 활성화된 모든 옵션 조회
    public List<MenuOptionResponseDto> getActiveMenuOptionsByGroup(Long groupId) {
        log.info("getActiveMenuOptionsByGroup() 메서드 실행");

        List<MenuOption> activeOptions = menuOptionRepository.findByIsDeletedAndOptionGroup_Id(false, groupId);
        return activeOptions.stream()
                .map(MenuOptionResponseDto::new)
                .collect(Collectors.toList());
    }

    // 옵션 ID에 해당하는 옵션 그룹 ID
    public Long getOptionGroupIdByOptionId(Long optionId) {
        MenuOption menuOption = menuOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("해당 옵션을 찾을 수 없습니다."));
        return menuOption.getOptionGroup().getId();
    }
}