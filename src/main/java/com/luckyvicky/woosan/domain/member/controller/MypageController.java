package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.domain.member.service.MyPageService;
import com.luckyvicky.woosan.domain.messages.dto.MessageAddDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/myPage")
public class MypageController {



    private final MyPageService myPageService;

    //마이페이지에서 내가 쓴 게시물 조회
    @GetMapping("/Board")
    public ResponseEntity<List<BoardDTO>> getBoardsByWriterId(@RequestBody Long writerId) {

        List<BoardDTO> boards = myPageService.getBoardsByWriterId(writerId);
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/ViewLikes")
    public ResponseEntity<List<BoardDTO>> viewLikesById(@RequestBody Long id) {
        List<BoardDTO> boards = myPageService.getTargetIdByLikes(id);
        return ResponseEntity.ok(boards);
    }

    //마이페이지 내가 쓴 댓글 조회
    @GetMapping("/ViewComment")
    public ResponseEntity<List<ReplyDTO>> getCommentsByBoardId(@RequestBody ReplyDTO replyDTO) {
        Long writerId = replyDTO.getWriterId();
        List<ReplyDTO> comments = myPageService.getReplyByWriterId(writerId);
        return ResponseEntity.ok(comments);
    }

    //마이페이지 보낸 쪽지함
    @GetMapping("/ViewSendMessage")
    public ResponseEntity<List<MessageAddDTO>> getSendMessageById(@RequestBody Long id) {

        List<MessageAddDTO> sendMessage = myPageService.getSendMessageById(id);
        return ResponseEntity.ok(sendMessage);
    }

    //마이페이지 받은 쪽지함
    @GetMapping("/ViewReceiveMessage")
    public ResponseEntity<List<MessageAddDTO>> getReceiveMessageById(@RequestBody Long id) {

        List<MessageAddDTO> receiveMessage = myPageService.getReceiveMessageById(id);
        return ResponseEntity.ok(receiveMessage);
    }




//    //마이페이지 내가 쓴 대댓글 조회
//    @PostMapping("/ViewReply")
//    public ResponseEntity<ReplyDTO> getCommentById(@PathVariable Long id) {
//        ReplyDTO comment = ReplyService.getById(id);
//        return ResponseEntity.ok(comment);
//    }

}
