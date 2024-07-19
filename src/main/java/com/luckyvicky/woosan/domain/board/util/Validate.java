package com.luckyvicky.woosan.domain.board.util;


import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.exception.BoardException;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.MemberException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.luckyvicky.woosan.domain.board.util.Constants.*;

@Component
@AllArgsConstructor
public class Validate {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * BoardDTO 입력값 검증
     */
    public void validateBoardInput(BoardDTO boardDTO) {
        validateBoardTitle(boardDTO.getTitle());
        validateBoardContent(boardDTO.getContent());
        validateBoardCategory(boardDTO.getCategoryName());
    }

    private void validateBoardTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BoardException(ErrorCode.NULL_OR_BLANK);
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BoardException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateBoardContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BoardException(ErrorCode.NULL_OR_BLANK);
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BoardException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateBoardCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new BoardException(ErrorCode.NULL_OR_BLANK);
        }
        if (!VALID_CATEGORIES.contains(categoryName)) {
            throw new BoardException(ErrorCode.INVALID_TYPE); // 유효하지 않은 카테고리일 경우
        }
    }


    /**
     * Board 존재 여부 검증
     */
    public boolean validateBoardExist(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new BoardException(ErrorCode.BOARD_NOT_FOUND);
        }
        return true;
    }

    /**
     * Writer 존재 여부 검증
     */
    public Member validateWriter(Long writerId) {
        if (writerId == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return memberRepository.findById(writerId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }


    /**
     * 작성자 검증 및 조회 (포인트 추가)
     */
    public Member validateAndFetchWriter(Long writerId) {
        Member writer = memberRepository.findById(writerId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        writer.addPoint(10);
        return writer;
    }


    /**
     * 게시물 소유자 검증
     * 작성자 일치 여부 확인
     */
    public void checkBoardOwnership(Long boardId, Long writerId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
        if (!board.getWriter().getId().equals(writerId)) {
            throw new BoardException(ErrorCode.ACCESS_DENIED);
        }

    }



}
