package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
import com.luckyvicky.woosan.domain.matching.service.MemberMatchingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingBoardController {

    private final MatchingBoardService matchingBoardService;
    private final MemberMatchingService memberMatchingService;

    // 모든 매칭 가져오기
    @GetMapping("/list")
    public ResponseEntity<?> getAllMatching() {
        try {
            List<MatchingBoardResponseDTO> matchingList = matchingBoardService.getAllMatching();
            System.out.println("모든 매칭 데이터: " + matchingList);
            return new ResponseEntity<>(matchingList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("모든 매칭을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 정기모임 생성
    @PostMapping("/regularly")
    public ResponseEntity<?> createRegularly(@ModelAttribute MatchingBoardRequestDTO requestDTO) {
        try {
            MatchingBoardResponseDTO responseDTO = matchingBoardService.createMatchingBoard(requestDTO);
            System.out.println("정기모임 생성 성공: " + responseDTO);
            return new ResponseEntity<>("정기모임이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("정기모임 생성 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("정기모임 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 번개 생성
    @PostMapping("/temporary")
    public ResponseEntity<?> createTemporary(@ModelAttribute MatchingBoardRequestDTO requestDTO) {
        try {
            MatchingBoardResponseDTO responseDTO = matchingBoardService.createMatchingBoard(requestDTO);
            System.out.println("번개모임 생성 성공: " + responseDTO);
            return new ResponseEntity<>("번개모임이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("번개모임 생성 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("번개모임 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 셀프 소개팅 생성
    @PostMapping("/self")
    public ResponseEntity<?> createSelf(@ModelAttribute MatchingBoardRequestDTO requestDTO) {
        try {
            MatchingBoardResponseDTO responseDTO = matchingBoardService.createMatchingBoard(requestDTO);
            System.out.println("셀프 소개팅 생성 성공: " + responseDTO);
            return new ResponseEntity<>("셀프 소개팅이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("셀프 소개팅 생성 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("셀프 소개팅 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 정기모임 가져오기
    @GetMapping("/regularly")
    public ResponseEntity<?> getRegularly() {
        try {
            List<MatchingBoardResponseDTO> regularlyList = matchingBoardService.getMatchingByType(1);
            System.out.println("정기모임 목록: " + regularlyList);
            return new ResponseEntity<>(regularlyList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("정기모임 가져오기 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("정기모임을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 번개 가져오기
    @GetMapping("/temporary")
    public ResponseEntity<?> getTemporary() {
        try {
            List<MatchingBoardResponseDTO> temporaryList = matchingBoardService.getMatchingByType(2);
            System.out.println("번개모임 목록: " + temporaryList);
            return new ResponseEntity<>(temporaryList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("번개모임 가져오기 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("번개모임을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 셀프 소개팅 가져오기
    @GetMapping("/self")
    public ResponseEntity<?> getSelf() {
        try {
            List<MatchingBoardResponseDTO> selfList = matchingBoardService.getMatchingByType(3);
            System.out.println("셀프 소개팅 목록: " + selfList);
            return new ResponseEntity<>(selfList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("셀프 소개팅 가져오기 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("셀프 소개팅을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자가 만든 매칭 보드 목록 가져오기
    @GetMapping("/user/{memberId}")
    public ResponseEntity<?> getMatchingBoardsByMemberId(@PathVariable Long memberId) {
        try {
            List<MatchingBoardResponseDTO> matchingBoards = matchingBoardService.getMatchingBoardsByMemberId(memberId);
            System.out.println("사용자 매칭 보드 목록: " + matchingBoards);
            return new ResponseEntity<>(matchingBoards, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("사용자 매칭 보드 가져오기 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 매칭을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 매칭 보드 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatchingBoard(@PathVariable Long id,
                                                 @ModelAttribute MatchingBoardRequestDTO requestDTO,
                                                 @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            MatchingBoardResponseDTO responseDTO = matchingBoardService.updateMatchingBoard(id, requestDTO, images);
            System.out.println("매칭 보드 수정 성공: " + responseDTO);
            return new ResponseEntity<>("매칭 보드가 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("매칭 보드 수정 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("매칭 보드 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 매칭 보드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatchingBoard(@PathVariable Long id, @RequestParam Long memberId) {
        try {
            matchingBoardService.deleteMatchingBoard(id, memberId);
            System.out.println("매칭 보드 삭제 성공: id=" + id + ", memberId=" + memberId);
            return new ResponseEntity<>("매칭 보드가 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.out.println("매칭 보드 삭제 권한 없음: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("매칭 보드 삭제 권한이 없습니다: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("매칭 보드 삭제 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("매칭 보드 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 조회수 증가 엔드포인트 추가
    @PostMapping("/increaseViewCount")
    public ResponseEntity<?> increaseViewCount(@RequestBody Map<String, Long> request, HttpServletRequest httpRequest) {
        Long boardId = request.get("boardId");
        Long memberId = request.get("memberId");
        Long writerId = request.get("writerId");

        try {
            matchingBoardService.increaseViewCount(boardId, memberId, writerId, httpRequest);
            return ResponseEntity.ok("조회수가 성공적으로 증가했습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("조회수 증가 중 오류 발생: " + e.getMessage());
        }
    }
}
