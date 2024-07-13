package com.luckyvicky.woosan.domain.member.controller;


import com.luckyvicky.woosan.domain.board.dto.BoardDTO;

import com.luckyvicky.woosan.domain.member.dto.MyBoardDTO;
import com.luckyvicky.woosan.domain.member.dto.MyPageDTO;
import com.luckyvicky.woosan.domain.member.dto.MyReplyDTO;

import com.luckyvicky.woosan.domain.member.service.MyPageService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/my")
public class MypageController {

    private final MyPageService myPageService;

    /**
     * 게시글 조회
     */
    @PostMapping("board")
    public ResponseEntity<PageResponseDTO<MyBoardDTO>> getBoard(@RequestBody MyPageDTO myPageDTO) {
        PageResponseDTO<MyBoardDTO> myBoardDTO = myPageService.getMyBoard(myPageDTO);
        return ResponseEntity.ok(myBoardDTO);
    }

    /**
     * 댓글 조회
     */
    @PostMapping("replies")
    public ResponseEntity<PageResponseDTO<MyReplyDTO>> getReply(@RequestBody MyPageDTO myPageDTO) {
        PageResponseDTO<MyReplyDTO> myReplyDTO = myPageService.getMyReply(myPageDTO);
        return ResponseEntity.ok(myReplyDTO);
    }



    /**
     * 추천한 게시글
     * */
    @PostMapping("like/{memberId}")
    public ResponseEntity<PageResponseDTO<BoardDTO>> myLikedBoard(@RequestBody MyPageDTO myPageDTO) {
        PageResponseDTO<BoardDTO> responseDTO = myPageService.myLikeBoardList(myPageDTO);
        return ResponseEntity.ok(responseDTO);
    }




}

