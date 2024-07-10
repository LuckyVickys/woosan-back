package com.luckyvicky.woosan.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String email;
    private String nickname;
    private int point;
    private int nextPoint;
    private String memberType;
    private String level;
    private String accessToken;
    private String refreshToken;
}
