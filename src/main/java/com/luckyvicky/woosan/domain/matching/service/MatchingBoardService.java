package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;

public interface MatchingBoardService {
    MatchingBoard createMatchingBoard(MatchingBoardRequestDTO requestDTO);
}
