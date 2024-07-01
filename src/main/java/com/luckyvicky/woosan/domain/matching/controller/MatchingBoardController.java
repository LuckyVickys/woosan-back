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
    public ResponseEntity<List<MatchingBoardResponseDTO>> getAllMatching() {
        try {
            List<MatchingBoardResponseDTO> matchingList = matchingBoardService.getAllMatching();
            return new ResponseEntity<>(matchingList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 정기모임 생성
    @PostMapping("/regularly")
    public ResponseEntity<String> createRegularly(@RequestBody MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.createMatchingBoard(requestDTO);
            return new ResponseEntity<>("정기모임이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 정기모임 가져오기
    @GetMapping("/regularly")
    public ResponseEntity<List<MatchingBoardResponseDTO>> getRegularly() {
        try {
            List<MatchingBoardResponseDTO> regularlyList = matchingBoardService.getMatchingByType(1);
            return new ResponseEntity<>(regularlyList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 번개 생성
    @PostMapping("/temporary")
    public ResponseEntity<String> createTemporary(@RequestBody MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.createMatchingBoard(requestDTO);
            return new ResponseEntity<>("번개가 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 번개 가져오기
    @GetMapping("/temporary")
    public ResponseEntity<List<MatchingBoardResponseDTO>> getTemporary() {
        try {
            List<MatchingBoardResponseDTO> temporaryList = matchingBoardService.getMatchingByType(2);
            return new ResponseEntity<>(temporaryList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 셀프 소개팅 생성
    @PostMapping("/self")
    public ResponseEntity<String> createSelf(@RequestBody MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.createMatchingBoard(requestDTO);
            return new ResponseEntity<>("셀프 소개팅이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 셀프 소개팅 가져오기
    @GetMapping("/self")
    public ResponseEntity<List<MatchingBoardResponseDTO>> getSelf() {
        try {
            List<MatchingBoardResponseDTO> selfList = matchingBoardService.getMatchingByType(3);
            return new ResponseEntity<>(selfList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 매칭 보드 가져오기
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMatchingBoardById(@PathVariable Long id) {
        try {
            MatchingBoardResponseDTO matchingBoard = matchingBoardService.getMatchingBoardById(id);
            return new ResponseEntity<>(matchingBoard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // 매칭 보드 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMatchingBoard(@PathVariable Long id, @RequestBody MatchingBoardRequestDTO requestDTO) {
        try {
            matchingBoardService.updateMatchingBoard(id, requestDTO);
            return new ResponseEntity<>("매칭 보드가 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 매칭 보드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMatchingBoard(@PathVariable Long id, @RequestParam Long memberId) {
        try {
            matchingBoardService.deleteMatchingBoard(id, memberId);
            return new ResponseEntity<>("매칭 보드가 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
