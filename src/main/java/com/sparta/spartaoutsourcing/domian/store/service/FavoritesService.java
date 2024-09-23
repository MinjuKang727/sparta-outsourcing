package com.sparta.spartaoutsourcing.domian.store.service;

import com.sparta.spartaoutsourcing.domian.store.dto.favorites.FavoritesRequestDto;
import com.sparta.spartaoutsourcing.domian.store.dto.favorites.FavoritesResponseDto;
import com.sparta.spartaoutsourcing.domian.store.entity.Favorites;
import com.sparta.spartaoutsourcing.domian.store.entity.Store;
import com.sparta.spartaoutsourcing.domian.store.exception.FavoriteDuplicationException;
import com.sparta.spartaoutsourcing.domian.store.exception.StoreNotFoundException;
import com.sparta.spartaoutsourcing.domian.store.exception.UserNotFoundException;
import com.sparta.spartaoutsourcing.domian.store.repository.FavoritesRepository;
import com.sparta.spartaoutsourcing.domian.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

//    찜하기
    @Transactional
    public FavoritesResponseDto addFavorite(Long userId, Long storeId, FavoritesRequestDto favoritesRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("등록된 회원이 아닙니다"));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreNotFoundException("등록된 가게가 없습니다"));

        if(favoritesRepository.findByUsersAndStores(user, store).isPresent()) {
            throw new FavoriteDuplicationException("이미 찜한 가게 입니다");
        }

        Favorites favorites = new Favorites(user, store, favoritesRequestDto);
        return new FavoritesResponseDto(favoritesRepository.save(favorites));
    }

//    찜해제
//    public void removeFavorite(Long userId, Long storeId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("등록된 회원이 아닙니다"));
//        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreNotFoundException("등록된 가게가 없습니다"));
//        favoritesRepository.deleteByUsersAndStores(user, store);
//    }
}
