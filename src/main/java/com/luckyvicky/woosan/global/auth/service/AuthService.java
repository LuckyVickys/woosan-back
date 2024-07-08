package com.luckyvicky.woosan.global.auth.service;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;
import com.luckyvicky.woosan.domain.member.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
}
