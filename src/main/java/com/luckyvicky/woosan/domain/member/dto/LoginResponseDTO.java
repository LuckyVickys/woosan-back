package com.luckyvicky.woosan.domain.member.dto;

import lombok.Getter;

@Getter
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
    private int expirationTime;

    public LoginResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = 3600000;
    }
}
