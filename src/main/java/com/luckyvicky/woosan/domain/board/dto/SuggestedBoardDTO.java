package com.luckyvicky.woosan.domain.board.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuggestedBoardDTO {
    private Long id;
    private String categoryName;
    private String title;
    private LocalDateTime regDate;
    private int replyCount;
    private int likesCount;
}
