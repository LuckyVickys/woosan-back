package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.domain.member.service.MyPageService;
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
}