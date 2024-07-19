package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.*;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BoardService {


    Long createBoard(BoardDTO boardDTO);

    @Transactional(readOnly = true)
    BoardPageResponseDTO getBoardList(PageRequestDTO pageRequestDTO, String categoryName);

    @Transactional(readOnly = true)
    PageResponseDTO<BoardListDTO> getNoticePage(PageRequestDTO pageRequestDTO);

    @Transactional
    BoardDTO getBoard(Long id);

    BoardDTO getBoardForUpdate(Long id);

    void updateBoard(BoardDTO boardDTO);

    void deleteBoard(RemoveDTO removeDTO);

    @Transactional
    List<BoardListDTO> getNotices();

    @Transactional
    List<BoardListDTO> getBestBoard();


}


