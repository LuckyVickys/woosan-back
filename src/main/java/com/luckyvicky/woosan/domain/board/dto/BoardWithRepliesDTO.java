package com.luckyvicky.woosan.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardWithRepliesDTO {
    private BoardDTO board;
    private PageResponseDTO<ReplyDTO> replies;
}
