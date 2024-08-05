package com.luckyvicky.woosan.domain.board.dto;

import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BoardPageResponseDTO {
    private BoardListDTO notice;
    private List<BoardListDTO> popularList;
    private PageResponseDTO<BoardListDTO> boardPage;
}
