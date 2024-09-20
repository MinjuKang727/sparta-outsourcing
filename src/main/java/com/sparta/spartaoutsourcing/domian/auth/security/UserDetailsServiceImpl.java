package com.sparta.spartaoutsourcing.domian.auth.security;

import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j(topic = "UserDetailsServiceImpl")
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findUserByEmailAndIsDeleted(email, false);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Not Found : " + email);
        }

        return new UserDetailsImpl(user.get());
    }

}
