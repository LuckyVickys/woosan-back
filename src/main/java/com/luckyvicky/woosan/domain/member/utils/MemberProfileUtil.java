package com.luckyvicky.woosan.domain.member.utils;

import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MemberProfileUtil {
    private final MemberProfileRepository memberProfileRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public MemberProfileUtil(MemberProfileRepository memberProfileRepository, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberProfileRepository = memberProfileRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    public MemberProfile findOrCreateProfile(ProfileUpdateDTO profileUpdateDTO) {
        MemberProfile existingProfile = memberProfileRepository.findByMemberId(profileUpdateDTO.getMemberId()).orElse(null);

        if (existingProfile == null) {
            return modelMapper.map(profileUpdateDTO, MemberProfile.class);
        }

        ProfileUpdateMapper.updateMemberProfileFromDTO(existingProfile, profileUpdateDTO);
        return existingProfile;
    }
}
