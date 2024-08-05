package com.luckyvicky.woosan.global.auth.service;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;
import com.luckyvicky.woosan.domain.member.dto.LoginResponseDTO;
import com.luckyvicky.woosan.global.auth.dto.CustomUserInfoDTO;
import com.luckyvicky.woosan.global.auth.dto.RefreshTokenReqDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    CustomUserInfoDTO getKakaoMember(String accessToken);
    ResponseEntity<?> refreshAccessToken(String authHeader, RefreshTokenReqDTO request);
}
