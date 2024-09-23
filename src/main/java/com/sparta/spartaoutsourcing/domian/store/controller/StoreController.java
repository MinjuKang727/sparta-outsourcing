package com.sparta.spartaoutsourcing.domian.store.controller;

import com.sparta.spartaoutsourcing.domian.store.dto.store.StoreRequestDto;
import com.sparta.spartaoutsourcing.domian.store.dto.store.StoreResponseDto;
import com.sparta.spartaoutsourcing.domian.store.entity.Store;
import com.sparta.spartaoutsourcing.domian.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

//    가게 생성
    @PostMapping("/{userId}")
    public ResponseEntity<StoreResponseDto> create(@PathVariable("userId") Long userId, @RequestBody StoreRequestDto storeRequestDto) {
        return ResponseEntity.ok(storeService.createStore(userId,storeRequestDto));
    }

//    가게 단건 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<Store> getStore( @PathVariable("storeId") Long storeId) {
        return ResponseEntity.ok(storeService.getStore(storeId));
    }

//    가게 목록 조회
    @GetMapping("/name/{storeName}")
    public ResponseEntity<List<StoreResponseDto>> findAllStores(@PathVariable("storeName") String storeName) {
        return ResponseEntity.ok(storeService.findAllStores(storeName));
    }

//    가게 수정
    @PutMapping("/{userId}/{storeId}")
    public ResponseEntity<Store>update(@PathVariable("userId") Long userId, @PathVariable("storeId") Long storeId, @RequestBody StoreRequestDto storeRequestDto) throws AccessDeniedException {
        return ResponseEntity.ok(storeService.updateStore(userId,storeId,storeRequestDto));

    }
//    가게 삭제
    @DeleteMapping("/{userId}/{storeId}")
    public ResponseEntity<String> delete(@PathVariable("userId") Long userId, @PathVariable("storeId") Long storeId) throws AccessDeniedException {
        return ResponseEntity.ok(storeService.storeClose(userId,storeId));
    }

}
