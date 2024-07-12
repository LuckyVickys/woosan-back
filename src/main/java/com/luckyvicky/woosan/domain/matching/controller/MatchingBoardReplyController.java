package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyResponseDTO;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchingReply")
@RequiredArgsConstructor
public class MatchingBoardReplyController {

    private final MatchingBoardReplyService matchingBoardReplyService;

    // 댓글 생성
    @PostMapping("/save")
    public ResponseEntity<?> saveReply(@RequestBody MatchingBoardReplyRequestDTO requestDTO) {
        try {
            MatchingBoardReplyResponseDTO responseDTO = matchingBoardReplyService.saveReply(requestDTO);
            return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("댓글 저장에 실패하였습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("알 수 없는 오류로 댓글 저장에 실패하였습니다: " + e.getMessage());
        }
    }


    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReply(@PathVariable Long id, @RequestParam Long memberId) {
        try {
            matchingBoardReplyService.deleteReply(id, memberId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("댓글 삭제에 실패하였습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("알 수 없는 오류로 댓글 삭제에 실패하였습니다: " + e.getMessage());
        }
    }

    // 특정 매칭 보드의 모든 댓글 가져오기 (페이지네이션 포함)
    @GetMapping("/{matchingId}/replies")
    public ResponseEntity<?> getReplies(@PathVariable Long matchingId, Pageable pageable) {
        try {
            Page<MatchingBoardReplyResponseDTO> replies = matchingBoardReplyService.getRepliesByMatchingBoardId(matchingId, pageable);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 목록을 가져오는데 실패하였습니다: " + e.getMessage());
        }
    }

    // 특정 부모 댓글의 모든 자식 댓글 가져오기
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<?> getRepliesByParentId(@PathVariable Long parentId) {
        try {
            List<MatchingBoardReplyResponseDTO> replies = matchingBoardReplyService.getRepliesByParentId(parentId);
            return ResponseEntity.ok(replies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("답글 목록을 가져오는데 실패하였습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("알 수 없는 오류로 답글 목록을 가져오는데 실패하였습니다: " + e.getMessage());
        }
    }
}
