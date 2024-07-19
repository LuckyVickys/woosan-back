package com.luckyvicky.woosan.global.auth.controller;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;
import com.luckyvicky.woosan.domain.member.dto.LoginResponseDTO;
import com.luckyvicky.woosan.global.auth.dto.RefreshTokenReqDTO;
import com.luckyvicky.woosan.global.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> getMemberInfo(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        LoginResponseDTO dto = this.authService.login(request);
        String accessToken = dto.getAccessToken();
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String authHeader, @RequestBody RefreshTokenReqDTO request) {
        return authService.refreshAccessToken(authHeader, request);
    }

}
