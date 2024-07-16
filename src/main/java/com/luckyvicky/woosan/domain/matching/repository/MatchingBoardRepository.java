package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchingBoardRepository extends JpaRepository<MatchingBoard, Long> {
    // 회원이 생성한 매칭타입의 매칭보드를 찾습니다.
    List<MatchingBoard> findByMemberAndMatchingType(Member member, int matchingType);

    //정기 모임, 번개, 셀프 소개팅 구분
    List<MatchingBoard> findByMatchingType(int matchingType);

    // 회원이 특정 날짜 범위 내에 생성한 매칭타입의 매칭보드를 찾습니다.
    List<MatchingBoard> findByMemberAndMatchingTypeAndMeetDateBetween(Member member, int matchingType, LocalDateTime start, LocalDateTime end);

    // 번개는 당일 자정에 자동으로 삭제됩니다.
    List<MatchingBoard> findByMatchingTypeAndMeetDateBefore(int matchingType, LocalDateTime dateTime);

    long countByMember_IdAndMatchingType(Long memberId, int i);

    // 특정 사용자가 만든 매칭 보드 가져오기
    List<MatchingBoard> findByMemberId(Long memberId);
}