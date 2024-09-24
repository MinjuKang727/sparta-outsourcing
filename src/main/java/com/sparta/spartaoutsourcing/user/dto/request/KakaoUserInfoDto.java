package com.sparta.spartaoutsourcing.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    @NotNull
    private Long id;
    private String nickname;
    @NotBlank(message = "이메일은 null이나 공백일 수 없습니다.")
    private String email;

    public KakaoUserInfoDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}