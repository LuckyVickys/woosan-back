package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingBoardController {

    private final MatchingBoardService matchingBoardService;

    // 모든 매칭 가져오기
    @GetMapping("/list")
    public ResponseEntity<?> getAllMatching() {
        try {
            List<MatchingBoardResponseDTO> matchingList = matchingBoardService.getAllMatching();
            return new ResponseEntity<>(matchingList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("모든 매칭을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 정기모임 생성
    @PostMapping("/regularly")
    public ResponseEntity<?> createRegularly(@ModelAttribute MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.createMatchingBoard(requestDTO);
            return new ResponseEntity<>("정기모임이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            // 상세한 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("정기모임 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 번개 생성
    @PostMapping("/temporary")
    public ResponseEntity<?> createTemporary(@ModelAttribute MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.createMatchingBoard(requestDTO);
            return new ResponseEntity<>("번개모임이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            // 상세한 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("번개모임 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 셀프 소개팅 생성
    @PostMapping("/self")
    public ResponseEntity<?> createSelf(@ModelAttribute MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.createMatchingBoard(requestDTO);
            return new ResponseEntity<>("셀프 소개팅이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            // 상세한 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("셀프 소개팅 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 정기모임 가져오기
    @GetMapping("/regularly")
    public ResponseEntity<?> getRegularly() {
        try {
            List<MatchingBoardResponseDTO> regularlyList = matchingBoardService.getMatchingByType(1);
            return new ResponseEntity<>(regularlyList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("정기모임을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 번개 가져오기
    @GetMapping("/temporary")
    public ResponseEntity<?> getTemporary() {
        try {
            List<MatchingBoardResponseDTO> temporaryList = matchingBoardService.getMatchingByType(2);
            return new ResponseEntity<>(temporaryList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("번개모임을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 셀프 소개팅 가져오기
    @GetMapping("/self")
    public ResponseEntity<?> getSelf() {
        try {
            List<MatchingBoardResponseDTO> selfList = matchingBoardService.getMatchingByType(3);
            return new ResponseEntity<>(selfList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("셀프 소개팅을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자가 만든 매칭 보드 목록 가져오기
    @GetMapping("/user/{memberId}")
    public ResponseEntity<?> getMatchingBoardsByMemberId(@PathVariable Long memberId) {
        try {
            List<MatchingBoardResponseDTO> matchingBoards = matchingBoardService.getMatchingBoardsByMemberId(memberId);
            return new ResponseEntity<>(matchingBoards, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 매칭을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 매칭 보드 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatchingBoard(@PathVariable Long id, @ModelAttribute MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.updateMatchingBoard(id, requestDTO);
            return new ResponseEntity<>("매칭 보드가 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("매칭 보드 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 매칭 보드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatchingBoard(@PathVariable Long id, @RequestParam Long memberId) {
        try {
            matchingBoardService.deleteMatchingBoard(id, memberId);
            return new ResponseEntity<>("매칭 보드가 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("매칭 보드 삭제 권한이 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("매칭 보드 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
