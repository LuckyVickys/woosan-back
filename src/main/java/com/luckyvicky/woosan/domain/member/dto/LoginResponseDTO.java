package com.luckyvicky.woosan.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    private Boolean isActive;
}
