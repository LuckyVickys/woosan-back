package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoard;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardReplyRepository;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingBoardReplyServiceImpl implements MatchingBoardReplyService {

    private final MatchingBoardReplyRepository matchingBoardReplyRepository;
    private final MatchingBoardRepository matchingBoardRepository;
    private final MemberRepository memberRepository;

    // 댓글 생성
    @Override
    @Transactional
    public MatchingBoardReply createReply(MatchingBoardReplyRequestDTO requestDTO) {
        // 매칭 보드와 회원 객체 가져오기
        MatchingBoard matchingBoard = matchingBoardRepository.findById(requestDTO.getMatchingId())
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드가 존재하지 않습니다."));
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 댓글 객체 생성 및 저장
        MatchingBoardReply reply = MatchingBoardReply.builder()
                .content(requestDTO.getContent())
                .writer(member.getUsername())
                .regDate(LocalDateTime.now())
                .parentId(requestDTO.getParentId())
                .matchingId(matchingBoard.getId())
                .build();

        return matchingBoardReplyRepository.save(reply);
    }

    // 댓글 삭제
    @Override
    @Transactional
    public void deleteReply(Long id, Long memberId) {
        MatchingBoardReply reply = matchingBoardReplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 댓글 작성자인지 확인
        if (!reply.getWriter().equals(memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다.")).getUsername())) {
            throw new IllegalArgumentException("본인만 댓글을 삭제할 수 있습니다.");
        }

        matchingBoardReplyRepository.delete(reply);
    }

    // 특정 매칭 보드의 모든 댓글 가져오기
    @Override
    public List<MatchingBoardReply> getRepliesByMatchingId(Long matchingId) {
        return matchingBoardReplyRepository.findByMatchingId(matchingId);
    }
}
