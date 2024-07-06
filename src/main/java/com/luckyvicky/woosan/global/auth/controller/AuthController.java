package com.luckyvicky.woosan.global.auth.controller;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;
import com.luckyvicky.woosan.global.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> getMemberInfo(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        String token = this.authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
