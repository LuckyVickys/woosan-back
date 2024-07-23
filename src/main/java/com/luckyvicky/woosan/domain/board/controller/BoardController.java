package com.luckyvicky.woosan.domain.board.controller;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.BoardListDTO;
import com.luckyvicky.woosan.domain.board.dto.BoardPageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.RemoveDTO;
import com.luckyvicky.woosan.domain.board.service.AIService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final AIService aiService;

    /**
     * 게시물 작성
     */
    @PostMapping
    public ResponseEntity<Void> createBoard(@RequestPart("boardDTO") BoardDTO boardDTO,
                                            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        boardService.createBoard(boardDTO, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 게시물 전체 조회(+카테고리)
     */
    @GetMapping
    public ResponseEntity<BoardPageResponseDTO> getBoardList(PageRequestDTO pageRequestDTO,
                                                             @RequestParam(value = "categoryName", required = false) String categoryName) {
        BoardPageResponseDTO responseDTO = boardService.getBoardList(pageRequestDTO, categoryName);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    /**
     * 공지사항 다건 조회 (cs)
     */
    @GetMapping("/cs/notices")
    public ResponseEntity<PageResponseDTO<BoardListDTO>> getNoticePage(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BoardListDTO> responseDTO = boardService.getNoticePage(pageRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    /**
     * 공지사항 10개 조회 (메인페이지)
     */
    @GetMapping("/notices")
    public ResponseEntity<List<BoardListDTO>> getNotices() {
        List<BoardListDTO> boardListDTO = boardService.getNotices();
        return new ResponseEntity<>(boardListDTO, HttpStatus.OK);
    }


    /**
     * 인기글 10개 조회 (메인페이지)
     */
    @GetMapping("/best")
    public ResponseEntity<List<BoardListDTO>> getBestBoard() {
        List<BoardListDTO> boardListDTO = boardService.getBestBoard();
        return new ResponseEntity<>(boardListDTO, HttpStatus.OK);
    }


    /**
     * 게시물 단건 조회 - 상세 페이지
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable("id") Long id) {
        BoardDTO boardDTO = boardService.getBoard(id);
        return new ResponseEntity<>(boardDTO, HttpStatus.OK);
    }


    /**
     * 게시물 수정 페이지 조회
     */
    @GetMapping("/{id}/modify")
    public ResponseEntity<BoardDTO> getBoardForUpdate(@PathVariable Long id) {
        BoardDTO boardDTO = boardService.getBoardForUpdate(id);
        return new ResponseEntity<>(boardDTO, HttpStatus.OK);
    }


    /**
     * 게시물 수정
     */
    @PatchMapping
    public ResponseEntity<Void> updateBoard(@RequestPart(value = "boardDTO") BoardDTO boardDTO,
                                            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        boardService.updateBoard(boardDTO, images);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 게시물 삭제
     */
    @PatchMapping("/delete")
    public ResponseEntity<Void> deleteBoard(@RequestBody RemoveDTO removeDTO) {
        boardService.deleteBoard(removeDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 게시물 번역
     */
    @PostMapping("/{id}/translate")
    public ResponseEntity<BoardDTO> boardDetailTranslate(@PathVariable("id") Long id, @RequestBody BoardDTO boardDTO) {
        try {
            boardDTO = aiService.translateBoardDetailPage(boardDTO);
            return ResponseEntity.ok(boardDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 게시물 요약
     */
    @PostMapping("/{id}/summary")
    public ResponseEntity<String> boardDetailSummary(@PathVariable("id") Long id, @RequestBody BoardDTO boardDTO) {
        System.out.println("==========================================");
        System.out.println("요약 기능");
        System.out.println(boardDTO);
        System.out.println("==========================================");

        String summary = "";

        try {
            summary = aiService.summaryBoardDetailPage(boardDTO);
            System.out.println(summary);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}