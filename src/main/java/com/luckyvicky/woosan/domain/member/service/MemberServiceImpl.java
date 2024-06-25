package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 이메일 중복 체크
    @Override
    public Boolean existEmail(String email) throws Exception {
        return memberRepository.existsByEmail(email);
    }

    // 닉네임 중복 체크
    @Override
    public Boolean existNickname(String nickname) throws Exception {
        return memberRepository.existsByNickname(nickname);
    }

    // 회원가입
//    @Override
//    public Member addMember(Member member) throws Exception {
//        if(existEmail(member.getEmail()) == true) {
//            throw new Exception("중복된 회원입니다.");
//        }
//
//        member = Member.builder()
//                .email(member.getEmail())
//                .nickname(member.getNickname())
//                .password(bCryptPasswordEncoder.encode(member.getPassword()))
//                .point(0L)
//                .memberType(member.getEmail().equals("woosan@bitcamp.com") ? MemberType.ADMIN : MemberType.USER)
//                .level(member.getEmail().equals("woosan@bitcamp.com") ? null : MemberType.Level.LEVEL_1)
//                .isActive(true)
//                .socialType(SocialType.NORMAL)
//                .build();
//
//        return memberRepository.save(member);
//    }

}
