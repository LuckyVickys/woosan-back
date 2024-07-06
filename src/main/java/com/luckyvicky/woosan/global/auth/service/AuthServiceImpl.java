package com.luckyvicky.woosan.global.auth.service;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.mapper.MemberMapper;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.global.auth.dto.CustomUserInfoDTO;
import com.luckyvicky.woosan.global.auth.filter.JWTUtil;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberMapper mapper;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 로그인
    @Override
    public String login(LoginRequestDTO dto) {
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
        return accessToken;
    }
}
