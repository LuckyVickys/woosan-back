package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.LoginRequestDTO;
import com.luckyvicky.woosan.domain.member.dto.MailDTO;
import com.luckyvicky.woosan.domain.member.dto.MemberInfoDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;

public interface MemberService {
    Boolean existEmail(String email);
    Boolean existNickname(String nickname);
    Member addMember(Member member);

    /**
     * 임시 비밀번호 발급 및 비밀번호 변경 관련 코드들
     */
    MailDTO createMailAndChangePw(String email);
    void mailSend(MailDTO mailDTO);
    void updatePassword(String email, String password, String newPassword);
    MemberInfoDTO getMemberInfo(Long memberId);
}
