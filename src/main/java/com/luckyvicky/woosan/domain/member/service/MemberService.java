package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.MailDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;

public interface MemberService {
    Boolean existEmail(String email) throws Exception;
    Boolean existNickname(String nickname) throws Exception;
    Member addMember(Member member) throws Exception;

    /**
     * 임시 비밀번호 발급 및 비밀번호 변경 관련 코드들
     */
    MailDTO createMailAndChangePw(String email) throws Exception;
    void updateTempPw(String str, String email) throws Exception;
    String getTempPassword() throws Exception;
//    void mailSend(MailDTO mailDTO) throws Exception;
//    void updatePassword(String email, String newPassword) throws Exception;
}
