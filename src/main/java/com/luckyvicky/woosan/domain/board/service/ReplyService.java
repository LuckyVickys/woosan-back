package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.dto.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface ReplyService {
    ReplyDTO add(ReplyDTO replyDTO, Long parentId);

    void remove(Long id);

    @Transactional(readOnly = true)
    PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long boardId, PageRequestDTO pageRequestDTO);
}
