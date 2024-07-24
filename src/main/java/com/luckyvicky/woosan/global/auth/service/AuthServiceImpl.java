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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final MemberMapper mapper;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 로그인
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
        if(!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(ErrorCode.PW_NOT_FOUND);
        }

        CustomUserInfoDTO info = mapper.memberToCustomUserInfoDTO(member);

        String accessToken = jwtUtil.createAccessToken(info);
        String refreshToken = jwtUtil.createRefreshToken(info);

        return new LoginResponseDTO(member.getId(), member.getEmail(),
                member.getNickname(), member.getPoint(), member.getNextPoint(),
                member.getMemberType().toString(), member.getLevel().toString(),
                accessToken, refreshToken);
    }

    // accessToken으로 member 가져오기
    @Override
    public CustomUserInfoDTO getKakaoMember(String accessToken) {

        String email = getEmailFromKakaoAccessToken(accessToken);
        log.info("email: " + email);
        Member result = memberRepository.findByEmail(email);

        // 기존 회원
        if(result != null) {
            CustomUserInfoDTO user = mapper.memberToCustomUserInfoDTO(result);
            return user;
        }

        Member kakaoMember = makeKakaoMember(email);
        memberRepository.save(kakaoMember);

        CustomUserInfoDTO user = mapper.memberToCustomUserInfoDTO(kakaoMember);

        return user;
    }

    // accessToken으로 email 가져오기
    private String getEmailFromKakaoAccessToken(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        if(accessToken == null) {
            throw new JWTException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(
                        uriBuilder.toString(),
                        HttpMethod.GET,
                        entity,
                        LinkedHashMap.class);

        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("------------------------------");
        log.info(bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");

        log.info("kakaoAccount: " + kakaoAccount);

        return kakaoAccount.get("email");
    }

    // 임의의 패스워드 생성
    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();

        for(int i = 0; i < 10; i++) {
            buffer.append((char)((int)(Math.random()*55) + 65));
        }

        return buffer.toString();
    }

    // 카카오 회원 생성
    private Member makeKakaoMember(String email) {
        String tempPassword = makeTempPassword();

        log.info("tempPassword: " + tempPassword);

        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String nickname = "카카오";

        int idx = 0;
        for (int i = 0; i < 5; i++) {
            idx = (int) (charSet.length * Math.random());
            nickname += charSet[idx];
        }

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
        String accessToken = authHeader.substring(7);

        if(jwtUtil.validateToken(accessToken)) {
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
