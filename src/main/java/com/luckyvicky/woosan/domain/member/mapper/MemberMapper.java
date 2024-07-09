package com.luckyvicky.woosan.domain.member.mapper;

import com.luckyvicky.woosan.domain.member.dto.MemberInfoDTO;
import com.luckyvicky.woosan.domain.member.dto.SignUpReqDTO;
import com.luckyvicky.woosan.domain.member.dto.SignUpResDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.global.auth.dto.CustomUserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member singUpReqDTOToMember(SignUpReqDTO signUpReqDTO);
    SignUpResDTO memberToSignUpResDTO(Member member);
    CustomUserInfoDTO memberToCustomUserInfoDTO(Member member);
    MemberInfoDTO memberToMemberInfoDTO(Member member);
}
