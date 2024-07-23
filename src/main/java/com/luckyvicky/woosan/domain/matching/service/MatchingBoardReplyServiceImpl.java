package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
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
    private final FileImgService fileImgService;
    private final MatchingBoardReplyMapper matchingBoardReplyMapper;

    // 댓글 작성
    @Override
    public MatchingBoardReplyResponseDTO saveReply(MatchingBoardReplyRequestDTO requestDTO) {
        Member writer = memberRepository.findById(requestDTO.getWriterId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 ID가 잘못되었습니다."));
        MatchingBoardReply reply = matchingBoardReplyMapper.toEntity(requestDTO);
        reply = reply.toBuilder()
                .writer(writer)
                .matchingBoard(matchingBoardRepository.findById(requestDTO.getMatchingId())
                        .orElseThrow(() -> new IllegalArgumentException("매칭 보드 ID가 잘못되었습니다.")))
                .build();

        if (requestDTO.getParentId() != null) {
            validationParentId(requestDTO.getParentId());
            reply = reply.toBuilder().parentId(requestDTO.getParentId()).build();
        }

        MatchingBoardReply savedReply = matchingBoardReplyRepository.save(reply);
        return mapToResponseDTO(savedReply);
    }

    // 특정 매칭 보드의 모든 댓글과 답글을 페이지네이션으로 가져옵니다.
    @Override
    @Transactional(readOnly = true)
    public Page<MatchingBoardReplyResponseDTO> getAllRepliesByMatchingBoardId(Long matchingId, Pageable pageable) {
        if (!matchingBoardRepository.existsById(matchingId)) {
            throw new IllegalArgumentException("매칭 보드가 존재하지 않습니다.");
        }
        return matchingBoardReplyRepository.findByMatchingBoardId(matchingId, pageable)
                .map(reply -> {
                    MatchingBoardReplyResponseDTO responseDTO = mapToResponseDTO(reply);
                    responseDTO.setChildReplies(getRepliesByParentId(reply.getId()));
                    return responseDTO;
                });
    }

    // 댓글 삭제
    @Override
    public void deleteReply(Long id, Long memberId) {
        MatchingBoardReply reply = matchingBoardReplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 ID가 잘못되었습니다."));
        if (!reply.getWriter().getId().equals(memberId)) {
            throw new IllegalStateException("자신의 댓글만 삭제할 수 있습니다.");
        }
        matchingBoardReplyRepository.delete(reply);
    }

    // 특정 부모 댓글의 모든 자식 댓글을 가져옵니다.
    private List<MatchingBoardReplyResponseDTO> getRepliesByParentId(Long parentId) {
        return matchingBoardReplyRepository.findByParentId(parentId)
                .stream()
                .map(reply -> {
                    MatchingBoardReplyResponseDTO responseDTO = mapToResponseDTO(reply);
                    responseDTO.setChildReplies(getRepliesByParentId(reply.getId()));
                    return responseDTO;
                })
                .collect(Collectors.toList());
    }

    // 부모 댓글 ID 유효성 검사
    private void validationParentId(Long parentId) {
        if (!matchingBoardReplyRepository.existsById(parentId)) {
            throw new IllegalArgumentException("부모 댓글 ID가 잘못되었습니다.");
        }
    }

    // MatchingBoardReply 엔티티를 MatchingBoardReplyResponseDTO로 변환하는 메서드
    private MatchingBoardReplyResponseDTO mapToResponseDTO(MatchingBoardReply reply) {
        Member member = memberRepository.findById(reply.getWriter().getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        List<String> profileImageUrls = fileImgService.findFiles("member", member.getId());

        return MatchingBoardReplyResponseDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .writerId(reply.getWriter().getId())
                .regDate(reply.getRegDate())
                .parentId(reply.getParentId())
                .matchingId(reply.getMatchingBoard().getId())
                .nickname(member.getNickname())
                .profileImageUrl(profileImageUrls.isEmpty() ? null : profileImageUrls.get(0))
                .build();
    }
}
