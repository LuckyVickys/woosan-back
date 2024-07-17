package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberProfileServiceImpl implements MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;
    private final MemberRepository memberRepository;
    private final FileImgService fileImgService;
    private final ModelMapper modelMapper;

    @Override
    public ProfileUpdateDTO get(Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findByMemberId(id);
        ProfileUpdateDTO profileUpdateDTO = new ProfileUpdateDTO();
        Member member = memberOptional.get();

        if (!memberProfileOptional.isEmpty()) {
            MemberProfile memberProfile = memberProfileOptional.get();

            profileUpdateDTO.setGender(memberProfile.getGender());
            profileUpdateDTO.setLocation(memberProfile.getLocation());
            profileUpdateDTO.setAge(memberProfile.getAge());
            profileUpdateDTO.setHeight(memberProfile.getHeight());
            profileUpdateDTO.setMbti(memberProfile.getMbti());

            if(fileImgService.findFiles("member",member.getId()) != null) {
                profileUpdateDTO.setFileImg(fileImgService.findFiles("member",member.getId()));
            }

        }
        profileUpdateDTO.setMemberId(member.getId());
        profileUpdateDTO.setNickname(member.getNickname());
        profileUpdateDTO.setLevel(member.getLevel().toString());
        profileUpdateDTO.setNextPoint(member.getNextPoint());
        profileUpdateDTO.setPoint(member.getPoint());

        return profileUpdateDTO;
    }


    @Override
    @Transactional
    public void update(ProfileUpdateDTO profileUpdateDTO) {
        Member member = memberRepository.findById(profileUpdateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        member.setNickname(profileUpdateDTO.getNickname());
        memberRepository.save(member);

        MemberProfile existingProfile = memberProfileRepository.findByMemberId(profileUpdateDTO.getMemberId()).orElse(null);

        if (existingProfile != null) {
            if (profileUpdateDTO.getAge() != null) {
                existingProfile.setAge(profileUpdateDTO.getAge());
            }
            if (profileUpdateDTO.getGender() != null) {
                existingProfile.setGender(profileUpdateDTO.getGender());
            }
            if (profileUpdateDTO.getHeight() != null) {
                existingProfile.setHeight(profileUpdateDTO.getHeight());
            }
            if (profileUpdateDTO.getLocation() != null) {
                existingProfile.setLocation(profileUpdateDTO.getLocation());
            }
            if (profileUpdateDTO.getMbti() != null) {
                existingProfile.setMbti(profileUpdateDTO.getMbti());
            }
            memberProfileRepository.save(existingProfile);
        } else {
            MemberProfile memberProfile = modelMapper.map(profileUpdateDTO, MemberProfile.class);
            memberProfileRepository.save(memberProfile);
        }

        if (profileUpdateDTO.getImage() != null) {
            fileImgService.targetFilesDelete("member", profileUpdateDTO.getMemberId());
            fileImgService.fileUploadMultiple("member", profileUpdateDTO.getMemberId(), profileUpdateDTO.getImage());
        }
    }

}
