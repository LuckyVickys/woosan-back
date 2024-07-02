package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberMatchingRepository extends JpaRepository<MemberMatching, Long>, MemberMatchingRepositoryCustom {

    // 대기 중인 멤버 수 확인
    long countPendingByMember(Long memberId);

    // 특정 보드의 대기 중인 요청 가져오기
    List<MemberMatching> findPendingByBoardId(Long boardId);

    // 특정 보드의 특정 멤버 찾기
    Optional<MemberMatching> findByBoardIdAndMemberId(Long boardId, Long memberId);

    // 특정 보드의 모든 멤버 가져오기
    List<MemberMatching> findByBoardId(Long boardId);

    // 특정 매칭 타입의 멤버 수 확인
    long countByMemberIdAndType(Long memberId, int matchingType);

    // 특정 타입의 대기 중인 멤버 수 확인
    long countPendingByMemberIdAndType(Long memberId, int matchingType);

    // 특정 타입의 대기 중인 요청 가져오기
    List<MemberMatching> findPendingByMemberIdAndType(Long memberId, int matchingType);

    // 특정 보드 ID로 삭제
    void deleteByMatchingBoardId(Long boardId);


}
