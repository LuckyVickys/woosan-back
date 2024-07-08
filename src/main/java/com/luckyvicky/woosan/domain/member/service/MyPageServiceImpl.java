package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.WriterDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService{

    private final BoardRepository boardRepository;

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

}
