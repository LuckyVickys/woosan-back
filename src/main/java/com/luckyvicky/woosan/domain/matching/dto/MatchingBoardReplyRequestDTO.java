package com.luckyvicky.woosan.domain.matching.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingBoardReplyRequestDTO {
    private String content;
    private String writer;
    private Long parentId;
    private Long matchingId;
}
