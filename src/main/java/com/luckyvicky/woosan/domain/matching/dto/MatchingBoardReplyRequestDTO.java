package com.luckyvicky.woosan.domain.matching.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchingBoardReplyRequestDTO {
    private String content;
    private String writer;
    private Long parentId;
    private Long matchingId;
}
