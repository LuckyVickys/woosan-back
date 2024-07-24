package com.luckyvicky.woosan.global.auth.service;

import com.luckyvicky.woosan.domain.member.mapper.MemberMapper;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.global.auth.dto.CustomUserDetails;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.global.auth.dto.CustomUserInfoDTO;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        CustomUserInfoDTO dto = mapper.memberToCustomUserInfoDTO(member);

        return new CustomUserDetails(dto);
    }
}
