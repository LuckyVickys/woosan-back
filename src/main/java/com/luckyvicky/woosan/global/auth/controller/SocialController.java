package com.luckyvicky.woosan.global.auth.controller;

import com.luckyvicky.woosan.global.auth.dto.CustomUserInfoDTO;
import com.luckyvicky.woosan.global.auth.filter.JWTUtil;
import com.luckyvicky.woosan.global.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class SocialController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;

    @GetMapping("/kakao")
    public Map<String, Object> getMemberFromKakao(String accessToken) {

        log.info("accessToken ");
        log.info(accessToken);

        CustomUserInfoDTO user = authService.getKakaoMember(accessToken);

        String jwtAccessToken = jwtUtil.createAccessToken(user);
        String jwtRefreshToken = jwtUtil.createRefreshToken(user);

        authService.getKakaoMember(accessToken);

        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("password", user.getPassword());
        claims.put("role", user.getMemberType());
        claims.put("socialType", user.getSocialType());
        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }
}
