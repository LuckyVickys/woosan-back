package com.luckyvicky.woosan.domain.member.utils;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileUpdateMapper {
    private static FileImgService fileImgService;

    public static ProfileUpdateDTO toDTO(Member member, MemberProfile memberProfile, List<String> fileImg) {
        ProfileUpdateDTO profileUpdateDTO = new ProfileUpdateDTO();

        if (memberProfile != null) {
            profileUpdateDTO.setGender(memberProfile.getGender());
            profileUpdateDTO.setLocation(memberProfile.getLocation());
            profileUpdateDTO.setAge(memberProfile.getAge());
            profileUpdateDTO.setHeight(memberProfile.getHeight());
            profileUpdateDTO.setMbti(memberProfile.getMbti());
            profileUpdateDTO.setIntroduce(memberProfile.getIntroduce());
            profileUpdateDTO.setFileImg(fileImg);
        }

        profileUpdateDTO.setMemberId(member.getId());
        profileUpdateDTO.setNickname(member.getNickname());
        profileUpdateDTO.setLevel(member.getLevel().toString());
        profileUpdateDTO.setNextPoint(member.getNextPoint());
        profileUpdateDTO.setPoint(member.getPoint());

        return profileUpdateDTO;
    }

    public static void updateMemberFromDTO(Member member, ProfileUpdateDTO profileUpdateDTO) {
        member.setNickname(profileUpdateDTO.getNickname());
    }

    public static void updateMemberProfileFromDTO(MemberProfile memberProfile, ProfileUpdateDTO profileUpdateDTO) {
        if (profileUpdateDTO.getAge() != null) {
            memberProfile.setAge(profileUpdateDTO.getAge());
        }
        if (profileUpdateDTO.getGender() != null) {
            memberProfile.setGender(profileUpdateDTO.getGender());
        }
        if (profileUpdateDTO.getHeight() != null) {
            memberProfile.setHeight(profileUpdateDTO.getHeight());
        }
        if (profileUpdateDTO.getLocation() != null) {
            memberProfile.setLocation(profileUpdateDTO.getLocation());
        }
        if (profileUpdateDTO.getMbti() != null) {
            memberProfile.setMbti(profileUpdateDTO.getMbti());
        }
        if (profileUpdateDTO.getIntroduce() != null) {
            memberProfile.setIntroduce(profileUpdateDTO.getIntroduce());
        }
    }
}
