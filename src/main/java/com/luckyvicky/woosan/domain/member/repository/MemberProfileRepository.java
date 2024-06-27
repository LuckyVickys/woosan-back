package com.luckyvicky.woosan.domain.member.repository;

import com.luckyvicky.woosan.domain.member.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    Optional<MemberProfile> findByMemberId(Long memberId);
}
