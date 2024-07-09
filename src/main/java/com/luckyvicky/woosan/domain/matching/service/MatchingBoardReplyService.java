package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MatchingBoardReplyService {

    MatchingBoardReplyResponseDTO saveReply(MatchingBoardReplyRequestDTO requestDTO);

    Page<MatchingBoardReplyResponseDTO> getRepliesByMatchingBoardId(Long matchingId, Pageable pageable);

    List<MatchingBoardReplyResponseDTO> getRepliesByParentId(Long parentId);

    void deleteReply(Long id, Long memberId);
}
