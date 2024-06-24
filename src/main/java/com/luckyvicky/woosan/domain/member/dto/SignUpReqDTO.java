package com.luckyvicky.woosan.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpReqDTO {
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    @Size(min = 1, max = 8, message = "닉네임은 1자 이상 8자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
//    @Size(message = "비밀번호는 ")
    private String password;
}
