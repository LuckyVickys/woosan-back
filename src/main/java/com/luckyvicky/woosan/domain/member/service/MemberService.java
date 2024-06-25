package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.entity.Member;

public interface MemberService {
    Boolean existEmail(String email) throws Exception;
    Boolean existNickname(String nickname) throws Exception;
//    Member addMember(Member member) throws Exception;
}
