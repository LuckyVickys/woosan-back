package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.MyReplyDTO;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import org.springframework.transaction.annotation.Transactional;


public interface MyPageService {

    @Transactional(readOnly = true)
    PageResponseDTO<MyReplyDTO> getMyReply(Long writerId, PageRequestDTO pageRequestDTO);

    PageResponseDTO<BoardDTO> myLikeBoardList(MyPageDTO myPageDTO);

}
