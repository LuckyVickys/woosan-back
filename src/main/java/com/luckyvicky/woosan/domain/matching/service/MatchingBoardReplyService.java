package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;

import java.util.List;

public interface MatchingBoardReplyService {
    MatchingBoardReply createReply(MatchingBoardReplyRequestDTO requestDTO);

    void deleteReply(Long id, Long memberId);

    List<MatchingBoardReply> getRepliesByMatchingBoardId(Long matchingBoardId);
}
