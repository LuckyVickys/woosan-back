package com.luckyvicky.woosan.domain.member.repository;

import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndPassword(String email, String password);
    Member findByEmail(String email);

}
