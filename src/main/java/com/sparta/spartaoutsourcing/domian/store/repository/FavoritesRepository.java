package com.sparta.spartaoutsourcing.domian.store.repository;

import com.sparta.spartaoutsourcing.domian.store.entity.Favorites;
import com.sparta.spartaoutsourcing.domian.store.entity.Store;
import com.sparta.spartaoutsourcing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    Optional<Object> findByUsersAndStores(User user, Store store);
}
