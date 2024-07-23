package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import com.luckyvicky.woosan.domain.member.repository.MemberProfileRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.domain.member.utils.MemberProfileUtil;
import com.luckyvicky.woosan.domain.member.utils.ProfileUpdateMapper;
import com.luckyvicky.woosan.global.util.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberProfileServiceImpl implements MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;
    private final MemberRepository memberRepository;
    private final FileImgService fileImgService;
    private final MemberProfileUtil memberProfileUtil;

    @Override
    public ProfileUpdateDTO get(Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findByMemberId(id);
        Member member = memberOptional.orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        List<String> fileImg = fileImgService.findFiles(TargetType.MEMBER, member.getId());
        return ProfileUpdateMapper.toDTO(member, memberProfileOptional.orElse(null), fileImg);
    }

    @Override
    @Transactional
    public void update(ProfileUpdateDTO profileUpdateDTO, List<MultipartFile> images) {
        Member member = memberRepository.findById(profileUpdateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        ProfileUpdateMapper.updateMemberFromDTO(member, profileUpdateDTO);
        memberRepository.save(member);

        MemberProfile memberProfile = memberProfileUtil.findOrCreateProfile(profileUpdateDTO);
        memberProfileRepository.save(memberProfile);

        if (images != null) {
            fileImgService.targetFilesDelete(TargetType.MEMBER, profileUpdateDTO.getMemberId());
            fileImgService.fileUploadMultiple(TargetType.MEMBER, profileUpdateDTO.getMemberId(), images);
        }
    }
}