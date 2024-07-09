package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberMatchingRepository extends JpaRepository<MemberMatching, Long>, MemberMatchingRepositoryCustom {

    // 특정 보드의 대기 중인 요청 가져오기
    List<MemberMatching> findByMatchingBoard_IdAndIsAccepted(Long matchingId, Boolean isAccepted);

    // 특정 보드의 특정 멤버 찾기
    Optional<MemberMatching> findByMatchingBoard_IdAndMember_Id(Long matchingId, Long memberId);

    // 특정 보드의 모든 멤버 가져오기
    List<MemberMatching> findByMatchingBoard_Id(Long matchingId);

    // 특정 매칭 타입의 승인된 멤버 수 확인
    long countByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(Long memberId, int matchingType, Boolean isAccepted);

    // 특정 타입의 대기 중인 요청 가져오기
    List<MemberMatching> findByMember_IdAndMatchingBoard_MatchingTypeAndIsAccepted(Long memberId, int matchingType, Boolean isAccepted);

    // 특정 보드 ID로 삭제
    void deleteByMatchingBoard_Id(Long matchingId);

}
