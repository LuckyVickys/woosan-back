package com.luckyvicky.woosan.domain.matching.repository;

import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingBoardReplyRepository extends JpaRepository<MatchingBoardReply, Long> {

    // 특정 매칭 보드의 댓글 가져오기
    List<MatchingBoardReply> findByMatchingId(Long matchingId);
}
