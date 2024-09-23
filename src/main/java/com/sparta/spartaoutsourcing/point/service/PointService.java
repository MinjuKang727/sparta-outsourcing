package com.sparta.spartaoutsourcing.point.service;

import com.sparta.spartaoutsourcing.point.entity.Point;
import com.sparta.spartaoutsourcing.point.repository.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {
    final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public List<Point> getPoints(Long userId) {
        return pointRepository.findAllByUser_Id(userId);
    }
}
