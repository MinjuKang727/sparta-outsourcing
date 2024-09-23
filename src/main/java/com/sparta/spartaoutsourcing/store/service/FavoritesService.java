package com.sparta.spartaoutsourcing.store.service;

import com.sparta.spartaoutsourcing.store.dto.favorites.FavoritesResponseDto;
import com.sparta.spartaoutsourcing.store.dto.store.StoreResponseDto;
import com.sparta.spartaoutsourcing.store.entity.Favorites;
import com.sparta.spartaoutsourcing.store.entity.Store;
import com.sparta.spartaoutsourcing.store.exception.FavoriteNotFoundException;
import com.sparta.spartaoutsourcing.store.exception.StoreNotFoundException;
import com.sparta.spartaoutsourcing.store.exception.UserNotFoundException;
import com.sparta.spartaoutsourcing.store.repository.FavoritesRepository;
import com.sparta.spartaoutsourcing.store.repository.StoreRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    //    찜하기
    @Transactional
    public void addFavorite(Long userId, Long storeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("등록된 회원이 아닙니다"));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreNotFoundException("등록된 가게가 없습니다"));

        if (store.isClose()) {
            throw new IllegalArgumentException("폐업된 가게 입니다");
        }

        Optional<Favorites> favoritesOptional = favoritesRepository.findByUsersAndStores(user, store);
        Favorites favorites;
//        즐겨찾기에 없으면 추가
        if (favoritesOptional.isEmpty()) {
            favorites = new Favorites(user, store);
            favorites.setFavorite(true);
            favoritesRepository.save(favorites);
        }else{
//            즐겨찾기에 이미 있으면 비활성화
            favorites = favoritesOptional.get();
            favorites.setFavorite(!favorites.isFavorite());
            favoritesRepository.save(favorites);
        }

    }

    //    즐겨찾기 목록
    public List<FavoritesResponseDto> getListFavorites(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("등록된 회원이 아닙니다"));
        List<Favorites> favorites = favoritesRepository.findByUsers(user);

        if (favorites.isEmpty()) {
            throw new FavoriteNotFoundException("즐겨찾기 된 가게가 없습니다");
        }
        return favorites.stream().map(FavoritesResponseDto::new).toList();
    }
}
