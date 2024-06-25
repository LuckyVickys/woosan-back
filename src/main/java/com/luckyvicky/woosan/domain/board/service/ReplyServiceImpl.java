package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.dto.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.projection.IReply;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.ReplyRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    /**
     * 댓글 작성
     */
    @Override
    public ReplyDTO add(ReplyDTO replyDTO, Long parentId) {

        Board board = boardRepository.findById(replyDTO.getBoardId())
                .orElseThrow();
        Member writer = memberRepository.findById(replyDTO.getWriter().getId())
                .orElseThrow();

        Reply reply;

        if (parentId != null){
            //자식 댓글로 설정
            Reply parentReply = replyRepository.findById(parentId)
                    .orElseThrow();

            reply = Reply.builder()
                    .board(board)
                    .writer(writer)
                    .content(replyDTO.getContent())
                    .parentId(parentId)
                    .build();
        } else {
            //부모 댓글로 설정
            reply = Reply.builder()
                    .board(board)
                    .writer(writer)
                    .content(replyDTO.getContent())
                    .build();
        }

        //  1포인트 추가
        writer.addPoint(1);
        memberRepository.save(writer);

        Reply savedReply = replyRepository.save(reply);
        return modelMapper.map(savedReply, ReplyDTO.class);
    }



    /**
     * 댓글 조회
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long boardId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
        Page<IReply> parentReplies = replyRepository.findByBoardIdAndParentIdIsNull(boardId, pageable);

        List<ReplyDTO> parentReplyDTOs = parentReplies.getContent().stream()
                .map(this::convertToReplyDTOWithChildren)
                .collect(Collectors.toList());

        long totalCount = parentReplies.getTotalElements();

        return PageResponseDTO.<ReplyDTO>withAll()
                .dtoList(parentReplyDTOs)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }

    private ReplyDTO convertToReplyDTOWithChildren(IReply iReply) {
        ReplyDTO replyDTO = modelMapper.map(iReply, ReplyDTO.class);
        List<ReplyDTO> childReplyDTOs = replyRepository.findByParentId(iReply.getId()).stream()
                .map(this::convertToReplyDTOWithChildren)
                .collect(Collectors.toList());
        replyDTO.setChildren(childReplyDTOs);
        return replyDTO;
    }



    /**
     * 댓글 삭제
     */
    @Override
    public void remove(Long id) {
        replyRepository.deleteById(id);
    }


}
