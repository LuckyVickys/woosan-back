package com.luckyvicky.woosan.global.auth.service;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;

public interface AuthService {
    String login(LoginRequestDTO dto);
}
