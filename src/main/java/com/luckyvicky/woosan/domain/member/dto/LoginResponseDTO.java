package com.luckyvicky.woosan.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class LoginResponseDTO {

    private String accessToken;
    private int expirationTime = 3600;

    public LoginResponseDTO(String accessToken) {
        super();
        this.accessToken = accessToken;
        this.expirationTime = 3600;
    }
}
