package com.luckyvicky.woosan.domain.board.controller;

import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.dto.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.domain.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyController {


    private final ReplyService replyService;


    /**
     * 댓글 작성
     */
    @PostMapping("/add")
    public ResponseEntity<ReplyDTO> createReply(@RequestBody ReplyDTO replyDTO,
                                                  @RequestParam(value = "parentId", required = false) Long parentId) {
        ReplyDTO saveReply = replyService.add(replyDTO, parentId);
        return ResponseEntity.ok(saveReply);
    }

    /**
     * 댓글 조회
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<PageResponseDTO<ReplyDTO>> getReply(@PathVariable Long boardId, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ReplyDTO> replyDTO  = replyService.getRepliesByBoardId(boardId, pageRequestDTO);
        return ResponseEntity.ok(replyDTO);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void>  deleteReply(@PathVariable Long replyId){
        replyService.remove(replyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

