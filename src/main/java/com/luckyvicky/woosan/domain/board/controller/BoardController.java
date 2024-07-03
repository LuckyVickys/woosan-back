package com.luckyvicky.woosan.domain.board.controller;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.BoardPageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.domain.board.service.PapagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final PapagoService papagoService;


    /**
     * 게시물 작성
     */
    @PostMapping("/add")
    public ResponseEntity<Long> register(@ModelAttribute BoardDTO boardDTO) {
        Long boardId = boardService.add(boardDTO);
        return ResponseEntity.ok(boardId);
    }



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
     * 공지사항 10개 조회 (메인페이지)
     */
    @GetMapping("/notices")
    public ResponseEntity<List<BoardDTO>> getNotices() {
        List<BoardDTO> boardDTO = boardService.getNotices();
        return ResponseEntity.ok(boardDTO);
    }

    /**
     * 인기글 10개 조회 (메인페이지)
     */
    @GetMapping("/best")
    public ResponseEntity<List<BoardDTO>> getbest() {
        List<BoardDTO> boardDTO = boardService.getBest();
        return ResponseEntity.ok(boardDTO);
    }

    /**
     * 게시물 단건 조회 - 상세 페이지
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long id) {
        BoardDTO boardDTO = boardService.getBoard(id);
        return ResponseEntity.ok(boardDTO);
    }


    /**
     * 게시물 수정 페이지 조회
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
    public ResponseEntity<String> modifyBoard(@PathVariable Long id, @ModelAttribute BoardDTO boardDTO) {
        boardDTO.setId(id);
        boardService.modify(boardDTO);
        return ResponseEntity.ok("수정 완료");
    }

    /**
     * 게시물 삭제
     */
    @PatchMapping("/delete/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        boardService.remove(id);
        return ResponseEntity.ok("삭제 완료");
    }


    /**
     * 게시물 번역
     */
    @GetMapping("translate")
    public ResponseEntity<BoardDTO> boardDetailTranslate(@RequestBody BoardDTO boardDTO) {
        try {
            boardDTO = papagoService.tanslateBoardDetailPage(boardDTO);
            return ResponseEntity.ok(boardDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
