package com.luckyvicky.woosan.domain.member.dto;

import lombok.Getter;

@Getter
public class UpdatePwDTO {
    private String email;
    private String password;
    private String newPassword;
}
