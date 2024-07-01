package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardReplyRepository;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchingBoardReplyServiceImpl implements MatchingBoardReplyService {

    @Autowired
    private MatchingBoardReplyRepository matchingBoardReplyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public MatchingBoardReply createReply(MatchingBoardReplyRequestDTO requestDTO) {
        MatchingBoardReply reply = MatchingBoardReply.builder()
                .content(requestDTO.getContent())
                .writer(requestDTO.getWriter())
                .parentId(requestDTO.getParentId())
                .matchingId(requestDTO.getMatchingId())
                .regDate(LocalDateTime.now())
                .build();

        return matchingBoardReplyRepository.save(reply);
    }

    @Override
    @Transactional
    public void deleteReply(Long id, Long memberId) {
        MatchingBoardReply reply = matchingBoardReplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 작성자 확인
        if (!reply.getWriter().equals(memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다.")).getNickname())) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        matchingBoardReplyRepository.delete(reply);
    }

    @Override
    public List<MatchingBoardReply> getRepliesByMatchingBoardId(Long matchingBoardId) {
        return matchingBoardReplyRepository.findByMatchingId(matchingBoardId);
    }
}
