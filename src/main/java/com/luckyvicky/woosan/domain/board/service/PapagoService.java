package com.luckyvicky.woosan.domain.board.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;

import java.io.IOException;

public interface PapagoService {
    BoardDTO tanslateBoardDetailPage(BoardDTO boardDTO) throws IOException;
}
