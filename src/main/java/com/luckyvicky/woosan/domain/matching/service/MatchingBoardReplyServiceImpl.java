package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MatchingBoardReplyResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MatchingBoardReply;
import com.luckyvicky.woosan.domain.matching.mapper.MatchingBoardReplyMapper;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardReplyRepository;
import com.luckyvicky.woosan.domain.matching.repository.MatchingBoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingBoardReplyServiceImpl implements MatchingBoardReplyService {

    private final MatchingBoardReplyRepository matchingBoardReplyRepository;
    private final MemberRepository memberRepository;
    private final MatchingBoardRepository matchingBoardRepository;
    private final MatchingBoardReplyMapper matchingBoardReplyMapper;

    // 댓글 작성
    @Override
    @Transactional
    public MatchingBoardReplyResponseDTO saveReply(MatchingBoardReplyRequestDTO requestDTO) {
        Member writer = memberRepository.findById(requestDTO.getWriterId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 ID가 잘못되었습니다."));
        MatchingBoardReply.MatchingBoardReplyBuilder replyBuilder = matchingBoardReplyMapper.toEntity(requestDTO).toBuilder();

        replyBuilder.writer(writer);
        replyBuilder.matchingBoard(matchingBoardRepository.findById(requestDTO.getMatchingId())
                .orElseThrow(() -> new IllegalArgumentException("매칭 보드 ID가 잘못되었습니다.")));

        if (requestDTO.getParentId() != null) {
            validationParentId(requestDTO.getParentId());
            replyBuilder.parentId(requestDTO.getParentId());
        }

        MatchingBoardReply savedReply = matchingBoardReplyRepository.save(replyBuilder.build());

        return matchingBoardReplyMapper.toResponseDTO(savedReply);
    }

    // 특정 매칭 보드의 모든 댓글을 페이지네이션으로 가져옵니다.
    @Override
    @Transactional(readOnly = true)
    public Page<MatchingBoardReplyResponseDTO> getRepliesByMatchingBoardId(Long matchingId, Pageable pageable) {
        return matchingBoardReplyRepository.findAllByMatchingBoardId(matchingId, pageable)
                .map(matchingBoardReplyMapper::toResponseDTO);
    }


    // 특정 부모 댓글의 모든 자식 댓글을 가져옵니다.
    @Override
    @Transactional(readOnly = true)
    public List<MatchingBoardReplyResponseDTO> getRepliesByParentId(Long parentId) {
        return matchingBoardReplyRepository.findAllByParentId(parentId)
                .stream()
                .map(matchingBoardReplyMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 댓글 삭제
    @Override
    @Transactional
    public void deleteReply(Long id, Long memberId) {
        MatchingBoardReply reply = matchingBoardReplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 ID가 잘못되었습니다."));
        if (!reply.getWriter().getId().equals(memberId)) {
            throw new IllegalStateException("자신의 댓글만 삭제할 수 있습니다.");
        }
        matchingBoardReplyRepository.delete(reply);
    }

    private void validationParentId(Long parentId) {
        if (!matchingBoardReplyRepository.existsById(parentId)) {
            throw new IllegalArgumentException("부모 댓글 ID가 잘못되었습니다.");
        }
    }


}
