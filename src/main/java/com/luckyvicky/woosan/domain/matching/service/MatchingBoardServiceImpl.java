package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MatchingBoardServiceImpl implements MatchingBoardService {

    @Autowired
    private MatchingBoardRepository matchingBoardRepository;

    @Override
    public MatchingBoard createMatchingBoard(MatchingBoardRequestDTO requestDTO){
        MatchingBoard matchingBoard = new MatchingBoard();
        matchingBoard.setPostType(requestDTO.getPostType());
        matchingBoard.setTitle(requestDTO.getTitle());
        matchingBoard.setContent(requestDTO.getContent());
        matchingBoard.setPlaceName(requestDTO.getPlaceName());
        matchingBoard.setLocationX(requestDTO.getLocationX());
        matchingBoard.setLocationY(requestDTO.getLocationY());
        matchingBoard.setAddress(requestDTO.getAddress());
        matchingBoard.setMeetDate(requestDTO.getMeetDate());
        matchingBoard.setType(requestDTO.getType());
        matchingBoard.setTag(requestDTO.getTag());
        matchingBoard.setHeadCount(requestDTO.getHeadCount());
        matchingBoard.setRegDate(LocalDateTime.now());
        matchingBoard.setViews(0);
        matchingBoard.setIsDeleted(false);
        return matchingBoardRepository.save(matchingBoard);

    }
}
