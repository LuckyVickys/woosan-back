package com.luckyvicky.woosan.global.auth.service;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;
import com.luckyvicky.woosan.domain.member.dto.LoginResponseDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import com.luckyvicky.woosan.domain.member.mapper.MemberMapper;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.global.auth.dto.CustomUserInfoDTO;
import com.luckyvicky.woosan.global.auth.dto.RefreshTokenReqDTO;
import com.luckyvicky.woosan.global.auth.dto.RefreshTokenResDTO;
import com.luckyvicky.woosan.global.auth.exception.JWTException;
import com.luckyvicky.woosan.global.auth.filter.JWTUtil;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final MemberMapper mapper;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String KAKAO_USER_URL = "https://kapi.kakao.com/v2/user/me";
    private static final int NICKNAME_LENGTH = 5;
    private static final int TEMP_PASSWORD_LENGTH = 10;

    // 로그인
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if(!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(ErrorCode.PW_NOT_FOUND);
        }

        CustomUserInfoDTO info = mapper.memberToCustomUserInfoDTO(member);
        String accessToken = jwtUtil.createAccessToken(info);
        String refreshToken = jwtUtil.createRefreshToken(info);

        return new LoginResponseDTO(member.getId(), member.getEmail(),
                member.getNickname(), member.getPoint(), member.getNextPoint(),
                member.getMemberType().toString(), member.getLevel().toString(),
                accessToken, refreshToken, member.getIsActive());
    }

    // accessToken으로 member 가져오기
    @Override
    public CustomUserInfoDTO getKakaoMember(String accessToken) {
        String email = getEmailFromKakaoAccessToken(accessToken);
        Member result = memberRepository.findByEmail(email);

        if(result != null) {
            return mapper.memberToCustomUserInfoDTO(result);
        }

        Member kakaoMember = createKakaoMember(email);
        memberRepository.save(kakaoMember);

        return mapper.memberToCustomUserInfoDTO(kakaoMember);
    }

    // accessToken으로 email 가져오기
    private String getEmailFromKakaoAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new JWTException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(
                KAKAO_USER_URL, HttpMethod.GET, entity, LinkedHashMap.class);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();
        if (bodyMap == null) {
            throw new JWTException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");
        return kakaoAccount != null ? kakaoAccount.get("email") : null;
    }

    // 임의의 패스워드 생성
    private String generateRandomPassword(int length) {
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            buffer.append((char) (random.nextInt(26) + 'A')); // A-Z
        }

        return buffer.toString();
    }

    // 카카오 회원 생성
    private Member createKakaoMember(String email) {
        String tempPassword = generateRandomPassword(TEMP_PASSWORD_LENGTH);
        String nickname = "카카오" + generateRandomPassword(NICKNAME_LENGTH);

        return Member.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(tempPassword))
                .nickname(nickname)
                .memberType(MemberType.USER)
                .socialType(SocialType.KAKAO)
                .isActive(true)
                .level(MemberType.Level.LEVEL_1)
                .point(0)
                .nextPoint(100)
                .build();
    }

    // 리프레시 토큰이 존재할 시 accessToken 재발급
    @Override
    public ResponseEntity<?> refreshAccessToken(String authHeader, RefreshTokenReqDTO request) {
        String refreshToken = request.getRefreshToken();
        String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : "";

        if (jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.ok(new RefreshTokenResDTO(accessToken, refreshToken));
        }

        if (jwtUtil.validateRefreshToken(refreshToken)) {
            try {
                String newAccessToken = jwtUtil.getAccessTokenFromRefreshToken(refreshToken);
                return ResponseEntity.ok(new RefreshTokenResDTO(newAccessToken, refreshToken));
            } catch (JWTException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
}
