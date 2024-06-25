package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface BoardService {
    Long add(BoardDTO boardDTO);

    @Transactional(readOnly = true)
    BoardDTO getNotice(String categoryName);

    void modify(BoardDTO boardDTO);

    void remove(Long id);

    BoardDTO get(Long id);

    PageResponseDTO<BoardDTO> getlist(PageRequestDTO pageRequestDTO,  String categoryName);






    // <--------------------------------------------프로젝션 Test-------------------------------------------->

    @Transactional(readOnly = true)
    BoardPageResponseDTO getBoardPage(PageRequestDTO pageRequestDTO, String categoryName);

    /**
     *  단일 인터페이스
      */
//    List<IBoard> findAllProjectedBoard();


    @Transactional
    BoardWithRepliesDTO getWithReplies(Long id, PageRequestDTO pageRequestDTO);

    @Transactional(readOnly = true)
    List<BoardDTO> getTop3ByLikes();

    /**
     * 게시물 단건 조회 프로젝션
     */
    Optional<IBoardMember> findProjectedBoardMemberById(Long id);


    /**
     * 게시물 전체 조회 프로젝션
     */
    Page<IBoardMember> findAllProjectedBoardMember(Pageable pageable);

    @Transactional(readOnly = true)
    Page<IBoardMember> findAllProjectedBoardMemberByCategoryName(String categoryName, Pageable pageable);



    // <--------------------------------------------프로젝션 Test-------------------------------------------->
}


