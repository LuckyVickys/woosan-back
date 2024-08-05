package com.luckyvicky.woosan.domain.member.repository.jpa;

import com.luckyvicky.woosan.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Member findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
}
