package com.luckyvicky.woosan.domain.admin.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.exception.BoardException;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.service.BoardService;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final FileImgService fileImgService;
    private final BoardService boardService;

    private static final int MAX_TITLE_LENGTH = 40;     // 제목 최대 길이
    private static final int MAX_CONTENT_LENGTH = 1960;     // 내용 최대 길이
    private static final List<String> VALID_CATEGORIES = List.of("공지사항"); // 유효한 카테고리 목록

    /**
     * 공지사항 작성
     */
    @Override
    public Long add(BoardDTO boardDTO) {
        validateBoardDTO(boardDTO); // 입력값 검증
        Member writer = validateAdmin(boardDTO.getWriterId()); // 작성자 검증

        // Board 엔티티를 생성합니다.
        Board board = Board.builder()
                .writer(writer)
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .categoryName(boardDTO.getCategoryName())
                .build();

        board = boardRepository.save(board);

        //파일이 있으면 파일 정보를 버킷 및 db에 저장합니다.
        if (boardDTO.getImages() != null) {
            fileImgService.fileUploadMultiple("board", board.getId(), boardDTO.getImages());
        }

        return board.getId();
    }



    /**
     * 게시물 수정
     */
    @Override
    public void modify(BoardDTO boardDTO) {
        validateBoardDTO(boardDTO); // 입력값 검증

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));

        validateAdmin(boardDTO.getWriterId());


        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());

        if (boardDTO.getFilePathUrl() == null) {
            fileImgService.targetFilesDelete("board", board.getId());
        } else {
            List<String> beforeFiles = fileImgService.findFiles("board", board.getId());
            List<String> afterFiles = boardDTO.getFilePathUrl();

            for (String beforeFile : beforeFiles) {
                if (!afterFiles.contains(beforeFile)) {
                    fileImgService.deleteS3FileByUrl(board.getId(), "board", beforeFile);
                }
            }
        }

        if (boardDTO.getImages() != null) {
            fileImgService.fileUploadMultiple("board", board.getId(), boardDTO.getImages());
        }
        boardRepository.save(board);
    }


    /**
     * 게시물 삭제
     */
    @Override
    public void remove(Long id, Long writerId) {
        validateAdmin(writerId); // 작성자 검증

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));

        if (board.isDeleted()) {
            throw new BoardException(ErrorCode.BOARD_ALREADY_DELETED); // 이미 삭제된 게시물인 경우
        }

        // 작성자 검증
//        Long userId = (Long) session.getAttribute("userId"); // 토큰으로 변경 필요
//        if (!board.getWriter().getId().equals(userId)) {
//            throw new BoardException(ErrorCode.ACCESS_DENIED);
//        }

        board.changeIsDeleted(true);
        boardRepository.save(board);

    }



//    <------------------------------예외처리------------------------------>
    /**
     * writerId 검증
     */
    private Member validateAdmin(Long writerId) {
        if (writerId != 1) {
            throw new MemberException(ErrorCode.ACCESS_DENIED);
        }

        return memberRepository.findById(writerId)
                .orElseThrow(() -> new MemberException(ErrorCode.ACCESS_DENIED));
    }



    /**
     * BoardDTO 입력값 검증
     */
    public void validateBoardDTO(BoardDTO boardDTO) {
        if (boardDTO.getTitle() == null || boardDTO.getTitle().trim().isEmpty()) {
            throw new BoardException(ErrorCode.NULL_OR_BLANK);
        }
        if (boardDTO.getTitle().length() > MAX_TITLE_LENGTH) {
            throw new BoardException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (boardDTO.getContent() == null || boardDTO.getContent().trim().isEmpty()) {
            throw new BoardException(ErrorCode.NULL_OR_BLANK);
        }
        if (boardDTO.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new BoardException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (boardDTO.getCategoryName() == null || boardDTO.getCategoryName().trim().isEmpty()) {
            throw new BoardException(ErrorCode.NULL_OR_BLANK);
        }
        if (!VALID_CATEGORIES.contains(boardDTO.getCategoryName())) {
            throw new BoardException(ErrorCode.INVALID_TYPE); // 공지사항 아닐 경우
        }
    }
}
