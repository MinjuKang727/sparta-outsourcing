package com.sparta.spartaoutsourcing.store.service;

import com.sparta.spartaoutsourcing.store.dto.store.StoreRequestDto;
import com.sparta.spartaoutsourcing.store.dto.store.StoreResponseDto;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.exception.MaxStoreLimitException;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    //    가게 생성
    @Transactional
    public StoreResponseDto createStore(Long userId, StoreRequestDto storeRequestDto) {
        User user = userRepository.findById(userId).orElseThrow((
                () -> new NullPointerException("회원이 아닙니다")
        ));

        Long activeStoreCount = storeRepository.countStoreByIsCloseFalse(user);
        if (activeStoreCount + 1 > 3) {
            throw new MaxStoreLimitException("가게는 3개까지 생성 할 수 있습니다");
        }

//        검증
        validateStoreRequest(userId, storeRequestDto);

        Store store = new Store(storeRequestDto);
        store.setUsers(user);
        Store saveStore = storeRepository.save(store);

        return new StoreResponseDto(saveStore);
    }

    //    가게 단건 조회
    @Transactional
    public StoreResponseDto getStore(Long storeId) {
        Store foundStore = storeRepository.findById(storeId).orElseThrow(()
                -> new NullPointerException("가게가 존재 하지 없습니다"));

        if (foundStore.isClose()) {
            throw new IllegalArgumentException("폐업된 가게 입니다");
        }
//        가게 조회 시 보이는 메뉴
        return new StoreResponseDto(foundStore);
    }

    //    가게 목록 조회
    @Transactional
    public List<StoreResponseDto> findAllStores(String storeName) {
        List<Store> stores = storeRepository.findByIsCloseFalseAndStoreNameContaining(storeName);
        return stores.stream().map(StoreResponseDto::new).toList();
    }

    //    가게 수정
    @Transactional
    public Store updateStore(Long userId, Long storeId, StoreRequestDto storeRequestDto) throws AccessDeniedException {
        User user = userRepository.findById(userId).orElseThrow((
                () -> new NullPointerException("회원이 아닙니다")
        ));

        if (user.getRole() != UserRole.OWNER) {
            throw new AccessDeniedException("가게 수정는 사장님만 가능합니다");
        }

        Store foundStore = storeRepository.findByUsersIdAndId(userId, storeId).orElseThrow(()
                -> new NullPointerException("가게가 존재 하지 않습니다"));


        foundStore.update(storeRequestDto.getStoreName(), storeRequestDto.getOpenTime(), storeRequestDto.getCloseTime(), storeRequestDto.getMinOrderPrice(),storeRequestDto.getOwnerContent());
        return storeRepository.save(foundStore);
    }

    //    가게 폐업
    public String storeClose(Long userId, Long storeId) throws AccessDeniedException {
        User user = userRepository.findById(userId).orElseThrow((
                () -> new NullPointerException("회원이 아닙니다")
        ));
        if (user.getRole() != UserRole.OWNER) {
            throw new AccessDeniedException("가게 폐업는 사장님만 가능합니다");
        }

        Store foundStore = storeRepository.findById(storeId).orElseThrow(() ->
                new NullPointerException("가게가 존재 하지 않습니다"));

        if (foundStore.isClose()) {
            throw new IllegalArgumentException("이미 폐업한 가게 입니다");
        }

//        폐점활성화
        foundStore.activateStore();
        storeRepository.save(foundStore);

        return "폐업 되었습니다";
    }

    //    검증
    private void validateStoreRequest(Long userId, StoreRequestDto storeRequestDto) {

        User user = userRepository.findById(userId).orElseThrow((
                () -> new NullPointerException("회원이 아닙니다")
        ));

        if (user.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("가게 생성은 사장님만 가능합니다");
        }


        if (storeRequestDto.getOpenTime() == null && storeRequestDto.getCloseTime() == null) {
            throw new NullPointerException("오픈시간 또는 마감 시간이 누락되었습니다");
        }
        if (storeRequestDto.getMinOrderPrice() == null) {
            throw new NullPointerException("최소 주문 금액이 누락되었습니다");
        }
    }

}
