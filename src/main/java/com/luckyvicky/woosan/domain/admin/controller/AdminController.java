package com.luckyvicky.woosan.domain.admin.controller;

import com.luckyvicky.woosan.domain.admin.service.AdminService;
import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BoardService boardService;

    /**
     * 게시물 작성
     */
    @PostMapping("/add")
    public ResponseEntity<Long> register(@ModelAttribute BoardDTO boardDTO) {
        Long boardId = adminService.add(boardDTO);
        return ResponseEntity.ok(boardId);
    }


    /**
     * 공지사항 다건 조회 (cs)
     */
    @GetMapping("/notices")
    public ResponseEntity<PageResponseDTO<BoardDTO>> getNoticePage(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BoardDTO> responseDTO = boardService.getNoticePage(pageRequestDTO);
        return ResponseEntity.ok(responseDTO);
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
    public ResponseEntity<String> modifyBoard(
            @PathVariable Long id,
            @ModelAttribute BoardDTO boardDTO) {

        adminService.modify(boardDTO);
        return ResponseEntity.ok("수정 완료");
    }

    /**
     * 게시물 삭제
     */
    @PatchMapping("/delete/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id, @RequestBody Long writerId) {
        adminService.remove(id, writerId);
        return ResponseEntity.ok("삭제 완료");
    }


}
