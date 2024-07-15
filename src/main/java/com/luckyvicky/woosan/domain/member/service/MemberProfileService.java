package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;

public interface MemberProfileService {
    ProfileUpdateDTO get(Long id);

    void update(ProfileUpdateDTO profileUpdateDTO);

}
