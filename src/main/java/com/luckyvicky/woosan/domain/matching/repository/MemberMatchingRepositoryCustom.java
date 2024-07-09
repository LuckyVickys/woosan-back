package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;

import java.util.List;

public interface MemberMatchingRepositoryCustom {
    // 특정 매칭 타입의 승인된 멤버 수 확인
    long countByMemberIdAndMatchingBoard_MatchingType(Long memberId, int matchingType);

    // 특정 타입의 대기 중인 멤버 수 확인
    long countPendingByMemberIdAndMatchingBoard_MatchingType(Long memberId, int matchingType);

    // 특정 타입의 대기 중인 요청 가져오기
    List<MemberMatching> findPendingByMemberIdAndMatchingBoard_MatchingType(Long memberId, int matchingType);
}
