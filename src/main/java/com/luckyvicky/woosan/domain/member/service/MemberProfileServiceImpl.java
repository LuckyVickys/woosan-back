package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberProfileServiceImpl implements MemberProfileService {

    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Autowired
    public MemberProfileServiceImpl(MemberRepository memberRepository, MemberProfileRepository memberProfileRepository) {
        this.memberRepository = memberRepository;
        this.memberProfileRepository = memberProfileRepository;
    }

    //회원정보 업데이트
    @Override
    public void updateProfile(ProfileUpdateDTO profileUpdateDTO) {

        Member member = memberRepository.findById(profileUpdateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setNickname(profileUpdateDTO.getNickname());
        memberRepository.save(member);

        MemberProfile memberProfile = memberProfileRepository.findById(profileUpdateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        memberProfile.setLocation(profileUpdateDTO.getLocation());
        memberProfile.setGender(profileUpdateDTO.getGender());
        memberProfile.setAge(profileUpdateDTO.getAge());
        memberProfile.setHeight(profileUpdateDTO.getHeight());
        memberProfile.setMbti(profileUpdateDTO.getMbti());

        memberProfileRepository.save(memberProfile);
    }

    //포인트 확인란
    @Override
    public ProfileUpdateDTO getPointLevel(ProfileUpdateDTO profileUpdateDTO) {
        Member member = memberRepository.findById(profileUpdateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        int point = member.getPoint();
        MemberType.Level level = member.getLevel();

        profileUpdateDTO.setLevel(level);
        profileUpdateDTO.setPoint(point);

        return profileUpdateDTO;
    }
}
