package com.luckyvicky.woosan.domain.likes.dto;

import lombok.Data;

@Data
public class ToggleRequestDTO {
    private Long memberId;
    private String type;
    private Long targetId;
}
