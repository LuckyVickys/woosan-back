package com.luckyvicky.woosan.domain.board.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyBestBoardDTO {
    private Long id;
    private String title;
    private int replyCount;
    private int views;
    private int likesCount;
}
