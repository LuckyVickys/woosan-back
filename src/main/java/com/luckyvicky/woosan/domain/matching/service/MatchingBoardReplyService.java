package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;

public interface MatchingBoardReplyService {
    MatchingBoardReply createReply(MatchingBoardReplyRequestDTO requestDTO);
}
