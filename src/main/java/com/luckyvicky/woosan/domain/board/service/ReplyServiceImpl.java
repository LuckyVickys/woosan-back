package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.dto.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.exception.BoardNotFoundException;
import com.luckyvicky.woosan.domain.board.exception.MemberNotFoundException;
import com.luckyvicky.woosan.domain.board.exception.ReplyNotFoundException;
import com.luckyvicky.woosan.domain.board.projection.IReply;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.ReplyRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.global.exception.ErrorCode;
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
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final BoardService boardService;

    /**
     * 댓글 작성
     */
    @Override
    public ReplyDTO add(ReplyDTO replyDTO, Long parentId) {

        // 게시글과 작성자 조회
        Board board = boardRepository.findById(replyDTO.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND));
        Member writer = memberRepository.findById(replyDTO.getWriterId())
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 댓글 생성 빌더
        Reply.ReplyBuilder replyBuilder = Reply.builder()
                .board(board)
                .writer(writer)
                .content(replyDTO.getContent());

        // 부모 댓글이 있는 경우
        if (parentId != null) {
            // 부모 댓글 존재 여부 확인
            validationParentId(parentId);
            replyBuilder.parentId(parentId);
        }

        Reply reply = replyBuilder.build();

        // 작성자 포인트 추가 및 저장
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
        boardService.validationBoardId(boardId);
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
        validationReplyId(id);
        replyRepository.deleteById(id);
    }


//    <--------------------------------예외처리-------------------------------->

    /**
     * 요청된 댓글이 존재하지 않을 때
     */
    public boolean validationReplyId(Long replyId) {
        boolean exists = replyRepository.existsById(replyId);
        if (!exists) {
            throw new ReplyNotFoundException(ErrorCode.REPLY_NOT_FOUND);
        }
        return true;
    }

    /**
     * 부모 댓글이 존재하지 않을 때
     */
    public boolean validationParentId(Long parentId) {
        boolean exists = replyRepository.existsById(parentId);
        if (!exists) {
            throw new ReplyNotFoundException(ErrorCode.REPLY_NOT_FOUND);
        }
        return true;
    }

}
