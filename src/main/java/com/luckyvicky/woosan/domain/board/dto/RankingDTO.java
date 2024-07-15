package com.luckyvicky.woosan.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingDTO {
    private int rank;
    private String keyword;
    private String change; // "+", "-", " "
}
