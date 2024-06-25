package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching-board")
public class MatchingBoardController {

    @Autowired
    private MatchingBoardService matchingBoardService;

    //정기모임생성
    @PostMapping
    public MatchingBoard createMatchingBoard(@RequestBody MatchingBoardRequestDTO requestDTO){
        return matchingBoardService.createMatchingBoard(requestDTO);
    }

}
