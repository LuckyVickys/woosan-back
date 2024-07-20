package com.luckyvicky.woosan.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TargetDTO {
    private String type;
    private Long targetId;

}
