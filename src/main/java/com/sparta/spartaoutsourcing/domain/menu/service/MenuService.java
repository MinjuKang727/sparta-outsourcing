package com.sparta.spartaoutsourcing.domain.menu.service;

import com.sparta.spartaoutsourcing.domain.exception.AlreadyDeletedException;
import com.sparta.spartaoutsourcing.domain.exception.NotFoundException;
import com.sparta.spartaoutsourcing.domain.menu.dto.request.MenuRequestDto;
import com.sparta.spartaoutsourcing.domain.menu.dto.response.MenuResponseDto;
import com.sparta.spartaoutsourcing.domain.menu.entity.Menu;
import com.sparta.spartaoutsourcing.domain.menu.repository.MenuRepository;
import com.sparta.spartaoutsourcing.domain.store.entity.Store;
import com.sparta.spartaoutsourcing.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "MenuService")
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public MenuResponseDto createMenu(Long store_id, MenuRequestDto menuRequestDto) {
        log.info("createMenu() 메서드 실행");

        Store store = storeRepository.findById(store_id)
                .orElseThrow(() -> new NotFoundException("해당하는 가게가 없습니다."));

        // 메뉴 생성
        Menu menu = new Menu(store, menuRequestDto.getName(), menuRequestDto.getPrice());

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

        // 메뉴 이름 및 가격 수정
        menu.update(menuRequestDto.getName(), menuRequestDto.getPrice());


        menuRepository.save(menu);
        return new MenuResponseDto(menu);
    }

    // 메뉴 삭제
    public void deleteMenu(Long store_id, Long menu_id) {
        log.info("deleteMenu() 메서드 실행");

        Menu menu = menuRepository.findById(menu_id)
                .orElseThrow(() -> new NotFoundException("해당하는 메뉴가 없습니다."));

        if (!menu.getStore().getId().equals(store_id)) {
            throw new NotFoundException("해당하는 메뉴가 등록된 가게가 아닙니다.");
        }

        if (menu.getIsDeleted()) {
            throw new AlreadyDeletedException("해당하는 메뉴는 이미 삭제되었습니다.");
        }

        menu.setDeleted(true);
        menuRepository.save(menu);
    }
}
