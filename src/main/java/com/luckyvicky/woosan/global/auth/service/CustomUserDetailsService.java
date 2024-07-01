package com.luckyvicky.woosan.global.auth.service;

import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.global.auth.dto.CustomUserDetails;
import com.luckyvicky.woosan.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // DB에서 조회
        Member member = memberRepository.findByEmail(email);

        if(member != null) {
            // UserDetails에 담아서 return 하면 AuthenticationManager가 검증함
            return new CustomUserDetails(member);
        }

        log.info("User not found: {}", email);
        throw new UsernameNotFoundException("존재하지 않는 이메일입니다.");
    }
}
