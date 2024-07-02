package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;

import java.util.List;

public interface MemberMatchingRepositoryCustom {
    long countByMemberIdAndType(Long memberId, int matchingType);
    long countPendingByMemberIdAndType(Long memberId, int matchingType);
    List<MemberMatching> findPendingByMemberIdAndType(Long memberId, int matchingType);
}
