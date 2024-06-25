package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMatchingRepository extends JpaRepository<MemberMatching, Long> {

    //멤버가 생성한 모임이 있는지 확인
    boolean existsByMemberId(Long memberId);

    //멤버가 가입한 모임 수 확인
    long countByMemberId(Long memberId);
}
