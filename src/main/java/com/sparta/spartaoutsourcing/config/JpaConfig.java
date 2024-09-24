package com.sparta.spartaoutsourcing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 아래 설정을 등록하여 활성화 합니다.
@EnableJpaAuditing // 시간 자동 변경이 가능하도록 합니다.
// Controller 테스트에 Repository 부분은 필요가 없음.
// @EnableAuditing이 Mockito에 방해되어서 따로 JpaConfig를 만들었음
public class JpaConfig {
}