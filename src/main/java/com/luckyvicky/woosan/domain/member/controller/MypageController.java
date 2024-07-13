package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.board.dto.MyReplyDTO;
import com.luckyvicky.woosan.domain.member.service.MyPageService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/my")
public class MypageController {

    private final MyPageService myPageService;


    /**
     * 댓글 조회
     */
    @GetMapping("replies/{writerId}")
    public ResponseEntity<PageResponseDTO<MyReplyDTO>> getReply(@PathVariable Long writerId, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<MyReplyDTO> myReplyDTO  = myPageService.getMyReply(writerId, pageRequestDTO);
        return ResponseEntity.ok(myReplyDTO);
    }

}

