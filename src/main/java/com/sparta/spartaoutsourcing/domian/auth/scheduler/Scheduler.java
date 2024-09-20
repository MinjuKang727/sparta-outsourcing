package com.sparta.spartaoutsourcing.domian.auth.scheduler;

import com.sparta.spartaoutsourcing.domian.auth.token.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final TokenBlacklistRepository blacklistRepository;

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    @Transactional
    public void removeTokenFromBlackList() {
        log.info("만료된 토큰 블랙리스트 정리");
        this.blacklistRepository.deleteByExpirationTimeBefore(new Date());
    }
}
