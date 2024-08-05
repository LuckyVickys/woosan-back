package com.luckyvicky.woosan.domain.board.dto;

import com.luckyvicky.woosan.global.util.PageResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailDTO {
    private BoardDTO boardDTO;
    private List<SuggestedBoardDTO> suggestedBoards;
}
