package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BoardService {
    Long add(BoardDTO boardDTO);

    void modify(BoardDTO boardDTO);

    void remove(Long id);

    BoardDTO get(Long id);



    @Transactional(readOnly = true)
    BoardPageResponseDTO getBoardPage(PageRequestDTO pageRequestDTO, String categoryName);


    @Transactional
    BoardDTO getBoard(Long id);

    @Transactional(readOnly = true)
    BoardDTO getNotice(String categoryName);

    @Transactional(readOnly = true)
    List<BoardDTO> getTop3ByLikes();


    @Transactional
    List<BoardDTO> getNotices();

    @Transactional
    List<BoardDTO> getBest();

    boolean validationBoardId(Long boardId);

    //내가 작성한 게시글 조회(마이페이지)
    List<BoardDTO> getBoardsByWriterId(Long writerId);
}


