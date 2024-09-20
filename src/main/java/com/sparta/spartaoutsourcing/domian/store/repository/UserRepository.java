package com.sparta.spartaoutsourcing.domian.store.repository;

import com.sparta.spartaoutsourcing.domian.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
