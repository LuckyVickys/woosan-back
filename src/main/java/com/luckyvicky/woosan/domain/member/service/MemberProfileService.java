package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;

public interface MemberProfileService {
    void updateProfile(ProfileUpdateDTO memberUpdateDTO);

    ProfileUpdateDTO getPointLevel(ProfileUpdateDTO memberUpdateDTO);
}
