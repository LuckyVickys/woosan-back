package com.luckyvicky.woosan.domain.admin.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.dto.BoardPageResponseDTO;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface AdminService {

    Long add(BoardDTO boardDTO);

    void modify(BoardDTO boardDTO);

    void remove(Long id, Long writerId);
}
