package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.service.MatchingBoardService;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingBoardController {

    private final MatchingBoardService matchingBoardService;
    private final MemberRepository memberRepository;

    // 모든 매칭 가져오기
    @GetMapping("/list")
    public List<MatchingBoardResponseDTO> getAllMatching() {
        return matchingBoardService.getAllMatching();
    }

    // 정기모임 생성
    @PostMapping("/regularly")
    public MatchingBoard createRegularly(@RequestBody MatchingBoardRequestDTO requestDTO) {
        return matchingBoardService.createMatchingBoard(requestDTO);
    }

    // 정기모임 가져오기
    @GetMapping("/regularly")
    public List<MatchingBoard> getRegularly() {
        return matchingBoardService.getMatchingByType(1);
    }

    // 번개 생성
    @PostMapping("/temporary")
    public MatchingBoard createTemporary(@RequestBody MatchingBoardRequestDTO requestDTO) {
        MatchingBoardRequestDTO dto = MatchingBoardRequestDTO.builder()
                .memberId(requestDTO.getMemberId())
                .matchingType(2)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .placeName(requestDTO.getPlaceName())
                .locationX(requestDTO.getLocationX())
                .locationY(requestDTO.getLocationY())
                .address(requestDTO.getAddress())
                .meetDate(requestDTO.getMeetDate())
                .tag(requestDTO.getTag())
                .headCount(requestDTO.getHeadCount())
                .location(requestDTO.getLocation())
                .introduce(requestDTO.getIntroduce())
                .mbti(requestDTO.getMbti())
                .gender(requestDTO.getGender())
                .age(requestDTO.getAge())
                .height(requestDTO.getHeight())
                .build();
        return matchingBoardService.createMatchingBoard(dto);
    }

    // 번개 가져오기
    @GetMapping("/temporary")
    public List<MatchingBoard> getTemporary() {
        return matchingBoardService.getMatchingByType(2);
    }

    // 셀프 소개팅 생성
    @PostMapping("/self")
    public MatchingBoard createSelf(@RequestBody MatchingBoardRequestDTO requestDTO) {
        MatchingBoardRequestDTO dto = MatchingBoardRequestDTO.builder()
                .memberId(requestDTO.getMemberId())
                .matchingType(3)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .placeName(requestDTO.getPlaceName())
                .locationX(requestDTO.getLocationX())
                .locationY(requestDTO.getLocationY())
                .address(requestDTO.getAddress())
                .meetDate(requestDTO.getMeetDate())
                .tag(requestDTO.getTag())
                .headCount(requestDTO.getHeadCount())
                .location(requestDTO.getLocation())
                .introduce(requestDTO.getIntroduce())
                .mbti(requestDTO.getMbti())
                .gender(requestDTO.getGender())
                .age(requestDTO.getAge())
                .height(requestDTO.getHeight())
                .build();
        return matchingBoardService.createMatchingBoard(dto);
    }

    // 셀프 소개팅 가져오기
    @GetMapping("/self")
    public List<MatchingBoard> getSelf() {
        return matchingBoardService.getMatchingByType(3);
    }

    // 특정 매칭 보드 가져오기
    @GetMapping("/{id}")
    public MatchingBoard getMatchingBoardById(@PathVariable Long id) {
        return matchingBoardService.getMatchingBoardById(id);
    }

    // 매칭 보드 수정
    @PutMapping("/{id}")
    public MatchingBoard updateMatchingBoard(@PathVariable Long id, @RequestBody MatchingBoardRequestDTO requestDTO) {
        return matchingBoardService.updateMatchingBoard(id, requestDTO);
    }

    // 매칭 보드 삭제
    @DeleteMapping("/{id}")
    public void deleteMatchingBoard(@PathVariable Long id, @RequestParam Long memberId) {
        matchingBoardService.deleteMatchingBoard(id, memberId);
    }
}
