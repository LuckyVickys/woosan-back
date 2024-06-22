package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.PageRequestDTO;
import com.luckyvicky.woosan.domain.board.dto.PageResponseDTO;
import com.luckyvicky.woosan.domain.board.projection.IBoard;
import com.luckyvicky.woosan.domain.board.projection.IBoardMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface BoardService {
    Long register(BoardDTO boardDTO);

    BoardDTO get(Long id);

    PageResponseDTO<BoardDTO> getlist(PageRequestDTO pageRequestDTO,  String categoryName);

    void modify(BoardDTO boardDTO);

    void delete(Long id);




    // <---------------프로젝션----------------->

    /**
     *  단일 인터페이스
      */
//    List<IBoard> findAllProjectedBoard();


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
}


