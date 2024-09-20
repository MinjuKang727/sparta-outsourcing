package com.sparta.spartaoutsourcing.auth.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "token_blacklist")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    private Date expirationTime;

    public Token(String tokenValue, Date expirationTime) {
        this.token = tokenValue;
        this.expirationTime = expirationTime;
    }
}
