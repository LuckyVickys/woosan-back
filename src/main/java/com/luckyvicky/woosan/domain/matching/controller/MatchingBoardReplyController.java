package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
