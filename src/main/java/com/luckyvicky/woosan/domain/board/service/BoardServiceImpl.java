package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
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
    private final ReplyService replyService;
    private final FileImgService fileImgService;

    /**
     * 게시물 작성
     */
    @Override
    public Long add(BoardDTO boardDTO) {
        // writer 정보를 통해 Member 엔티티를 조회합니다.
        Member writer = memberRepository.findById(boardDTO.getWriter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid writer ID"));

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
        //파일 정보를 저장합니다.
        fileImgService.fileUploadMultiple("board", board.getId(), boardDTO.getImages());


        return board.getId();
    }


    /**
     * 게시물 다건 조회 (카테고리)
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardDTO> getlist(PageRequestDTO pageRequestDTO, String categoryName) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").ascending());

        Page<IBoardMember> result;

        if (categoryName != null && !categoryName.isEmpty()) {
            result = boardRepository.findAllProjectedByCategoryNameAndIsDeletedFalse(categoryName, pageable);
        } else {
            result = boardRepository.findAllProjectedByIsDeletedFalse(pageable);
        }

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<BoardDTO> responseDTO = PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();

        return responseDTO;
    }


    /**
     * 게시물 다건 조회(공지사항, 인기글, 전체 조회(카테고리)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO getBoardPage(PageRequestDTO pageRequestDTO, String categoryName){
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").ascending());

        // 공지사항 조회
        BoardDTO notice = getNotice("공지사항");

        // 인기글 조회
        List<BoardDTO> popularList = getTop3ByLikes();

        // 일반 게시물 조회
        Page<IBoardMember> result;
        if(categoryName != null && !categoryName.isEmpty()) {
            result = boardRepository.findAllProjectedByCategoryNameAndIsDeletedFalse(categoryName, pageable);
        } else {
            result = boardRepository.findAllProjectedByIsDeletedFalse(pageable);
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
     * 단건 - 게시물과 댓글 함께 조회
     */
    @Transactional
    @Override
    public BoardWithRepliesDTO getWithReplies(Long id, PageRequestDTO pageRequestDTO) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);


        // 조회수 증가
        board.addViewCount();
        boardRepository.save(board);

        PageResponseDTO<ReplyDTO> replies = replyService.getRepliesByBoardId(id, pageRequestDTO);

        return BoardWithRepliesDTO.builder()
                .board(boardDTO)
                .replies(replies)
                .build();
    }


    /**
     * 게시물 단건 조회 (수정)
     */
    @Override
    public BoardDTO get(Long id) {
        Optional<IBoardMember> result = boardRepository.findById(id, IBoardMember.class);
        return result.map(boardMember -> modelMapper.map(boardMember, BoardDTO.class)).orElse(null);
    }


    /**
     * 게시물 수정
     */
    @Override
    public void modify(BoardDTO boardDTO) {
        Optional<Board> result = boardRepository.findById(boardDTO.getId());
        Board board = result.orElseThrow();

        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());

        boardRepository.save(board);
    }


    /**
     * 게시물 삭제
     */
    @Override
    public void remove(Long id) {
        Optional<Board> result = boardRepository.findById(id);
        Board board = result.orElseThrow();

        board.changeIsDeleted(true);
        boardRepository.save(board);
    }


    /**
     * 공지사항 상단 고정
     * 최신 게시물 단건 조회
     */
    @Override
    @Transactional(readOnly = true)
    public BoardDTO getNotice(String categoryName) {
        Optional<IBoardMember> result = boardRepository.findFirstByCategoryNameAndIsDeletedFalse(categoryName);
        return result.map(boardMember -> modelMapper.map(boardMember, BoardDTO.class)).orElse(null);
    }


    /**
     * 인기글 상단 고정
     * 인기 게시물 상위 3개 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<BoardDTO> getTop3ByLikes() {
        List<IBoardMember> result = boardRepository.findTop3ByIsDeletedFalseOrderByViewsDesc();
        return result.stream()
                .map(boardMember -> modelMapper.map(boardMember, BoardDTO.class))
                .collect(Collectors.toList());
    }

    // <--------------------------------------------미완-------------------------------------------->


//    /**
//     * 공지사항 상단 고정
//     * 특정 게시물 단건 조회
//     */
//    @Transactional(readOnly = true)
//    public BoardDTO getSpecificNotice(Long id, String categoryName) {
//        Optional<IBoardMember> result = boardRepository.findFirstByIdAndCategoryNameAndIsDeletedFalse(id, categoryName, IBoardMember.class);
//        return result.map(boardMember -> modelMapper.map(boardMember, BoardDTO.class)).orElse(null);
//    }


// <--------------------------------------------프로젝션 Test-------------------------------------------->
    /**
     *  <Test>
     * board 단일 인터페이스 프로젝션
     */
//    @Override
//    @Transactional(readOnly = true)
//    public List<IBoard> findAllProjectedBoard() {
//        return boardRepository.findAllProject(IBoard.class);
//    }


    /**
     * <Test>
     * board member 연관관계 인터페이스 프로젝션
     * 게시물 단건 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<IBoardMember> findProjectedBoardMemberById(Long id) {
        return boardRepository.findById(id, IBoardMember.class);
    }


    /**
     * <Test>
     * 연관관계 인터페이스 프로젝션
     * 게시물 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IBoardMember> findAllProjectedBoardMember(Pageable pageable) {
        return boardRepository.findAllProjectedByIsDeletedFalse(pageable);
    }


    /**
     * <Test>
     * 카테고리별 게시물 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IBoardMember> findAllProjectedBoardMemberByCategoryName(String categoryName, Pageable pageable) {
        return boardRepository.findAllProjectedByCategoryNameAndIsDeletedFalse(categoryName, pageable);
    }
    // <--------------------------------------------프로젝션 Test-------------------------------------------->






}
