package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;

import java.util.List;

public interface MatchingBoardReplyService {
    // 댓글 생성
    MatchingBoardReply createReply(MatchingBoardReplyRequestDTO requestDTO);

    // 댓글 삭제
    void deleteReply(Long id, Long memberId);

    // 특정 매칭 보드의 모든 댓글 가져오기
    List<MatchingBoardReply> getRepliesByMatchingId(Long matchingId);
}
