package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface ReplyService {
    ReplyDTO add(ReplyDTO replyDTO);

    void remove(Long id);

    @Transactional(readOnly = true)
    PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long boardId, PageRequestDTO pageRequestDTO);
}
