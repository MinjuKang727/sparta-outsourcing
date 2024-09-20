package com.sparta.spartaoutsourcing.domian.store.repository;

import com.sparta.spartaoutsourcing.domian.store.entity.Store;
import com.sparta.spartaoutsourcing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByUsersIdAndId(Long userId, Long storeId);

    @Query("select s from Store s where s.isClose = false and s.storeName like concat('%', ?1, '%')")
    List<Store> findByIsCloseFalseAndStoreNameContaining(String storeName);

    @Query("select count(s) from Store s where s.isClose = false")
    Long countStoreByIsCloseFalse(User user);
}
