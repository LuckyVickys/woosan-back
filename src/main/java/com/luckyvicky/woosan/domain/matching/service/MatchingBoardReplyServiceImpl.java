package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MatchingBoardReplyServiceImpl implements MatchingBoardReplyService {

    @Autowired
    private MatchingBoardReplyRepository matchingBoardReplyRepository;

    @Override
    public MatchingBoardReply createReply(MatchingBoardReplyRequestDTO requestDTO) {
        MatchingBoardReply reply = new MatchingBoardReply();
        reply.setContent(requestDTO.getContent());
        reply.setWriter(requestDTO.getWriter());
        reply.setParentId(requestDTO.getParentId());
        reply.setMatchingId(requestDTO.getMatchingId());
        reply.setRegDate(LocalDateTime.now());
        return matchingBoardReplyRepository.save(reply);
    }
}
