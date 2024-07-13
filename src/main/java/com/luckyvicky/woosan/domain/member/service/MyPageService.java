package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.MyReplyDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MyPageService {


    @Transactional(readOnly = true)
    PageResponseDTO<MyReplyDTO> getMyReply(Long writerId, PageRequestDTO pageRequestDTO);
}
