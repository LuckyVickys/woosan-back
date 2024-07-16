package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MatchingBoardReplyService {

    // 댓글 저장
    MatchingBoardReplyResponseDTO saveReply(MatchingBoardReplyRequestDTO requestDTO);

    // 특정 매칭 보드의 모든 댓글 가져오기 (페이지네이션 포함)
    Page<MatchingBoardReplyResponseDTO> getRepliesByMatchingBoardId(Long matchingId, Pageable pageable);

    // 특정 부모 댓글의 모든 자식 댓글 가져오기
    List<MatchingBoardReplyResponseDTO> getRepliesByParentId(Long parentId);

    // 댓글 삭제
    void deleteReply(Long id, Long memberId);
}
