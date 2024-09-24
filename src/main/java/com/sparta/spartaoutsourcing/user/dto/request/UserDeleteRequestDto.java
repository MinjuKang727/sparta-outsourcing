package com.sparta.spartaoutsourcing.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDeleteRequestDto {
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8글자 이상, 최대 15자입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 포함합니다.")
    private String password;

    public UserDeleteRequestDto(String password) {
        this.password = password;
    }
}
