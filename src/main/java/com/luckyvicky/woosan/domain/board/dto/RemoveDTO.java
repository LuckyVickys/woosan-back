package com.luckyvicky.woosan.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveDTO {
    private Long id;
    private Long writerId;
}
