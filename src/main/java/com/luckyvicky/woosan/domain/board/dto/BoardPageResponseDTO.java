package com.luckyvicky.woosan.domain.board.dto;

import com.luckyvicky.woosan.domain.board.entity.Board;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BoardPageResponseDTO {
    private BoardDTO notice;
    private List<BoardDTO> popularList;
    private PageResponseDTO<BoardDTO> boardPage;
}
