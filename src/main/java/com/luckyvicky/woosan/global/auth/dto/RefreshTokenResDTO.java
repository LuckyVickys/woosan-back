package com.luckyvicky.woosan.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenResDTO {
    private String accessToken;
    private String refreshToken;
}
