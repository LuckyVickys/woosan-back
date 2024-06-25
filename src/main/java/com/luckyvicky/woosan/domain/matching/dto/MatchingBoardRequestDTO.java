package com.luckyvicky.woosan.domain.matching.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class MatchingBoardRequestDTO {
    private int postType;
    private String title;
    private String content;
    private String placeName;
    private BigDecimal locationX;
    private BigDecimal locationY;
    private String address;
    private LocalDateTime meetDate;
    private String type;
    private String tag;
    private int headCount;
}
