package com.sparta.spartaoutsourcing.point.repository;

import com.sparta.spartaoutsourcing.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findAllByUser_Id(Long user_id);
}
