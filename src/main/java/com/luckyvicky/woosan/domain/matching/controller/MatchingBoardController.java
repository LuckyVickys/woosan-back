package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingBoardController {

    private final MatchingBoardService matchingBoardService;


    // 모든 매칭 가져오기
    @GetMapping("/list")
    public List<MatchingBoardResponseDTO> getAllMatching() {
        return matchingBoardService.getAllMatching();
    }

    // 정기모임 생성
    @PostMapping("/regularly")
    public MatchingBoardResponseDTO createRegularly(@RequestBody MatchingBoardRequestDTO requestDTO) {
        return matchingBoardService.createMatchingBoard(requestDTO);
    }

    // 정기모임 가져오기
    @GetMapping("/regularly")
    public List<MatchingBoardResponseDTO> getRegularly() {
        return matchingBoardService.getMatchingByType(1);
    }

    // 번개 생성
    @PostMapping("/temporary")
    public MatchingBoardResponseDTO createTemporary(@RequestBody MatchingBoardRequestDTO requestDTO) {
        return matchingBoardService.createMatchingBoard(requestDTO);
    }

    // 번개 가져오기
    @GetMapping("/temporary")
    public List<MatchingBoardResponseDTO> getTemporary() {
        return matchingBoardService.getMatchingByType(2);
    }

    // 셀프 소개팅 생성
    @PostMapping("/self")
    public MatchingBoardResponseDTO createSelf(@RequestBody MatchingBoardRequestDTO requestDTO) {
        return matchingBoardService.createMatchingBoard(requestDTO);
    }

    // 셀프 소개팅 가져오기
    @GetMapping("/self")
    public List<MatchingBoardResponseDTO> getSelf() {
        return matchingBoardService.getMatchingByType(3);
    }

    // 특정 매칭 보드 가져오기
    @GetMapping("/{id}")
    public MatchingBoardResponseDTO getMatchingBoardById(@PathVariable Long id) {
        return matchingBoardService.getMatchingBoardById(id);
    }

    // 매칭 보드 수정
    @PutMapping("/{id}")
    public MatchingBoardResponseDTO updateMatchingBoard(@PathVariable Long id, @RequestBody MatchingBoardRequestDTO requestDTO) {
        return matchingBoardService.updateMatchingBoard(id, requestDTO);
    }

    // 매칭 보드 삭제
    @DeleteMapping("/{id}")
    public void deleteMatchingBoard(@PathVariable Long id, @RequestParam Long memberId) {
        matchingBoardService.deleteMatchingBoard(id, memberId);
    }
}
