package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching-board-reply")
public class MatchingBoardReplyController {

    @Autowired
    private MatchingBoardReplyService matchingBoardReplyService;

    //댓글생성
    @PostMapping
    public MatchingBoardReply createReply(@RequestBody MatchingBoardReplyRequestDTO requestDTO){
        return matchingBoardReplyService.createReply(requestDTO);
    }

    //댓글 삭제
    @DeleteMapping("/{id}")
    public void deleteReply(@PathVariable Long id, @RequestParam Long memberId){
        matchingBoardReplyService.deleteReply(id, memberId);
    }

    //특정 매칭 보드의 모든 댓글 가져오기
    @GetMapping("/{matchingBoardId}/replies")
    public List<MatchingBoardReply> getReplies(@PathVariable Long matchingBoardId){
        return matchingBoardReplyService.getRepliesByMatchingBoardId(matchingBoardId);
    }
}
