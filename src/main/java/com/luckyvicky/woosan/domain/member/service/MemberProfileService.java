package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberProfileService {
    ProfileUpdateDTO get(Long id);
    void update(ProfileUpdateDTO profileUpdateDTO, List<MultipartFile> images);

}
