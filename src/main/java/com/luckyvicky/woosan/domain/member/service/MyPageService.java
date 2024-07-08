package com.luckyvicky.woosan.domain.member.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MyPageService {

    //내가 작성한 게시글 조회(마이페이지)
    List<BoardDTO> getBoardsByWriterId(Long writerId);
    List<BoardDTO> getTargetIdByLikes(Long targetId);

}
