package com.sparta.spartaoutsourcing.domian.store.service;

import com.sparta.spartaoutsourcing.domian.store.dto.StoreRequestDto;
import com.sparta.spartaoutsourcing.domian.store.dto.StoreResponseDto;
import com.sparta.spartaoutsourcing.domian.store.entity.Store;
import com.sparta.spartaoutsourcing.domian.store.repository.DeleteStoreRepository;
import com.sparta.spartaoutsourcing.domian.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.domian.store.repository.UserRepository;
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
    private final DeleteStoreRepository deleteStoreRepository;

//    가게 생성
    @Transactional
    public StoreResponseDto createStore(StoreRequestDto storeRequestDto) {
//        검증
        validateStoreRequest(storeRequestDto);
        
        Store store = new Store(storeRequestDto);
        Store saveStore = storeRepository.save(store);

        return new StoreResponseDto(saveStore);
    }
//    가게 단건 조회
    @Transactional
    public Store getStore(Long userId,Long storeId) {
        userRepository.findById(userId).orElseThrow(() -> new NullPointerException("회원이 아닙니다"));
        return storeRepository.findById(storeId).orElseThrow(()
                -> new RuntimeException("가게가 존재 하지 않습니다"));
    }
//    가게 목록 조회
    @Transactional
    public List<StoreResponseDto> findAllStores(String storeName) {
        storeRepository.findByStoreName(storeName).orElseThrow(() -> new NullPointerException("가게 이름이 잘못 되었거나 가게가 존재 하지 않습니다"));
        return storeRepository.findAll().stream().map(StoreResponseDto::new).toList();
    }
//    가게 수정
    @Transactional
    public Store updateStore(Long userId,Long storeId, StoreRequestDto storeRequestDto) throws AccessDeniedException {
        Store foundStore = storeRepository.findById(storeId).orElseThrow(()
                -> new NullPointerException("가게가 없습니다"));

        if(!userRepository.existsById(userId)){
            throw new AccessDeniedException("사장님이 아닙니다");
        }

        foundStore.update(storeRequestDto.getStoreName(),storeRequestDto.getOpenTime(),storeRequestDto.getCloseTime(),storeRequestDto.getMinOrderPrice());
        return storeRepository.save(foundStore);
    }
//    가게 삭제
    public String storeDelete(Long userId,Long storeId) throws AccessDeniedException {
        Store foundStore = storeRepository.findById(storeId).orElseThrow(() -> new NullPointerException("가게가 존재 하지 않습니다"));
        if(!userRepository.existsById(userId)){
            throw new AccessDeniedException("사장님이 아닙니다");
        }

        storeRepository.deleteById(foundStore.getId());
        
//        폐점활성화
        foundStore.activateStore();
        deleteStoreRepository.save(foundStore);

        return "삭제 성공";
    }

    private void validateStoreRequest(StoreRequestDto storeRequestDto) {
        if(storeRequestDto.getOpenTime() == null && storeRequestDto.getCloseTime() == null){
            throw new NullPointerException("오픈시간 또는 마감 시간이 누락되었습니다");
        }
        if(storeRequestDto.getMinOrderPrice() == null){
            throw new NullPointerException("최소 주문 금액이 누락되었습니다");
        }
    }

}
