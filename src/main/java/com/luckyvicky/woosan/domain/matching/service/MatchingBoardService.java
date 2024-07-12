package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardResponseDTO;
import java.util.List;

public interface MatchingBoardService {

    // 모든 매칭 게시글을 가져오는 메서드
    List<MatchingBoardResponseDTO> getAllMatching();

    // 특정 타입의 매칭 게시글을 가져오는 메서드
    List<MatchingBoardResponseDTO> getMatchingByType(int matchingType);

    // 매칭 게시글을 생성하는 메서드
    MatchingBoardResponseDTO createMatchingBoard(MatchingBoardRequestDTO requestDTO);

    // 특정 사용자가 만든 매칭 게시글을 가져오는 메서드
    List<MatchingBoardResponseDTO> getMatchingBoardsByMemberId(Long memberId);

    // 특정 매칭 게시글을 수정하는 메서드
    MatchingBoardResponseDTO updateMatchingBoard(Long id, MatchingBoardRequestDTO requestDTO);

    // 특정 매칭 게시글을 삭제하는 메서드
    void deleteMatchingBoard(Long id, Long memberId);
}
