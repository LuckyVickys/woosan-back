package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.exception.BoardException;
import com.luckyvicky.woosan.global.exception.MemberException;
import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.util.HashUtil;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final HttpSession session;
    private final FileImgService fileImgService;

    private static final int MAX_TITLE_LENGTH = 40;     // 제목 최대 길이
    private static final int MAX_CONTENT_LENGTH = 1960;     // 내용 최대 길이
    private static final List<String> VALID_CATEGORIES = List.of("맛집", "청소", "요리", "재테크", "인테리어", "정책", "기타", "공지사항"); // 유효한 카테고리 목록



    /**
     * 게시물 작성
     */
    @Override
    public Long add(BoardDTO boardDTO) {
        validateBoardDTO(boardDTO); // 입력값 검증
        validateWriterId(boardDTO.getWriterId()); // 작성자 검증


            // writer 정보를 통해 Member 엔티티를 조회
            Member writer = memberRepository.findById(boardDTO.getWriterId())
                    .orElseThrow(() ->  new MemberException(ErrorCode.MEMBER_NOT_FOUND));

            // 10 포인트 추가
            writer.addPoint(10);
            memberRepository.save(writer);

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
     * 게시물 다건 조회(공지사항, 인기글, 전체 조회(카테고리)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO getBoardPage(PageRequestDTO pageRequestDTO, String categoryName) {
        pageRequestDTO.validate();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").ascending());

        // 공지사항 조회
        BoardDTO notice = getNotice("공지사항");

        // 인기글 조회
        List<BoardDTO> popularList = getTop3ByLikes();

        // 일반 게시물 조회
        Page<IBoardMember> result;
        if (categoryName != null && !categoryName.isEmpty()) {
            result = boardRepository.findAllProjectedByCategoryNameAndIsDeletedFalseOrderByIdDesc(categoryName, pageable);
        } else {
            result = boardRepository.findAllProjectedByCategoryNameNotAndIsDeletedFalseOrderByIdDesc("공지사항", pageable);
        }
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<BoardDTO> boardPage = PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();

        return BoardPageResponseDTO.builder()
                .notice(notice)
                .popularList(popularList)
                .boardPage(boardPage)
                .build();
    }



    /**
     * 공지사항 다건 조회 (cs)
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardDTO> getNoticePage(PageRequestDTO pageRequestDTO) {
        pageRequestDTO.validate();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").descending());
        Page<IBoardMember> result = boardRepository.findAllProjectedByCategoryNameAndIsDeletedFalseOrderByIdDesc("공지사항", pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }






    /**
     * 게시물 단건 조회 - 상세 페이지 (조회수 증가)
     */
    @Transactional
    @Override
    public BoardDTO getBoard(Long id) {

        // 세션 키 설정
        String sessionKey = "viewedBoard_" + HashUtil.sha256(String.valueOf(id));
        // 세션에서 조회 여부 확인
        Boolean hasViewed = (Boolean) session.getAttribute(sessionKey);
        // Board 엔티티를 조회하여 조회수를 증가
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));

        // 조회수 증가
        if (hasViewed == null || !hasViewed) {
            board.addViewCount();
            boardRepository.save(board);
            session.setAttribute(sessionKey, true); // 세션에 조회 여부 저장
        }


        Optional<IBoardMember> result = boardRepository.findById(id, IBoardMember.class);
        return result.map(boardMember -> {
            BoardDTO boardDTO = modelMapper.map(boardMember, BoardDTO.class);
            boardDTO.setViews(board.getViews());  // 최신 조회수 DTO에 반영
            boardDTO.setFilePathUrl(fileImgService.findFiles("board", id));   // 버킷에서 이미지 url 꺼내고 DTO에 반영
            boardDTO.setWriterProfile(fileImgService.findFiles("member", boardDTO.getWriterId()));   // 버킷에서 이미지 url 꺼내고 DTO에 반영

            return boardDTO;
        }).orElse(null);

//        + 작성자 프로필 이미지 조회 포함 필요
    }


    /**
     * 게시물 단건 조회 (수정)
     */
    @Override
    public BoardDTO get(Long id) {
        validationBoardId(id);

        Optional<IBoardMember> result = boardRepository.findById(id, IBoardMember.class);
        List<String> filesUrl = fileImgService.findFiles("board", id);

        BoardDTO boardDTO = result.map(boardMember -> modelMapper.map(boardMember, BoardDTO.class)).orElse(null);
        boardDTO.setFilePathUrl(filesUrl);
        return boardDTO;
    }


    /**
     * 게시물 수정
     */
    @Override
    public void modify(BoardDTO boardDTO) {
        validateBoardDTO(boardDTO); // 입력값 검증

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));

        // 작성자 검증
        if (!board.getWriter().getId().equals(boardDTO.getWriterId())) {
            throw new BoardException(ErrorCode.ACCESS_DENIED);
        }

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
    public void remove(Long id) {
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


    /**
     * (공지사항 상단 고정)
     * 최신 게시물 단건 조회
     */
    @Transactional(readOnly = true)
    public BoardDTO getNotice(String categoryName) {
        Optional<IBoardMember> result = boardRepository.findFirstByCategoryNameAndIsDeletedFalse(categoryName);
        return result.map(boardMember -> modelMapper.map(boardMember, BoardDTO.class)).orElse(null);
    }


    /**
     * (인기글 상단 고정)
     * 인기 게시물 상위 3개 조회
     */
    @Transactional(readOnly = true)
    public List<BoardDTO> getTop3ByLikes() {
        List<IBoardMember> result = boardRepository.findTop3ByIsDeletedFalseOrderByViewsDesc();
        return result.stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());
    }


    /**
     * 공지사항 게시물 10개 조회
     */
    @Override
    @Transactional
    public List<BoardDTO> getNotices() {
        String categoryName = "공지사항";
        List<IBoardMember> result = boardRepository.findTop10ProjectedByCategoryNameAndIsDeletedFalse(categoryName);
        return result.stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 인기글 게시물 10개 조회
     */
    @Override
    @Transactional
    public List<BoardDTO> getBest() {
        List<IBoardMember> result = boardRepository.findTop10ProjectedByIsDeletedFalseOrderByViewsDesc();
        return result.stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());
    }


//    /**
//     * (공지사항 상단 고정)
//     * 특정 게시물 단건 조회
//     */
//    @Transactional(readOnly = true)
//    public BoardDTO getSpecificNotice(Long id, String categoryName) {
//        Optional<IBoardMember> result = boardRepository.findFirstByIdAndCategoryNameAndIsDeletedFalse(id, categoryName, IBoardMember.class);
//        return result.map(boardMember -> modelMapper.map(boardMember, BoardDTO.class)).orElse(null);
//    }




    //내가 작성한 게시글 조회(마이페이지)
    @Override
    public List<BoardDTO> getBoardsByWriterId(Long writerId) {
        List<Board> boards = boardRepository.findByWriterId(writerId);

        if (boards.isEmpty()) {
            throw new IllegalArgumentException("Member not found");
        }

        WriterDTO writerDTO = new WriterDTO();
        return boards.stream().map(board -> BoardDTO.builder()
                .id(board.getId())
                .writerId(writerDTO.builder()
                        .id(board.getWriter().getId())
                        .nickname(board.getWriter().getNickname())
                        .build().getId())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .views(board.getViews())
                .likesCount(board.getLikesCount())
                .categoryName(board.getCategoryName())
                // .images(null)  // 필요한 경우 적절히 매핑
                // .filePathUrl(null)  // 필요한 경우 적절히 매핑
                .build()).collect(Collectors.toList());
    }




//    <--------------------------------예외처리-------------------------------->

    /**
     * 요청된 게시글이 존재하지 않을 때
     */
    @Override
    public boolean validationBoardId(Long boardId) {
        boolean exists = boardRepository.existsById(boardId);
        if (!exists) {
            throw new BoardException(ErrorCode.BOARD_NOT_FOUND);
        }
        return true;
    }

    /**
     * BoardDTO 입력값 검증
     */
    private void validateBoardDTO(BoardDTO boardDTO) {
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
            throw new BoardException(ErrorCode.INVALID_TYPE); // 유효하지 않은 카테고리일 경우
        }
    }

    /**
     * writerId 검증
     */
    @Override
    public Member validateWriterId(Long writerId) {
        if (writerId == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return memberRepository.findById(writerId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }


}
