package com.luckyvicky.woosan.domain.admin.controller;

import com.luckyvicky.woosan.domain.admin.service.AdminService;
import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.BoardListDTO;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.domain.fileImg.dto.FileUpdateDTO;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.dto.TargetDTO;
import com.luckyvicky.woosan.domain.report.service.ReportService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BoardService boardService;
    private final ReportService reportService;
    private final FileImgService fileImgService;


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
    public ResponseEntity<PageResponseDTO<BoardListDTO>> getNoticePage(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BoardListDTO> responseDTO = boardService.getNoticePage(pageRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }


    /**
     * 게시물 수정 페이지 조회
     */
    @GetMapping("/modify/{id}")
    public ResponseEntity<BoardDTO> getBoardForModification(@PathVariable Long id) {
        BoardDTO boardDTO = boardService.getBoard(id);
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


    /**
     * 신고 목록
     */
    @GetMapping("/report")
    public ResponseEntity<PageResponseDTO<ReportDTO>> getReportList(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ReportDTO> responseDTO = reportService.reportList(pageRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }


    /**
     * 신고 상세보기
     */
    @GetMapping("/report/{id}")
    public ResponseEntity<ReportDTO> getReport(@PathVariable Long id) {
        ReportDTO result = reportService.getReport(id);
        return ResponseEntity.ok(result);
    }


    /**
     * 신고 확인
     */
    @PostMapping("/report/{id}")
    public ResponseEntity<Long> checkReport(@PathVariable Long id) {
        Long reportId = reportService.checkReport(id);
        return ResponseEntity.ok(reportId);
    }


    /**
     * 신고 대상으로 이동 (게시글 = 해당 게시글, 댓글 = 댓글이 작성된 게시글)
     */
    @GetMapping("/report/target")
    public ResponseEntity<Long> goToTarget(@RequestParam Long id) {
        Long boardId = reportService.goToTarget(id);
        return ResponseEntity.ok(boardId);
    }


    /**
     * 메인 화면 배너사진 목록
     */
    @GetMapping("/myBanner")
    public ResponseEntity<List<String>> adminBanner() {
        List<String> bannerList = fileImgService.findFiles("admin", 0L);
        return ResponseEntity.ok(bannerList);
    }


    /**
     * 메인 화면 배너사진 업데이트
     * */
    @PostMapping("/myBanner/modify")
    public ResponseEntity<String> updateBanner(FileUpdateDTO fileUpdateDTO){
        fileImgService.updateMainBanner(fileUpdateDTO);
        return ResponseEntity.ok("banner 수정 완료");
    }

}