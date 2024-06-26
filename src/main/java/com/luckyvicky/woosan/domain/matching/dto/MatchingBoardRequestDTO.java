package com.luckyvicky.woosan.domain.matching.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class MatchingBoardRequestDTO {
    private Long memberId;
    private int matchingType; // 1: 정기 모임, 2: 번개, 3: 셀프 소개팅
    private String title;
    private String content;
    private String placeName;
    private BigDecimal locationX;
    private BigDecimal locationY;
    private String address;
    private LocalDateTime meetDate;
    private String tag;
    private int headCount;
}
