package com.sparta.spartaoutsourcing.optionGroup.service;

import com.sparta.spartaoutsourcing.menu.exception.AlreadyDeletedException;
import com.sparta.spartaoutsourcing.menu.exception.AlreadyExistsException;
import com.sparta.spartaoutsourcing.menu.exception.NotFoundException;
import com.sparta.spartaoutsourcing.menu.entity.Menu;
import com.sparta.spartaoutsourcing.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.option.repository.MenuOptionRepository;
import com.sparta.spartaoutsourcing.optionGroup.dto.request.OptionGroupRequestDto;
import com.sparta.spartaoutsourcing.optionGroup.dto.response.OptionGroupSimpleResponseDto;
import com.sparta.spartaoutsourcing.optionGroup.entity.OptionGroup;
import com.sparta.spartaoutsourcing.optionGroup.repository.OptionGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "OptionGroupService")
@Service
@RequiredArgsConstructor
public class OptionGroupService {

    private final MenuRepository menuRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final MenuOptionRepository menuOptionRepository;

    // 옵션 그룹 생성
    @Transactional
    public OptionGroupSimpleResponseDto addOptionGroup(Long menu_id, OptionGroupRequestDto requestDto) {
        log.info("addOptionGroup() 메서드 실행");

        Menu menu = menuRepository.findById(menu_id)
                .orElseThrow(() -> new NotFoundException("해당하는 메뉴가 없습니다."));

        if (optionGroupRepository.existsByName(requestDto.getName())) {
            throw new AlreadyExistsException("해당 이름의 옵션 그룹이 이미 존재합니다.");
        }

        OptionGroup optionGroup = new OptionGroup(requestDto.getName(), menu);

        optionGroupRepository.save(optionGroup);
        return new OptionGroupSimpleResponseDto(optionGroup);
    }

    // 옵션 그룹 수정
    @Transactional
    public OptionGroupSimpleResponseDto updateOptionGroup(Long id, OptionGroupRequestDto requestDto) {
        log.info("updateOptionGroup() 메서드 실행");

        OptionGroup optionGroup = optionGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 옵션 그룹이 없습니다."));

        if (optionGroupRepository.existsByName(requestDto.getName())) {
            throw new AlreadyExistsException("해당하는 카테고리는 이미 존재합니다.");
        }

        // 이름이 변경되었는지 확인
        if (!optionGroup.getName().equals(requestDto.getName())) {
            // 옵션이 존재하는지 확인
            if (menuOptionRepository.existsByOptionGroup_Id(optionGroup.getId())) {
                // 새로 생성하는 로직
                OptionGroup newGroup = new OptionGroup(requestDto.getName(), optionGroup.getMenu());
                optionGroupRepository.save(newGroup);
                return new OptionGroupSimpleResponseDto(newGroup);
            }

            // 기존 이름이 아닌 새로운 이름이 이미 존재하는지 확인
            if (optionGroupRepository.existsByName(requestDto.getName())) {
                throw new AlreadyExistsException("해당 이름의 옵션 그룹이 이미 존재합니다.");
            }
        }

        // 이름이 변경되지 않았거나 옵션이 없는 경우 수정
        optionGroup.update(requestDto.getName());
        optionGroupRepository.save(optionGroup);

        return new OptionGroupSimpleResponseDto(optionGroup);
    }

    // 옵션 그룹 삭제
    @Transactional
    public void deleteOptionGroup(Long id) {
        log.info("deleteOptionGroup() 메서드 실행");

        OptionGroup optionGroup = optionGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 옵션 그룹이 없습니다."));

        // 해당하는 옵션 그룹에 옵션이 존재하는 지 확인
        if (!optionGroup.getMenuOptions().isEmpty()) {
            // 옵션들의 상태를 변경
            for (var menuOption : optionGroup.getMenuOptions()) {
                menuOption.setDeleted(true);
                menuOptionRepository.save(menuOption);
            }
        }
        
        optionGroup.setDeleted(true);
        optionGroupRepository.save(optionGroup);
    }

    // 옵션 그룹 복원
    @Transactional
    public void restoreOptionGroup(Long id) {
        log.info("restoreOptionGroup() 메서드 실행");

        OptionGroup optionGroup = optionGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 옵션 그룹이 없습니다."));

        // 해당 옵션 그룹이 이미 복원되었는지 확인
        if (!optionGroup.getIsDeleted()) {
            throw new AlreadyDeletedException("해당하는 옵션 그룹은 이미 복원되었습니다.");
        }

        optionGroup.setDeleted(false);
        optionGroupRepository.save(optionGroup);

        // 해당 그룹에 해당하는 옵션들도 복원
        for (var option : optionGroup.getMenuOptions()) {
            option.setDeleted(false);
            menuOptionRepository.save(option);
        }
    }

    // 활성화된 모든 옵션 그룹 조회
    public List<OptionGroupSimpleResponseDto> getAllOptionGroups() {
        log.info("getAllOptionGroups() 메서드 실행");

        List<OptionGroup> activeOptionGroup = optionGroupRepository.findByIsDeleted(false);
        return activeOptionGroup.stream()
                .map(OptionGroupSimpleResponseDto::new)
                .collect(Collectors.toList());
    }
}