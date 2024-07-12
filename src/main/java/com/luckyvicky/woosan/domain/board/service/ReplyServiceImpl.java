package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.dto.ReplyDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.exception.BoardException;
import com.luckyvicky.woosan.global.exception.MemberException;
import com.luckyvicky.woosan.domain.board.exception.ReplyException;
import com.luckyvicky.woosan.domain.board.projection.IReply;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
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
    private final FileImgService fileImgService;

    private static final int MAX_CONTENT_LENGTH = 1000;     // 내용 최대 길이
    /**
     * 댓글 작성
     */
    @Override
    public ReplyDTO add(ReplyDTO replyDTO) {

        //필수 입력값 검증
        validateReplyDTO(replyDTO);

        // 게시글과 작성자 조회
        Board board = boardRepository.findById(replyDTO.getBoardId())
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
        Member writer = memberRepository.findById(replyDTO.getWriterId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 댓글 생성 빌더
        Reply.ReplyBuilder replyBuilder = Reply.builder()
                .board(board)
                .writer(writer)
                .content(replyDTO.getContent());

        // 부모 댓글이 있는 경우
        if (replyDTO.getParentId() != null) {
            // 부모 댓글 존재 여부 확인
            validationParentId(replyDTO.getParentId());

            replyBuilder.parentId(replyDTO.getParentId());
        }

        Reply reply = replyBuilder.build();

        // 작성자 포인트 추가 및 저장
        writer.addPoint(1);
        memberRepository.save(writer);

        board.changeReplyCount(+1);
        boardRepository.save(board);

        Reply savedReply = replyRepository.save(reply);

        return null;
//                modelMapper.map(savedReply, ReplyDTO.class);
    }


    /**
     * 댓글 조회
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long boardId, PageRequestDTO pageRequestDTO) {
        boardService.validationBoardId(boardId);

        pageRequestDTO.validate();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
        Page<IReply> parentReplies = replyRepository.findByBoardIdAndParentIdIsNull(boardId, pageable);

        List<ReplyDTO> parentReplyDTOs = parentReplies.getContent().stream()
                .map(this::convertToReplyDTOWithChildren)
                .collect(Collectors.toList());

        System.out.println("====================================");
        System.out.println(parentReplyDTOs);
        System.out.println("====================================");

        long totalCount = parentReplies.getTotalElements();

        return PageResponseDTO.<ReplyDTO>withAll()
                .dtoList(parentReplyDTOs)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }

    private ReplyDTO convertToReplyDTOWithChildren(IReply iReply) {
        ReplyDTO replyDTO = modelMapper.map(iReply, ReplyDTO.class);
        replyDTO.setWriterProfile(fileImgService.findFiles("member", iReply.getWriter().getId()));

        List<ReplyDTO> childReplyDTOs = replyRepository.findByParentId(iReply.getId()).stream()
                .map(childReply -> {
                    ReplyDTO childReplyDTO = modelMapper.map(childReply, ReplyDTO.class);
                    childReplyDTO.setWriterProfile(fileImgService.findFiles("member", childReply.getWriter().getId()));
                    return childReplyDTO;
                })
                .collect(Collectors.toList());

        replyDTO.setChildren(childReplyDTOs);
        return replyDTO;
    }

    /**
     * 댓글 삭제
     */
    @Override
    @Transactional
    public void remove(Long id) {
        validationReplyId(id);

        // 부모 댓글 조회
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ReplyException(ErrorCode.REPLY_NOT_FOUND));

        // 자식 댓글 수 계산 및 삭제
        int childReplyCount = deleteChildReplies(reply.getId());

        // 부모 댓글 삭제
        Board board = reply.getBoard();
        board.changeReplyCount(-(childReplyCount + 1));  // 자식 댓글 수와 부모 댓글을 포함한 삭제
        boardRepository.save(board);

        replyRepository.delete(reply);
    }

    private int deleteChildReplies(Long parentId) {
        List<IReply> childReplies = replyRepository.findByParentId(parentId);
        int count = 0;

        for (IReply child : childReplies) {
            count += deleteChildReplies(child.getId()) + 1;
            replyRepository.deleteById(child.getId());
        }

        return count;
    }

//    <--------------------------------예외처리-------------------------------->

    /**
     * 요청된 댓글이 존재하지 않을 때
     */
    public boolean validationReplyId(Long replyId) {
        boolean exists = replyRepository.existsById(replyId);
        if (!exists) {
            throw new ReplyException(ErrorCode.REPLY_NOT_FOUND);
        }
        return true;
    }

    /**
     * 부모 댓글이 존재하지 않을 때
     */
    public boolean validationParentId(Long parentId) {
        boolean exists = replyRepository.existsById(parentId);
        if (!exists) {
            throw new ReplyException(ErrorCode.PARENT_REPLY_NOT_FOUND);
        }
        return true;
    }

    /**
     * 필수 입력값 검증
     */
    private void validateReplyDTO(ReplyDTO replyDTO) {
        if (replyDTO.getBoardId() == null || replyDTO.getWriterId() == null || replyDTO.getContent() == null || replyDTO.getContent().trim().isEmpty()) {
            throw new ReplyException(ErrorCode.NULL_OR_BLANK);
        }
        if (replyDTO.getContent().length() > MAX_CONTENT_LENGTH){
            throw new ReplyException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

}
