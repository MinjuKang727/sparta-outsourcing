//package com.sparta.spartaoutsourcing.util;
//
//import com.sparta.spartaoutsourcing.user.entity.User;
//import com.sparta.spartaoutsourcing.user.enums.UserRole;
//import com.sparta.spartaoutsourcing.user.repository.UserRepository;
//import com.sparta.spartaoutsourcing.user.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestDataRunner implements ApplicationRunner {
//    // ApplicationRunner를 상속받으면 Spring이 처음 실행될 때, 내부 코드들이 실행됨.
//    // 한번 사용하고 나서는 전체 주석처리하는 것이 좋습니다.
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//
//    @Override
//    public void run(ApplicationArguments args) {
//        // 테스트 User 생성
//        User testUser1 = new User("user1@sparta.com", "user1", passwordEncoder.encode("password1111!"), UserRole.USER);
//        testUser1 = userRepository.save(testUser1);
//        User testUser2 = new User("user2@sparta.com", "user2", passwordEncoder.encode("password222!"), UserRole.USER);
//        testUser2 = userRepository.save(testUser2);
//        User testOwner1 = new User("owner1@sparta.com", "owner1", passwordEncoder.encode("PASSWORD1111!"), UserRole.OWNER);
//        testOwner1 = userRepository.save(testOwner1);
//
//    }
//
//    private void createTestData() {
//
//    }
//}