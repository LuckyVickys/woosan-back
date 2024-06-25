package com.luckyvicky.woosan.domain.board.controller;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;


    /**
     * 게시물 작성
     */
    @PostMapping("/add")
    public ResponseEntity<Long> register(@RequestBody BoardDTO boardDTO) {
        Long boardId = boardService.add(boardDTO);
        return ResponseEntity.ok(boardId);
    }


//    /**
//     * 게시물 전체 조회(+카테고리)
//     */
//    @GetMapping
//    public ResponseEntity<PageResponseDTO<BoardDTO>> getList(PageRequestDTO pageRequestDTO,
//                                                             @RequestParam(value = "categoryName", required = false) String categoryName) {
//        PageResponseDTO<BoardDTO> responseDTO = boardService.getlist(pageRequestDTO, categoryName);
//        return ResponseEntity.ok(responseDTO);
//    }

    /**
     * 게시물 전체 조회(+카테고리)
     */
    @GetMapping
    public ResponseEntity<BoardPageResponseDTO> getList(PageRequestDTO pageRequestDTO,
                                                             @RequestParam(value = "categoryName", required = false) String categoryName) {
        BoardPageResponseDTO responseDTO = boardService.getBoardPage(pageRequestDTO, categoryName);
        return ResponseEntity.ok(responseDTO);
    }


    /**
     * 단건 _ 댓글과 게시물 한 번에 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardWithRepliesDTO> getBoardWithReplies(@PathVariable Long id,
                                                                   PageRequestDTO pageRequestDTO) {
        BoardWithRepliesDTO boardWithRepliesDTO = boardService.getWithReplies(id, pageRequestDTO);
        return ResponseEntity.ok(boardWithRepliesDTO);
    }


    /**
     * 게시물 수정 페이지
     */
    @GetMapping("/modify/{id}")
    public ResponseEntity<BoardDTO> getBoardForModification(@PathVariable Long id) {
        BoardDTO boardDTO = boardService.get(id);
        return ResponseEntity.ok(boardDTO);
    }




    /**
     * 게시물 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> modifyBoard(@PathVariable Long id, @RequestBody BoardDTO boardDTO) {
        boardDTO.setId(id);
        boardService.modify(boardDTO);
        return ResponseEntity.ok("수정 완료");
    }

    /**
     * 게시물 삭제
     */
    @PatchMapping("/{id}/delete")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        boardService.remove(id);
        return ResponseEntity.ok("삭제 완료");
    }






}
