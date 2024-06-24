package com.luckyvicky.woosan.domain.member.mapper;

import com.luckyvicky.woosan.domain.member.dto.SignUpReqDTO;
import com.luckyvicky.woosan.domain.member.dto.SignUpResDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member singUpReqDTOToMember(SignUpReqDTO signUpReqDTO);
    SignUpResDTO memberToSignUpResDTO(Member member);
}
