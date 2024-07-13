package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.member.dto.MyBoardDTO;
import com.luckyvicky.woosan.domain.member.dto.MyPageDTO;
import com.luckyvicky.woosan.domain.member.dto.MyReplyDTO;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import org.springframework.transaction.annotation.Transactional;


public interface MyPageService {


    @Transactional(readOnly = true)
    PageResponseDTO<MyReplyDTO> getMyReply(MyPageDTO myPageDTO);

    @Transactional
    PageResponseDTO<MyBoardDTO> getMyBoard(MyPageDTO myPageDTO);
}
