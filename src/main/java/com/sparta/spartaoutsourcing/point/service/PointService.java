package com.sparta.spartaoutsourcing.point.service;

import com.sparta.spartaoutsourcing.point.entity.Point;
import com.sparta.spartaoutsourcing.point.repository.PointRepository;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PointService {
    final PointRepository pointRepository;
    final UserRepository userRepository;

    public PointService(PointRepository pointRepository, UserRepository userRepository) {
        this.pointRepository = pointRepository;
        this.userRepository = userRepository;
    }

    public List<Point> getPoints(Long userId) {
        return pointRepository.findAllByUser_Id(userId);
    }

    @Transactional
    public Point updatePoints(Long userId, Integer amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "해당하는 유저가 없습니다."));

        if (amount < 0) {
            int pointSum = getPoints(userId).stream().mapToInt(Point::getAmount).sum();
            if (pointSum < amount) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "포인트가 부족합니다.");
            }
        }
        
        Point point = Point.builder().user(user).amount(amount).build();
        return pointRepository.save(point);
    }
}
