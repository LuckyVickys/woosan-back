package com.luckyvicky.woosan.domain.member.controller;


import com.luckyvicky.woosan.domain.board.dto.BoardDTO;

import com.luckyvicky.woosan.domain.member.dto.MyBoardDTO;
import com.luckyvicky.woosan.domain.member.dto.MyPageDTO;
import com.luckyvicky.woosan.domain.member.dto.MyReplyDTO;

import com.luckyvicky.woosan.domain.member.service.MyPageService;
import com.luckyvicky.woosan.domain.messages.dto.MessageDTO;
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
        PageResponseDTO<MyBoardDTO> responseDTO = myPageService.getMyBoard(myPageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 댓글 조회
     */
    @PostMapping("replies")
    public ResponseEntity<PageResponseDTO<MyReplyDTO>> getReply(@RequestBody MyPageDTO myPageDTO) {
        PageResponseDTO<MyReplyDTO> responseDTO = myPageService.getMyReply(myPageDTO);
        return ResponseEntity.ok(responseDTO);
    }



    /**
     * 추천한 게시글
     * */
    @PostMapping("like")
    public ResponseEntity<PageResponseDTO<BoardDTO>> myLikedBoard(@RequestBody MyPageDTO myPageDTO) {
        PageResponseDTO<BoardDTO> responseDTO = myPageService.myLikeBoardList(myPageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 보낸 쪽지함
     */
    @PostMapping("/message/list/send")
    public ResponseEntity<PageResponseDTO<MessageDTO>> mySendMessages(@RequestBody MyPageDTO myPageDTO) {
        PageResponseDTO<MessageDTO> responseDTO = myPageService.mySendMessages(myPageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 받은 쪽지함
     */
    @PostMapping("/message/list/receive")
    public ResponseEntity<PageResponseDTO<MessageDTO>> myReceiveMessages(@RequestBody MyPageDTO myPageDTO) {
        PageResponseDTO<MessageDTO> responseDTO = myPageService.myReceiveMessages(myPageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 보낸 쪽지 삭제
     */
    @DeleteMapping("/message/del/send")
    public ResponseEntity<String> deleteSendMessage(@RequestParam Long id) {
        myPageService.removeSendMessage(id);
        return ResponseEntity.ok("삭제 완료");
    }

    /**
     * 받은 쪽지 삭제
     */
    @DeleteMapping("/message/del/receive")
    public ResponseEntity<String> deleteReceiveMessage(@RequestParam Long id) {
        myPageService.removeReceiveMessage(id);
        return ResponseEntity.ok("삭제 완료");
    }
}

