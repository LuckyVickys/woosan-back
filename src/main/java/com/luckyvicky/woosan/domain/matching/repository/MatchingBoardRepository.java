package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchingBoardRepository extends JpaRepository<MatchingBoard, Long> {
    // 특정 회원이 생성한 특정 타입의 매칭 보드를 찾습니다.
    List<MatchingBoard> findByMemberAndMatchingType(Member member, int matchingType);

    // 특정 회원이 특정 날짜 범위 내에 생성한 특정 타입의 매칭 보드를 찾습니다.
    List<MatchingBoard> findByMemberAndMatchingTypeAndMeetDateBetween(Member member, int matchingType, LocalDateTime start, LocalDateTime end);

    // 특정 타입의 매칭 보드를 특정 날짜 이전에 삭제합니다.
    void deleteByMatchingTypeAndMeetDateBefore(int matchingType, LocalDateTime dateTime);
}